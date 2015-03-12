
import javax.swing.*;
import java.awt.image.*;
import java.awt.*;
import java.awt.event.*;

public class SpotRenderer extends JComponent
{

   int plateWidth;
   int plateHeight;
   int[][] spotCenters;
   int[][] spotRadii;
   int spotCount;
   
   public SpotRenderer(int[][] spotCenters,int[][] spotRadii)
   {
      this.spotCenters = spotCenters;
      this.spotRadii = spotRadii;
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
      g.setColor(Color.magenta);
      int ovalWidth = 1;
      int ovalHeight = 1;

      for (int i=0; i<plateHeight; i++)
      {
         for (int j=0; j<plateWidth; j++)
         {
            if (spotCenters[j][i] == 0)
            {
               //do nothing
            }
            else  //spot center
            {  
               spotCount++;

               /*if(spotCount==1)
               {  
                  System.out.println("spot 1: (j,i) " + j + " " + i);
                  g.setColor(Color.black);
                  g.drawOval(j,i,ovalWidth,ovalHeight);

               }
               if(spotCount == 4)
               {
                  System.out.println("spot: (j,i) " + j + " " + i);
                  g.setColor(Color.red);
                  g.drawOval(j,i,ovalWidth,ovalHeight);
               }
	       if(spotCount == 37)
               {
                  System.out.println("spot: (j,i) " + j + " " + i);
                  g.setColor(Color.cyan);
                  g.drawOval(j,i,ovalWidth,ovalHeight);
               }*/
               
               //int ovalWidth = 2*spotRadii[j][i];
               //int ovalHeight = ovalWidth;

               g.drawOval(j,i,ovalWidth,ovalHeight);
               g.fillOval(j,i,ovalWidth,ovalHeight);

            }
         }
      }                   
      System.out.println("Number of spots painted: " + spotCount);
   }
   
}


