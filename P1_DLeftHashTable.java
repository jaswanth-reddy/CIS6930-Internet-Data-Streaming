import java.io.File;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.PrintStream;

public class P1_DLeftHashTable {
    int number_Of_Entries;
    int number_Of_Flows;
    int number_Of_Segments;

    static int Flow_Count;

    public static void main(String[] args) {

        P1_DLeftHashTable DF = new P1_DLeftHashTable();

        DF.number_Of_Entries = 1000;
        DF.number_Of_Flows = 1000;

        // given for each of the segment it has 250 entries for tables
        DF.number_Of_Segments = 4;

        // Array of hash functions defined using the number of segments
        int [] hashFunctions = new int[DF.number_Of_Segments];

        // array to store hash table entries, by default, it has '0' for all the index positions
        int[] hashTableArray = new int[DF.number_Of_Entries];



        DF.constructHashTable(DF.number_Of_Entries, DF.number_Of_Flows, DF.number_Of_Segments, hashFunctions, hashTableArray);
    }


    private static void constructHashTable(int numberOfEntries, int numberOfFlows, int numberOfSegments, int[] randomIntArr, int[] hashTableArray) {

        // initializing randomIntArr with "number_Of_Segments" of random integers
        for (int i = 0; i < numberOfSegments; i++) {

            // Math.random() - generates a random Integer within the given range 1 - 1000000000
            randomIntArr[i] = (int) (Math.random() * Math.pow(10, 9));
        }

        // Iterate over all the entries possible to push to the hash table using DLeft
        for (int i = 0; i < numberOfEntries; i++) {

            // generate random flow id to be inserted to the hashtable using DLeft
            int flowID = (int) (Math.random() * 10000);

            // checking if the flow id can be inserted to the hash table by passing ID and parameters necessary
            checkinsert_DF(flowID, randomIntArr, numberOfFlows, hashTableArray);
        }

        // Pushing the values array formed based on the hash technique used to a file
        try {
            PrintStream fileStream = new PrintStream(new File("P1_DLeftHash_Output.txt"));

            fileStream.println("Total Flows Used : " + Flow_Count);
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

        // Printing the values to console
        System.out.println("Total flows Used: " + Flow_Count);

        for (int l = 0; l < hashTableArray.length; l++) {

            // Values assigned to the flow ids in the hashtable are pushed to an output file.
            System.out.println("Hash value at "+ l + " : " + hashTableArray[l] + ", ");
        }

    }

    // insert element in hash table
    public static void checkinsert_DF(int flowID, int[] randomIntArr, int flows, int[] hashTableArray) {

        int index_position, normalize;

        for (int i = 0; i < randomIntArr.length; i++) {

            // Evaluating the index value for each flowID as per the number of Flows per each segment by normalizing
            normalize =  flowID ^ randomIntArr[i] ;
            index_position = ( normalize % flows / 4 ) + ( flows / 4 * i );

            // By checking if there is any empty position in the hash table by checking if it has any default value '0'
            if (hashTableArray[index_position] == 0) {

                // assign the flow id to the hashtable for the storing its value.
                hashTableArray[index_position] = flowID;

                Flow_Count++;
                break;
            }
        }
    }
}
