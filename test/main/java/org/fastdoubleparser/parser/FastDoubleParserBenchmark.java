/*
 * @(#)FastDoubleParserBenchmark.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package org.fastdoubleparser.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.DoubleSummaryStatistics;
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
 * The code runs the benchmark {@value NUMBER_OF_TRIALS} times and prints
 * the average, minimal and maximal times.
 * <p>
 * References:
 * <dl>
 *     <dt>Daniel Lemire, fast_double_parser, 4x faster than strtod.
 *     Apache License 2.0 or Boost Software License.</dt>
 *     <dd><a href="https://github.com/lemire/fast_double_parser">github</a></dd>
 * </dl>
 */
public class FastDoubleParserBenchmark {

    public static final int NUMBER_OF_TRIALS = 32;

    public static void main(String... args) throws Exception {
        System.out.printf("%s\n", getCpuInfo());
        System.out.printf("%s\n\n", getRtInfo());
        FastDoubleParserBenchmark benchmark = new FastDoubleParserBenchmark();
        if (args.length == 0) {
            benchmark.demo(1_000_000);
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
        DoubleSummaryStatistics fastDoubleParserStats = new DoubleSummaryStatistics();
        DoubleSummaryStatistics doubleStats = new DoubleSummaryStatistics();
        int numberOfTrials = NUMBER_OF_TRIALS;
        System.out.printf("=== number of trials %d =====\n", numberOfTrials);
        for (int i = 0; i < numberOfTrials; i++) {
            t1 = System.nanoTime();
            ts = findmaxFastDoubleParserParseDouble(lines);
            t2 = System.nanoTime();
            if (ts == 0) {
                System.out.print("bug\n");
            }
            dif = t2 - t1;
            if (i > 0) {
                fastDoubleParserStats.accept(volumeMB * 1000000000 / dif);
            }
            t1 = System.nanoTime();
            ts = findmaxDoubleParseDouble(lines);
            t2 = System.nanoTime();
            if (ts == 0) {
                System.out.print("bug\n");
            }
            dif = t2 - t1;
            if (i > 0) {
                doubleStats.accept(volumeMB * 1000000000 / dif);
            }
        }
        System.out.printf("FastDoubleParser.parseDouble  MB/s avg: %2f, min: %.2f, max: %.2f\n", fastDoubleParserStats.getAverage(), fastDoubleParserStats.getMin(), fastDoubleParserStats.getMax());
        System.out.printf("Double.parseDouble            MB/s avg: %2f, min: %.2f, max: %.2f\n", doubleStats.getAverage(), doubleStats.getMin(), doubleStats.getMax());
        System.out.printf("Speedup FastDoubleParser vs Double: %2f\n", fastDoubleParserStats.getAverage() / doubleStats.getAverage());
        System.out.print("\n\n");
    }

    private void validate(List<String> lines) {
        for (String line : lines) {
            double expected = Double.parseDouble(line);
            double actual = FastDoubleParser.parseDouble(line);
            if (Double.doubleToLongBits(expected) != Double.doubleToLongBits(actual)) {
                System.err.println("FastDoubleParser disagrees. input=" + line + " expected=" + expected + " actual=" + actual);
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
