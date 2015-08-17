package de.tuberlin.dima.schubotz.wikisim.cpa.tests;

import de.tuberlin.dima.schubotz.wikisim.cpa.types.list.DoubleListValue;
import de.tuberlin.dima.schubotz.wikisim.cpa.utils.StringUtils;
import org.apache.flink.shaded.com.google.common.collect.MinMaxPriorityQueue;
import org.junit.Ignore;
import org.junit.Test;

import java.util.regex.Pattern;

/**
 * Local tests to measure performance.
 */
public class PerformanceTests {

    @Ignore
    public void parseDoublePerformanceTest2() {

        int runs = 999999;
        long start = System.nanoTime();
        for (int i = 0; i < runs; i++) {
            Double.valueOf("3.3489451534507196E-104");
        }
        long time = System.nanoTime() - start;
        System.out.printf("DD to double took an average of %.1f us%n", time / runs / 1000.0);

        long startB = System.nanoTime();
        for (int i = 0; i < runs; i++) {
            Double.valueOf("3.348");
        }
        long timeB = System.nanoTime() - startB;
        System.out.printf("II to double took an average of %.1f us%n", timeB / runs / 1000.0);

    }

    @Ignore
    public void parseDoublePerformanceTest() {

        int runs = 100000;

        String[] ints = new String[runs];
        String[] doubles = new String[runs];

        // 3.3489451534507196E-104

        for (int i = 0; i < runs; i++) {

            double doub = Math.random();
            int inte = (int) (doub * 100);

            doubles[i] = String.valueOf(doub);
            ints[i] = String.valueOf(inte);
        }

        long start = System.nanoTime();
        for (int x = 0; x < 100; x++) {
            for (String s : ints) {
                Double d = Double.valueOf(s);
            }
        }

        long time = System.nanoTime() - start;
        System.out.printf("Integer to double took an average of %.1f us%n", time / runs / 1000.0);


        long startB = System.nanoTime();
        for (int x = 0; x < 100; x++) {
            for (String s : doubles) {
                Double dd = Double.valueOf(s);
            }
        }

        long timeB = System.nanoTime() - startB;
        System.out.printf("Double to double took an average of %.1f us%n", timeB / runs / 1000.0);

    }

    @Ignore
    public void MinMaxQueuePerformanceTest() {

        int listlength = 100000;
        int runs = 1000;

        double[] ints = new double[listlength];
        double[] doubles = new double[listlength];


        for (int i = 0; i < listlength; i++) {

            double doub = Math.random();
            int inte = (int) (doub * 100);

            doubles[i] = doub;
            ints[i] = (double) inte;
        }

        MinMaxPriorityQueue<Double> queue = MinMaxPriorityQueue.maximumSize(10).create();
        long start = System.nanoTime();
        for (int x = 0; x < runs; x++) {
            for (double s : ints) {
                queue.add(Double.valueOf(s));
            }
        }

        long time = System.nanoTime() - start;
        System.out.printf("Adding Integer to MinMaxQueue took an average of %.1f us%n", time / (runs * listlength) / 1000.0);

        queue = MinMaxPriorityQueue.maximumSize(10).create();

        long startB = System.nanoTime();
        for (int x = 0; x < runs; x++) {
            for (double s : doubles) {
                queue.add(Double.valueOf(s));
            }
        }

        long timeB = System.nanoTime() - startB;
        System.out.printf("Adding Double to MinMaxQueue took an average of %.1f us%n", timeB / (runs * listlength) / 1000.0);

    }

    @Ignore
    @Test
    public void DoubleListPerformance() {
        int runs = 999999;
        String testStr = "1.0|2.0|100|0.05|1245.67";
        String delimiter = Pattern.quote("|");

        long start = System.nanoTime();
        for (int i = 0; i < runs; i++) {
            DoubleListValue.valueOf(testStr, delimiter);
        }
        long time = System.nanoTime() - start;
        System.out.printf("Parse DoubleListValue took an average of %.1f us%n", time / runs / 1000.0);

        long startB = System.nanoTime();
        for (int i = 0; i < runs; i++) {
            StringUtils.getDoubleListFromString(testStr, delimiter);
        }
        long timeB = System.nanoTime() - startB;
        System.out.printf("Parse ArrayList<Double> took an average of %.1f us%n", timeB / runs / 1000.0);

    }
}