
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

public class ReportImagePixels
{

   public ReportImagePixels(File[] files)
   {
       FileWriter out = null;

      try
      {
          String fileName = new String("pixelImageData.txt");
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

             for(int j = 0; j < eliFile.getHeight(); j++)
             {
                    row = new Integer(j);
                    for(int k = 0; k < eliFile.getWidth(); k++)
                    {
                        column = new Integer(k);

                        if(thePixels [3][column.intValue()][row.intValue()] != 255)
                        {

                            //Filename
                            out.write(files[i].getName());
                            out.write(" ");

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

         out.close();

      }
      catch (IOException e)
      {
         System.err.println("Caught IOException: " + e.getMessage());
      }


   }
   
}
