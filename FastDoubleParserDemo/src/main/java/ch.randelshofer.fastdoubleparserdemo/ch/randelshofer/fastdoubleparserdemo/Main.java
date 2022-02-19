/*
 * @(#)FastDoubleParserBenchmark.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparserdemo;

import ch.randelshofer.fastdoubleparser.FastDoubleParser;
import ch.randelshofer.fastdoubleparser.FastDoubleParserFromByteArray;
import ch.randelshofer.fastdoubleparser.FastDoubleParserFromCharArray;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This benchmark for {@link FastDoubleParser} aims to provide results that
 * can be compared easily with the benchmark of Daniel Lemire's fast_double_parser.
 * <p>
 * Most of the code in this class stems from
 * https://github.com/lemire/fast_double_parser/blob/master/benchmarks/benchmark.cpp
 * <p>
 * The code runs the benchmark multiple times, until the confidence interval
 * for a confidence level of {@value DESIRED_CONFIDENCE_LEVEL}
 * is smaller than {@value DESIRED_CONFIDENCE_INTERVAL_WIDTH} of the average
 * measured time.
 * <p>
 * It then prints the the average times, standard deviations and confidence intervals.
 * <p>
 * References:
 * <dl>
 *     <dt>Daniel Lemire. fast_double_parser, 4x faster than strtod.
 *     Apache License 2.0 or Boost Software License.</dt>
 *     <dd><a href="https://github.com/lemire/fast_double_parser">github</a></dd>
 * </dl>
 * References:
 * <dl>
 *     <dt>Andy Georges, Dries Buytaert, Lieven Eeckhout.
 *     Statistically Rigorous Java Performance Evaluation.
 *     Department of Electronics and Information Systems, Ghent University, Belgium.</dt>
 *     <dd><a href="https://dri.es/files/oopsla07-georges.pdf">dri.es</a></dd>
 * </dl>
 */
public class Main {
    /**
     * Number of trials must be ≥ 30 to approximate normal distribution of
     * the test samples.
     */
    public static final int NUMBER_OF_TRIALS = 64;

    /**
     * Desired confidence interval width in percent of the average value.
     */
    private static final double DESIRED_CONFIDENCE_INTERVAL_WIDTH = 0.01;
    /**
     * One minus desired confidence level in percent.
     */
    private static final double DESIRED_CONFIDENCE_LEVEL = 0.998;

    public static void main(String... args) throws Exception {
        System.out.println(SystemInfo.getSystemSummary());
        System.out.println();
        Main benchmark = new Main();
        if (args.length == 0) {
            benchmark.demo(100_000);
            System.out.println("You can also provide a filename: it should contain one "
                    + "string per line corresponding to a number.");
        } else {
            benchmark.fileload(args[0]);
        }
    }

    public void demo(int howmany) {
        System.out.println("parsing random numbers in the range [0,1)");
        List<String> lines = new Random().doubles(howmany).mapToObj(Double::toString)
                .collect(Collectors.toList());
        validate(lines);
        process(lines);
    }

    private double findmaxFastDoubleParserParseDouble(List<String> s) {
        double answer = 0;
        for (String st : s) {
            double x = FastDoubleParser.parseDouble(st);
            answer = Math.max(answer, x);
        }
        return answer;
    }

    private double findmaxFastDoubleParserFromByteArrayParseDouble(List<byte[]> s) {
        double answer = 0;
        for (byte[] st : s) {
            double x = FastDoubleParserFromByteArray.parseDouble(st);
            answer = Math.max(answer, x);
        }
        return answer;
    }

    private double findmaxFastDoubleParserFromCharArrayParseDouble(List<char[]> s) {
        double answer = 0;
        for (char[] st : s) {
            double x = FastDoubleParserFromCharArray.parseDouble(st);
            answer = Math.max(answer, x);
        }
        return answer;
    }

