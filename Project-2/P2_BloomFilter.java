import java.io.File;
import java.io.PrintStream;
import java.util.Random;

public class P2_BloomFilter {

    static int no_Of_Hashes;
    static int no_Of_EncodeElements;
    static int no_Of_FilterBits;

    static int[] Array_A;
    static int[] Array_B;
    static int[] HashArray;
    static int[] BloomFilterBitMap;

    static P2_BloomFilter BF;
    static Random generateRandomNumber;

    P2_BloomFilter(){

        // Initialize the hyper parameters for performing the Algorithm
        no_Of_Hashes = 7;
        no_Of_EncodeElements = 1000;
        no_Of_FilterBits = 10000;

        // Arrays (Array A, B Hash, BloomFilter) for Encoding using Bloom Filter
        Array_A = new int[no_Of_EncodeElements];
        Array_B = new int[no_Of_EncodeElements];
        HashArray = new int[no_Of_Hashes];
        BloomFilterBitMap = new int[no_Of_FilterBits];


        // Creating class for generating random numbers using the seed: nanotime
        generateRandomNumber = new Random(System.nanoTime());
    }

    public static void main(String[] args) {

        //Creating object for the class P2_BloomFilter
        BF = new P2_BloomFilter();

        // Populate the arrays with the random values in it
        insertElements(Array_A);
        insertElements(Array_B);
        insertElements(HashArray);

        // Encode the Bloom Filter Bitmap
        encodeBloomFilterBitMap();

        // publish output to file and console
        generateFileOutput();
    }

    // Checking the valid number of Hashed elements in the given array
    public static int evaluateValidHashCount(int[] Array){

        int validHashCount = 0;

        // Iterating over all the elements needed for the demo
        for (int elePos = 0; elePos < no_Of_EncodeElements; elePos++) {

            int tempCounter = 0;
            // performing the required number of hashes for the bloom filter
            for (int hashPos = 0; hashPos < no_Of_Hashes; hashPos++) {

                //evaluating the hash value for the element using the respective hash function and normalizing to the array size
                int normalize = Array[elePos] ^ HashArray[hashPos];
                int index = normalize % no_Of_FilterBits;

                // Counting the number of bitmaps for the element
                if (BloomFilterBitMap[index] == 1){
                    tempCounter = tempCounter + 1;
                }
            }

            // Counting the number of valid hashes upon the implementing the bloom filter
            if (tempCounter == no_Of_Hashes) {
                validHashCount = validHashCount + 1;
            }
       }
        return validHashCount;
    }

    //Encode the Bloom Filter
    public static void encodeBloomFilterBitMap() {

        // Iterating over all the elements needed for the demo
        for(int elementPos = 0; elementPos < no_Of_EncodeElements; elementPos++){

            // performing the required number of hashes for the bloom filter
            for (int hashPos = 0; hashPos < no_Of_Hashes; hashPos++){

                //evaluating the hash value for the element using the respective hash function and normalizing to the array size
                int normalize = Array_A[elementPos] ^ HashArray[hashPos];
                int indexPos = normalize % no_Of_FilterBits;

                //Initializing the index position of the generated hash value for the element in the bloom filter bitmap to 1
                BloomFilterBitMap[indexPos] = 1;
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

    // Function to push the output to console and file
    public static void generateFileOutput(){

        try {
            PrintStream fileStream = new PrintStream(new File("P2_BloomFilterOutput.txt"));

            // Outputting the values to a file using PrintStream
            fileStream.println("Array A: Value of the Lookup Count : " + evaluateValidHashCount(Array_A));
            fileStream.println("Array B: Value of the Lookup Count : " + evaluateValidHashCount(Array_B));

            // Printing values to console
            System.out.println("Array A: Value of the Lookup Count : " + evaluateValidHashCount(Array_A));
            System.out.println("Array B: Value of the Lookup Count : " + evaluateValidHashCount(Array_B));

            // closing th filestream
            fileStream.close();
        }
        catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
}
