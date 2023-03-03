import java.io.File;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.PrintStream;

public class P1_CuckooHashTable {
    int number_Of_Flows;
    int number_Of_Entries;
    int number_Of_Hashes;
    int number_Of_Steps;

    static int flow_Count = 0;

    public static void main(String[] args) {

        P1_CuckooHashTable cH = new P1_CuckooHashTable();
        cH.number_Of_Entries = 1000;
        cH.number_Of_Flows = 1000;
        cH.number_Of_Hashes = 3;
        cH.number_Of_Steps = 2;

        // Array of hash functions defined using the number of hash functions for multiplicative hashing
        int [] hashFunctions = new int[cH.number_Of_Hashes];

        // array to store hash table entries, by default, it has '0' for all the index positions
        int[] hashTableArray = new int[cH.number_Of_Entries];

        cH.constructCuckooHashTable(cH.number_Of_Entries, cH.number_Of_Flows, cH.number_Of_Hashes, cH.number_Of_Steps, hashFunctions, hashTableArray);
    }

    public static void constructCuckooHashTable(int number_Of_Entries, int number_Of_Flows, int number_Of_Hashes,
                                                 int number_Of_Steps, int[] randomIntArr, int[] hashTableArray) {

        int counter = 0;
        while (counter < number_Of_Hashes) {

            // initializing randomIntArr with "number_Of_Hashes" of random integers
            randomIntArr[counter] = (int) ( Math.random() * Math.pow( 10, 8 ) );
            counter++;
        }


        for (int length = 0; length < number_Of_Entries; length++) {

            // generating a random flowID using th Math Library random function
            int flow_ID = (int) (Math.random() * 10000);

            // inserting an element in the Cuckoo Hash table with the specified number of cuckoo steps
            insert_CH(flow_ID, randomIntArr, number_Of_Flows, hashTableArray, number_Of_Steps);

        }

        // Number of flows used as per the given parameter values and the randomly generated integers
        System.out.println("Total flows used : " + flow_Count);

        for (int j = 0; j < hashTableArray.length; j++) {

            // Values assigned to the flow ids in the hashtable are pushed to an output file.
            System.out.println("Hash value at "+ j + " : " + hashTableArray[j] + ", ");
        }

        try {
            PrintStream fileStream = new PrintStream(new File("P1_CuckooHash_Output.txt"));

            fileStream.println("Total Flows Used : " + flow_Count);
            fileStream.println("--------------------");

            for (int j = 0; j < hashTableArray.length; j++) {

                // Values assigned to the flow ids in the hashtable are pushed to an output file.
                fileStream.println("Hash value at "+ j + " : " + hashTableArray[j] + ", ");
            }

            // closing th filestream
            fileStream.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    // insert element in hash table
    public static boolean insert_CH(int flowID, int[] randomIntArr, int flows, int[] hashTableArray, int cuckoo_Steps) {

        int index_position;

        for (int i = 0; i < randomIntArr.length; i++) {

            // Evaluating the index value for each flowID as per the number of Flows by normalizing
            index_position = ( flowID ^ randomIntArr[i] ) % flows;

            // Insert into the hash table array if there is no value int it, by default the hashTableArray has '0' in it.
            if (hashTableArray[index_position] == 0) {

                // assign the flow id to the hashtable for the storing its value.
                hashTableArray[index_position] = flowID;
                flow_Count++;

                // Unlike the Multi hash table, Cuckoo returns boolean when it finds the position to fill the hash value
                return true;
            }
        }


        for (int i = 0; i < randomIntArr.length; i++) {

            index_position = ( flowID ^ randomIntArr[i] ) % flows;

            // If there is any feasible slot available for the insertion of the flowID into the hash table
            Boolean hasAvailableSlots = changeFlowIDToAnotherSlot(flowID, cuckoo_Steps, randomIntArr, flows, hashTableArray, index_position);

            if (hasAvailableSlots == true) {

                // has empty slot in the hash table and assign the value to hashtable array
                hashTableArray[index_position] = flowID;
                flow_Count++;
                return true;
            }
        }

        // when no available slot is present to fill the flowID
        return false;
    }


    // move already existing element to another index if possible
    public static boolean changeFlowIDToAnotherSlot(int flowID, int cuckoo_Steps, int[] randomIntArr, int flows, int[] hashTableArray, int index_position) {

        // Base case when there are no cuckoo steps
        if (cuckoo_Steps == 0)
            return false;

        int flowId_Temp = hashTableArray[index_position];

        int index;

        // Iterate over all the hash values to check if there is any position available for inserting flowid
        for (int length = 0; length < randomIntArr.length; length++) {

            index = (flowId_Temp ^ randomIntArr[length]) % flows;

            // If there is any position for insertion, insert the value to the hash table array and return true.
            if (index != index_position && hashTableArray[index] == 0) {

                hashTableArray[index] = flowId_Temp;
                return true;

            }

        }

        for (int i = 0; i < randomIntArr.length; i++) {

            index = ( flowId_Temp ^ randomIntArr[i] ) % flows;

            // Given the Cuckoo steps as 2, we need to drill one level to check if there is any availability for the insertion of the FlowID
            // Recursive call with the reduction of the cuckoo step count
            Boolean recursiveCheck = changeFlowIDToAnotherSlot(flowId_Temp, cuckoo_Steps - 1, randomIntArr, flows, hashTableArray, index_position);

            if (index != index_position && recursiveCheck == true) {

                hashTableArray[index] = flowId_Temp;
                return true;

            }
        }

        return false;
    }

}