    private double findmaxDoubleParseDouble(List<String> s) {
        double answer = 0;
        for (String st : s) {
            double x = Double.parseDouble(st);
            answer = Math.max(answer, x);
        }
        return answer;
    }

    private void process(List<String> lines) {
        double volumeMB = lines.stream().mapToInt(String::length).sum() / (1024. * 1024.);

        VarianceStatistics fdpStringStatsMBs = new VarianceStatistics();
        VarianceStatistics fdpByteStatsMBs = new VarianceStatistics();
        VarianceStatistics fdpCharStatsMBs = new VarianceStatistics();
        VarianceStatistics doubleStatsMBs = new VarianceStatistics();
        VarianceStatistics fdpStringStatsNSf = new VarianceStatistics();
        VarianceStatistics fdpByteStatsNSf = new VarianceStatistics();
        VarianceStatistics fdpCharStatsNSf = new VarianceStatistics();
        VarianceStatistics doubleStatsNSf = new VarianceStatistics();
        int numberOfTrials = NUMBER_OF_TRIALS;
        List<byte[]> byteArrayLines = lines.stream().map(l -> l.getBytes(StandardCharsets.ISO_8859_1)).collect(Collectors.toList());
        List<char[]> charArrayLines = lines.stream().map(String::toCharArray).collect(Collectors.toList());

        System.out.printf("Trying to reach a confidence level of %,.1f %% which only deviates by %,.0f %% from the average measured duration.\n",
                100 * DESIRED_CONFIDENCE_LEVEL, 100 * DESIRED_CONFIDENCE_INTERVAL_WIDTH);
        double blackHole = 0;
        blackHole += measure(charArrayLines,
                this::findmaxFastDoubleParserFromCharArrayParseDouble,
                volumeMB, fdpCharStatsMBs, fdpCharStatsNSf, numberOfTrials);
        blackHole += measure(byteArrayLines,
                this::findmaxFastDoubleParserFromByteArrayParseDouble,
                volumeMB, fdpByteStatsMBs, fdpByteStatsNSf, numberOfTrials);
        blackHole = measure(lines,
                this::findmaxFastDoubleParserParseDouble,
                volumeMB, fdpStringStatsMBs, fdpStringStatsNSf, numberOfTrials);
        blackHole += measure(lines,
                this::findmaxDoubleParseDouble,
                volumeMB, doubleStatsMBs, doubleStatsNSf, numberOfTrials);
/*
        printMbStats("FastDoubleParser", fdpStringStatsMBs, fdpStringStatsMBs);
        printMbStats("FastDoubleParserFromCharArray", fdpCharStatsMBs, fdpCharStatsMBs);
        printMbStats("FastDoubleParserFromByteArray", fdpByteStatsMBs, fdpByteStatsMBs);
        printMbStats("Double", doubleStatsMBs, doubleStatsMBs);
*/
        System.out.println();
        extractMbStats2("FastDoubleParser", fdpStringStatsMBs, fdpStringStatsNSf);
        extractMbStats2("FastDoubleParserFromCharArray", fdpCharStatsMBs, fdpCharStatsNSf);
        extractMbStats2("FastDoubleParserFromByteArray", fdpByteStatsMBs, fdpByteStatsNSf);
        extractMbStats2("Double", doubleStatsMBs, doubleStatsNSf);

        System.out.println();
        System.out.printf("Speedup FastDoubleParser              vs Double: %,.2f\n", fdpStringStatsMBs.getAverage() / doubleStatsMBs.getAverage());
        System.out.printf("Speedup FastDoubleParserFromCharArray vs Double: %,.2f\n", fdpCharStatsMBs.getAverage() / doubleStatsMBs.getAverage());
        System.out.printf("Speedup FastDoubleParserFromByteArray vs Double: %,.2f\n", fdpByteStatsMBs.getAverage() / doubleStatsMBs.getAverage());
        System.out.print("\n\n");
    }

