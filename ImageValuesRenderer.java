
import javax.swing.*;
import java.awt.image.*;
import java.awt.*;
import java.awt.event.*;

public class ImageValuesRenderer extends JComponent
{

   int plateWidth;
   int plateHeight;
   int[][][] thePixels;
   double[][] spotProb;
   int spotCount;
   int pixSize=30;
   int iMin, iMax, jMin, jMax;
   
   public ImageValuesRenderer(int[][][] thePixels,double[][] spotProb,int iMin,int iMax,int jMin,int jMax)
   {
      this.thePixels = thePixels;
      this.spotProb = spotProb;
      this.iMin = iMin;
      this.iMax = iMax;
      this.jMin = jMin;
      this.jMax = jMax;
      //plateWidth = spotCenters[0].length;
      //plateHeight = spotCenters[0].length;     
      plateWidth = 481;
      plateHeight = 470; 
   }

   public void paint(Graphics g)
   {
      spotCount = 0;
  
      //draw plate
      //g.drawOval(0,0,plateWidth,plateHeight);
      //g.setColor(Color.pink);
      //g.fillOval(0,0,plateWidth,plateHeight);

      Font myFont = new Font("SansSerif",0,7);

      //draw original image
      //for (int i=0; i<plateHeight; i++)
      for (int i=iMin; i<iMax; i++)
      {
         //for (int j=0; j<plateWidth; j++)
         for (int j=jMin; j<jMax; j++)
         {
               Color color = new Color(thePixels[0][j][i],thePixels[1][j][i],thePixels[2][j][i]);
               g.setColor(color);
               g.drawRect((j-jMin)*pixSize,(i-iMin)*pixSize,pixSize,pixSize);
               g.fillRect((j-jMin)*pixSize,(i-iMin)*pixSize,pixSize,pixSize);
               Double probValue = new Double(spotProb[j][i]);
               String probability = probValue.toString();
               g.setColor(Color.black);
               g.setFont(myFont);
               
               if(probability.length()<5)
               {
     	          g.drawString(probability,(j-jMin)*pixSize,(i-iMin)*pixSize+pixSize);
               }
               else
               {
                  g.drawString(probability.substring(0,4),(j-jMin)*pixSize,(i-iMin)*pixSize+pixSize);
               }
         }
      }      
      
      //g.setColor(Color.black);
      //g.drawString(".11345",30,30);
                  
      //System.out.println("Number of spots painted: " + spotCount);

   }
   
}


