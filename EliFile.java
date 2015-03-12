
//import org.fhcrc.elispot.SpotRenderer2b;

import java.io.*;
import javax.media.jai.*;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.util.Arrays;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
//import java.lang.Math;

public class EliFile
{
   private File tiffFile;
   private PlanarImage image;
   private int width, height, minX, minY;
   // 4 levels: r,g,b,avg.
   private int thePixels[][][];
   private int imageMask[][];
   private int spotCenters[][];
   private int spotCentersNoNoise [][];
   private int spotCentersFinal[][];
   private int theCenters[][];
   private int spotRadii[][];
   private double spotProb[][];
   private int numSpots;
   private PixelNeighborhood pixnghd;
   private IterationStats iterationStats;
//   private int radius = 1;
   private int radius = 2;
//   private int radius = 3;
//   private int radius = 5;
   private double cutoff;
   private ImageParameters imageParameters;
   int[][] pixelsFg;
   int spotCount = 0;
   int numberOfIterations = 5;
   private int pixelClusters = 0;
   int[][] clusterNumber;
   private TreeMap globPixelCount;
   private TreeMap globPixelCountNoNoise;
   private TreeMap globPixelCountFinal;
   private TreeMap globData;
   private TreeMap globDataFinal;
   private HashMap meanIntensityPerGlob;
   //private int spotTooSmall = 10;
   private int spotTooSmall = 1;
   private double smallSpotRatio = .95;
   private int[] numberOfSpots;
   private double[] likelihood;
   private int[] aFinal;
   private double[] bFinal;
   private int meanSpots;
   private double meanVariance;

   // Yellow Sheet - based on the 10 recount plates
   //private double[][] probabilityTable = {  {.99,.01,0,0},
   //                                         {.1,.9,0,0},
   //                                         {.050,.948,.002,0},
   //                                         {0,.971,.023,.006},
   //                                         {0,.815,.170,.015},
   //                                         {0,.703,.156,.141},
   //                                         {0,.586,.345,.069},
   //                                         {0,.656,.125,.219},
   //                                       };

   // Add purple sheet - E9, modify cats 3 and 4
   private double[][] probabilityTable = {  {.99,.01,0,0},
                                            {.1,.9,0,0},
                                            {.050,.948,.002,0},
                                            {0,.971,.023,.006},
                                            {0,.67,.33,0},
                                            {0,.14,.43,.43},
                                            {0,.586,.345,.069},
                                            {0,.656,.125,.219},
                                          };

   private double meanNumberSpots=0.0;
   private double varianceNumberSpots=0.0;

   //private boolean DISPLAY = true;
   private boolean DISPLAY = false;

   //make these (a,b) arrays or create an object for them.

   private int a[] = {0,1,2};
   private double b[] = {0.5,1.0,1.5};

   private int A_OPTIONS = 3;
   private int B_OPTIONS = 3;
   


   public EliFile(File file)
   {
      tiffFile = file;
      image = JAIImageReader.readImage(file.getAbsolutePath());
      width = image.getWidth();
      height = image.getHeight();       
      minX = image.getMinX();
      minY = image.getMinY(); 

      //System.out.println("width:" + width);
      //System.out.println("height:" + height);
      //System.out.println("minX:" + minX);
      //System.out.println("minY:" + minY);

      imageMask = new int[width][height];
      spotCenters = new int[width][height];
      spotCentersNoNoise = new int[width][height];
      spotCentersFinal = new int[width][height];
      theCenters = new int[width][height];
      spotRadii = new int[width][height];
      spotProb = new double[width][height];
      clusterNumber = new int[width][height];
      pixelsFg = new int[width][height];
      globPixelCount = new TreeMap();
      globPixelCountNoNoise = new TreeMap();
      globPixelCountFinal = new TreeMap();
      globData = new TreeMap();
      globDataFinal = new TreeMap();
      meanIntensityPerGlob = new HashMap();

      getPixels();
      createImageMask();
      countSpots();
   }

   private void getPixels()
   {
      //Populate the 2D Array, thePixels
      //with the avg. value of the R, G, and B values
      //thePixels hold the intensity values for each pixel

      int numBands = image.getNumBands();
      //System.out.println("numberBands:" + numBands);
      byte[][] theimage = new byte[numBands][width*height];
      Rectangle imagedim = new Rectangle(minX,minY,width,height);
      PixelAccessor pa = new PixelAccessor(image);

      //System.out.println("theimage default:" + theimage[0][0]);
      //System.out.println("theimage default:" + theimage[1][0]);
      //System.out.println("theimage default:" + theimage[2][0]);
   
      //System.out.println("therect. x:" + imagedim.getX());
      //System.out.println("therect. y:" + imagedim.getY());
      //System.out.println("therect. height:" + imagedim.getHeight());
      //System.out.println("therect. width:" + imagedim.getWidth());
      //System.out.println("type:" + image.getData().getDataBuffer().getDataType());

      ColorModel imageCM = image.getColorModel();           
      //System.out.println("getRGB/pixel0,0" + imageCM.getAlpha(0));

      try
      {

      //UnpackedImageData theUID = pa.getPixels(image.getData(),
      //      imagedim,image.getData().getDataBuffer().getDataType(),false);

      UnpackedImageData theUID = pa.getComponentsRGB(image.getData(),imagedim);

      if(theUID.getIntData()!= null)
      {
         System.out.println("int data");
      }
      if(theUID.getByteData()!= null)
      {
         System.out.println("byte data");
      }
      if(theUID.getShortData()!= null)
      {
         System.out.println("short data");
      } 
      if(theUID.getFloatData()!= null)
      {
         System.out.println("float data");
      }
      if(theUID.getDoubleData()!= null)
      {
         System.out.println("double data");
      } 

         theimage = theUID.getByteData();
      }
      catch (Exception e) 
      {
         System.err.println("interrupted waiting for pixels");
         return;
      }   

         //correct the pixel's data type
         //as put the pixels into a 2d array
   
         thePixels = new int[numBands+1][width][height];
         

         int w, h;
         int correction8bit = 256;         
         
         //System.out.println("setting thePixels values");

         for (int i = 0; i < numBands; i++)
         {
            for (int j = 0; j < width*height; j++)
            {
                  w = (j % width);
                  h = (j/width);

                  if (i == 0 && j==0)
                  {
                     //System.out.println("width:" + w);
                     //System.out.println("height:" + h);
                     //System.out.println("theimage00:" + theimage[i][j]);
                     int testsum = theimage[i][j] + 256;
                     //System.out.println("theimage00plus:" + testsum);
                  }

                  if (theimage[i][j] < 0)
                  {
                      if(i==0&&j==0)
                      {
			             //System.out.println("first pixel:" + thePixels[0][0][0]);
                      }
                      thePixels[i][w][h] = theimage[i][j] + correction8bit;
			          if(i==0&&j==0)
                      {
                         //System.out.println("firstpixelafter:" + thePixels[i][w][h]);
                      }
                  }
                  else
                  {
                     thePixels[i][w][h] = theimage[i][j];
                  }
            }
         }
       
         //System.out.println("summing the 3 bands");
         // Sum the r,g,b bands, store in the 4th level of thePixels array

         int divisor3 = 3;

         for (int i = 0; i < width; i++)
         {
            for (int j = 0; j < height; j++)
            {
               thePixels[3][i][j] = (thePixels[0][i][j]+thePixels[1][i][j]+thePixels[2][i][j])/ divisor3;
            }
         }

         //System.out.println("check h:" + (113275/width));
         //System.out.println("check w:" + (113275 % width));

         //System.out.println("theimage:first pixel");
         //System.out.println(theimage[0][0]);
         //System.out.println(theimage[1][0]);
         //System.out.println(theimage[2][0]);

         //System.out.println("thePixels:first pixel");
         //System.out.println(thePixels[0][0][0]);
         //System.out.println(thePixels[1][0][0]);
         //System.out.println(thePixels[2][0][0]);
         //System.out.println(thePixels[3][0][0]);

         //System.out.println("theimage: a middle pixel");
         //System.out.println(theimage[0][113275]);
         //System.out.println(theimage[1][113275]);
         //System.out.println(theimage[2][113275]);

         //System.out.println("thePixels: a middle pixel");
         //System.out.println(thePixels[0][240][235]);
         //System.out.println(thePixels[1][240][235]);
         //System.out.println(thePixels[2][240][235]);
         //System.out.println(thePixels[3][240][235]);

         //initialize pixelsFg
         for (int i=0; i<height; i++)
         {
            for (int j=0; j<width; j++)
            {
               if(thePixels[3][j][i]==255)
               {
                  pixelsFg[j][i]=-1;
               }
            }
         }

   }  //end getPixels()


