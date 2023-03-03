import java.io.File;
import java.io.PrintStream;
import java.util.Random;

public class P2_CountingBloomFilter {

    static int no_Of_Hashes;
    static int no_Of_EncodeElements;
    static int no_Of_FilterBits;

    static int[] Array_A;
    static int[] Updated_A;
    static int[] HashArray;

    static int[] CountingBloomFilterCounter;

    static P2_CountingBloomFilter CBF;
    static Random generateRandomNumber;

    P2_CountingBloomFilter(){

        // Initialize the hyper parameters for performing the Algorithm
        no_Of_Hashes = 7;
        no_Of_EncodeElements = 1000;
        no_Of_FilterBits = 10000;

        Array_A = new int[no_Of_EncodeElements];
        Updated_A = new int[500];
        HashArray = new int[no_Of_Hashes];
        CountingBloomFilterCounter = new int[no_Of_FilterBits];

        // Creating class for generating random numbers using the seed: nanotime
        generateRandomNumber = new Random(System.nanoTime());

    }

    public static void main(String[] args) {

        //Creating object for the class P2_CountingBloomFilter
        CBF = new P2_CountingBloomFilter();

        // Populate the arrays with the random values in it
        insertElements(Array_A);
        insertElements(Updated_A);
        insertElements(HashArray);

        // Adding elements of Array_A into bloomFilter
        encodingAllElements(Array_A, HashArray, no_Of_EncodeElements);

        // 500 Elements to be removed from the Array_A
        removeElements(500, Array_A, HashArray);

        // Adding elements of Updated_A into bloomFilter
        encodingAllElements(Updated_A, HashArray, 500);

        // publish output to file and console
        generateFileOutput();

    }

    // This function is used to insert the random elements to the provided array
    public static void insertElements(int[] Array) {

        for(int length = 0; length < Array.length; length++){

            // Inserting random values to the provided array
            Array[length] = generateRandomNumber.nextInt(Integer.MAX_VALUE);
            //System.out.println(Array[length]);
        }
    }

    public static void encodingAllElements(int[] Array, int[] HashArray, int elementCount) {

        // Iterating over all the elements needed for the demo
        for (int elePos = 0; elePos < elementCount; elePos++) {

            // performing the required number of hashes for the counting bloom filter
            for (int hashPos = 0; hashPos < no_Of_Hashes; hashPos++) {

                //evaluating the hash value for the element using the respective hash function and normalizing to the array size
                int normalize = Array[elePos] ^ HashArray[hashPos];
                int index = normalize % no_Of_FilterBits;

                // Update the counter of the element by adding one
                CountingBloomFilterCounter[index] = CountingBloomFilterCounter[index] + 1;
            }
        }
    }

    public static void removeElements(int n, int[] Array, int[] HashArray) {

        // Iterating over all the elements needed for the demo
        for (int elePos = 0; elePos < n; elePos++) {

            // performing the required number of hashes for the counting bloom filter
            for (int hashPos = 0; hashPos < no_Of_Hashes; hashPos++) {

                //evaluating the hash value for the element using the respective hash function and normalizing to the array size
                int normalize = Array[elePos] ^ HashArray[hashPos];
                int index = normalize % no_Of_FilterBits;

                if (CountingBloomFilterCounter[index] > 0)

                    // Decrement the counter of the element that has to be removed from the counting bloom filter
                    CountingBloomFilterCounter[index] -= 1;
            }
        }
    }

    // Searches for given array elements in bloomFilter
    public static int evaluateLookUpCount(int[] Array, int[] HashArray) {

        int lookupCount = 0;

        // Iterating over all the elements needed for the demo
        for (int elePos = 0; elePos < no_Of_EncodeElements; elePos++) {

            int minCount = Integer.MAX_VALUE;
            int tempCount = 0;

            // performing the required number of hashes for the counting bloom filter
            for (int hashPos = 0; hashPos < no_Of_Hashes; hashPos++) {

                //evaluating the hash value for the element using the respective hash function and normalizing to the array size
                int normalize = Array[elePos] ^ HashArray[hashPos];
                int index = normalize % no_Of_FilterBits;

                // Finding the sum of all the index counters
                if (CountingBloomFilterCounter[index] > 0){
                    tempCount = tempCount + 1;
                }

                // Equating the minimum value to the counter value for the element
                minCount = Math.min(minCount, CountingBloomFilterCounter[index]);
            }

            // Counting the number of valid hashes upon the implementing the counting bloom filter
            if (tempCount == no_Of_Hashes) {
                lookupCount =  lookupCount + minCount;
            }

        }
        return lookupCount;
    }

    // Function to push the output to console and file
    public static void generateFileOutput(){

        try {
            PrintStream fileStream = new PrintStream(new File("P2_CountingBloomFilterOutput.txt"));

            // Outputting the values to a file using PrintStream
            fileStream.println("Value of Lookup Count for the Array: " + evaluateLookUpCount(Array_A, HashArray));

            // Printing values to console
            System.out.println("Value of Lookup Count for the Array: " + evaluateLookUpCount(Array_A, HashArray));

            // closing th filestream
            fileStream.close();
        }
        catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

}
