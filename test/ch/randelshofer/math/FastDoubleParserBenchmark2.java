/*
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.math;

import java.io.IOException;
import java.util.LongSummaryStatistics;
import java.util.Random;

/**
 * Benchmark for {@link FastDoubleParser}.
 */
public class FastDoubleParserBenchmark2 {
    public static void main(String... args) throws IOException {
        FastDoubleParserBenchmark2 benchmark = new FastDoubleParserBenchmark2();
        benchmark.runBenchmark();
    }

    /**
     * Compares the performance of {@link FastDoubleParser#parseDouble(CharSequence)}
     * against {@link Double#parseDouble(String)};
     */
    public void runBenchmark() {
        System.out.println("Benchmark for doubles in regular Java Strings");
        System.out.println("=============================================");

        Random r = new Random(0);
        String[] strings = r.longs(100_000)
                .mapToDouble(Double::longBitsToDouble)
                .mapToObj(Double::toString)
                .toArray(String[]::new);
        LongSummaryStatistics baselineStats = new LongSummaryStatistics();
        LongSummaryStatistics doubleParseDoubleStats = new LongSummaryStatistics();
        LongSummaryStatistics fastDoubleParserStats = new LongSummaryStatistics();

        double d = 0;
        for (int i = 0; i < 32; i++) {
            {
                long start = System.nanoTime();
                for (String string : strings) {
                    d += string.length();
                }
                long end = System.nanoTime();
                baselineStats.accept(end - start);
            }
            {
                long start = System.nanoTime();
                for (String string : strings) {
                    d += FastDoubleParser.parseDouble(string);
                }
                long end = System.nanoTime();
                fastDoubleParserStats.accept(end - start);
            }
            {
                long start = System.nanoTime();
                for (String string : strings) {
                    d += Double.parseDouble(string);
                }
                long end = System.nanoTime();
                doubleParseDoubleStats.accept(end - start);
            }
        }
        System.out.println("Sum of random numbers: " + d);

        System.out.println("\nBaseline (loop + add String length):"
                + "\n  " + baselineStats
                + "\n  " + baselineStats.getAverage() / strings.length + "ns per double"
        );
        double doubleParseDoubleNsPerDouble = (doubleParseDoubleStats.getAverage() - baselineStats.getAverage()) / strings.length;
        System.out.println("\nDouble.parseDouble:"
                + "\n  " + doubleParseDoubleStats
                + "\n  " + doubleParseDoubleNsPerDouble + "ns per double (adjusted to baseline)"
        );
        double fastDoubleParserNsPerDouble = (fastDoubleParserStats.getAverage() - baselineStats.getAverage()) / strings.length;
        System.out.println("\nFastDoubleParser.parseDouble:"
                + "\n  " + fastDoubleParserStats
                + "\n  " + fastDoubleParserNsPerDouble + "ns per double (adjusted to baseline)"
        );

        System.out.println("\nSpeedup factor: " + (doubleParseDoubleNsPerDouble / fastDoubleParserNsPerDouble));
        System.out.println("\n");

    }
}
