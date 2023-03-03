package IDS_3;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

public class P3_CounterUpdate {

    public void counterUpdateSketch(int k, int w, BufferedReader p2Input, int [][] CountMinFilterCounter, int[] HashArray) throws IOException {

        FlowHandler[] FlowHandlers;

        // insert FlowHandlers to CountMinFilterCounter
        FlowHandlers = CU_insert(p2Input);

        // increase the counter for the FlowHandlers using Counter Update Algo
        increaseCU_Counter(FlowHandlers, CountMinFilterCounter, HashArray);

        // evaluate Estimate of counter
        evaluateEstimateSize(FlowHandlers, CountMinFilterCounter, HashArray);

        // evaluate average error
        double avgError = evaluateError(FlowHandlers);

        // Sort the FlowHandler array for the given condition : "FlowHandlers of the largest estimated sizes"
        Arrays.sort(FlowHandlers, (a, b)->b.getEstimatedVal() - a.getEstimatedVal());

        // publish output to file and console
        generateFileOutput(FlowHandlers, avgError);

    }

    // Function to Insert the FlowHandler ID along with its counter into the hash table
    public static FlowHandler[] CU_insert(BufferedReader p2Input) {

        String line;
        int index = 0;

        try{
            // Number of FlowHandlers for the Demo
            int no_of_FlowHandlers = Integer.parseInt(p2Input.readLine());
            FlowHandler[] FlowHandlers = new FlowHandler[no_of_FlowHandlers];

            while( (line = p2Input.readLine()) != null) {

                // split the input line using the split function and regex for space
                String[] curr = line.split("\\s+");

                //assign the variables to the respective FlowHandlers
                FlowHandlers[index] = new FlowHandler(curr[0], curr[1]);

                //update the index position for the FlowHandler
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
    public static void increaseCU_Counter(FlowHandler[] FlowHandlers, int[][] CountMinFilterCounter, int[] HashArray){

        char operation;
        // Iterate over all the FlowHandlers from the input file
        for(int line = 0; line < FlowHandlers.length; line++) {

            // Extract FlowHandler properties
            int fSize = Integer.parseInt(FlowHandlers[line].getPktSize());
            int FlowHandlerHash = FlowHandlers[line].getFlowHandlerId().hashCode();

            //running the algorithm for all individual packets in the FlowHandler
            for(int i=0; i<fSize; i++) {

                //Iterating over all the FlowHandlers available
                for(int j=0; j<line; j++) {

                    int normalize = FlowHandlerHash ^ HashArray[j];

                    //checking for MSB of hashed value for FlowHandlerID with the Random Integer are XOR is performed.
                    // when the resultant value is equal to 1 then added and subtracted otherwise/
                    if((normalize>>31 & 1) == 1){
                        operation ='+';
                    }
                    else
                        operation ='-';

                    int index = (int)Math.abs(normalize % 3000);

                     //some of the FlowHandlers are assigned positively and some other negatively
                    if(operation == '+')
                        CountMinFilterCounter[j][index] = CountMinFilterCounter[j][index] + 1;
                    else
                        CountMinFilterCounter[j][index] = CountMinFilterCounter[j][index] - 1;
                }
            }
        }
    }

    // Function to evaluate the estimated value for each of the FlowHandler
    public static void evaluateEstimateSize(FlowHandler[] FlowHandlers, int[][] CountMinFilterCounter, int[] HashArray){

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
        for(int len = 0; len<FlowHandlers.length; len++) {

            String FlowHandlerId = FlowHandlers[len].getFlowHandlerId();
            int est_Size = FlowHandlers[len].getEstimatedVal();
            int original_Size = Integer.parseInt(FlowHandlers[len].getPktSize());

            Final_Error += Math.abs(est_Size-original_Size);
        }
        return Final_Error / (FlowHandlers.length);
    }

    // Function to push the output to console and file
    public static void generateFileOutput(FlowHandler[] FlowHandlers, double avgError){

        try {
            PrintStream fileStream = new PrintStream(new File("P2_CounterUpdateOutput.txt"));

            // Outputting the values to a file using PrintStream
            fileStream.println("Average error for the FlowHandlers: " + avgError);
            fileStream.println("FlowHandler\t\t\t\tEst\t\t\tActual");

            // Printing values to console
            System.out.println("Average error for the FlowHandlers: " + avgError);
            System.out.println("----------------------------");
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
