
import javax.swing.*;
import java.awt.image.*;
import java.awt.*;
import java.awt.event.*;

public class SpotRendererM3 extends JComponent
{
   int plateWidth;
   int plateHeight;
   int[][] spotCenters;
   int[][] theCenters;
   int spotCount;
   Color[] myColors = {Color.white, Color.black};
   
   public SpotRendererM3(int[][] theCenters)
   {
      //this.spotCenters = spotCenters;
      this.theCenters = theCenters;
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
      //int currentColor = 0;
      //g.setColor(myColors[currentColor]);
      //g.setColor(Color.magenta);
      int ovalWidth = 1;
      int ovalHeight = 1;

       
      for (int i=0; i<plateHeight; i++)
      {
         for (int j=0; j<plateWidth; j++)
         {
            
            if(theCenters[j][i] == 1)
            {
               //System.out.println("A 1 Center!");
               g.setColor(Color.black);
               g.drawOval(j,i,ovalWidth,ovalHeight);
            } 
            else if (theCenters[j][i] == 2)
            {
               g.setColor(Color.white);
               g.drawOval(j,i,ovalWidth,ovalHeight);
            }

            //g.drawOval(j,i,ovalWidth,ovalHeight);
            //g.fillOval(j,i,ovalWidth,ovalHeight);   
            
         }
      }                   

   } //end paint
   
} //end class


