
import java.lang.Math;

public class ImageParameters
{

   double meanSpot, meanNotSpot, meanImage; 
   double standardDeviationSpot, standardDeviationNotSpot, standardDeviationImage;
   double initialMeanSpot = 89;
   double initialMeanNotSpot = 220;
   double initialStandardDeviationSpot = 18;
   double initialStandardDeviationNotSpot = 5; 
   double power2 = 2;
   double powerSqRoot = 0.5;

   public ImageParameters(int iteration, int width, int height, int[][] pixelsFg, int[][][] thePixels,
      int[][] spotCenters)
   {
      if (iteration == 1)
      {
         //set parameters to initial values
         /*meanSpot = initialMeanSpot;
         meanNotSpot = initialMeanNotSpot;
         standardDeviationSpot = initialStandardDeviationSpot;
         standardDeviationNotSpot = initialStandardDeviationNotSpot; */
         fitAllData(width,height,thePixels);
         fitSpotData(width,height,thePixels);

      }
      else
      {
         //estimate parameters based on the last iteration's spot center assignments
         //estimateParameters1(width,height,pixelsFg,thePixels);
	 estimateParameters2(width,height,spotCenters,thePixels);
      }   
   }

   private void fitAllData(int width, int height, int[][][] thePixels)
   {

      double meanSumImage = 0;
      double stdSumImage = 0;
      int countPixels = 0;

      //go thru matrix, EliFile.thePixels
      for (int i=0; i<height; i++)
      {
         for (int j=0; j<width; j++)
         {
            if(thePixels[3][j][i] != 255)
            {
               meanSumImage += thePixels[3][j][i];
               stdSumImage += Math.pow(thePixels[3][j][i],power2);
               countPixels += 1;
            }
         }      
      }

      if (countPixels > 0)    
      {
         meanImage = meanSumImage/countPixels;
	 standardDeviationImage = Math.pow((stdSumImage - meanImage*meanSumImage)/(countPixels-1),powerSqRoot);
         System.out.println("computing initial Image Parameters, countPixels:" + countPixels);
         System.out.println("computing initial Image Parameters, meanImage:" + meanImage);
         System.out.println("computing initial Image Parameters, stdImage:" + standardDeviationImage);  
      }
      else
      {
         meanSpot = initialMeanSpot;
         standardDeviationSpot = initialStandardDeviationSpot;
      }
      
   }  

   private void fitSpotData(int width, int height, int[][][] thePixels)
   {

      double meanSumSpot = 0;
      double stdSumSpot = 0;
      int countSpotPixels = 0;
      double cutoff = meanImage - 3*standardDeviationImage;

      System.out.println("ImageParameters: cutoff " + cutoff);

      //go thru matrix, EliFile.thePixels
      for (int i=0; i<height; i++)
      {
         for (int j=0; j<width; j++)
         {
            if(thePixels[3][j][i] < cutoff)
            {
               meanSumSpot += thePixels[3][j][i];
               stdSumSpot += Math.pow(thePixels[3][j][i],power2);
               countSpotPixels += 1;
            }
         }      
      }

      if (countSpotPixels > 0)    
      {
         meanSpot = meanSumSpot/countSpotPixels;
	 standardDeviationSpot = Math.pow((stdSumSpot - meanSpot*meanSumSpot)/(countSpotPixels-1),powerSqRoot);
         System.out.println("computing initial Spot Parameters, countSpotPixels:" + countSpotPixels);
         System.out.println("computing initial Spot Parameters, meanSpot:" + meanSpot);
         System.out.println("computing initial Spot Parameters, stdSpot:" + standardDeviationSpot);  
      }
      else
      {
         meanSpot = 0;
         standardDeviationSpot = 0;
      }

      meanNotSpot = meanImage;
      standardDeviationNotSpot = standardDeviationImage;
      
   }  


