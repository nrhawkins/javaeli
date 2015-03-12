
import java.lang.Math;
import java.lang.System;

public class PixelNeighborhood
{
   private int top, bottom, left, right;
   int[] pixelIntensity;
   int[] pixelIntensityX;
   int[] pixelIntensityY;
   private int[] pixelIntensityTemp;
   private int[] pixelIntensityTempX;
   private int[] pixelIntensityTempY;
   private int caseTop = 1;
   private int caseBottom = 2;
   private int caseLeft = 3;
   private int caseRight = 4;
   private int boundary;
   private int imageHeight;
   private int imageWidth;
   private int radiusSquared;
   private int radiusTimesTwoPlusOneSquared;
   private double power2 = 2;
   private int next = -1;
   private int pixelCount = 0;

   public PixelNeighborhood(int radius, int x, int y, int width, int height, int[][][] thePixels)
   {

      //System.out.println("the radius:" + radius);            
      double testRadius = (double) radius;
      //System.out.println("the test radius:" + testRadius);

      imageHeight = height;
      imageWidth = width;
      radiusSquared = (int) Math.pow((double)radius,power2);
      radiusTimesTwoPlusOneSquared = (int) Math.pow((double)(2*radius + 1),power2);

      //System.out.println("radiusPlusSquared:" + radiusTimesTwoPlusOneSquared);
      //System.out.println("imageHeight:" + imageHeight);
      //System.out.println("imageWidth:" + imageWidth);

      // -----------------
      // set top boundary
      // -----------------
      boundary = y - radius;
      if (boundary >= 0)
      {
         top = boundary;
      }
      else
      {
         top = findBoundary(caseTop);
      }

      //System.out.println("top:" + top);

      // -------------------- 
      // set bottom boundary 
      // --------------------
      boundary = y + radius;
      if (boundary < imageHeight)
      {
         bottom = boundary;
      }
      else
      {
         bottom = findBoundary(caseBottom);
      }

      //System.out.println("bottom:" + bottom);

      // ------------------
      // set left boundary
      // ------------------
      boundary = x - radius;
      if (boundary >= 0)
      {
         left = boundary;
      }
      else
      {
         left = findBoundary(caseLeft);
      }

      //System.out.println("left:" + left);

      // -------------------
      // set right boundary
      // -------------------
      boundary = x + radius;      
      if (boundary < imageWidth)
      {
         right = boundary;
      }
      else
      {
         right = findBoundary(caseRight);
      }

      //System.out.println("right:" + right);

      // collect the pixel intensities in the neighborhood
      
      
      //if ((x==0 && y==0) || (x==1 && y==0))
      //{
         setPixelIntensities(x,y,radius,thePixels);
      //}
              
   }

   private int findBoundary(int side)
   {
      // top
      if (side == caseTop)
      {
         while (boundary < 0)
         {
            boundary = boundary + 1;
         }
      }

      // bottom
      if (side == caseBottom)
      {
         while (boundary >= imageHeight)
         {
            boundary = boundary - 1;                
         }
      }

      // left
      if (side == caseLeft)
      {
         while (boundary < 0)
         {
            boundary = boundary + 1;
         }      
      }

      // right
      if (side == caseRight)
      {
         while (boundary >= imageWidth)
         {
            boundary = boundary - 1;
         }
      }

      return boundary;

   }   

