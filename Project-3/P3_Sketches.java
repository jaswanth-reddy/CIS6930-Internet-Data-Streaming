package IDS_3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class P3_Sketches {

    static int k;
    static int w;
    static int n;

    static int Counter_number;
    static int Counter_exponent;
    static int ActiveCounterSize;

    static int [][] CountMinFilterCounter;
    static int [] HashArray;
    static BufferedReader Project3_Input;

    static Random generateRandomNumber;

    static P3_Sketches SK;
    static P3_CounterUpdate CU;
    static P3_ActiveCounter AC;
    static P3_CountMin CM;

    static P3_SketchHelper helper;

    P3_Sketches(){

        k = 3;                                          // number of counter arrays
        w = 3000;                                      // number of counters in each counter array
        n = 10000;

        Counter_number = 16;
        Counter_exponent = 16;
        ActiveCounterSize = 1000000;

        CountMinFilterCounter = new int[k][w];
        HashArray = new int[k];

        helper  = new P3_SketchHelper();

        // read input from file
        try {
            Project3_Input = new BufferedReader(new FileReader("project3input.txt"));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        };

        // Objects for the Algorithms to implement
        CM = new P3_CountMin();
        CU = new P3_CounterUpdate();
        AC = new P3_ActiveCounter();

        // Creating class for generating random numbers using the seed: nanotime
        generateRandomNumber = new Random(System.nanoTime());
    }

    public static void main(String[] args) {

        SK = new P3_Sketches();

        // Populate the arrays with the random values in it
        insertElements(HashArray);

        if (Project3_Input == null) return;

        else{
            try {

                CM.countMinSketch(k, w, Project3_Input, CountMinFilterCounter, HashArray);
                CU.counterUpdateSketch(k, w, Project3_Input, CountMinFilterCounter, HashArray);
                AC.activeCounterSketch(ActiveCounterSize, Counter_number);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // This function is used to insert the random elements to the provided array
    public static void insertElements(int[] Array) {

        for(int length = 0; length < Array.length; length++){

            // Inserting random values to the provided array
            Array[length] = generateRandomNumber.nextInt(Integer.MAX_VALUE);
            //System.out.println(Array[length]);
        }
    }
}
