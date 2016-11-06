// sample files need to be same environment, same pitch, same shape
// input file needs to be same (pitch) and shape as sample files
// gets frequency vs amplitude fingerprint
// next steps: make it talking instead of singing, plus above

// can i just use StdAudio to read in wav and alter partials and spit out wav?
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdDraw;
import java.lang.Math;

public class RaiseOctave {
    private static final int HI_FREQ      = 30000;
    private static final int SKIP         = 5;
    private static final double THRESHOLD = 0.6;
    private static final int RANGE        = 70; 
    private static final int PARTIALS     = 15; // # of partials to delete
    private static final int SAMPLE       = 100; // assumes 100th frame best
    
    // gets the pitch, rounded to nearest int
    public static double getPitch(String file) {
        
        // parse into freq vs amp array
        int duration = 0;
        double[] amp = new double[HI_FREQ];
        In in = new In(file);
        for (int i = 0; i < SKIP; i++) in.readLine(); // skip over first 5 lines
        double[] freqs = new double[HI_FREQ];
        int c = 0;
        while (!in.isEmpty()) { // for (int i = 0; i < DURATION; i++)
            String line = in.readLine();
            String[] fields = line.split(" ");
            int max = Integer.parseInt(fields[1])*3+2;

            for (int x = 2; x < max; x+=3) {
                // makes tones that are within 10Hz equal (truncate) // *** delete
                double num = Double.parseDouble(fields[x+1]);
                if (SAMPLE == c) freqs[x/3] = num;
                int d = (int)Math.round(num);
                amp[d] += Double.parseDouble(fields[x+2]);
            }
            duration++;
            c++;
        }
        
        // average amp over time
        for (int i = 0; i < amp.length; i++) amp[i] /= duration;
        
        // normalize amp
        double max = 0;
        for (int i = 0; i < amp.length; i++) if (amp[i] > max) max = amp[i];
        for (int i = 0; i < amp.length; i++) amp[i] /= max;
        
        // find lowest pitch
        int pitch = 0;
        for (int i = 0; i < amp.length; i++) {
            if (amp[i] > THRESHOLD) {
                pitch = i;
                break;
            }
        }
        System.out.println("Integral pitch: " + pitch);
                
        // find closest decimal in freqs to p
        double champ = 0;
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < freqs.length; i++) {
            double delta = Math.abs(pitch - freqs[i]);
            if (delta < min) {
                min = delta;
                champ = freqs[i];
            }
        }
                    
        System.out.println("Decimal pitch: " + champ);
        return champ;
    }
    
    // delete partials in N*pitch but not in N*2*pitch
    public static void filter(String input, String output, double p) {

        // build avoid array
        double[] avoid = new double[PARTIALS];
        for (int i = 0; i < avoid.length; i++) {
            avoid[i] = (i*2+1)*p;
        }
        
        // edit file
        In in = new In(input);
        Out out = new Out(output);
        for (int i = 0; i < SKIP; i++) out.println(in.readLine()); // skip over first 5 lines
        
        while (!in.isEmpty()) {
            String line = in.readLine();
            String[] fields = line.split(" ");
            int max = Integer.parseInt(fields[1])*3+2;
            // find number of partials
            int partials = 0;
            for (int x = 2; x < max; x+=3) {
                boolean legal = true;
                double num = Double.parseDouble(fields[x+1]);
                int d = (int)Math.round(num);
                for (int i = 0; i < avoid.length; i++) 
                    if ((d > avoid[i]-RANGE) && (d < avoid[i]+RANGE)) legal = false;
                if (legal) partials++;
            }
            
            // print
            out.print(fields[0] + " " + partials);
            for (int x = 2; x < max; x+=3) {
                boolean legal = true;
                double num = Double.parseDouble(fields[x+1]);
                int d = (int)Math.round(num);
                for (int i = 0; i < avoid.length; i++) 
                    if ((d > avoid[i]-RANGE) && (d < avoid[i]+RANGE)) legal = false;
                // make louder
                double ampUp = 0;
                if ((d > p*2-RANGE) && (d < p*2+RANGE)) ampUp = Double.parseDouble(fields[x+2])*2;
                else ampUp = Double.parseDouble(fields[x+2]);
                if (legal) out.print(" " + fields[x] + " " + fields[x+1] + " " + ampUp);
            }
            out.println();
        }
    }
    
    public static void main(String[] args) {
        double x = getPitch(args[0]);
        filter(args[0], args[1], x);
    }
}