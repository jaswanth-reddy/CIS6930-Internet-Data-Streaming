package IDS_3;

import java.io.File;
import java.io.PrintStream;
import java.util.Random;

public class P3_ActiveCounter {

    public void activeCounterSketch(int ActiveCounterSize, int Counter_number) {

        int counter_num = 0, counter_exp = 0;

        // Increase of the flow and when overflown, increase the exponent and increase counter num probabilistically
        increaseActiveCounter(ActiveCounterSize, counter_num, counter_exp, Counter_number);

        // Compute Active Counter value
        int counterValue = computeDecimalValue(counter_num, counter_exp);

        // publish output to file and console
        generateFileOutput(counterValue);

    }

    // Computer the Decimal value given the number and exponent
    public int computeDecimalValue(int counter_num, int counter_exp) {

        return counter_num * (int)Math.pow(2, counter_exp);
    }

    public static void increaseActiveCounter(int AC_Size, int counter_num, int counter_exp, int Counter_number) {

        //Trying to increase the Active counter 1,000,000 times
        for(int counter = 0; counter < AC_Size; counter++) {

            int prob = new Random().nextInt((int)Math.pow(2, counter_exp));

            // ncreasing counter_num by a probability of 1/(2 ^ counter_exp)
            if(prob == 0)
                counter_num++;

            String binaryNumberString = convertToBinary(counter_num);

            // when 'counter_num' is overflown => right shift 'counter_num' by 1 bit and increment 'counter_exp'
            if(binaryNumberString.length() > Counter_number) {

                // increament the counter_exp
                counter_exp = counter_exp + 1;

                // Right shift the counter_num
                counter_num = counter_num >> 1;
            }
        }
    }

    // Function used to convert the number value to Binary by dividing by 2
    public static String convertToBinary(int number) {

        String result = "";
        while(number > 0) {
            if(number % 2 == 0) result = '0'+ result;
            else
                result = '1' + result;

            // update the number by dividing with 2
            number = number / 2;
        }
        return (result);
    }

    // Function to push the output to console and file
    public static void generateFileOutput(int result){

        try {
            PrintStream fileStream = new PrintStream(new File("P3_ActiveCounterOutputFile.txt"));

            // Outputting the values to a file using PrintStream
            fileStream.println("final value of the active counter in decimal: "+ result);

            // Printing values to console
            System.out.println("final value of the active counter in decimal: "+ result);

            // closing th filestream
            fileStream.close();
        }
        catch (Exception e) {
            System.out.println("Error occurred.");
            e.printStackTrace();
        }

    }

}
