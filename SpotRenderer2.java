
import javax.swing.*;
import java.awt.image.*;
import java.awt.*;
import java.awt.event.*;
import java.util.TreeMap;
import java.util.HashMap;

public class SpotRenderer2 extends JComponent
{
   int plateWidth;
   int plateHeight;
   int[][] spotCentersNoNoise;
   int[][] spotRadii;
   TreeMap globPixelCountNoNoise;
   HashMap meanIntensityPerGlob;
   double cutoff;
   double smallSpotRatio;
   int spotCount;
   int currentGlobNumber = 0;
   int lastGlobNumber = 0;
   int globNumber = 0;
   Integer numPixels;
   Color[] myColors = {Color.cyan,Color.blue,Color.white, Color.black, Color.green};
   
   public SpotRenderer2(int[][] spotCentersNoNoise,int[][] spotRadii,TreeMap globPixelCountNoNoise,
                        HashMap meanIntensityPerGlob, double cutoff, double smallSpotRatio)
   {
      this.spotCentersNoNoise = spotCentersNoNoise;
      this.spotRadii = spotRadii;
      this.globPixelCountNoNoise = globPixelCountNoNoise;
      this.meanIntensityPerGlob = meanIntensityPerGlob;
      this.cutoff = cutoff;
      this.smallSpotRatio = smallSpotRatio;
      //plateWidth = spotCenters[0].length;
      //plateHeight = spotCenters[0].length;     
      plateWidth = 481;
      plateHeight = 470; 
   }

   public void paint(Graphics g)
   {
      spotCount = 0;
  
      //draw plate
      g.drawOval(0,0,plateWidth,plateHeight);
      g.setColor(Color.pink);
      g.fillOval(0,0,plateWidth,plateHeight);

      //draw spots
      int currentColor = 0;
      g.setColor(myColors[currentColor]);
      int ovalWidth = 1;
      int ovalHeight = 1;
      Double meanIntensity;

      //int count = 0;
      for(int i=0; i<plateHeight; i++)
      {
         for(int j=0; j<plateWidth; j++)
         {
               if(spotCentersNoNoise[j][i] == 0)
               {
                   //do nothing
               }
               else
               {
                  //globNumber = spotCenters[j][i] % 2;
                  globNumber = spotCentersNoNoise[j][i];
                  //globNumber = new Integer(firstGlob.intValue()+count);
                  //count = count + 1;
                  numPixels = (Integer) globPixelCountNoNoise.get(new Integer(globNumber));
                  meanIntensity = (Double) meanIntensityPerGlob.get(new Integer(globNumber));

                  if(numPixels.intValue() < 10 && meanIntensity.doubleValue() > smallSpotRatio*cutoff)
                  //if(globNumber == 0)
                  {
                     g.setColor(myColors[0]);
                  }
                  else if(numPixels.intValue() < 10 && meanIntensity.doubleValue() <= smallSpotRatio*cutoff)
                  //if(globNumber == 0)
                  {
                     g.setColor(myColors[1]);
                  }
                  else if(numPixels.intValue() <= 20)
                  //if(globNumber == 0)
                  {
                     g.setColor(myColors[2]);
                  }
                  else if(numPixels.intValue() <= 40)
                  {
                     g.setColor(myColors[3]);
                  }
                  else
                  {
                      g.setColor(myColors[4]);
                  }

               //int ovalWidth = 2*spotRadii[j][i];
               //int ovalHeight = ovalWidth;

               //if(currentGlobNumber <= 10)
               //{
               g.drawOval(j,i,ovalWidth,ovalHeight);
               g.fillOval(j,i,ovalWidth,ovalHeight);
               //}

            }
         }
      }                   

      System.out.println("Number of spots painted: " + spotCount);
   }
   
}


