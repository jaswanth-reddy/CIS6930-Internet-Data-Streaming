import java.io.*;
import java.util.*;

public class P5_VBitMap {

    // Declare the variables to be used to implement the VBitMap
    static int Flow_Count;
    static int Physical_BitCount;
    static int Virtual_BitCount;
    static String Source;
    static int FlowID;
    static int PnumbOfZeros;
    static int VnumbOfZeros;
    static double EstimatedFlowSpread;

    static int [] VBitMapHashArray;
    static int [] PBitMapHashArray;

    static BufferedReader RawProject5_Input;
    static Map<String, Integer> Project5_Input;
    static Set<Integer> randomElementSet;
    static BufferedWriter forGraph;


    P5_VBitMap() throws IOException {
        // Initialize the variables as per the document for problem statement
        Physical_BitCount = 500000;
        Virtual_BitCount = 500;
        PnumbOfZeros = 0;
        VnumbOfZeros = 0;
        EstimatedFlowSpread = 0.0;

        Project5_Input = new HashMap<String, Integer>();
        randomElementSet = new HashSet<Integer>();

        RawProject5_Input = new BufferedReader(new FileReader("/Users/jaswanth/Desktop/IDS-Jaswanth/Project4/IDS4/src/project5input.txt"));

        //writing the output
        forGraph = new BufferedWriter(new FileWriter("P5_VBitMapOutput.xls"));
        forGraph.write("N_f" + "\t" + "Nf^" + "\n");
    }

    public static void main(String[] args) throws IOException
    {
        P5_VBitMap VB = new P5_VBitMap();

        // initialize the BitMap Array's with the random values
        VB.init_BitMapHashArray();

        // read input from file and fetch the flow count values
        VB.fetch_VBitMapInputDetails();

        // Estimate true sizes of Elements
        VB.estimateTrueSizes();

    }

    // function to estimate the true size of each element in the input file
    public void estimateTrueSizes() throws IOException {
        // Evaluate VB from Physical Array
        eval_VB();

        // Evaluate VF from Physical Array
        eval_VF();
    }


    public void eval_VF() throws IOException {

        // Iterating over all the Map Entries corresponding to individual flows
        for (Map.Entry<String, Integer> each_Flow: Project5_Input.entrySet())
        {
            int temp_VHash = 0, map_Source = each_Flow.getKey().hashCode(), VHash;

            // Iterate over all the index elements to find the number of Zeros to calculate Vf
            for(int VArray_index = 0; VArray_index < Virtual_BitCount; VArray_index++)
            {
                temp_VHash = Math.abs(Integer.valueOf(VBitMapHashArray[VArray_index] ^ map_Source).hashCode());
                VHash = temp_VHash % Physical_BitCount;

                if (PBitMapHashArray[VHash] == 0)
                    VnumbOfZeros = VnumbOfZeros + 1;
            }

            // to avoid the base case when there are no 0 and entire array filled with all 1's
            if(VnumbOfZeros == 0)
                VnumbOfZeros = 1 ;

            generateOutput(each_Flow);
        }
    }

    // Display the output
    public void generateOutput(Map.Entry<String, Integer> Flow) throws IOException {
        double temp1 = (double)PnumbOfZeros / Physical_BitCount;
        double temp1_Log = Math.log(temp1);

        double temp2 = (double)VnumbOfZeros / Virtual_BitCount;
        double temp2_Log = Math.log(temp1);
        EstimatedFlowSpread = Virtual_BitCount * temp1_Log - Virtual_BitCount * temp2_Log;

        if(EstimatedFlowSpread < 0) EstimatedFlowSpread = 0;

        System.out.println(Flow.getKey() + "     " + Flow.getValue() + ": Estimated Flow Spread - " + EstimatedFlowSpread);

        // For Graph code
        forGraph.write(Flow.getValue() + "\t" + EstimatedFlowSpread + "\n");
    }

    //counting the number of zeros for calculation of VB from the Physical Array
    public void eval_VB()
    {
        // Iterate over all the index elements to find the number of Zeros
        for(int PArray_index = 0; PArray_index < Physical_BitCount; PArray_index++)
        {
            if (PBitMapHashArray[PArray_index] == 0)
                PnumbOfZeros = PnumbOfZeros + 1;
        }

        // to avoid the base case when there are no 0 and entire array filled with all 1's
        if(PnumbOfZeros == 0)
            PnumbOfZeros = 1 ;
    }


    public static Set<Integer> obtainDistinctElements(int Physical_BitCount){

        while(randomElementSet.size() != Physical_BitCount)
        {
            randomElementSet.add(generateRandomNumbers());
        }
        return randomElementSet;
    }

    // initialize the BitMap Array's with the random values
    public void init_BitMapHashArray() {
        int arr_index = 0;

        // creating arrays for Virtual and Physical arrays with the size provided
        VBitMapHashArray = new int[Virtual_BitCount];
        PBitMapHashArray = new int[Physical_BitCount];

        // assigning the VBitMap array with the random value for respective index
        while(arr_index < Virtual_BitCount)
        {
            VBitMapHashArray[arr_index] = generateRandomNumbers();

            // incrementing the array index by 1
            arr_index = arr_index + 1;
        }
    }

    // generate the random numbers
    public static int generateRandomNumbers(){
        int Max = 1000000009;
        int Min = 1;
        int temp = (Max - Min + 1 ) + Min;

        return (int)Math.floor( Math.random() * temp );
    }

    // Open the Input file and fetch the details
    public void fetch_VBitMapInputDetails() {
        // read input from file
        try {

            // Fetch the Flow Count value from the input file
            Flow_Count = Integer.parseInt(RawProject5_Input.readLine());

            // Extract the source and id details of the flow and save as a Map
            transform_RawInputFile(RawProject5_Input);
        }
        catch (Exception e) {
            e.printStackTrace();
        };
    }

    public void transform_RawInputFile(BufferedReader RawProject5_Input)
    {
        String input_Line;
        String input_regex = "\\s+";

        try
        {
            // Iterating over all the lines in the Input file
            while((input_Line = RawProject5_Input.readLine()) != null)
            {
                // split the input line using the split function and input regex for 'space' as given
                String[] line_arr = input_Line.trim().split(input_regex);

                // assign the variables to the respective positions in the Input File Map
                Source = line_arr[0];
                FlowID = Integer.parseInt(line_arr[1]);

                // Insert the values into the input map
                Project5_Input.put(Source, FlowID);

                // record the VBitMap values
                Record_VBitMapArray(Source);
            }
        }
        catch(Exception e){

        }
    }

    public void Record_VBitMapArray(String Source)
    {
        Set<Integer> randomElementSet = obtainDistinctElements(Physical_BitCount);
        List<Integer> elementsList = randomElementSet.stream().toList();

        // Iterating over all the elements in the list
        for(Integer elementinList : elementsList)
        {
            int temp_HashValue = 0;
            int V_BitMapHash = 0;
            int VBitMapHashIndex = 0;
            int Source_HashCode = Source.hashCode();

            // Evaluate the Virtual BitMap Hash Index value
            temp_HashValue = Math.abs(Integer.valueOf(elementinList).hashCode());
            V_BitMapHash = VBitMapHashArray[temp_HashValue % Virtual_BitCount];

            temp_HashValue = Math.abs(Integer.valueOf(V_BitMapHash ^ Source_HashCode).hashCode());
            VBitMapHashIndex = temp_HashValue % Physical_BitCount;

            // Updating the value of the index with 1
            PBitMapHashArray[VBitMapHashIndex] = 1;
        }
    }

}
