import java.io.*;
import java.util.*;

public class P5_bSkt_HLL {

    static BufferedReader RawProject5_Input;

    static String FlowID;
    static int TrueFlowSize;

    static int Flow_Count;
    static int HLLEstimators;
    static int FiveBitReg_Count;
    static int EstimatorPerFlow;
    static int positiveresponse;
    static int negativeresponse;

    static long temp_Zeros;
    static int LeadinZeros;
    static long randomlong;
    static double Min_EstimatedSpreadSize;

    static long[] RawbSktHashArray;
    static int[][] BSkt;

    static Map<String, Double> EstimatedSpreadSizes;
    static Map<String, Integer> TrueSpreadSizes;
    static Set<Long> randomElementSet;

    static PriorityQueue<Flow_Info> CountFlows_MoreThan25;
    static List<Flow_Info> DescendingOrderFlowSpread;

    static Random random;

    P5_bSkt_HLL() throws IOException {

        HLLEstimators = 4000;
        FiveBitReg_Count = 128;
        EstimatorPerFlow = 3;

        temp_Zeros = 0;
        LeadinZeros = 0;
        Min_EstimatedSpreadSize = 0.0;

        positiveresponse = 1;
        negativeresponse = -1;

        random = new Random();
        TrueSpreadSizes = new HashMap<String, Integer>();
        EstimatedSpreadSizes = new HashMap<String, Double>();
        DescendingOrderFlowSpread = new ArrayList<Flow_Info>();

        randomlong = generateRandomNumbers();
        RawbSktHashArray = new long[EstimatorPerFlow];
        BSkt = new int[HLLEstimators][FiveBitReg_Count];

        RawProject5_Input = new BufferedReader(new FileReader("/Users/jaswanth/Desktop/IDS-Jaswanth/Project4/IDS4/src/project5input.txt"));

        //Defining a Priority Queue using a comparator by checking the values passed to it.
        CountFlows_MoreThan25 = new PriorityQueue<Flow_Info>(
                (FlowX, FlowY) ->
                {
                    if(FlowX.EstimateSpread > FlowY.EstimateSpread)
                        return positiveresponse;
                    else return negativeresponse;
                });

        // Overriding the Collecctions.sort() based on the requirement
        Collections.sort(DescendingOrderFlowSpread,(FlowX, FlowY) ->{

            // Comapre the Spread value and take the desired one
            if(FlowY.EstimateSpread > FlowX.EstimateSpread)
                return  positiveresponse;
            else return  negativeresponse;
        });

    }

