
public class IterationStats
{
   int[] iteration;
   int[] spotCount;
   double[] meanSpot;
   double[] meanNotSpot;
   double[] standardDeviationSpot;
   double[] standardDeviationNotSpot;

   int maxIterations = 5;
   
   public IterationStats()
   {      
      iteration = new int[maxIterations];
      spotCount = new int[maxIterations];
      meanSpot = new double[maxIterations];
      meanNotSpot = new double[maxIterations];
      standardDeviationSpot = new double[maxIterations];
      standardDeviationNotSpot = new double[maxIterations];
   }

   public IterationStats(int numberOfIterations)
   {      
      iteration = new int[numberOfIterations];
      spotCount = new int[numberOfIterations];
      meanSpot = new double[numberOfIterations];
      meanNotSpot = new double[numberOfIterations];
      standardDeviationSpot = new double[numberOfIterations];
      standardDeviationNotSpot = new double[numberOfIterations];
   }

   public int[] getIteration()
   {
      return iteration;
   }

   public int[] getSpotCount()
   {
      return spotCount;
   }

   public double[] getMeanSpot()
   {
      return meanSpot;
   }

   public double[] getMeanNotSpot()
   {
      return meanNotSpot;
   }

   public double[] getStandardDeviationSpot()
   {
      return standardDeviationSpot;
   }

   public double[] getStandardDeviationNotSpot()
   {
      return standardDeviationNotSpot;
   }

   // setter methods 

   public void setIteration(int iteration)
   {
      this.iteration[iteration] = iteration;                  
   }

   public void setSpotCount(int iteration, int spotCount)
   {
      this.spotCount[iteration] = spotCount;                  
   }   

   public void setMeanSpot(int iteration, double meanSpot)
   {
      this.meanSpot[iteration] = meanSpot;      
   }

   public void setMeanNotSpot(int iteration, double meanNotSpot)
   {
      this.meanNotSpot[iteration] = meanNotSpot;
   }

   public void setStandardDeviationSpot(int iteration, double standardDeviationSpot)
   {
      this.standardDeviationSpot[iteration] = standardDeviationSpot;
   }

   public void setStandardDeviationNotSpot(int iteration, double standardDeviationNotSpot)
   {
      this.standardDeviationNotSpot[iteration] = standardDeviationNotSpot;
   }

}