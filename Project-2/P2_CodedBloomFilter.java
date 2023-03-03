import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Random;

public class P2_CodedBloomFilter {

    static int no_Of_Hashes;
    static int no_Of_EncodeElements;
    static int no_Of_GCodeBits;
    static int no_Of_BloomFilters;
    static int no_Of_Sets;

    static int[][] MultiSets;
    static int[] HashArray;


    static int[][] CodedBloomFilterBitmap;

    static P2_CodedBloomFilter CBF;
    static Random generateRandomNumber;

    P2_CodedBloomFilter(){

        no_Of_Hashes = 7;
        no_Of_Sets = 7;
        no_Of_EncodeElements = 1000;
        no_Of_GCodeBits = 30000;
        no_Of_BloomFilters = 3;

        CodedBloomFilterBitmap = new int[no_Of_BloomFilters][no_Of_GCodeBits];

        MultiSets = new int[no_Of_Sets][no_Of_EncodeElements];
        HashArray = new int[no_Of_Hashes];

        // Creating class for generating random numbers using the seed: nanotime
        generateRandomNumber = new Random(System.nanoTime());
    }

    public static void main(String[] args) {

        //Creating object for the class P2_CodedBloomFilter
        CBF = new P2_CodedBloomFilter();

        // Populate the Multisets with the random values
        populate(MultiSets);

        // Populate the Hash Array with the random values
        populate(HashArray);

        addMultiSetsToBloomFilter();

        generateFileOutput();
    }

    // populate sets
    public static void populate(int [][] sets){

        for (int setPos = 0; setPos < no_Of_Sets; setPos++) {

            for (int elePos = 0; elePos < no_Of_EncodeElements; elePos++) {

                sets[setPos][elePos] = generateRandomNumber.nextInt(Integer.MAX_VALUE);
            }
        }
    }

    // populate HashArray
    public static void populate(int [] HashArray){

        for (int hashPos = 0; hashPos < no_Of_Hashes; hashPos++) {

            HashArray[hashPos] = generateRandomNumber.nextInt(Integer.MAX_VALUE);

        }

    }

    // Multi-sets are added to bloom filter
    public static void addMultiSetsToBloomFilter(){

        // Iterating over all the number of sets
        for (int setPos = 1; setPos < no_Of_Sets + 1; setPos++) {

            // Iterating over all the number of filters - bits in gcode of the set
            for (int bitPos = 0; bitPos < no_Of_BloomFilters; bitPos++) {

                // Shifting the bits to right
                int x = (setPos >> bitPos) & 1;

                // Validating if the Bloom Filter is to consider or not
                if (x == 1) {

                    // Iterating over all the elements needed for the demo
                    for (int elePos = 0; elePos < no_Of_EncodeElements; elePos++) {

                        int element = MultiSets[setPos - 1][elePos];
                        // performing the required number of hashes for the bloom filter
                        for (int j = 0; j < no_Of_Hashes; j++) {

                            int normalize = element ^ HashArray[j];
                            int index = normalize % no_Of_GCodeBits;

                            // Update the Bitmap of the Bloom filter of the respectice bloom filter element with one
                            CodedBloomFilterBitmap[bitPos][index] = 1;
                        }
                    }
                }
            }
        }
    }


    public static int search(int[][] sets, int[] hashes) {

        int count = 0;

        // Iterating over all the number of sets
        for (int i = 1; i < no_Of_Sets + 1; i++) {

            // Iterating over all the elements needed for the demo
            for (int k = 0; k < no_Of_EncodeElements; k++) {

                int result = 0;
                int encodeElement = sets[i - 1][k];

                // Iterating over all the number of filters - bits in gcode of the set
                for (int bit = 0; bit < no_Of_BloomFilters; bit++) {

                    int temp = 0;
                    // performing the required number of hashes for the bloom filter
                    for (int j = 0; j < no_Of_Hashes; j++) {

                        int normalize = encodeElement ^ hashes[j];
                        int index = normalize % no_Of_GCodeBits;

                        // Updating the counter of the element
                        if (CodedBloomFilterBitmap[bit][index] == 1)
                            temp += 1;

                        if (temp == 7)
                            result |= (1 << bit);

                    }
                }
                if (result == i)
                    count += 1;
            }
        }
        return count;
    }

    // Function to push the output to console and file
    public static void generateFileOutput(){

        try {
            PrintStream fileStream = new PrintStream(new File("P2_CodedBloomFilterOutput.txt"));

            // Outputting the values to a file using PrintStream
            fileStream.println("Value of Lookup Count for the Array: " + search(MultiSets, HashArray));

            // Printing values to console
            System.out.println("Value of Lookup Count for the Array: " + search(MultiSets, HashArray));

            // closing th filestream
            fileStream.close();
        }
        catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

}
