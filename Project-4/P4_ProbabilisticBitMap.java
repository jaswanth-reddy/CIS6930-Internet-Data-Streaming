import java.util.*;

public class P4_ProbabilisticBitMap {

    static int[] Flowspread;
    static int[] BitMap;
    static int RandomValue;
    static int BitMap_Size;
    static double Probability;
    static Random random;

    P4_ProbabilisticBitMap(){
        Flowspread = new int[]{100, 1000, 10000, 100000, 1000000};
        random = new Random();
        RandomValue = generateRandomNumbers();
    }

    public static void main(String[] args) {

        P4_ProbabilisticBitMap PBM = new P4_ProbabilisticBitMap();

        BitMap_Size = Integer.parseInt(args[0]);
        Probability = Double.parseDouble(args[1]);

        PBM.generateFlowElements(Flowspread, BitMap_Size);
    }

    // generate the random numbers
    public static int generateRandomNumbers(){
        int Max = 1000000009;
        int Min = 1;
        int temp = (Max - Min + 1 ) + Min;

        return (int)Math.floor( Math.random() * temp );
    }

    public static void generateFlowElements(int[] Flowspread, int BitMap_Size){

        long Max_HashValue;
        System.out.println("Element_Count\t\tEstimatedFlow_Size");
        for(int element_Count : Flowspread){

            Max_HashValue = Long.MIN_VALUE;
            //Initialize BitMap with '0' for the user given input size
            BitMap = new int[BitMap_Size];

            probabilisticBitMapRecording(BitMap, element_Count, Max_HashValue, BitMap_Size);
        }
    }

    public static void probabilisticBitMapRecording(int[] BitMap, int element_Count, long Max_HashValue, int BitMap_Size){

        Set<Integer> randomValueSet = obtainDistinctElements(element_Count);
        List<Integer> elementToStore = randomValueSet.stream().toList();

        for(Integer elementinStore : elementToStore) {

            long temp_HashValue = Math.abs(Integer.valueOf(elementinStore ^ RandomValue).hashCode());
            Max_HashValue = Math.max(Max_HashValue, temp_HashValue);
        }

        for(int element : elementToStore){

            int temp = element * RandomValue;
            int hashIndex = Math.abs(Integer.valueOf(temp).hashCode());
            if(hashIndex < Max_HashValue * Probability) {

                if(BitMap[hashIndex % BitMap_Size] == 0)
                    BitMap[hashIndex % BitMap_Size] = 1;
            }
        }

        estimateFlowSize(element_Count ,BitMap_Size);
    }

    public static Set<Integer> obtainDistinctElements(int element_Count){
        Set<Integer> randomValueSet = new HashSet<>();
        while(randomValueSet.size() != BitMap_Size)
        {
            randomValueSet.add(generateRandomNumbers());
        }
        return randomValueSet;
    }

    // Estimate the flow size
    public static void estimateFlowSize(int element_Count, int BitMap_Size){

        int numbOfZeros = 0;
        double EstimatedFlow_Size;
        double FractionOfZeros;

        for(int index = 0; index < BitMap_Size; index++) {

            if (BitMap[index] == 0)
                numbOfZeros = numbOfZeros + 1;
        }

        // approximate flow size =  -(BitMap_Size/ Probablity) * ln(fraction of elements that are zeros)
        // if(numbOfZeros == 0)
        //    numbOfZeros = 1;

        FractionOfZeros = (double)numbOfZeros / BitMap_Size;
        EstimatedFlow_Size = -(BitMap_Size / Probability) * Math.log(FractionOfZeros);

        System.out.println(element_Count + "                 "+ EstimatedFlow_Size);
    }

}
