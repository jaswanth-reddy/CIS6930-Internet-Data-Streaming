package IDS_3;

import java.io.BufferedReader;
import java.io.FileReader;

public class P3_SketchHelper {

    // Parsing the Input file to perform the Sketches
    public BufferedReader parseSketchInputFile(int[] HashArray, int[][] CountMinFilterCounter) {

        String k;
        int idx=0;

        try (BufferedReader br = new BufferedReader(new FileReader("/Users/jaswanth/Desktop/Internet-Data-Streaming-main/IDS_3/project3input.txt"))) {

            // Number of flowHandlers for the Demo
            int no_of_flowHandlers = Integer.parseInt(br.readLine());
            FlowHandler[] flowHandlers = new FlowHandler[no_of_flowHandlers];

            while((k = br.readLine()) != null) {

                idx= idx + 1;

                String[] current = k.split("\\s+");
                flowHandlers[idx] = new FlowHandler(current[0], current[1]);

            }

            // increase the counter for the flowHandlers
            increaseCounter(flowHandlers, CountMinFilterCounter, HashArray);

            // maintain the estimated counter values for each of the flowHandler

            return br;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Using Integer Hash Function on
    public int parseSourceAddress(String input) {

        int SourceAddress = input.hashCode() % Integer.MAX_VALUE;

        SourceAddress = ~SourceAddress + (SourceAddress << 15);
        SourceAddress = SourceAddress ^ (SourceAddress >>> 12);
        SourceAddress = SourceAddress + (SourceAddress << 2);
        SourceAddress = SourceAddress ^ (SourceAddress >>> 4);
        SourceAddress = SourceAddress * 2057;
        SourceAddress = SourceAddress ^ (SourceAddress >>> 16);
        return SourceAddress;
    }

    public void increaseCounter(FlowHandler[] flowHandlers, int[][] CountMinFilterCounter, int[] HashArray){

        for(int k=0;k<flowHandlers.length;k++) {

            int flowHandlersize = Integer.parseInt(flowHandlers[k].getPktSize());
            for(int i=0; i<flowHandlersize; i++) {
                //running algorithm for all individual packets
                for(int j=0; j<k; j++) {

                    int normalize = Math.abs(flowHandlers[i].getFlowHandlerId().hashCode()) ^ HashArray[j];
                    int index = normalize % 3000;

                    CountMinFilterCounter[j][index] = CountMinFilterCounter[j][index] + 1;
                }
            }
        }
    }

}