   private void createImageMask()
   {

      // creates a 2D Array, imageMask
      // cell in imageMask is set to 1 for pixels making up the pink well image
      // and set to 0 for the pixels in the 4 white corners around the well
      // only pixels w/i a well are considered for analysis
      // the white corners are ignored

      for(int i=0; i<height; i++)
      {
         for(int j=0; j<width; j++)
         {
            if(thePixels[3][j][i] != 255)
            {
               imageMask[j][i] = 1;
            }            
         }
      }               
   }

   private void countSpots()
   {
      int iteration=1;
      imageParameters = new ImageParameters(iteration,width,height,pixelsFg,thePixels,spotCenters);
      cutoff = imageParameters.getMeanNotSpot() - 3*imageParameters.getStandardDeviationNotSpot();
      cutoff = 173;
      System.out.println("While loop, cutoff: " + cutoff);


      // NOTICE! Self-assigning cutoff here to a higher threshold
      // to test on image p2_E2 to see how the spot counting changes.
      // After this test, this line should be removed.

      //cutoff = 151;

      // End of Above comments.

      assignspotCenters2(cutoff);

      if(DISPLAY)
      {
         //pink drawing
         //drawResult();
      }

      findContiguousBlobs();
      countPixelsPerGlob();
      removeNoise();
      getGlobData();
      //printGlobData2();
      computeMeanIntensityPerGlob();

      if(DISPLAY)
      {
         //black and white drawing - now only of spots - noise
         //drawResult2();
      }

      if(DISPLAY)
      {
         //black and white drawing - now only of spots - noise
         drawResult2b();
      }

      //spotCount = globData.size();
      spotCount = globPixelCountNoNoise.size();

      System.out.println("EliFile spotCount No Noise: " + spotCount);

      //computes mean and variance of number of spots for the image
      //uses the multinomial distribution
      countGlobSize();

      //Previously, had been counting < 10 pixels and < threshold intensity
      //Now, only want to count those if intensity < lower threshold intensity
      //i.e. small globs have to be darker to count
      dropSmallLightGlobs();

      spotCount = globPixelCountFinal.size();
      System.out.println("EliFile spotCount: " + spotCount);

      //
      // THE END - for now
      //


      //m1: want to identify just the spot centers,
      //    drops x% of the edges
      //thresholdGlobs();
      //drawResult3();

      //m2: likelihood method
      //modelGlobsTwoMinCenters();
      //drawResultM2();


      //System.out.println("calling modelGlobsAllCenters");

      //System.out.println("fg mean, std: " + imageParameters.getMeanSpot() + " "
      //   + imageParameters.getStandardDeviationSpot());
      //System.out.println("bg mean, std: " + imageParameters.getMeanNotSpot() + " "
      //   + imageParameters.getStandardDeviationNotSpot());


      //m3: likelihood method
      //modelGlobsAllCenters();
      //drawResultM3();
      
      //printGlobData();

      //findSpots();
  
   }

   void dropSmallLightGlobs()
   {
      //when TreeMap Size = 0, line below won't work...
      //It can be 0 because some images have no globs

      System.out.println("dropSmall start");

      if(globPixelCountNoNoise.size() > 0)
      {
         Double meanIntensity;
         Integer globNumber;
         Integer lastGlobNumber = (Integer) globPixelCountNoNoise.lastKey();
         Integer pixelCount;
         int globCount = 0;
         ArrayList globList;
         EliPixel globPixel;
         int globPixelx;
         int globPixely;

         System.out.println("dropSmall lastGlobNumber: " + lastGlobNumber);

         //
         //Process each glob identified so far, based on: globPixelCountNoNoise
         //
         for(int i=1; i<=lastGlobNumber.intValue();i++)
         {
            globNumber = new Integer(i);
            pixelCount = (Integer) globPixelCountNoNoise.get(globNumber);
            meanIntensity = (Double) meanIntensityPerGlob.get(globNumber);

            if(pixelCount.intValue() < 10 && meanIntensity.doubleValue() > smallSpotRatio*cutoff)
            {
                //small, light globs
                //want to drop these
            }
            else
            {
                //small, dark globs
                //and all other larger globs
                //want to keep these

                globCount = globCount + 1;

                //if(globCount==1)
                //{
                //    System.out.println("dropSmall pixelCount: " + pixelCount.intValue());
                //}

                globPixelCountFinal.put(new Integer(globCount),pixelCount);

                globList = (ArrayList) globData.get(globNumber);

                globDataFinal.put(new Integer(globCount),globList);

                for(Iterator j = globList.iterator(); j.hasNext();)
                {
                   globPixel = (EliPixel)j.next();

                   globPixelx = globPixel.getX();
                   globPixely = globPixel.getY();

                   spotCentersFinal[globPixelx][globPixely] = globCount;

      		    }

            } //else

         } // end for - each glob number

         System.out.println("dropSmall globCount: " + globCount);

      } // if - globs in this image

   } // end - class


   void computeMeanIntensityPerGlob()
   {
	    ArrayList globList;
   	    EliPixel eliPixel;
   	    double meanIntensity = 0;
        int globNumber = 0;
        int numberOfPixels = 0;

  	    for(Iterator i=globData.keySet().iterator(); i.hasNext();)
   	    {
            globList = (ArrayList)globData.get(i.next());
            globNumber = globNumber + 1;

            for(Iterator j = globList.iterator(); j.hasNext();)
            {
                eliPixel = (EliPixel)j.next();
			    meanIntensity = meanIntensity + eliPixel.getIntensity();
                numberOfPixels = numberOfPixels + 1;
      		}

            meanIntensity = meanIntensity/numberOfPixels;

            meanIntensityPerGlob.put(new Integer(globNumber),new Double(meanIntensity));

            meanIntensity = 0;
            numberOfPixels = 0;

   	    }

    }