   private void setPixelIntensities(int x, int y, int radius, int[][][] thePixels)
   {
      pixelIntensityTemp = new int[radiusTimesTwoPlusOneSquared];
      pixelIntensityTempX = new int[radiusTimesTwoPlusOneSquared];
      pixelIntensityTempY = new int[radiusTimesTwoPlusOneSquared];

      //System.out.println("radiusTimesTwo...:" + radiusTimesTwoPlusOneSquared);

      
      for (int i=0; i < radiusTimesTwoPlusOneSquared; i++)
      {
         pixelIntensityTemp[i] = -1;
      }
    
      //System.out.println("getting pixels from the top-left quadrant");
      //System.out.println("pixelCount:" + pixelCount);

      //get intensity values from top-left quadrant
      for (int i=top; i <= y; i++)
      {
         for (int j=left; j <= x; j++)
         {
            //System.out.println("top left quadrant");
            checkNeighborhood(i,j,x,y,radius,thePixels);             

         }
      }

      //System.out.println("getting pixels from the top-right quadrant");
      //System.out.println("pixelCount:" + pixelCount);

      //get intensity values from top-right quadrant
      for (int i=top; i <= y; i++)
      {
         for (int j=x; j <= right; j++)
         {

            checkNeighborhood(i,j,x,y,radius,thePixels);

         }
      }     

      //System.out.println("getting pixels from the bottom-left quadrant");
      //System.out.println("pixelCount:" + pixelCount);


      //get intensity values from bottom-left quadrant
      for (int i=y; i <= bottom; i++)
      {
         for (int j=left; j <= x; j++)
         {

            checkNeighborhood(i,j,x,y,radius,thePixels);

         }
      }      

      //System.out.println("getting pixels from the bottom-right quadrant");
      //System.out.println("pixelCount:" + pixelCount);

      //get intensity values from bottom-right quadrant         
      for (int i=y; i <= bottom; i++)
      {
         //System.out.println("checking nghd i:" + i);
         for (int j=x; j <= right; j++)
         {
            //System.out.println("checking nghd j:" + j);
            checkNeighborhood(i,j,x,y,radius,thePixels);
            //System.out.println("checked nghd:" + i + " " + j + " " + x + " " + y + " " + radius);
         }
      }

      //System.out.println("finished getting the pixels");
      //System.out.println("pixelCount:" + pixelCount);

      //Now truncate the pixelIntensities array to the right size
      int pixelIntensityTemplength = 0;
      int i=0;
      while((pixelIntensityTemp[i] != -1) && (i < pixelIntensityTemp.length)) 
      {
         pixelIntensityTemplength += 1;
         i += 1;                    
      }

      //System.out.println("pixelIntensity real length:" + pixelIntensityTemplength);

      pixelIntensity = new int[pixelIntensityTemplength];
      pixelIntensityX = new int[pixelIntensityTemplength];
      pixelIntensityY = new int[pixelIntensityTemplength];

      System.arraycopy(pixelIntensityTemp, 0, pixelIntensity, 0, pixelIntensityTemplength);               
      System.arraycopy(pixelIntensityTempX, 0, pixelIntensityX, 0, pixelIntensityTemplength);
      System.arraycopy(pixelIntensityTempY, 0, pixelIntensityY, 0, pixelIntensityTemplength);

      //System.out.println("checking arraycopy");
      //System.out.println("length the 2 arrays");
      //System.out.println("pIT:" + pixelIntensityTemp.length);
      //System.out.println("pI:" + pixelIntensity.length);

      //for (int k=0; k<20; k++)
      //{
      //   System.out.println("pixelIntensityTemp:" + pixelIntensityTemp[k]);
      //   System.out.println("pixelIntensity:" + pixelIntensity[k]);
      //}

      //System.out.println("finished checking arraycopy.");      
 
   }   

   private void checkNeighborhood(int i, int j, int x, int y, double radius, int[][][] thePixels)
   {
            double distanceFromX = j-x;
            double distanceFromY = i-y;

            //System.out.println("cN: j:" + j);
            //System.out.println("pixelIntensityTempLength:" + pixelIntensityTemp.length);
            //System.out.println("next:" + next);

            if(Math.pow(distanceFromX,power2) + Math.pow(distanceFromY,power2) <= radiusSquared)
            {
               //System.out.println("found a pixel");
               next += 1;
               //System.out.println("(incremented) next:" + next);
               pixelIntensityTemp[next] = thePixels[3][j][i];
               pixelIntensityTempX[next] = j;
               pixelIntensityTempY[next] = i;
               //System.out.println("pixelCount:" + pixelCount);
               pixelCount += 1;               
               //System.out.println("pixelCount:" + pixelCount);
            }

   }

   public int getPixelCount()
   {
      return pixelCount;      
   }

   public int[] getPixelIntensity()
   {
      return pixelIntensity;
   }

   public int[] getPixelIntensityX()
   {
      return pixelIntensityX;
   }   

   public int[] getPixelIntensityY()
   {
      return pixelIntensityY;
   }

}






