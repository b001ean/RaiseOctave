// % java-algs4 VoiceMatch sample1.txt sample2.txt input.txt cutoffHz
// sample files need to be same environment, same pitch, same shape
// input file needs to be same (pitch) and shape as sample files
// gets frequency vs amplitude fingerprint
// next steps: make it talking instead of singing, plus above
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import java.lang.Math;

public class VoiceMatch {
    private static final int GROUP      = 500; // instead of comparing by Hz, compare by 500Hz. requires (almost) same pitch tho
    private static final int HI_FREQ    = 30000/GROUP;
    private static final int SKIP       = 5;
    private static final double ALPHA   = 0.2;
    private static final double LOW_AMP = 0.3;
    
    public static double[] freqArray(String file, int cutoff) {
        // parse into freq vs amp array
        cutoff /= GROUP; // changes based on which notes are sung (calculate from lowest note)
        int duration = 0;
        double[] amp = new double[HI_FREQ];
        In in = new In(file);
        for (int i = 0; i < SKIP; i++) in.readLine(); // skip over first 5 lines
        while (!in.isEmpty()) { // for (int i = 0; i < DURATION; i++)
            String line = in.readLine();
            String[] fields = line.split(" ");
            int max = Integer.parseInt(fields[1])*3+2;
            for (int x = 2; x < max; x+=3) {
                // makes tones that are within 10Hz equal (truncate) // *** delete
                double divided = Double.parseDouble(fields[x+1])/GROUP;
                int d = (int)Math.round(divided);
                amp[d] += Double.parseDouble(fields[x+2]);
            }
            duration++;
        }
        
        // average amp over time
        for (int i = 0; i < amp.length; i++) amp[i] /= duration;
        
        // delete anything less than cutoff
        for (int i = 0; i < cutoff; i++) amp[i] = 0;
        
        // normalize amp
        double max = 0;
        for (int i = 0; i < amp.length; i++) if (amp[i] > max) max = amp[i];
        for (int i = 0; i < amp.length; i++) amp[i] /= max;
        
        // ambient noise OK because will produce equal errors in both.
        return amp;
    }
    
//    public static int getPitch(double[]
//    // detect different pitches
//        int pitch1, pitch2, pitchU = 0;
//        for (int i = 0; i < base1.length; i++) {
//            if (base1[i] > LOW_AMP) {
//                pitch1 = i;
//                break;
//            }
//        }
//        for (int i = 0; i < base2.length; i++) {
//            if (base2[i] > LOW_AMP) {
//                pitch2 = i;
//                break;
//            }
//        }
//        for (int i = 0; i < unknown.length; i++) {
//            if (unknown[i] > LOW_AMP) {
//                pitchU = i;
//                break;
//            }
//        }
    
    public static void main(String[] args) {
        double[] base1 = freqArray(args[0], Integer.parseInt(args[3]));
        double[] base2 = freqArray(args[1], Integer.parseInt(args[3]));
        double[] unknown = freqArray(args[2], Integer.parseInt(args[3]));
        
        // calculate least squares
        double sum1 = 0;
        double sum2 = 0;
        for (int i = 0; i < base1.length; i++) {
            // if big difference, make it worth more.
            sum1 += (base1[i] - unknown[i])*(base1[i] - unknown[i]);
            sum2 += (base2[i] - unknown[i])*(base2[i] - unknown[i]);
        }
        System.out.println(sum1 + " " + sum2);
        if ((sum1/sum2 > (1-ALPHA)) && (sum1/sum2 < (1+ALPHA))) System.out.println("Insignificant");
        if (sum1 < sum2) System.out.println(args[0] + " is most similar to " + args[2]);
        else System.out.println(args[1] + " is most similar to " + args[2]);
    }
}