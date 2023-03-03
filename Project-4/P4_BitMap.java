import java.util.Random;

public class P4_BitMap {

    static int[] Flows;
    static int[] BitMap;
    static int RandomValue;
    static int BitMap_Size;

    P4_BitMap(){
        Flows = new int[]{100, 1000, 10000, 100000, 1000000};
        RandomValue = generateRandomNumbers();
    }

    public static void main(String[] args) {

        P4_BitMap BM = new P4_BitMap();
        BitMap_Size = Integer.parseInt(args[0]);

        BM.generateFlowElements(Flows, BitMap_Size);
    }

    // generate random number
    public static int generateRandomNumbers(){
        int Max = 1000000009;
        int Min = 1;
        int temp = (Max - Min + 1 ) + Min;

        return (int)Math.floor( Math.random() * temp );
    }

    // record the bitmap for HLL algorithm
    public static void bitMapRecording(int[] BitMap, int element_Count,int RandomValue, int BitMap_Size){

        for (int count = 0; count < element_Count; count++) {

            int temp_Random = generateRandomNumbers();
            int hashValue = Math.abs(Integer.valueOf(temp_Random * RandomValue).hashCode());
            int hash_Index = hashValue % BitMap_Size;

            if (BitMap[hash_Index] == 0)
            {
                BitMap[hash_Index] = 1;
            }
        }
    }

    // Estimate the error value
    public static void estimateFlowSize(int element_Count, int BitMap_Size){

        int numbOfZeros = 0;
        double EstimatedFlow_Size;
        double FractionOfZeros;

        for(int index = 0; index < BitMap_Size; index++) {

            if (BitMap[index] == 0)
                numbOfZeros = numbOfZeros + 1;
        }

        // approximate flow size =  -BitMap_Size * ln(fraction of elements that are zeros)
        // if(numbOfZeros == 0)
        //    numbOfZeros = 1;

        FractionOfZeros = (double)numbOfZeros / BitMap_Size;
        EstimatedFlow_Size = -BitMap_Size * Math.log(FractionOfZeros);

        System.out.println(element_Count + "                 "+ EstimatedFlow_Size);
    }

    public static void generateFlowElements(int[] Flows, int BitMap_Size){

        System.out.println("Element_Count\t\tEstimatedFlow_Size");
        for(int element_Count : Flows){

            // Initialize BitMap with '0' for the user given input size
            BitMap = new int[BitMap_Size];

            bitMapRecording(BitMap, element_Count, RandomValue, BitMap_Size);

            estimateFlowSize(element_Count ,BitMap_Size);
        }

    }
}
