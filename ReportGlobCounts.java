

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ReportGlobCounts
{

   public ReportGlobCounts(File[] files)
   {
       FileWriter out = null;

      try
      {
          String fileName = new String("meanVarAllSmall.txt");
          File outputFile = new File(fileName);
          out = new FileWriter(outputFile);

         for (int i=0;i<files.length;i++)
         {
             EliFile eliFile = new EliFile(files[i]);

             out.write(files[i].getName());
             out.write(" ");

             Integer result = new Integer(eliFile.getSpotCount());

             out.write(result.toString());
             out.write(" ");

             Double mean = new Double(eliFile.getMeanNumberSpots());
             out.write(mean.toString());
             out.write(" ");

             Double variance = new Double(eliFile.getVarianceNumberSpots());
             out.write(variance.toString());
             out.write("\n");

         }

         out.close();

      }
      catch (IOException e)
      {
         System.err.println("Caught IOException: " + e.getMessage());
      }


   }

}