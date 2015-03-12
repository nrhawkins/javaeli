

//import java.lang.*;


public class EliPixel implements Comparable
{
   private int x;
   private int y;
   private int intensity;
   
   public EliPixel(int x, int y, int intensity)
   {  
      this.x = x;   
      this.y = y;
      this.intensity = intensity;    
   }

   public int getX()
   {
      return x;
   }

   public int getY()
   {
      return y;
   }

   public int getIntensity()
   {
      return intensity;
   }

   public int compareTo(Object otherPixel)
   {
      EliPixel comparisonPixel = (EliPixel)otherPixel;
      
      //these return values cause the TreeSet to be sorted
      //from low intensity to high
      if( comparisonPixel.intensity < intensity )
      {
         return 1;
      }
      else if (comparisonPixel.intensity > intensity)
      {
         return -1;
      }
      else
      {
         return 0;
      }    
        
   }
    
}



