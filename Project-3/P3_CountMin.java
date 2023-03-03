package IDS_3;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

public class P3_CountMin {

   public static void countMinSketch(int k, int w, BufferedReader p2Input, int [][] CountMinFilterCounter, int[] HashArray) throws IOException {

       FlowHandler[] FlowHandlers;

       // insert FlowHandlers to CountMinFilterCounter
       FlowHandlers = CM_insert(p2Input, HashArray, CountMinFilterCounter);

       // increase the counter for the FlowHandlers
       increaseCounter(FlowHandlers, CountMinFilterCounter, HashArray);

       // evaluate Estimate of counter
       evaluateEstimate(FlowHandlers, CountMinFilterCounter, HashArray);

       // evaluate average error
       double avgError = evaluateError(FlowHandlers);

       // Sort the FlowHandler array for the given condition : "FlowHandlers of the largest estimated sizes"
       Arrays.sort(FlowHandlers, (a, b)->b.getEstimatedVal() - a.getEstimatedVal());

       // publish output to file and console
       generateFileOutput(FlowHandlers, avgError);
   }

   // Function to Insert the FlowHandler ID along with its counter into the hash table
   public static FlowHandler[] CM_insert(BufferedReader p2Input, int[] HashArray, int[][] CountMinFilterCounter) {
       String line;
       int index = 0;

       try{
           // Number of FlowHandlers for the Demo
           int no_of_FlowHandlers = Integer.parseInt(p2Input.readLine());
           FlowHandler[] FlowHandlers = new FlowHandler[no_of_FlowHandlers];

           while( (line = p2Input.readLine()) != null) {

               // split the input line using the split function and regex for space
               String[] curr = line.split("\\s+");

               // assign the variables to the respective FlowHandlers
               FlowHandlers[index] = new FlowHandler(curr[0], curr[1]);

               // update the index position for the FlowHandler
               index= index + 1;
           }
       return FlowHandlers;
       }
       catch (Exception e) {
           System.out.println("Error occurred.");
           e.printStackTrace();
           return null;
       }
   }

   // function used to increase the counter upon the visiting the same FlowHandler id using CountMin Algo
   public static void increaseCounter(FlowHandler[] FlowHandlers, int[][] CountMinFilterCounter, int[] HashArray){

       for( int len = 0; len < FlowHandlers.length; len++) {

           int fSize = Integer.parseInt(FlowHandlers[len].getPktSize());

           for( int i = 0; i < fSize; i++) {

               //Iterating over all the FlowHandlers available
               for( int j = 0; j < len; j++) {

                   //System.out.println("k :"+k+ ", i ="+ i+", j = "+j);
                   // Evaluate the index position for the FlowHandlerID
                   int normalize = Math.abs(FlowHandlers[i].getFlowHandlerId().hashCode()) ^ HashArray[j];
                   int index = normalize % 3000;

                   CountMinFilterCounter[j][index] = CountMinFilterCounter[j][index] + 1;
               }
           }
       }
   }

   // Function to evaluate the estimated value for each of the FlowHandler
   public static void evaluateEstimate(FlowHandler[] FlowHandlers, int[][] CountMinFilterCounter, int[] HashArray){

       // Evaluate the Estimated Values
       for( int len = 0; len < FlowHandlers.length; len++) {

           int est_Size = Integer.MAX_VALUE;
           for( int i = 0; i < len; i++) {

               int normalize = Math.abs(FlowHandlers[i].getFlowHandlerId().hashCode()) ^ HashArray[i];
               int index = normalize % 3000;

               //taking min value of all hashed indices
               est_Size = Math.min(est_Size, CountMinFilterCounter[i][index]);
           }

           FlowHandlers[len].setEstimatedVal(est_Size);
       }
   }

   // Function to evaluate the difference error between the original and the estimated value for the FlowHandler size
   public static double evaluateError(FlowHandler[] FlowHandlers){

       double Final_Error = 0.0;

       // Iterating over all the FlowHandlers to sum up the error for each FlowHandler
       for( int len = 0; len < FlowHandlers.length; len++) {

           String FlowHandlerId = FlowHandlers[len].getFlowHandlerId();
           int est_Size = FlowHandlers[len].getEstimatedVal();
           int original_Size = Integer.parseInt(FlowHandlers[len].getPktSize());

           Final_Error += Math.abs(est_Size - original_Size);
       }
       return Final_Error / (FlowHandlers.length);
   }

   // Function to push the output to console and file
   public static void generateFileOutput(FlowHandler[] FlowHandlers, double avgError){

       try {
           PrintStream fileStream = new PrintStream(new File("P3_CountMinOutput.txt"));

           // Outputting the values to a file using PrintStream
           fileStream.println("Average error for the FlowHandlers: " + avgError);
           fileStream.println("FlowHandler\t\t\t\tEst\t\t\tActual");

           // Printing values to console
           System.out.println("Average error for the FlowHandlers: " + avgError);
           System.out.println("--------------------");
           System.out.println("FlowHandler\t\t\t\tEst\t\t\tActual");

           for (int i=0;i<100;i++) {

               // Values assigned to the FlowHandler ids in the hashtable are pushed to an output file.
               fileStream.println(FlowHandlers[i]+"\n");
               System.out.println(FlowHandlers[i]+"\n");
           }

           // closing th filestream
           fileStream.close();
       }
       catch (Exception e) {
           System.out.println("An error occurred.");
           e.printStackTrace();
       }

   }

}