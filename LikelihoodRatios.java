
import java.lang.Math;

public class LikelihoodRatios
{

   // make these single value variables arrays instead when have multiple radii to test?
   double probabilityNotSpot;
   double probabilitySpot;
   double power2 = 2;

   public LikelihoodRatios(ImageParameters imageParameters, 
      PixelNeighborhood pixelNeighborhood)
   {
      //System.out.println("computing the likelihood");      
      computeLikelihood(imageParameters, pixelNeighborhood);
   }

   private void computeLikelihood(ImageParameters imageParameters,
      PixelNeighborhood pixelNeighborhood)
   {

      //System.out.println("inside computeLikelihood");

      double squaredSumSpot = 0;
      double squaredSumNotSpot = 0;

      //System.out.println("squaredSumSpot: "  + squaredSumSpot);
      //System.out.println("squaredSumNotSpot: " + squaredSumNotSpot);
      
      //System.out.println("pixelnghd.pI.length: " + pixelNeighborhood.pixelIntensity.length);

      int n = pixelNeighborhood.pixelIntensity.length;
   
      //System.out.println("entering for loop");

      for (int i=0; i < n; i++)
      {
         double diffSpot = pixelNeighborhood.pixelIntensity[i] - imageParameters.meanSpot;
         double diffNotSpot = pixelNeighborhood.pixelIntensity[i] - imageParameters.meanNotSpot;

         squaredSumSpot += Math.pow(diffSpot, power2);
         squaredSumNotSpot += Math.pow(diffNotSpot, power2);           
      }

      //System.out.println("the squaredSumSpot: " + squaredSumSpot);
      //System.out.println("the squaredSumNotSpot: " + squaredSumNotSpot);

      double exponentSpot = 
         Math.exp(-squaredSumSpot/(2*Math.pow(imageParameters.standardDeviationSpot,power2))); 
      double exponentNotSpot = 
         Math.exp(-squaredSumNotSpot/(2*Math.pow(imageParameters.standardDeviationNotSpot,power2)));

      //System.out.println("exponentSpot: " + exponentSpot);
      //System.out.println("exponentNotSpot: " + exponentNotSpot);

      probabilitySpot = (1/Math.pow(imageParameters.standardDeviationSpot,n))*exponentSpot;             
      probabilityNotSpot = (1/Math.pow(imageParameters.standardDeviationNotSpot,n))*exponentNotSpot;
      
      //System.out.println("probabilitySpot: " + probabilitySpot);
      //System.out.println("probabilityNotSpot: " + probabilityNotSpot); 
     
   }

}


