
import java.io.*;


public class SpotCounter 
{
   boolean DISPLAY = false;
   //boolean DISPLAY = false;

   public SpotCounter(File[] files)
   {
      //System.out.println("Displaying the image file");
      if(DISPLAY)
      {
         new ImageViewer(files[0]);
      }
      //System.out.println("Creating the EliFile");

      //Create globPixelCount report
      //new ReportPixelCounts(files);
      //new ReportGlobCounts(files);
      new ReportPixels(files);
      //new ReportImagePixels(files);
   }
}


