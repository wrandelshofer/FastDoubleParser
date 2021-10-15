/*
 * @(#)FastDoubleParserBenchmark.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.stats.Stats;
import ch.randelshofer.stats.VarianceStatistics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
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
public class FastDoubleParserBenchmark {
    /**
     * Number of trials must be ≥ 30 to approximate normal distribution of
     * the test samples.
     */
    public static final int NUMBER_OF_TRIALS = 32;

    /**
     * Desired confidence interval width in percent of the average value.
     */
    private static final double DESIRED_CONFIDENCE_INTERVAL_WIDTH = 0.02;
    /**
     * One minus desired confidence level in percent.
     */
    private static final double DESIRED_CONFIDENCE_LEVEL = 0.98;

    public static void main(String... args) throws Exception {
        System.out.printf("%s\n", getCpuInfo());
        System.out.printf("%s\n\n", getRtInfo());
        FastDoubleParserBenchmark benchmark = new FastDoubleParserBenchmark();
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
        long t1, t2;
        double dif, ts;
        VarianceStatistics fastDoubleParserStats = new VarianceStatistics();
        VarianceStatistics fastDoubleParserFromByteArrayStats = new VarianceStatistics();
        VarianceStatistics fastDoubleParserFromCharArrayStats = new VarianceStatistics();
        VarianceStatistics doubleStats = new VarianceStatistics();
        int numberOfTrials = NUMBER_OF_TRIALS;
        List<byte[]> byteArrayLines = lines.stream().map(l -> l.getBytes(StandardCharsets.ISO_8859_1)).collect(Collectors.toList());
        List<char[]> charArrayLines = lines.stream().map(l -> l.toCharArray()).collect(Collectors.toList());
        double confidenceWidth;
        System.out.printf("Trying to reach a confidence level of %,.1f %% which only deviates by %,.0f %% from the average measured duration.\n",
                100 * DESIRED_CONFIDENCE_LEVEL, 100 * DESIRED_CONFIDENCE_INTERVAL_WIDTH);
        int count = 0;
        do {
            count += numberOfTrials;
            System.out.printf("=== number of trials %,d =====\n", count);
            for (int i = 0; i < numberOfTrials; i++) {
                t1 = System.nanoTime();
                findmaxFastDoubleParserParseDouble(lines);
                t2 = System.nanoTime();
                dif = t2 - t1;
                fastDoubleParserStats.accept(volumeMB * 1000000000 / dif);

                t1 = System.nanoTime();
                findmaxFastDoubleParserFromCharArrayParseDouble(charArrayLines);
                t2 = System.nanoTime();
                dif = t2 - t1;
                fastDoubleParserFromCharArrayStats.accept(volumeMB * 1000000000 / dif);

                t1 = System.nanoTime();
                findmaxFastDoubleParserFromByteArrayParseDouble(byteArrayLines);
                t2 = System.nanoTime();
                dif = t2 - t1;
                fastDoubleParserFromByteArrayStats.accept(volumeMB * 1000000000 / dif);

                t1 = System.nanoTime();
                findmaxDoubleParseDouble(lines);
                t2 = System.nanoTime();
                dif = t2 - t1;
                doubleStats.accept(volumeMB * 1000000000 / dif);
            }
            confidenceWidth = Stats.confidence(1 - DESIRED_CONFIDENCE_LEVEL, doubleStats.getSampleStandardDeviation(), doubleStats.getCount()) / doubleStats.getAverage();
        } while (confidenceWidth > DESIRED_CONFIDENCE_INTERVAL_WIDTH);


        System.out.printf("FastDoubleParser               MB/s avg: %2f, stdev: ±%,.2f, conf%,.1f%%: ±%,.2f\n",
                fastDoubleParserStats.getAverage(),
                fastDoubleParserStats.getSampleStandardDeviation(),
                100 * DESIRED_CONFIDENCE_LEVEL,
                Stats.confidence(1 - DESIRED_CONFIDENCE_LEVEL, fastDoubleParserStats.getSampleStandardDeviation(), fastDoubleParserFromByteArrayStats.getCount()));
        System.out.printf("FastDoubleParserFromCharArray  MB/s avg: %2f, stdev: ±%,.2f, conf%,.1f%%: ±%,.2f\n",
                fastDoubleParserFromCharArrayStats.getAverage(),
                fastDoubleParserFromCharArrayStats.getSampleStandardDeviation(),
                100 * DESIRED_CONFIDENCE_LEVEL,
                Stats.confidence(1 - DESIRED_CONFIDENCE_LEVEL, fastDoubleParserFromCharArrayStats.getSampleStandardDeviation(), fastDoubleParserFromCharArrayStats.getCount()));
        System.out.printf("FastDoubleParserFromByteArray  MB/s avg: %2f, stdev: ±%,.2f, conf%,.1f%%: ±%,.2f\n",
                fastDoubleParserFromByteArrayStats.getAverage(),
                fastDoubleParserFromByteArrayStats.getSampleStandardDeviation(),
                100 * DESIRED_CONFIDENCE_LEVEL,
                Stats.confidence(1 - DESIRED_CONFIDENCE_LEVEL, fastDoubleParserFromByteArrayStats.getSampleStandardDeviation(), fastDoubleParserFromByteArrayStats.getCount()));
        System.out.printf("Double                         MB/s avg: %2f, stdev: ±%,.2f, conf%,.1f%%: ±%,.2f\n",
                doubleStats.getAverage(),
                doubleStats.getSampleStandardDeviation(),
                100 * DESIRED_CONFIDENCE_LEVEL,
                Stats.confidence(1 - DESIRED_CONFIDENCE_LEVEL, doubleStats.getSampleStandardDeviation(), doubleStats.getCount()));
        System.out.printf("Speedup FastDoubleParser              vs Double: %,.2f\n", fastDoubleParserStats.getAverage() / doubleStats.getAverage());
        System.out.printf("Speedup FastDoubleParserFromCharArray vs Double: %,.2f\n", fastDoubleParserFromCharArrayStats.getAverage() / doubleStats.getAverage());
        System.out.printf("Speedup FastDoubleParserFromByteArray vs Double: %,.2f\n", fastDoubleParserFromByteArrayStats.getAverage() / doubleStats.getAverage());
        System.out.print("\n\n");
    }

    private void validate(List<String> lines) {
        for (String line : lines) {
            double expected = Double.parseDouble(line);
            double actual = FastDoubleParser.parseDouble(line);
            if (Double.doubleToLongBits(expected) != Double.doubleToLongBits(actual)) {
                System.err.println("FastDoubleParser disagrees. input=" + line + " expected=" + expected + " actual=" + actual);
            }
            double actualFromByteArray = FastDoubleParserFromByteArray.parseDouble(line.getBytes(StandardCharsets.ISO_8859_1));
            if (Double.doubleToLongBits(expected) != Double.doubleToLongBits(actualFromByteArray)) {
                System.err.println("FastDoubleParserFromByteArray disagrees. input="
                        + line + " expected=" + expected + " actual=" + actualFromByteArray
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

    private static String getCpuInfo() {
        final Runtime rt = Runtime.getRuntime();

        final String osName = System.getProperty("os.name").toLowerCase();
        final String cmd;
        if (osName.startsWith("mac")) {
            cmd = "sysctl -n machdep.cpu.brand_string";
        } else if (osName.startsWith("win")) {
            cmd = "wmic cpu get name";
        } else {
            return "Unknown Processor";
        }
        final StringBuilder buf = new StringBuilder();
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(rt.exec(cmd).getInputStream()))) {
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                buf.append(line);
            }
        } catch (final IOException ex) {
            return ex.getMessage();
        }
        return buf.toString();
    }

    private static String getRtInfo() {
        final RuntimeMXBean mxbean = ManagementFactory.getRuntimeMXBean();
        return mxbean.getVmName() + ", " + mxbean.getVmVendor() + ", " + mxbean.getVmVersion();
    }

}