   private void estimateParameters1(int width, int height, int[][] pixelsFg, int[][][] thePixels)
   {
      //go thru matrix, EliFile.pixelsFg 
   
      double meanSumSpot = 0;
      double meanSumNotSpot = 0;
      double stdSumSpot = 0;
      double stdSumNotSpot = 0;
      int countSpotPixels = 0;
      int countNotSpotPixels = 0;
      int countSpotPixelsRedundant = 0;
 
      for(int i=0; i<height; i++)
      {
         for(int j=0; j<width; j++)
         { 
            // pixels assigned to spots
            if(pixelsFg[j][i] > 0)
            {
               meanSumSpot += thePixels[3][j][i];
               stdSumSpot += Math.pow(thePixels[3][j][i],power2);
               countSpotPixels += 1;
               countSpotPixelsRedundant += pixelsFg[j][i];               
            }  
            else
            {  
               // pixels found in the background (i.e. plate only, not white corners which equal -1)
               if(pixelsFg[j][i] == 0)
               {  
                  meanSumNotSpot += thePixels[3][j][i];
                  stdSumNotSpot += Math.pow(thePixels[3][j][i],power2); 
                  countNotSpotPixels += 1;
               }
            }             
         }            
      }      

      // estimate mean and standard deviation for spots 
      if (countSpotPixels > 0)
      {
         meanSpot = meanSumSpot/countSpotPixels;
	 standardDeviationSpot = Math.pow((stdSumSpot - meanSpot*meanSumSpot)/(countSpotPixels-1),powerSqRoot);
         System.out.println("computing new Image Parameters, countSpotPixels:" + countSpotPixels);
      }
      else
      {
         meanSpot = initialMeanSpot;
         standardDeviationSpot = initialStandardDeviationSpot;
      }

      // estimate mean for not spots/background
      if (countNotSpotPixels > 0)
      {           
         meanNotSpot = meanSumNotSpot/countNotSpotPixels;
	 standardDeviationNotSpot = Math.pow((stdSumNotSpot - 
            meanNotSpot*meanSumNotSpot)/(countNotSpotPixels-1),powerSqRoot);
      }
      else
      {
         meanNotSpot = initialMeanNotSpot;
         standardDeviationNotSpot = initialStandardDeviationNotSpot;
      }

      System.out.println("countSpotPixels: " + countSpotPixels);      
      System.out.println("countNotSpotPixels: " + countNotSpotPixels);      
      System.out.println(" ");

      System.out.println("countSpotPixelsRedundant: " + countSpotPixelsRedundant);

   }

   private void estimateParameters2(int width, int height, int[][] spotCenters, int[][][] thePixels)
   {
      //go thru matrix, EliFile.spotCenters 
   
      double meanSumSpot = 0;
      double meanSumNotSpot = 0;
      double stdSumSpot = 0;
      double stdSumNotSpot = 0;
      int countSpotPixels = 0;
      int countNotSpotPixels = 0;
      int countSpotPixelsRedundant = 0;
 
      for(int i=0; i<height; i++)
      {
         for(int j=0; j<width; j++)
         { 
            // pixels assigned to spots
            if(spotCenters[j][i] > 1)
            {
               meanSumSpot += thePixels[3][j][i];
               stdSumSpot += Math.pow(thePixels[3][j][i],power2);
               countSpotPixels += 1;
               //countSpotPixelsRedundant += pixelsFg[j][i];               
            }  
            else
            {  
               // pixels found in the background (i.e. plate only, not white corners)
               if(thePixels[3][j][i] != 255)
               {  
                  meanSumNotSpot += thePixels[3][j][i];
                  stdSumNotSpot += Math.pow(thePixels[3][j][i],power2); 
                  countNotSpotPixels += 1;
               }
            }             
         }            
      }      

      // estimate mean and standard deviation for spots 
      if (countSpotPixels > 0)
      {
         meanSpot = meanSumSpot/countSpotPixels;
	 standardDeviationSpot = Math.pow((stdSumSpot - meanSpot*meanSumSpot)/(countSpotPixels-1),powerSqRoot);
         System.out.println("computing new Image Parameters, countSpotPixels:" + countSpotPixels);
      }
      else
      {
         meanSpot = initialMeanSpot;
         standardDeviationSpot = initialStandardDeviationSpot;
      }

      // estimate mean for not spots/background
      if (countNotSpotPixels > 0)
      {           
         meanNotSpot = meanSumNotSpot/countNotSpotPixels;
	 standardDeviationNotSpot = Math.pow((stdSumNotSpot - 
            meanNotSpot*meanSumNotSpot)/(countNotSpotPixels-1),powerSqRoot);
      }
      else
      {
         meanNotSpot = initialMeanNotSpot;
         standardDeviationNotSpot = initialStandardDeviationNotSpot;
      }

      System.out.println("countSpotPixels: " + countSpotPixels);      
      System.out.println("countNotSpotPixels: " + countNotSpotPixels);      
      System.out.println(" ");

      System.out.println("countSpotPixelsRedundant: " + countSpotPixelsRedundant);

   }

   public double getMeanSpot()
   {
      return meanSpot;
   }

   public double getMeanNotSpot()
   {
      return meanNotSpot;
   }

   public double getStandardDeviationSpot()
   {
      return standardDeviationSpot;
   }

   public double getStandardDeviationNotSpot()
   {
      return standardDeviationNotSpot;
   }

   public double getMeanImage()
   {
      return meanImage;
   }

   public double getStandardDeviationImage()
   {
      return standardDeviationImage;
   }
   

}