    private <T> double measure(List<T> lines,
                               Function<List<T>, Double> func,
                               double volumeMB, VarianceStatistics fdpStringStatsMBs, VarianceStatistics fdpStringStatsNSf, int numberOfTrials) {
        long t1;
        double confidenceWidth;
        long t2;
        double elapsed;
        double blackHole = 0;
        VarianceStatistics stats = new VarianceStatistics();
        // warmup
        for (int i = 0; i < numberOfTrials; i++) {
            blackHole += func.apply(lines);
        }

        // measure
        do {
            for (int i = 0; i < numberOfTrials; i++) {
                t1 = System.nanoTime();
                blackHole += func.apply(lines);
                t2 = System.nanoTime();
                elapsed = t2 - t1;
                stats.accept(elapsed);
                fdpStringStatsMBs.accept(volumeMB * 1000000000 / elapsed);
                fdpStringStatsNSf.accept(elapsed / lines.size());
            }
            confidenceWidth = Stats.confidence(1 - DESIRED_CONFIDENCE_LEVEL, stats.getSampleStandardDeviation(), stats.getCount()) / stats.getAverage();
        } while (confidenceWidth > DESIRED_CONFIDENCE_INTERVAL_WIDTH);
        return blackHole;
    }

    private void extractMbStats2(String fastDoubleParser, VarianceStatistics fdpStringStatsMBs, VarianceStatistics fdpStringStatsNSf) {
        System.out.printf("%-30s :  %8.2f MB/s (+/-%4.1f %%)  %7.2f Mfloat/s    %7.2f ns/f\n",
                fastDoubleParser,
                fdpStringStatsMBs.getAverage(),
                fdpStringStatsMBs.getSampleStandardDeviation() * 100 / fdpStringStatsMBs.getAverage(),
                1000 / fdpStringStatsNSf.getAverage(),
                fdpStringStatsNSf.getAverage());
    }

    private void printMbStats(String fastDoubleParser, VarianceStatistics fdpStringStatsMBs, VarianceStatistics fdpByteStatsMBs) {
        System.out.printf("%-30s MB/s avg: %8.2f, stdev: +/-%5.2f, conf%,4.1f%%: +/-%,.2f\n",
                fastDoubleParser,
                fdpStringStatsMBs.getAverage(),
                fdpStringStatsMBs.getSampleStandardDeviation(),
                100 * DESIRED_CONFIDENCE_LEVEL,
                Stats.confidence(1 - DESIRED_CONFIDENCE_LEVEL, fdpStringStatsMBs.getSampleStandardDeviation(),
                        fdpByteStatsMBs.getCount()));
    }

    private void validate(List<String> lines) {
        for (String line : lines) {
            double expected = Double.parseDouble(line);
            double actual = FastDoubleParser.parseDouble(line);
            if (Double.doubleToLongBits(expected) != Double.doubleToLongBits(actual)) {
                System.err.println("FastDoubleParser disagrees. input=" + line + " expected=" + expected + " actual=" + actual);
            }
            actual = FastDoubleParserFromByteArray.parseDouble(line.getBytes(StandardCharsets.ISO_8859_1));
            if (Double.doubleToLongBits(expected) != Double.doubleToLongBits(actual)) {
                System.err.println("FastDoubleParserFromByteArray disagrees. input="
                        + line + " expected=" + expected + " actual=" + actual
                );
            }
            actual = FastDoubleParserFromCharArray.parseDouble(line.toCharArray());
            if (Double.doubleToLongBits(expected) != Double.doubleToLongBits(actual)) {
                System.err.println("FastDoubleParserFromCharArray disagrees. input="
                        + line + " expected=" + expected + " actual=" + actual
                );
            }
        }
    }

    public void fileload(String filename) throws IOException {
        Path path = FileSystems.getDefault().getPath(filename).toAbsolutePath();
        System.out.printf("parsing numbers in file %s\n", path);
        List<String> lines = Files.lines(path).collect(Collectors.toList());
        System.out.printf("read %d lines\n", lines.size());
        validate(lines);
        process(lines);
    }
}
