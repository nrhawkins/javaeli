

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
public class SpotRenderer2b extends JComponent
{
   int plateWidth;
   int plateHeight;
   int[][] spotCentersFinal;
   TreeMap globPixelCountFinal;
   int currentGlobNumber = 0;
   int lastGlobNumber = 0;
   int globNumber = 0;
   Integer numPixels;
   Color[] myColors = {Color.cyan,Color.blue,Color.white, Color.black, Color.green};

   public SpotRenderer2b(int[][] spotCentersFinal,TreeMap globPixelCountFinal)
   {
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
      g.drawOval(0,0,plateWidth,plateHeight);
      g.setColor(Color.pink);
      g.fillOval(0,0,plateWidth,plateHeight);

      //draw spots
      int currentColor = 0;
      g.setColor(myColors[currentColor]);
      int ovalWidth = 1;
      int ovalHeight = 1;

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
                  //globNumber = spotCenters[j][i] % 2;
                  globNumber = spotCentersFinal[j][i];
                  //globNumber = new Integer(firstGlob.intValue()+count);
                  //count = count + 1;
                  numPixels = (Integer) globPixelCountFinal.get(new Integer(globNumber));

                  if(numPixels.intValue() < 10)
                  //if(globNumber == 0)
                  {
                     g.setColor(myColors[0]);
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

               g.drawOval(j,i,ovalWidth,ovalHeight);
               g.fillOval(j,i,ovalWidth,ovalHeight);

            }
         }
      }

   }

}