   private void modelGlobsAllCenters()
   {      
      TreeSet theGlobTreeSet;

      EliPixel Center1 = new EliPixel(0,0,0);
      EliPixel Center2 = new EliPixel(0,0,0);

      int centerSeparation = 0;

      double likelihoodOneSpot = 0.0;
      double likelihoodTwoSpots = 0.0;
      double likelihoodZeroSpot = 0.0;

      int aUsed;
      double bUsed;

      int theA;
      double theB;

      int distanceC1;
      int distanceC2;

      int distanceOneSpot;
      int distanceTwoSpots;

      int numberOfGlobs;
      int globCount = 0;

      double meanOneSpot;
      double meanTwoSpots;   
      double meanZeroSpot;

      double maxLikelihood = 0.0;
  
      int maxCenter1X=0;
      int maxCenter1Y=0;
      int maxCenter2X=0;
      int maxCenter2Y=0;

      int numberOfCenters=0;
      int numberCenterTwos=0;

      numberOfGlobs = globData.size();

      numberOfSpots = new int[numberOfGlobs];
      likelihood = new double[numberOfGlobs];
      aFinal = new int[numberOfGlobs];
      bFinal = new double[numberOfGlobs];


      //  /   /   /   /   /
      // Process glob     /   
      //  /   /   /   /   /

      //for (int ii=0; ii<1; ii++)
      //{
      //   Iterator i=globData.keySet().iterator();

      for(Iterator i=globData.keySet().iterator(); i.hasNext();)
      {

         theGlobTreeSet = (TreeSet)globData.get(i.next());               
         globCount = globCount + 1;         

         maxLikelihood = 0.0;
         numberOfCenters = 1;

         numberCenterTwos=0;
         int mCount = 0;

         theA = -1;
         theB = -1;
                           
         //System.out.println("globcount: " + globCount);
         //System.out.println("Size of theGlobTreeSet: " + theGlobTreeSet.size());

         for(int j=0; j<theGlobTreeSet.size(); j++)
         {

            //System.out.println("j: " + j);

            
            Iterator k = theGlobTreeSet.iterator();


            for (int l = 0; l <= j; l++)
            {
               Center1 = (EliPixel)k.next();
            }
   
            //if(j==0 || j==1)
            //{
            //   System.out.println("Center1: " + Center1.getX() + " " + Center1.getY() + " " + 
            //      Center1.getIntensity());
            //}
            
            while(k.hasNext())
            {       
               Center2 = (EliPixel)k.next();

               //if(j==0 || j==1)
               //{
               //   System.out.println("Center2: " + Center2.getX() + " " + Center2.getY() + " " + 
               //      Center2.getIntensity());   
               //}

               if(Math.abs(Center2.getX()-Center1.getX()) > centerSeparation &&
                     Math.abs(Center2.getY()-Center1.getY()) > centerSeparation)                         
               {

                   numberCenterTwos = numberCenterTwos + 1;           

       
                   //System.out.println("J inside big-if statement, j= " + j);

                   //Compute likelihood one spot, likelihood 2 spots, given the two centers


                // Add for loop - to compute likelihood for each a,b combination
                      
                for(int n = 0; n < A_OPTIONS; n++)
                {
                   for(int o=0; o < B_OPTIONS; o++)
                   {

                      aUsed = a[n];
                      bUsed = b[o];


                      //   /   /    /     /     /    /    /
                      //Process each pixel of the glob   //
                      //   /   /    /    /     /    /    / 

                      likelihoodOneSpot = 0.0;
                      likelihoodTwoSpots = 0.0;
                      likelihoodZeroSpot = 0.0;


                      for(Iterator m = theGlobTreeSet.iterator(); m.hasNext();)
                      {

                         mCount = mCount + 1;                       
                      
                         //System.out.println("J inside Iterator m, j= " + j);

                         EliPixel thisPixel = (EliPixel)m.next();

                         if(j==0 && mCount == 1)
                         {
			        //System.out.println("thisPixel: " + thisPixel.getX() + " " + 
                            //   thisPixel.getY() + " " + thisPixel.getIntensity());
                         }
 
                         // compute distance from center
                         distanceC1 = Math.max(Math.abs(thisPixel.getX()-Center1.getX()),
                                  Math.abs(thisPixel.getY()-Center1.getY()));

                         distanceC2 = Math.max(Math.abs(thisPixel.getX()-Center2.getX()),
                                  Math.abs(thisPixel.getY()-Center2.getY()));

                         distanceOneSpot = distanceC1;
                         distanceTwoSpots = Math.min(distanceC1,distanceC2); 

                         if(j==0 && mCount==1)
                         {
                            //System.out.println("distanceC1: " + distanceC1); 
                            //System.out.println("distanceC2: " + distanceC2);
                            //System.out.println("distanceOneSpot: " + distanceOneSpot);
                            //System.out.println("distanceTwoSpots: " + distanceTwoSpots);
                         }
                      

                         meanOneSpot = findMeanIntensity(distanceOneSpot,aUsed,bUsed);
                         meanTwoSpots = findMeanIntensity(distanceTwoSpots,aUsed,bUsed);            
                         meanZeroSpot = 225;

                         if(j==0 && mCount==1)
                         {
                            //System.out.println("meanFG: " + imageParameters.getMeanSpot());
                            //System.out.println("stdFG: " + imageParameters.getStandardDeviationSpot());
                            //System.out.println("meanBG: " + imageParameters.getMeanNotSpot());
                            //System.out.println("stdBG: " + imageParameters.getStandardDeviationNotSpot());
                            //System.out.println("meanOneSpot: " + meanOneSpot);
                            //System.out.println("meanTwoSpots: " + meanTwoSpots);
                         }

                         double differenceFromMeanOneSpot = thisPixel.getIntensity() - meanOneSpot;
                         double differenceFromMeanTwoSpots = thisPixel.getIntensity() - meanTwoSpots;
                         double differenceFromMeanZeroSpot = thisPixel.getIntensity() - meanZeroSpot;

                         likelihoodOneSpot = likelihoodOneSpot - Math.pow(differenceFromMeanOneSpot,2);           
                         likelihoodTwoSpots = likelihoodTwoSpots - Math.pow(differenceFromMeanTwoSpots,2);
                         likelihoodZeroSpot = likelihoodZeroSpot - Math.pow(differenceFromMeanZeroSpot,2);

                         if(j==0 && mCount<=17)
                         {
                            //System.out.println("diffOneSpot: " + differenceFromMeanOneSpot);
                            //System.out.println("diffTwoSpot: " + differenceFromMeanTwoSpots);
                            //System.out.println("likelihoodOneSpot: " + likelihoodOneSpot);
                            //System.out.println("likelihoodTwoSpots: " + likelihoodTwoSpots);
                         }
 
                         //add final components to likelihood values?
                         //not necessary here - cuz won't change if L1 bigger/smaller than L2
                         //since all pixels belong to foreground; i.e. there's no fg,bg separation


                     } //end 'm' loop thru all pixels in the glob
                   

                     if(j==0 && numberCenterTwos == 1) //=first choice for center1, first choice for C2
                     {
                        //System.out.println("likeOneSpot: " + likelihoodOneSpot);
                        //System.out.println("likeTwoSpot: " + likelihoodTwoSpots);
                        //System.out.println("maxLikelihood: " + maxLikelihood);                      
                     }


                     if(maxLikelihood == 0)
                     {
                        maxLikelihood = likelihoodOneSpot;
                     }


                     if(likelihoodZeroSpot > likelihoodOneSpot)
                     {

                          if(likelihoodZeroSpot > maxLikelihood)
                          {
                             maxLikelihood = likelihoodZeroSpot;
                             numberOfCenters = 0;
                             theA = aUsed;
                             theB = bUsed;
                          }

                     }
                     else
                     {
 
                          if(likelihoodOneSpot >= likelihoodTwoSpots)
                          {
                             if(likelihoodOneSpot >= maxLikelihood)
                             {
                                maxLikelihood = likelihoodOneSpot;
                                maxCenter1X = Center1.getX();
                                maxCenter1Y = Center1.getY(); 
                                numberOfCenters = 1;
                                theA = aUsed;
                                theB = bUsed; 
                             }
                          }                       
                          else
                          {
                             if(likelihoodTwoSpots > maxLikelihood)
                             {
                                maxLikelihood = likelihoodTwoSpots;
                                maxCenter1X = Center1.getX();
                                maxCenter1Y = Center1.getY(); 
                                maxCenter2X = Center2.getX();
                                maxCenter2Y = Center2.getY();
                                numberOfCenters = 2;
                                theA = aUsed;
                                theB = bUsed;
                             }

                          }

                      }

                    } //end 'b' 'for' loop thru all B-options

                 } //end 'a' 'for' loop thru all A-options

                      
               }//end 'if' pixel separation sufficiently big
 
             }//end 'while' for getting Center2
  
           }//end 'for' 'j' for getting Center1

           numberOfSpots[globCount-1] = numberOfCenters;
           likelihood[globCount-1] = maxLikelihood;
           aFinal[globCount-1] = theA;
           bFinal[globCount-1] = theB;


           //if(globCount == 1 || globCount == 2)
           //{
              //System.out.println("maxLikelihood after assign: " + maxLikelihood);
              //System.out.println("numberOfCenters, theA, theB: " + numberOfCenters + " " + theA + " " + theB);
           //}            
          
           if(numberOfCenters == 1)
           {
              theCenters[maxCenter1X][maxCenter1Y] = 1;        
              //System.out.println("Center1: " + maxCenter1X + ", " + maxCenter1Y);
           }
           else
           {
              theCenters[maxCenter1X][maxCenter1Y] = 1;
              theCenters[maxCenter2X][maxCenter2Y] = 2;  
              //System.out.println("Center1: " + maxCenter1X + ", " + maxCenter1Y);
              //System.out.println("Center2: " + maxCenter2X + ", " + maxCenter2Y);
           }                       


        }//end 'for' each glob in the image           
                              
   }//end modelGlobsAllCenters


