import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class P4_HLL {

    static int[] SpreadValueArray;
    static int[] HLLBitMap;
    static long RandomValue;
    static int BitMap_Size;
    static double Probability;
    static Random random;

    P4_HLL(){
        SpreadValueArray = new int[]{1000, 10000, 100000, 1000000};
        random = new Random();
        RandomValue = generateRandomNumbers();
    }

    public static void main(String[] args) {

        P4_HLL HLL = new P4_HLL();
        HLL.BitMap_Size = Integer.parseInt(args[0]);

        HLL.HLL_Recording(SpreadValueArray);
    }

    public static void HLL_Recording(int[] SpreadValueArray){

        System.out.println("Element_Count\t\tEstimatedFlowSpread_Size");

        for(int element_Count : SpreadValueArray){

            HLLBitMap = new int[BitMap_Size];
            Set<Long> randomValueSet = obtainDistinctElements(element_Count);

            List<Long> distinctElementList = randomValueSet.stream().toList();
            HLL_Insert(distinctElementList);
            estimateFlowSpread(element_Count, BitMap_Size);

        }
    }

    // Insert the elements as per HLL algorithm
    public static void HLL_Insert(List<Long> distinctElementList){

        for(Long elementInSet : distinctElementList)
        {
            checkLeadingZeros(elementInSet);
        }
    }

    // finding the number of leading zeros
    public static void checkLeadingZeros(Long elementInSet){

        String temp = new String();
        temp = String.valueOf(Math.abs(elementInSet));
        long hashValue = temp.hashCode();
        int numberOfLeadingZeros = (Long.numberOfLeadingZeros(hashValue) % 32) + 1;

        int hashIndex = (int)Math.abs(hashValue % BitMap_Size);

        // check the max value between the Geometric Hash and the HLLBitMap Hash value and update it.
        if(HLLBitMap[hashIndex] < numberOfLeadingZeros)
        {
            HLLBitMap[hashIndex] = numberOfLeadingZeros;
        }
    }

    // Alpha * BitMap_Size^2(1/2^HLLBitMap[Hash(Element)])^-1
    public static void estimateFlowSpread(int element_Count, int BitMap_Size){

        double temp = 0;
        for(int bitInHLLBitMap : HLLBitMap)
        {
            double denominator = Math.pow(2, bitInHLLBitMap);
            temp = temp + (1 / denominator);
        }

        double alpha = (0.7213 / (1 + 1.079 / BitMap_Size));
        double EstimatedFlowSpread = alpha * BitMap_Size * BitMap_Size * (1/temp);

        System.out.println(element_Count + "                 "+ EstimatedFlowSpread);
    }

    public static Set<Long> obtainDistinctElements(int element_Count){
        Set<Long> randomValueSet = new HashSet<>();
        while(randomValueSet.size() != BitMap_Size)
        {
            randomValueSet.add(generateRandomNumbers());
        }
        return randomValueSet;
    }

    public static long generateRandomNumbers()
    {
        return random.nextLong(1, Long.MAX_VALUE);
    }


}
