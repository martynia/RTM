/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.dboard.util;

import java.util.Arrays;
import java.util.Random;

public class WeightedRandomGenerator {

    double[] Totals;
    int[] Itotals;

    public WeightedRandomGenerator(double[] weights) {
        Totals = new double[weights.length];
        initWRNG(weights);
    }

    public WeightedRandomGenerator(int[] weights) {
        Itotals = new int[weights.length];
        initWRNG(weights);
    }
    /*
     * Initializing function of Random Number generator
     * @param weights in a double array. Note that the weights here are assumed to
     * be positive. If there are negative ones. Sort the Totals array before the binary search
     */

    private void initWRNG(double[] weights) {
        double runningTotal = 0;
        int i = 0;
        for (double w : weights) {
            runningTotal += w;
            Totals[i++] = runningTotal;
        }
    }

    private void initWRNG(int[] weights) {
        int runningTotal = 0;
        int i = 0;
        for (int w : weights) {
            runningTotal += w;
            Itotals[i++] = runningTotal;
        }
    }
    /*
     * @return the weighted random number. Actually this sends the weighted randomly
     * selected index of weights vector.
     */

    public int next() {
        Random rnd = new Random(System.nanoTime());
        int sNum, rndNum;
        if (Itotals.length == 1) {
            return 0; // wrong, FIX ME for float
        }            //System.out.println( " integer weights");
        rndNum = rnd.nextInt(Itotals.length - 1);
        sNum = Arrays.binarySearch(Itotals, rndNum);
        int idx = (sNum < 0) ? (Math.abs(sNum) - 1) : sNum;
        //System.out.println("next() random no, bin search, index " + rndNum + "," + sNum + "," + idx);
        return idx;
    }

    public static void main(String[] args) {
        // code application logic here
        double weights[] = {0., 1., 0., 0., 0., 0.};
        int iweights[] = {10};
        double zeros[] = {0, 0, 0, 0, 0, 0};
        System.out.println(" Equal ? " + Arrays.equals(weights, zeros));
        //WeightedRandomGenerator wrng = new WeightedRandomGenerator(weights);
        //for (int i = 1; i < 5; i++) {
        //    int result = wrng.next();
        //    System.out.println("Weighted random is " + result);
        //}
        WeightedRandomGenerator iwrng = new WeightedRandomGenerator(iweights);
        for (int i = 1; i < 15; i++) {
            int result = iwrng.next();
            System.out.println("Weighted random is " + result);
        }
    }
}
