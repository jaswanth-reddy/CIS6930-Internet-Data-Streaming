import java.io.File;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.PrintStream;

public class P1_MultiHashTable {
    int number_Of_Entries;
    int number_Of_Flows;
    int number_Of_Hashes;

    static int flow_Count = 0;
    static FileWriter myWriter;

    public static void main(String[] args) {

        P1_MultiHashTable mH = new P1_MultiHashTable();

        // values as mentioned on the project file
        mH.number_Of_Entries = 1000;
        mH.number_Of_Flows = 1000;
        mH.number_Of_Hashes = 3;

        // Array of hash functions defined using the number of hash functions for multiplicative hashing
        int [] hashFunctions = new int[mH.number_Of_Hashes];

        // hashTableArray for storing the Hash table values
        int[] hashTableArray = new int[mH.number_Of_Entries];

        mH.constructMultiHashtable(mH.number_Of_Entries, mH.number_Of_Flows, mH.number_Of_Hashes, hashFunctions, hashTableArray);
    }


    public static void constructMultiHashtable(int number_Of_Entries, int number_Of_Flows, int number_Of_Hashes, int[] randomIntArr, int[] hashTableArray) {

        int counter = 0;
        while (counter < number_Of_Hashes) {

            // initializing randomIntArr with "number_Of_Hashes" of random integers
            randomIntArr[counter] = (int) ( Math.random() * Math.pow( 10, 8 ) );
            counter++;
        }

        // Iteratively check for all the entries using the Multi Hash technique
        for (int length = 0; length < number_Of_Entries; length++) {

            // generating a random flowID using th Math Library random function
            int flow_ID = (int) (Math.random() * 10000);

            // inserting an element in the Multi Hash table
            insert_MH(flow_ID, randomIntArr, number_Of_Flows, hashTableArray);
        }

        try {
            PrintStream fileStream = new PrintStream(new File("P1_MultiHash_Output.txt"));

            fileStream.println("Total Flows Used : " + flow_Count);
            fileStream.println("--------------------");

            for (int j = 0; j < hashTableArray.length; j++) {

                // Values assigned to the flow ids in the hashtable are pushed to an output file.
                fileStream.println("Hash value at "+ j + " : " + hashTableArray[j] + ", ");
            }

            // closing th filestream
            fileStream.close();

        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        // Number of flows used as per the given parameter values and the randomly generated integers
        System.out.println("Total Flows Used : " + flow_Count);
        System.out.println("--------------------");

        for (int j = 0; j < hashTableArray.length; j++) {
            // Values assigned to the flow ids in the hashtable
            System.out.println("Hash value at "+ j + " : " + hashTableArray[j] + ", ");
        }
    }

    // insert in Hash table
    public static void insert_MH(int flow_ID, int[] randomIntArr, int number_Of_Flows, int[] hashTableArray) {

        int index_position, normalize;

        for (int i = 0; i < randomIntArr.length; i++) {

            // Evaluating the index value for each flowID as per the number of Flows by normalizing
            normalize = (flow_ID ^ randomIntArr[i]);
            index_position = normalize % number_Of_Flows;

            // Insert into the hash table array if there is no value int it.
            if (hashTableArray[index_position] == 0) {

                // assign the flow id to the hashtable for the storing its value.
                hashTableArray[index_position] = flow_ID;
                flow_Count++;

                // stop checking the other hash functions once an empty value is found in the hashtable array
                break;
            }
        }

    }

}