   private void modelGlobsTwoMinCenters()
   {
      TreeSet theGlobTreeSet;
      EliPixel Center1;
      EliPixel Center2;
      boolean foundCenter2 = false;
      int centerSeparation = 2;

      double likelihoodOneSpot = 0.0;
      double likelihoodTwoSpots = 0.0;

      int distanceC1;
      int distanceC2;
      int distanceOneSpot;
      int distanceTwoSpots;

      int numberOfGlobs;
      int globCount = 0;

      double meanOneSpot;
      double meanTwoSpots;   

      numberOfGlobs = globData.size();

      numberOfSpots = new int[numberOfGlobs];
      likelihood = new double[numberOfGlobs];

      // Process each glob      
      for(Iterator i=globData.keySet().iterator(); i.hasNext();)
      {
         theGlobTreeSet = (TreeSet)globData.get(i.next());               
         
         Iterator j = theGlobTreeSet.iterator();
         Center1 = (EliPixel)j.next();

         // need to check has.Next()?
         do 
         {
            Center2 = (EliPixel)j.next();

            if(Math.abs(Center2.getX()-Center1.getX()) > centerSeparation &&
               Math.abs(Center2.getY()-Center2.getY()) > centerSeparation)                         
            {
               foundCenter2 = true;               
            }                    

         } while(!foundCenter2);               

         //Compute likelihood one spot, likelihood 2 spots, given the two centers
 
         //Process each pixel of the glob

         for(Iterator k = theGlobTreeSet.iterator(); k.hasNext();)
         {

            EliPixel thisPixel = (EliPixel)k.next();

            // compute distance from center
            distanceC1 = Math.max(Math.abs(thisPixel.getX()-Center1.getX()),
                                  Math.abs(thisPixel.getY()-Center1.getY()));

            distanceC2 = Math.max(Math.abs(thisPixel.getX()-Center2.getX()),
                                  Math.abs(thisPixel.getY()-Center2.getY()));

            distanceOneSpot = distanceC1;
            distanceTwoSpots = Math.min(distanceC1,distanceC2); 

            int a=0;
            int b=0;

            meanOneSpot = findMeanIntensity(distanceOneSpot,a,b);
            meanTwoSpots = findMeanIntensity(distanceTwoSpots,a,b);            

            double differenceFromMeanOneSpot = thisPixel.getIntensity() - meanOneSpot;
            double differenceFromMeanTwoSpots = thisPixel.getIntensity() - meanTwoSpots;

            likelihoodOneSpot = likelihoodOneSpot + Math.pow(differenceFromMeanOneSpot,2);           
            likelihoodTwoSpots = likelihoodTwoSpots + Math.pow(differenceFromMeanTwoSpots,2);

         }

         //add final components to likelihood values?
         //not necessary here - cuz won't change if L1 bigger/smaller than L2
         //since all pixels belong to foreground; i.e. there's no fg,bg separation

         if(likelihoodOneSpot > likelihoodTwoSpots)
         {
            numberOfSpots[globCount] = 1;
            likelihood[globCount] = likelihoodOneSpot;
            theCenters[Center1.getX()][Center1.getY()] = 1;            
         }
         else
         {
            numberOfSpots[globCount] = 2;
            likelihood[globCount] = likelihoodTwoSpots;
            theCenters[Center1.getX()][Center1.getY()] = 1;
            theCenters[Center2.getX()][Center2.getY()] = 2;  
         }            
                           
      } //end glob processing

      //compute total number of spots

   } //end m2


   private double findMeanIntensity(int distance, int a, double b)
   {
      double mean = 0;
      int maxRadiusSpot = 8;

      if(distance <= a)
      {  
         mean = imageParameters.getMeanSpot();
      }
      else if (distance > a && distance <= maxRadiusSpot)
      {
         // b = rise/run = rise/(d-a), so intensity to add above
         // the fg intensity is: rise = (d-a)*b
         mean = imageParameters.getMeanSpot() + (distance-a)*b;
      }
      else if (distance > maxRadiusSpot)
      {
         mean = imageParameters.getMeanNotSpot();
      } 
   
      return mean;
   }

   private void thresholdGlobs()
   {

      int numberOfCenterPixels;
      int xPercent = 30;
      int denominator = 100;
      TreeSet theGlobTreeSet;
      EliPixel eliPixel;
      int x;
      int y;

      // Process each of the globs
      for(Iterator i = globData.keySet().iterator(); i.hasNext();)
      {
         theGlobTreeSet = (TreeSet)globData.get(i.next());                                    
       
         numberOfCenterPixels = (theGlobTreeSet.size()*xPercent)/denominator;
         if(numberOfCenterPixels==0)
         {
            numberOfCenterPixels = 1;
         }
      
         Iterator iteratorTreeSet = theGlobTreeSet.iterator();         

         for(int j=1; j <= numberOfCenterPixels; j++)
         {
            eliPixel = (EliPixel)iteratorTreeSet.next();           
            x = eliPixel.getX();
            y = eliPixel.getY();

            theCenters[x][y] = 1;           
    
         }

      }   
      
   }

