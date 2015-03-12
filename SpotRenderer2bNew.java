
import javax.swing.*;
import java.util.TreeMap;
import java.util.HashMap;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: hawkins
 * Date: Nov 6, 2003
 * Time: 6:28:20 PM
 * To change this template use Options | File Templates.
 */
public class SpotRenderer2bNew extends JComponent
{
   int plateWidth;
   int plateHeight;
   int[][][] thePixels;
   int[][] spotCentersFinal;
   TreeMap globPixelCountFinal;
   int currentGlobNumber = 0;
   int lastGlobNumber = 0;
   int globNumber = 0;
   Integer numPixels;
   Color[] myColors = {Color.blue,Color.white, Color.red, Color.green, Color.magenta};

   public SpotRenderer2bNew(int[][][] thePixels, int[][] spotCentersFinal,TreeMap globPixelCountFinal)
   {
      this.thePixels = thePixels;
      this.spotCentersFinal = spotCentersFinal;
      this.globPixelCountFinal = globPixelCountFinal;
      //plateWidth = spotCenters[0].length;
      //plateHeight = spotCenters[0].length;
      plateWidth = 481;
      plateHeight = 470;
   }

   public void paint(Graphics g)
   {

      //draw plate
      //g.drawOval(0,0,plateWidth,plateHeight);
      //g.setColor(Color.pink);
      //g.fillOval(0,0,plateWidth,plateHeight);

      //draw spots
      int currentColor = 0;
      g.setColor(myColors[currentColor]);
      int ovalWidth = 1;
      int ovalHeight = 1;
      int globCount = 0;
      int r,gr,b;

      for(int i=0; i<plateHeight; i++)
      {
         for(int j=0; j<plateWidth; j++)
         {
             r = thePixels[0][j][i];
             gr = thePixels[1][j][i];
             b = thePixels[2][j][i];

             g.setColor(new Color(r,gr,b));

             g.drawOval(j,i,ovalWidth,ovalHeight);
             g.fillOval(j,i,ovalWidth,ovalHeight);

         }

      }

      //int count = 0;
      for(int i=0; i<plateHeight; i++)
      {
         for(int j=0; j<plateWidth; j++)
         {

               if(spotCentersFinal[j][i] == 0)
               {
                   //do nothing
               }
               else
               {
                  //System.out.println("Spot Centers Final != 0");
                  //globNumber = spotCenters[j][i] % 2;
                  globNumber = spotCentersFinal[j][i];
                  //globNumber = new Integer(firstGlob.intValue()+count);
                  //count = count + 1;
                  //numPixels = (Integer) globPixelCountFinal.get(new Integer(globNumber));

                  g.setColor(Color.black);

               if(globNumber > globCount)
               {
                   globCount += 1;
                   //Left
                   /*
                   if(globCount == 4)
                   {
                       g.drawLine(j-4,i,j-25,i);
                       g.drawString(Integer.toString(globNumber),j-35,i);
                   }
                   */
                   //Right
                   /*
                   else
                   if(globCount == 7)
                   {
                         g.drawLine(j+4,i+2,j+25,i+2);
                         g.drawString(Integer.toString(globNumber),j+35,i+2);
                   }
                   */
                   //else
                   if(globCount%2 == 1)
                   {
                         g.drawLine(j-4,i,j-25,i);
                         g.drawString(Integer.toString(globNumber),j-35,i);
                   }
                   else
                        {
                         g.drawLine(j+4,i,j+25,i);
                         g.drawString(Integer.toString(globNumber),j+35,i);
                        }

               }
            }

         }
      }

   }

}

