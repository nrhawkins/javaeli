
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

public class ReportPixelCounts
{

   public ReportPixelCounts(File[] files)
   {
       FileWriter out = null;

      try
      {
          String fileName = new String("pixelCounts.txt");
          File outputFile = new File(fileName);
          out = new FileWriter(outputFile);
          Integer pixelCount, globNumber;
          int spotCount;

          for (int i=0;i<files.length;i++)
          {

             EliFile eliFile = new EliFile(files[i]);
             spotCount = eliFile.getSpotCount();

             System.out.println("report: " + spotCount);

             if(spotCount > 0)
             {
                for(int j = 1; j <= spotCount; j++)
                {

                    //Filename
                    out.write(files[i].getName());
                    out.write(" ");

                    //GlobCount
                    Integer result = new Integer(eliFile.getSpotCount());
                    out.write(result.toString());
                    out.write(" ");

                    //GlobNumber
                    out.write(new Integer(j).toString());
                    out.write(" ");

                    //Number of Pixels in Glob
                    TreeMap globPixelCount = eliFile.getGlobPixelCountFinal();
                    globNumber = new Integer(j);
                    pixelCount = (Integer) globPixelCount.get(globNumber);
                    out.write(pixelCount.toString());
                    out.write(" ");

                    //End of row of data
                    out.write("\n");
                }

             }
             else
            {
                //Filename
                out.write(files[i].getName());
                out.write(" ");

                //GlobCount
                out.write("0");
                out.write(" ");

                //GlobNumber
                out.write("0");
                out.write(" ");

                //Number of Pixels in Glob
                out.write("0");
                out.write(" ");

                //End of row of data
                out.write("\n");
            }

         }

         out.close();

      }
      catch (IOException e)
      {
         System.err.println("Caught IOException: " + e.getMessage());
      }


   }
   
}