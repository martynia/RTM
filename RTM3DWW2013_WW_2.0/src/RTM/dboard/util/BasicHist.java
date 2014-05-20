package RTM.dboard.util;

/** A simple histogram class to count the frequency of
 * values of a parameter of interest.
 * from http://www.particle.kth.se/~lindsey/JavaCourse/Book/Part1/Tech/Chapter03/histogram.html
 **/
public class BasicHist {

    int[] bins;
    int numBins;
    int underflows;
    int overflows;
    double lo;
    double hi;
    double range;

    /** The constructor will create an array of a given
     * number of bins. The range of the histogram given
     * by the upper and lower limt values.
     **/
    public BasicHist(int numBins, double lo, double hi) {
        this.numBins = numBins;

        bins = new int[numBins];

        this.lo = lo;
        this.hi = hi;
        range = hi - lo;
    }

    /**
     * Add an entry to a bin.
     * Include if value is in the range
     * lo <= x < hi
     **/
    public void add(double x) {
        if (x >= hi) {
            overflows++;
        } else if (x < lo) {
            underflows++;
        } else {
            double val = x - lo;

            // Casting to int will round off to lower
            // integer value.
            int bin = (int) (numBins * (val / range));

            // Increment the corresponding bin.
            bins[bin]++;
        }
    }

    /** Clear the histogram bins. **/
    public void clear() {
        for (int i = 0; i < numBins; i++) {
            bins[i] = 0;
            overflows = 0;
            underflows = 0;
        }
    }

    /** Provide access to the bin values. **/
    public int getValue(int bin) {
        if (bin < 0) {
            return underflows;
        } else if (bin >= numBins) {
            return overflows;
        } else {
            return bins[bin];
        }
    }
}