   private void getGlobData()
   {

     ArrayList globList;
     int testCount = 0;

     for(int i=0; i<height; i++)
     {
        for(int j=0; j<width; j++)
        {

           if(spotCentersNoNoise[j][i] > 0)
           {
              //add data point to globData TreeMap

              Integer globNumber = new Integer(spotCentersNoNoise[j][i]);

              if(globData.containsKey(globNumber))
              {
                 //retrieve existing tree set so can add to it
                 globList = (ArrayList) globData.get(globNumber);
                                                                    
              }
              else
              {
                 //create a tree set
                 globList = new ArrayList();

              }


              if(globNumber.intValue()==5)
              {
                 testCount = testCount + 1;
              }
              globList.add(new EliPixel(j,i,thePixels[3][j][i]));
              //globData.remove(globNumber);
              globData.put(globNumber,globList);
              
           }        
           
        }
     }

       System.out.println("getGlobData, testCount: " + testCount);
   }


   private void countPixelsPerGlob()
   {
      Integer pixelCount;
      
      for(int i=0; i<height; i++)
      {
         for(int j=0; j<width; j++)
         {
            pixelCount = new Integer(0);

            if(spotCenters[j][i] > 0)
            {
               Integer globNumber = new Integer(spotCenters[j][i]);

               if(globPixelCount.containsKey(globNumber))
               {
                  pixelCount = (Integer) globPixelCount.get(globNumber); 
                  globPixelCount.put(globNumber, new Integer(pixelCount.intValue() + 1));                   
               }
               else //first pixel for this glob
               {
                  globPixelCount.put(globNumber, new Integer(1));
               }                              
               
            }                        

         }
      }          
      
      //System.out.println("Number of globs, treeMap globPixelCount: " + globPixelCount.size());

      //System.out.println("Number of Pixels in glob 0:" + globPixelCount.get(new Integer(0)));
      //System.out.println("Number of Pixels in glob 1:" + globPixelCount.get(new Integer(1)));
      //System.out.println("Number of Pixels in glob 2:" + globPixelCount.get(new Integer(2)));
      //System.out.println("Number of Pixels in glob 3:" + globPixelCount.get(new Integer(3)));
      //System.out.println("Number of Pixels in glob 4:" + globPixelCount.get(new Integer(4)));
   }

   private void countGlobSize()
   {
      //when TreeMap Size = 0, line below won't work...
      if(globPixelCountNoNoise.size() > 0)
      {
         Double meanIntensity;
         Integer globNumber;
         Integer lastGlobNumber = (Integer) globPixelCountNoNoise.lastKey();

         int[] cat = {0,0,0,0,0,0,0,0};
         Integer pixelCount;

         for(int i=1; i<=lastGlobNumber.intValue();i++)
         {
            globNumber = new Integer(i);
            pixelCount = (Integer) globPixelCountNoNoise.get(globNumber);
            meanIntensity = (Double) meanIntensityPerGlob.get(globNumber);

            if(pixelCount.intValue() < 10 && meanIntensity.doubleValue() > smallSpotRatio*cutoff)
            {
                //small, light globs
                //for now, don't count them
                //cat[0] = cat[0] + 1;
            }
            else if(pixelCount.intValue() < 10 && meanIntensity.doubleValue() <= smallSpotRatio*cutoff)
            {
                //small, dark globs
                cat[1] = cat[1] + 1;
            }
            else if(pixelCount.intValue() <= 27)
            {
                cat[2] = cat[2] + 1;
            }
            else if(pixelCount.intValue() <= 43)
            {
                cat[3] = cat[3] + 1;
            }
            else if(pixelCount.intValue() <= 90)
            {
                cat[4] = cat[4] + 1;
            }
            else if(pixelCount.intValue() <= 150)
            {
                cat[5] = cat[5] + 1;
            }
            else if(pixelCount.intValue() <= 200)
            {
                cat[6] = cat[6] + 1;
            }
            else
            {
                cat[7] = cat[7] + 1;
            }

         }

        int numGlobSizeCats = 8, numSpotCats = 4;

        double probability1, probability2;
        int value1, value2;

        for(int i=0; i<numGlobSizeCats; i++)
        {
            for(int j=0;j<numSpotCats;j++)
            {
                probability1 = probabilityTable[i][j];
                value1 = j;

                if(j!=3)
                {
                    probability2 = probabilityTable[i][j+1];
                    value2 = j+1;
                }
                else
                {
                    probability2 = probabilityTable[i][j-2];
                    value2 = j-2;
                }

                meanNumberSpots = meanNumberSpots + cat[i]*j*probability1;

                varianceNumberSpots = varianceNumberSpots + cat[i]*Math.pow(j,2)*probability1*(1-probability1)
                   - cat[i]*value1*value2*probability1*probability2;

            }
        }

        System.out.println("countGlobSize:final:meanNumberSpots:" + meanNumberSpots);
        System.out.println("countGlobSize:final:varianceNumberSpots:" + varianceNumberSpots);

      }
      else
      {
        meanNumberSpots = 0;
        varianceNumberSpots = 0;
      }
   }

   private void removeNoise()
   {
      Integer pixelCount;
      HashMap renumberGlobs = new HashMap();
      int globCount = 0;

      for(int i=0; i<height; i++)
      {
          for(int j=0; j<width; j++)
          {

            if(spotCenters[j][i] > 0)
            {
               Integer globNumber = new Integer(spotCenters[j][i]);

               if(globPixelCount.containsKey(globNumber))
               {
                  pixelCount = (Integer) globPixelCount.get(globNumber); 
                  if(pixelCount.intValue() >= spotTooSmall)
                  {
                     spotCentersNoNoise[j][i] = globNumber.intValue();

                     if(!renumberGlobs.containsKey(globNumber))
                     {
                        globCount += 1;
                        renumberGlobs.put(globNumber, new Integer(globCount));

                        globPixelCountNoNoise.put(new Integer(globCount),pixelCount);
                     }

                  }
                  
               }                                   

            }            

         }
      }

      for(int i=0; i<height; i++)
      {
         for(int j=0; j<width; j++)
         {
            if(spotCentersNoNoise[j][i] > 0)
            {  
               Integer globNumber = new Integer(spotCentersNoNoise[j][i]);
               Integer newGlobNumber = (Integer)renumberGlobs.get(globNumber);

               spotCentersNoNoise[j][i] = newGlobNumber.intValue();

            }           

         }
      }     

   }
  
   private void assignspotCenters()
   {
      //check each pixel: left to right, top to bottom

      //testing: check first pixel only: i=0,j=0

      //for(int i=0; i<1;i++)
      for(int i=0; i<height;i++)
      {
         //System.out.println("row: " + i); 
         //for(int j=0; j<1; j++)
         for(int j=0; j<width;j++)
         {

            //if(i < 1 && j <10)
            //{ 
            //   System.out.println("getting pixel intensities for the nghd: " + i + " " + j);
            //}

            // get pixel intensities for the neighborhood

            //System.out.println("PN: " + radius + " " + j + " " + i + " " + width + " " + height);

            PixelNeighborhood pixnghd = new PixelNeighborhood(radius, j, i, width, height, thePixels);

	    //System.out.println("pixnghd:pixelCount: " + pixnghd.getPixelCount());


            /*
            if(i < 1 && j <10)
            { 
               System.out.println("computing the probabilities: " + i + " " + j);
            } 
            */
               
            //System.out.println("computing the probabilities.");   

            // compute probabilities
            LikelihoodRatios likelihoods = new LikelihoodRatios(imageParameters,pixnghd);

            /*if(i < 1 && j <10)
            { 
               System.out.println("assigning the spot centers: " + i + " " + j);
            }
            */

            //System.out.println("determining if a spot center...");
            //System.out.println("likelihoodSpot: " + likelihoods.probabilitySpot);
            //System.out.println("likelihoodNotSpot: " + likelihoods.probabilityNotSpot);


            // assign spot center (yes=1/no=0) to pixel based on likelihoods
            if (likelihoods.probabilityNotSpot >= likelihoods.probabilitySpot)
            {
               //initialized to zero, but during iterations pixels get assigned, so 
               //may not be zero (automatically) anymore
               spotCenters[j][i] = 0;     
               spotRadii[j][i] = 0;
            }  
            else
            //pixel is a spot center
            {
               //if(thePixels[3][j][i] != 255)
	       if(thePixels[3][j][i] < 205)	
               {
             
                  //System.out.println("found a spot");
               
                  //mark pixel as a spot center, store its radius
                  spotCenters[j][i] = 1;
                  spotRadii[j][i] = radius;
                  spotProb[j][i] = likelihoods.probabilitySpot;

                  //System.out.println(spotCenters[0][0]);
                  //System.out.println(spotRadii[0][0]);
                  //System.out.println("number of spot pixels: " + pixnghd.pixelIntensity.length);

                  //identify the spot pixels
                  for (int k = 0; k < pixnghd.pixelIntensity.length; k++)
                  {
	             pixelsFg[pixnghd.pixelIntensityX[k]][pixnghd.pixelIntensityY[k]] += 1;  
                  }                                         
               }
            }          
            
                                  
         }
      }
   }