    public static void main(String[] args) throws IOException
    {
        P5_bSkt_HLL BSK = new P5_bSkt_HLL();

        // init bSktHash Array with random values
        BSK.init_bSktHashArray();

        // read input from file and fetch the flow count values
        BSK.fetch_VBitMapInputDetails();

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
                FlowID = line_arr[0];
                TrueFlowSize = Integer.parseInt(line_arr[1]);

                // Insert the values into the input map
                TrueSpreadSizes.put(FlowID, TrueFlowSize);

                // record the VBitMap values
                Record_VBitMapArray(FlowID);
            }
            querying_flow();
        }
        catch(Exception e){
        }
    }

    public void querying_flow()
    {
        // Iterating over all the Flows for calculating the estimated sizes
        for(Map.Entry<String, Integer> FlowMapSize : TrueSpreadSizes.entrySet())
        {
            // Assigning the highest bound value and update it on comparision
            Min_EstimatedSpreadSize = Double.MAX_VALUE;

            // Iterating over all the Hashes of Estimators
            for (int hllEst_Cnt = 0; hllEst_Cnt < EstimatorPerFlow; hllEst_Cnt++)
            {
                int temp = Math.abs(Long.valueOf(FlowMapSize.getKey().hashCode() ^ RawbSktHashArray[hllEst_Cnt]).hashCode());
                int hash_Index = temp % HLLEstimators;
                int[] hashValues = BSkt[hash_Index];

                // Estimate the Flow spread value for each of the flow
                estimateFlowSpread(FiveBitReg_Count ,hashValues);
            }
        }
    }

    // Formula: Alpha * BitMap_Size^2(1/2^HLLBitMap[Hash(Element)])^-1
    public static void estimateFlowSpread(int BitMap_Size, int[] HLLBitMap){

        double temp = 0;
        for(int bitInHLLBitMap : HLLBitMap)
        {
            double denominator = Math.pow(2, bitInHLLBitMap);
            temp = temp + (1 / denominator);
        }

        double alpha = (0.7213 / (1 + 1.079 / BitMap_Size));
        Min_EstimatedSpreadSize = alpha * BitMap_Size * BitMap_Size * (1/temp);

        // Printing the flows as per the given requirements
        UpdatePriorityQueue();
    }

    // Constructing an object to store and sort based on requirements thus using Priority Queues
    static class Flow_Info
    {
        String FlowID;
        double EstimateSpread;

        // constructor to initialize the variables
        Flow_Info(String fID, double Est_Spread)
        {
            FlowID = fID;
            EstimateSpread = EstimateSpread;
        }
    }



    // Display top 25 flows
    public static void UpdatePriorityQueue()
    {
        //Iterating over all the entries
        for(Map.Entry<String, Double> spreadSize: EstimatedSpreadSizes.entrySet())
        {

            if(CountFlows_MoreThan25.peek().EstimateSpread < spreadSize.getValue())
            {
                // delete the flow that is not satisfying thee conditions
                CountFlows_MoreThan25.poll();

                Flow_Info temp = new Flow_Info(spreadSize.getKey(), spreadSize.getValue());
                // adding the satisfying flow to the priority queue.
                CountFlows_MoreThan25.add(temp);
            }
            //checking if it is satisfying to be in the top 25?
            else if (CountFlows_MoreThan25.size() < 25){
                Flow_Info temp = new Flow_Info(spreadSize.getKey(), spreadSize.getValue());
                // adding the satisfying flow to the priority queue.
                CountFlows_MoreThan25.add(temp);
            }
        }
        sortFlowsDescending();
    }

    // Using collections to reverse the flows based on the spread values obtained
    public static void sortFlowsDescending()
    {
        while (!CountFlows_MoreThan25.isEmpty())
        {
           // add the last flow to the array list collection
           DescendingOrderFlowSpread.add(CountFlows_MoreThan25.peek());

           // remove the last element from the priority queue
           CountFlows_MoreThan25.poll();
        }

        printSortedFlowArrayList();
    }

    // Function to sort the ArrayList using the comparator
    public static void printSortedFlowArrayList()
    {
        System.out.println("Flow Address       TrueSpread         EstimatedSpread");

        //Iterating over all the Flows to print to the Console
        for(Flow_Info flow: DescendingOrderFlowSpread)
        {
            System.out.println(flow.FlowID + "         "+ TrueSpreadSizes.get(flow.FlowID) + "             " + flow.EstimateSpread);
        }
    }

    public void Record_VBitMapArray(String Source)
    {
        Set<Long> randomElementSet = obtainDistinctElements(TrueFlowSize);
        List<Long> elementsList = randomElementSet.stream().toList();

        // Iterating over all the elements in the list
        for(int hllEst_Cnt = 0; hllEst_Cnt < EstimatorPerFlow; hllEst_Cnt++)
        {
            long temp1 = FlowID.hashCode() ^ RawbSktHashArray[hllEst_Cnt];
            int HashVal_Est = (int) Math.abs(Long.valueOf(temp1).hashCode()) % HLLEstimators;

            for(Long elementinList : elementsList)
            {
                temp_Zeros = Math.abs(Long.valueOf(elementinList ^ randomlong).hashCode());
                LeadinZeros = Long.numberOfLeadingZeros(temp_Zeros) % 32;

                int elementHash_Index = (int) (temp_Zeros % FiveBitReg_Count);

                BSkt[HashVal_Est][elementHash_Index] = Math.max(LeadinZeros,BSkt[HashVal_Est][elementHash_Index]);
            }
        }
    }

    public static Set<Long> obtainDistinctElements(int Physical_BitCount){

        while(randomElementSet.size() != Physical_BitCount)
        {
            randomElementSet.add(generateRandomNumbers());
        }
        return randomElementSet;
    }


    public void init_bSktHashArray()
    {
        int hash_cntIndex = 0;
        while(hash_cntIndex < EstimatorPerFlow)
        {
            RawbSktHashArray[hash_cntIndex] = generateRandomNumbers();
        }
    }

    // generate the random numbers
    public static long generateRandomNumbers(){
        return random.nextLong(1, Long.MAX_VALUE);
    }

}
