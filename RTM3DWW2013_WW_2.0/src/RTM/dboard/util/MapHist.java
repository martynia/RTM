package RTM.dboard.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import RTM.job.GenericJob;

/** A simple histogram class to count the frequency of
 * values of a parameter of interest.
 * from http://www.particle.kth.se/~lindsey/JavaCourse/Book/Part1/Tech/Chapter03/histogram.html
 **/
public class MapHist {

    ArrayList<Map<java.sql.Timestamp, GenericJob>> bins;
    int numBins;
    int underflows;
    int overflows;
    long lo;
    long hi;
    double range;

    /** The constructor will create an array of a given
     * number of bins. The range of the histogram given
     * by the upper and lower limit values.
     **/
    public MapHist(int numBins, long lo, long hi) {
        this.numBins = numBins;

        //bins = new int[numBins];
        //bins = Collections.nCopies(numBins, new HashMap<Number, Object>());
        bins = new ArrayList<Map<Timestamp, GenericJob>>(numBins);

        for (int i = 0; i < numBins; i++) {
            bins.add(new HashMap<Timestamp, GenericJob>());
        }
        this.lo = lo;
        this.hi = hi;
        range = hi - lo;
    }

    /**
     * Add an entry to a bin.
     * Include if value is in the range
     * lo <= x < hi
     **/
    public void add(long key, GenericJob o) {

        double x = (double) key;
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
            bins.get(bin).put(new Timestamp(key), o);
        }

    }

    /** Clear the histogram bins. **/
    public void clear() {
        Iterator<Map<Timestamp, GenericJob>> it = bins.iterator();
        while (it.hasNext()) {
            it.next().clear();
        }
        overflows = 0;
        underflows = 0;
    }

    /** Provide access to the bin values. **/
    public Map<Timestamp, GenericJob> getValue(int bin) {
        return bins.get(bin);
    }

    public int getNbins() {
        return numBins;
    }
    public long getLo() {
        return lo;
    }
    public long getHi() {
        return hi;
    }
}