   private void assignspotCenters2(double cutoff)
   {

      for(int i=0; i<height;i++)
      {
         for(int j=0; j<width;j++)
         {

            if (thePixels[3][j][i] >= cutoff)
            {
               //initialized to zero, but during iterations pixels get assigned, so 
               //may not be zero (automatically) anymore
               spotCenters[j][i] = 0;
               spotRadii[j][i] = 0;     
            }  
            else
            //pixel is a spot center
            {
                  //mark pixel as a spot center, store its radius
                  spotCenters[j][i] = 1;
                  spotRadii[j][i] = 0;
            }
                                  
         }
      }

   }


   private void assignTheCenters(double mean, double standardDeviation)
   {

      double threshold = mean - standardDeviation;
   
      //System.out.println("the spot center threshold is: " + threshold);

      for (int i=0; i<height; i++)
      {
         for (int j=0; j<width; j++)
         {
            if (thePixels[3][j][i] > threshold)
            {
               //do nothing
            }
            else
            {
               theCenters[j][i] = 1;
            }         

         }
      }

   }


   private void computeSpotCount()
   { 
      
      spotCount = 0;

      for(int i=0; i<height; i++)
      {
         for(int j=0; j<width; j++)
         {
            if(spotCenters[j][i] == 1)
            { 
                //System.out.println("found a spot center");
		spotCount += 1;	                                    
            }
         }
      }

   }

   private void printIterationStats(IterationStats iterationStats)
   {
      //System.out.println("Spot Counting Statistics:");
      //System.out.println("Iteration, Spot Count, Mean Spot, Std. Spot, Mean NotSpot, Std. NotSpot");

      int [] iteration = iterationStats.getIteration();
      int [] spotCount = iterationStats.getSpotCount();
      double[] meanSpot = iterationStats.getMeanSpot();
      double[] stdSpot = iterationStats.getStandardDeviationSpot();
      double[] meanNotSpot = iterationStats.getMeanNotSpot();
      double[] stdNotSpot = iterationStats.getStandardDeviationNotSpot();

      int length = iteration.length;

      for(int i=0; i<length; i++)
      {

         //System.out.println(iteration[i] + ", " + spotCount[i] + ", " + meanSpot[i] + ", " + stdSpot[i] + ", " 			   + meanNotSpot[i] + ", " + stdNotSpot[i]);

      }

      //System.out.println("End of Stats.");

   }


   private void drawResult()
   {
      JFrame frame = new JFrame();
      frame.getContentPane().add(new SpotRenderer(spotCenters,spotRadii));
      frame.pack();

      //Show the frame
      frame.setSize(new Dimension(500,500));
      frame.setVisible(true);

      //Exit code
      frame.addWindowListener(new WindowAdapter()
      {
         public void windowClosing(WindowEvent evt)
         {
            System.exit(0);
         }
      });
              
   }

    private void drawResult2()
    {
       JFrame frame = new JFrame();
       int[][] zeroRadii = new int[width][height];
       frame.getContentPane().add(new SpotRenderer2(spotCentersNoNoise,zeroRadii,globPixelCountNoNoise,
               meanIntensityPerGlob,cutoff,smallSpotRatio));
       frame.pack();

       //Show the frame
       frame.setSize(new Dimension(500,500));
       frame.setVisible(true);

       //Exit code
       frame.addWindowListener(new WindowAdapter()
       {
          public void windowClosing(WindowEvent evt)
          {
             System.exit(0);
          }
       });

    }

    private void drawResult2b()
    {
       JFrame frame = new JFrame();
       int[][] zeroRadii = new int[width][height];
       frame.getContentPane().add(new SpotRenderer2b(spotCentersFinal,globPixelCountFinal));
       frame.pack();

       //Show the frame
       frame.setSize(new Dimension(500,500));
       frame.setVisible(true);

       //Exit code
       frame.addWindowListener(new WindowAdapter()
       {
          public void windowClosing(WindowEvent evt)
          {
             System.exit(0);
          }
       });

    }


   private void drawResult3()
   {

      //assign 1's to theCenters, based on threshold
      
      JFrame frame = new JFrame();
      frame.getContentPane().add(new SpotRenderer3(theCenters));
      frame.pack();

      //Show the frame
      frame.setSize(new Dimension(500,500));
      frame.setVisible(true);

      //Exit code
      frame.addWindowListener(new WindowAdapter()
      {
         public void windowClosing(WindowEvent evt)
         {
            System.exit(0);
         }
      });
              
   }

   private void drawResultM2()
   {

      //assign 1's to theCenters, based on threshold
      
      JFrame frame = new JFrame();
      frame.getContentPane().add(new SpotRendererM2(theCenters));
      frame.pack();

      //Show the frame
      frame.setSize(new Dimension(500,500));
      frame.setVisible(true);

      //Exit code
      frame.addWindowListener(new WindowAdapter()
      {
         public void windowClosing(WindowEvent evt)
         {
            System.exit(0);
         }
      });
              
   }


   private void drawResultM3()
   { 

      JFrame frame = new JFrame();
      frame.getContentPane().add(new SpotRendererM3(theCenters));
      frame.pack();

      //Show the frame
      frame.setSize(new Dimension(500,500));
      frame.setVisible(true);

      //Exit code
      frame.addWindowListener(new WindowAdapter()
      {
         public void windowClosing(WindowEvent evt)
         {
            System.exit(0);
         }
      });
              
   }


   private void printGlobData()
   {

      FileWriter out = null;

      try {

         String fileName = new String(tiffFile.getName() + ".glob.txt");
         File outputFile = new File(fileName);
         out = new FileWriter(outputFile);

         for(int i=0; i<height; i++)
         {
            for (int j=0; j<width; j++)
            {
               if(spotCentersNoNoise[j][i] > 0) 
               {
                  Integer x = new Integer(j);
                  Integer y = new Integer(i);
                  Integer intensityValue = new Integer(thePixels[3][j][i]);    
                  Integer globNumber = new Integer(spotCentersNoNoise[j][i]);           

                  //x
                  out.write(x.toString());
                  out.write(" ");

                  //y
                  out.write(y.toString());
                  out.write(" ");

                  //intensity
                  out.write(intensityValue.toString());
                  out.write(" ");

                  //globnumber
                  out.write(globNumber.toString());
                  out.write(" ");


                  //newline
                  out.write("\n");

               }   

            }
         } 
         //System.out.println("Closing FileWriter");
         out.close();

      } 
      catch (IOException e)
      {
         System.err.println("Caught IOException: " + e.getMessage());        
      }
      /*finally 
      {  
         if (out != null)
         {
            System.out.println("Closing FileWriter");
            out.close();
         }
         else
         {
            System.out.println("FileWriter not open");
         } 
      } */ 

   }

