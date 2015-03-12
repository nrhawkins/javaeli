
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

public class ReportPixels
{

   public ReportPixels(File[] files)
   {
       FileWriter out = null;

      try
      {
          String fileName = new String("pixelData.txt");
          File outputFile = new File(fileName);
          out = new FileWriter(outputFile);
          Integer pixelCount;
          int spotCount;
          Integer globNumber;
          int spotCentersFinal[][];
          int thePixels[][][];
          Integer row, column, intensity;

          for (int i=0;i<files.length;i++)
          {

             EliFile eliFile = new EliFile(files[i]);
             spotCount = eliFile.getSpotCount();

             spotCentersFinal = eliFile.getSpotCentersFinal();
             thePixels = eliFile.getThePixels();

             System.out.println("report: " + spotCount);

             if(spotCount > 0)
             {
                for(int j = 0; j < eliFile.getHeight(); j++)
                {
                    row = new Integer(j);
                    for(int k = 0; k < eliFile.getWidth(); k++)
                    {
                        column = new Integer(k);
                        globNumber = new Integer(0);
                        if(spotCentersFinal[k][j] > 0)
                        {

                            //Filename
                            out.write(files[i].getName());
                            out.write(" ");

                            //GlobCount
                            Integer result = new Integer(eliFile.getSpotCount());
                            out.write(result.toString());
                            out.write(" ");

                            //GlobNumber
                            globNumber = new Integer(spotCentersFinal[k][j]);
                            out.write(globNumber.toString());
                            out.write(" ");

                            //Number of Pixels in Glob
                            //TreeMap globPixelCount = eliFile.getGlobPixelCountNoNoise();
                            //pixelCount = (Integer) globPixelCount.get(new Integer(globNumber));
                            //out.write(pixelCount.toString());
                            //out.write(" ");

                            //x
                            out.write(column.toString());
                            out.write(" ");

                            //y
                            out.write(row.toString());
                            out.write(" ");

                            //intensity
                            intensity = new Integer(thePixels [3][column.intValue()][row.intValue()]);
                            out.write(intensity.toString());
                            out.write(" ");

                            //End of row of data
                            out.write("\n");

                        }
                    }
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

                //x
                out.write("0");
                out.write(" ");

                //y
                out.write("0");
                out.write(" ");

                //intensity
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