    private void printGlobData2()
   {

      FileWriter out = null;

      try {

         String fileName = new String(tiffFile.getName() + ".All.glob.txt");
         System.out.println("printing SpotCenters glob data" + fileName);
         File outputFile = new File(fileName);
         out = new FileWriter(outputFile);

         for(int i=0; i<height; i++)
         {
            for (int j=0; j<width; j++)
            {
               if(spotCenters[j][i] > 0)
               {
                  Integer x = new Integer(j);
                  Integer y = new Integer(i);
                  Integer intensityValue = new Integer(thePixels[3][j][i]);
                  Integer globNumber = new Integer(spotCenters[j][i]);

                  //x
                  out.write(x.toString());
                  out.write(" ");

                  //y
                  out.write(y.toString());
                  out.write(" ");

                  //intensity
                  out.write(intensityValue.toString());
                  out.write(" ");

                  //globnumber
                  out.write(globNumber.toString());
                  out.write(" ");


                  //newline
                  out.write("\n");

               }

            }
         }
         //System.out.println("Closing FileWriter");
         out.close();

      }
      catch (IOException e)
      {
         System.err.println("Caught IOException: " + e.getMessage());
      }
      /*finally
      {
         if (out != null)
         {
            System.out.println("Closing FileWriter");
            out.close();
         }
         else
         {
            System.out.println("FileWriter not open");
         }
      } */

   }




   private void drawImageAndValues()
   {

      int[] iMin = {288,288,288,288,288,288,288,288,288,288,288,288,
                    324,324,324,324,324,324,324,324,324,324,324,324};
      int[] iMax = {324,324,324,324,324,324,324,324,324,324,324,324,
                    360,360,360,360,360,360,360,360,360,360,360,360};
      int[] jMin = {0,40,80,120,160,200,240,280,320,360,400,440,
                    0,40,80,120,160,200,240,280,320,360,400,440,};
      int[] jMax = {40,80,120,160,200,240,280,320,360,400,440,480,
                    40,80,120,160,200,240,280,320,360,400,440,480};

      //for (int i=0; i<24; i++)
      for (int i=0; i<12; i++)
      {
         JFrame frame = new JFrame();
         frame.getContentPane().add(new ImageValuesRenderer(thePixels,spotProb,iMin[i],iMax[i],
            jMin[i],jMax[i]));
         frame.pack();
      
         //Show the frame
         frame.setSize(new Dimension(1205,1085));
         frame.setVisible(true);

         //Exit code
         frame.addWindowListener(new WindowAdapter()
         {
            public void windowClosing(WindowEvent evt)
            {
               System.exit(0);
            }
         });

      }
   }

   private void clusterPixels()
   {
    
      //for each involved pixel, assign it to a clump/spot
      for (int i=0; i<height; i++)
      {
         for (int j=0; j<width; j++)
         {
            if (spotCenters[j][i]==1)
            {                              
               //assign a cluster number
               clusterNumber[j][i] = assignClusterNumber(j,i);
           
            } //if this pixel is involved in a spot

         } //for j

      } //for i

   }

   public int assignClusterNumber(int j, int i)
   {

               int lastX = width - 1;
               int lastY = height - 1;

               //check 4 neighbors (maybe 8 neighbors) 

               //check left
               if (j != 0)
               {
                  if (spotCenters[j-1][i] == 1)
                  {
                     return clusterNumber[j-1][i];
                  }
               }
             
               //check top
               if (i != 0)
               {
                  if (spotCenters[j][i-1] == 1)
                  {
                     return clusterNumber[j][i-1];
                  }
               }

               //check left-top
               if (i != 0 && j != 0)
               {
                  if (spotCenters[j-1][i-1] == 1)
                  {
                     return clusterNumber[j-1][i-1];
                  }
               } 

               //check right-top
               if (i != 0 && j != lastX)
               {
                  if (spotCenters[j+1][i-1] == 1)
                  {
                     return clusterNumber[j+1][i-1];
                  }
               }

               //check right-top + 1 to the right
               if (i != 0 && j != lastX-1)
               {
                  if (spotCenters[j+2][i-1] == 1)
                  {
                     return clusterNumber[j+2][i-1];
                  }
               }

               //check right-top + 2 to the right
               if (i != 0 && j != lastX-2)
               {
                  if (spotCenters[j+3][i-1] == 1)
                  {
                     return clusterNumber[j+3][i-1];
                  }
               }
      

               //check right  
               if (j != lastX)
               {
                  if (spotCenters[j+1][i]==1)
                  {
                     pixelClusters++;                            
                     return pixelClusters;
                  }               
               }               
               
               //check bottom
               if (i != lastY)
               {
                  if (spotCenters[j][i+1] == 1)
                  {
                     pixelClusters++;
                     return pixelClusters;
                  }
               }

      return 0;

   }
   
   private void findContiguousBlobs()
   {
      TreeMap equivalenceHash = new TreeMap();      
      TreeSet elementValue;
      Integer elementKey;
      int[] xUpperLeft = {-1,-1,0,1};
      int[] yUpperLeft = {0,-1,-1,-1}; 
      int[] xLowerRight = {1,1,0,-1};
      int[] yLowerRight = {0,1,1,1};
     
      int[] nghdValues = {0,0,0,0};
      int minNonZero=0;
      int numNeighbors = 4;
      int k,l;      
      int numGlobs = 1;

      //System.out.println("Finding contiguous globs.");

      //first pass - check upperLeft neighbors

      for (int i=1; i<height; i++)
      {
         for (int j=1; j<width-1; j++)
         {
            if(spotCenters[j][i]==1)
            {
               //check L, LT, T, RT (part of 8-nghd)                
               for (int ii=0; ii<numNeighbors; ii++)
               {  
                  k=j+xUpperLeft[ii]; l=i+yUpperLeft[ii];

                  //if the point is in image area
                  if(imageMask[k][l]==1)
                  {
                     //add its value to nghdValues
                     nghdValues[ii] = spotCenters[k][l];                         
                  }     
                  else
                  {
                     nghdValues[ii] = 0;
                  }
               
               }
 
               minNonZero = findMinNonZero(nghdValues,numNeighbors); 
               //if((j>248 && j<270) && (i==310))
               //{
                  //System.out.print(nghdValues[0] + " " + nghdValues[1] + " " +
                  //   nghdValues[2] + " " + nghdValues[3]);
                  //System.out.println("");
                  //System.out.println("minNonZero: " + minNonZero);
               //}
 
               if (minNonZero==0)
               {
                  //assign new glob # to this pixel
                  numGlobs += 1;
                  spotCenters[j][i] = numGlobs; 
               }                           
               else
               {
                  spotCenters[j][i] = minNonZero;
 
                 //update equivalence table
                  for (int ii=0; ii<numNeighbors; ii++)
                  {
                     if(nghdValues[ii] > minNonZero)
                     {
                        //add to equivalence table
                        elementKey = new Integer(nghdValues[ii]);
                        if (equivalenceHash.containsKey(elementKey))
                        {
                           elementValue = (TreeSet) equivalenceHash.get(elementKey);
                        }
                        else
                        {
                           elementValue = new TreeSet();
                        }
                        elementValue.add(new Integer(minNonZero)); 
                        equivalenceHash.put(elementKey,elementValue);
                     }
                  }


               }                   

            }//operating on a spot center            

         } //for width
      } //for height                   

      //System.out.println("numGlobs FirstPass: " + numGlobs);

      //System.out.println("thePixels, 258, 313: " + thePixels[3][258][313]);

      //look at first spot
      //for (int i=304; i<323; i++)
      //{
      //   for (int j=249; j<270; j++)
      //   {
            //System.out.print(spotCenters[j][i] + " ");
      //   }
         
         //System.out.println("");
      // }


       //System.out.println("equivalenceHash: " + equivalenceHash);
      

      //Renumber globs based on equivalence table    
      for (int i=0; i<height; i++)
      {
         for (int j=0; j<width; j++)
         {

            if(spotCenters[j][i] > 0)
            {
               elementKey = new Integer(spotCenters[j][i]);
               if(equivalenceHash.containsKey(elementKey))
               {
                  TreeSet treeSet = (TreeSet) equivalenceHash.get(elementKey);
                  Integer minValue = (Integer) treeSet.first();
                  spotCenters[j][i]= minValue.intValue();
               }
            }

         }
      }


      //look at spot
      /*for (int i=304; i<323; i++)
      {
         for (int j=249; j<270; j++)
         {
            System.out.print(spotCenters[j][i] + " ");
         }
         
         System.out.println("");
       } */

      //System.out.println("2 spot: " + thePixels[3][409][182]);
      //System.out.println("(409,181): " + thePixels[3][409][181]);
      //System.out.println("(408,183): " + thePixels[3][408][183]);
      //System.out.println("(409,183): " + thePixels[3][409][183]);

      //for (int i=178; i<187; i++)
      //{
      //   for (int j=405; j<414; j++)
      //   {
            //System.out.print(spotCenters[j][i] + " ");
      //   }
         
         //System.out.println("");
      //}


      //second-pass - check lowerRight neighbors
      //proceed top to bottom, right-to-left
      
      /*for (int z=0; z<5; z++)
      {
      for (int i=1; i<height-1; i++)
      {
         for (int j=width-2; j>0; j--)
         {

            if(spotCenters[j][i] > 0)
            {

               //check R, RL, L, LL (part of 8-nghd)                
               for (int ii=0; ii<numNeighbors; ii++)
               {  
                  k=j+xLowerRight[ii]; l=i+yLowerRight[ii];
 
                  //if the point is in image area
                  if(imageMask[k][l]==1)
                  {
                     //add its value to nghdValues
                     nghdValues[ii] = spotCenters[k][l];                         
                  }     
                  else
                  {
                     nghdValues[ii] = 0;
                  }
               
               }
 
               minNonZero = findMinNonZero(nghdValues,numNeighbors);        
 
               if (minNonZero!=0)
               {
                  spotCenters[j][i] = minNonZero;
               }                   

            }            

         }
      }
      }

      //third pass
      for (int i=1; i<height-1; i++)
      {
         for (int j=1; j<width-1; j++)
         {

            if(spotCenters[j][i] > 0)
            {

               //check R, RL, L, LL (part of 8-nghd)                
               for (int ii=0; ii<numNeighbors; ii++)
               {  
                  k=j+xUpperLeft[ii]; l=i+yUpperLeft[ii];
 
                  //if the point is in image area
                  if(imageMask[k][l]==1)
                  {
                     //add its value to nghdValues
                     nghdValues[ii] = spotCenters[k][l];                         
                  }     
                  else
                  {
                     nghdValues[ii] = 0;
                  }
               
               }
 
               minNonZero = findMinNonZero(nghdValues,numNeighbors);        
 
               if (minNonZero!=0)
               {
                  spotCenters[j][i] = minNonZero;
               }                   

            }            

         }
      } */
       
            
      printNumberofGlobs();
      
   }

   private void printNumberofGlobs()
   {

      Hashtable myHashTable = new Hashtable();     
      Integer globNumber;

      for(int i=0; i<height; i++)
      {
         for(int j=0; j<width; j++)
         {
            if(spotCenters[j][i] != 0)
            {
               globNumber = new Integer(spotCenters[j][i]);
               if(myHashTable.containsKey(globNumber))
               {
                  //increment value
                  Integer currentValue = (Integer) myHashTable.get(globNumber);
                  myHashTable.put(globNumber, new Integer(currentValue.intValue()+1));

               }
               else
               {
                  myHashTable.put(globNumber,new Integer(1));                   
               }                                                                   
 
            }  
         }
      }

      //System.out.println("Number of Unique Globs/HashTable:" + myHashTable.size());
      
   }


   private int findMinNonZero(int[] nghdValues, int numNeighbors)
   {

      int minNonZeroValue = 0;
    
      for(int i=0; i<numNeighbors; i++)
      {
         if(minNonZeroValue==0)
         {
            if (nghdValues[i] != 0)
            {
               minNonZeroValue = nghdValues[i];               
            }
         }
         else
         {
            if((nghdValues[i] != 0) && (nghdValues[i] < minNonZeroValue))
            {
               minNonZeroValue = nghdValues[i];
            }
         }                           

      } 

      return minNonZeroValue;     
   }

   private void findSpots()
   {}

   private void findSpots1()
   {
      //find minimum (keep x,y too) for each glob
      //find L,R,T,B edges for each glob
      //compute radius for minimum
      //select second radius, min in area outside first
      //compute likelihood for 2 spots, compute likelihood for 1 spot
      //array number of spots/glob
      //print the number w/2.      

      Hashtable minHash = new Hashtable();
      int globNumber = 0;
      Integer elementValue;
      Integer elementKey;

      for (int i=0; i<height; i++)
      {
         for (int j=0; j<width; j++)
         {

            //
            //find min value
            //           
            globNumber = spotCenters[j][i];
            elementKey = new Integer(globNumber);

            if(globNumber > 0)
            {
               
               if(minHash.containsKey(elementKey))
               {
                  elementValue = (Integer) minHash.get(elementKey);
                  if(thePixels[3][j][i] < elementValue.intValue())                                            
                  {
                     elementValue = new Integer(thePixels[3][j][i]);
                     minHash.put(elementKey,elementValue);
                  }
               }
               else
               {
                  elementValue = new Integer(thePixels[3][j][i]);
                  minHash.put(elementKey, elementValue);
               }    

            }      

            //
            //find L,R,T,B
            //      

         }//for width
      }//for height

   }

   private void findSpots2()
   {

   }
 
   private void findSpots3()
   {

   }

   private void findSpots4()
   {

   }

   public int getSpotCount()
   {
      return spotCount;
   }

   public double getMeanNumberSpots()
   {
       return meanNumberSpots;
   }

   public double getVarianceNumberSpots()
   {
       return varianceNumberSpots;
   }

   public TreeMap getGlobPixelCountNoNoise()
   {
      return globPixelCountNoNoise;
   }

   public TreeMap getGlobPixelCountFinal()
   {
      return globPixelCountFinal;
   }

   public int getHeight()
   {
      return height;
   }

   public int getWidth()
   {
      return width;
   }

   public int[][] getSpotCentersFinal()
   {
       return spotCentersFinal;
   }

   public int[][][] getThePixels()
   {
       return thePixels;
   }

}











