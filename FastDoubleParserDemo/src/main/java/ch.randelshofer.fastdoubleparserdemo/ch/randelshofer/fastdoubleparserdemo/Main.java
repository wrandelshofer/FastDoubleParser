/*
 * @(#)FastDoubleParserBenchmark.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparserdemo;

import ch.randelshofer.fastdoubleparser.FastDoubleParser;
import ch.randelshofer.fastdoubleparser.FastFloatParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This benchmark for {@link FastDoubleParser} aims to provide results that
 * can be compared easily with the benchmark of Daniel Lemire's fast_double_parser.
 * <p>
 * Most of the code in this class stems from
 * https://github.com/lemire/fast_double_parser/blob/master/benchmarks/benchmark.cpp
 * <p>
 * The code runs the benchmark multiple times, until the confidence interval
 * for a confidence level of {@value MEASUREMENT_CONFIDENCE_LEVEL}
 * is smaller than {@value MEASUREMENT_CONFIDENCE_INTERVAL_WIDTH} of the average
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
    public static final int MEASUREMENT_NUMBER_OF_TRIALS = 32;
    /**
     * Number of trials must be ≥ 30 to approximate normal distribution of
     * the test samples. Must be large enough, so that Java hits the C2
     * compiler.
     */
    public static final int WARMUP_NUMBER_OF_TRIALS = 256;

    /**
     * Desired confidence interval width in percent of the average value.
     */
    private static final double MEASUREMENT_CONFIDENCE_INTERVAL_WIDTH = 0.01;

    /**
     * Desired confidence interval width in percent of the average value.
     */
    private static final double WARMUP_CONFIDENCE_INTERVAL_WIDTH = 0.02;

    /**
     * One minus desired confidence level in percent.
     */
    private static final double MEASUREMENT_CONFIDENCE_LEVEL = 0.998;

    /**
     * One minus desired confidence level in percent.
     */
    private static final double WARMUP_CONFIDENCE_LEVEL = 0.99;

    private String filename = null;
    private boolean markdown = false;
    private boolean sleep = false;

    private boolean printConfidenceWidth = false;

    public static void main(String... args) throws Exception {
        System.out.println(SystemInfo.getSystemSummary());

        Main benchmark = new Main();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
            case "--markdown":
                benchmark.markdown = true;
                break;
            case "--sleep":
                benchmark.sleep = true;
                break;
            case "--print-confidence":
                benchmark.printConfidenceWidth = true;
                break;
            default:
                benchmark.filename = args[i];
                break;
            }
        }

        if (benchmark.filename == null) {
            benchmark.demo(100_000);
            System.out.println("You can also provide a filename: it should contain one "
                    + "string per line corresponding to a number.");
        } else {
            benchmark.loadFile(benchmark.filename);
        }
    }

    private Map<String, BenchmarkFunction> createBenchmarkFunctions(List<String> lines) {
        List<byte[]> byteArrayLines = lines.stream().map(l -> l.getBytes(StandardCharsets.ISO_8859_1)).collect(Collectors.toList());
        List<char[]> charArrayLines = lines.stream().map(String::toCharArray).collect(Collectors.toList());


        Map<String, BenchmarkFunction> functions = new LinkedHashMap<>();
        List.of(
                new BenchmarkFunction("FastDouble String", "Double", () -> sumFastDoubleFromCharSequence(lines)),
                new BenchmarkFunction("FastDouble char[]", "Double", () -> sumFastDoubleParserFromCharArray(charArrayLines)),
                new BenchmarkFunction("FastDouble byte[]", "Double", () -> sumFastDoubleParserFromByteArray(byteArrayLines)),
                new BenchmarkFunction("Double", "Double", () -> sumDoubleParseDouble(lines)),
                new BenchmarkFunction("FastFloat  String", "Float", () -> sumFastFloatFromCharSequence(lines)),
                new BenchmarkFunction("FastFloat  char[]", "Float", () -> sumFastFloatParserFromCharArray(charArrayLines)),
                new BenchmarkFunction("FastFloat  byte[]", "Float", () -> sumFastFloatParserFromByteArray(byteArrayLines)),
                new BenchmarkFunction("Float", "Float", () -> sumFloatParseFloat(lines))).forEach(b -> functions.put(b.title, b));

        return functions;
    }

    public void demo(int howmany) {
        System.out.println("Parsing random doubles in the range [0,1).");
        List<String> lines = new Random().doubles(howmany).mapToObj(Double::toString)
                .collect(Collectors.toList());
        validate(lines);
        process(lines);
    }

    public void loadFile(String filename) throws IOException {
        Path path = FileSystems.getDefault().getPath(filename).toAbsolutePath();
        System.out.printf("Parsing numbers in file %s\n", path);
        List<String> lines = Files.lines(path).collect(Collectors.toList());
        System.out.printf("Read %d lines\n", lines.size());
        validate(lines);
        process(lines);
    }

    private VarianceStatistics measure(Supplier<? extends Number> func, int numberOfTrials,
                                       double confidenceLevel, double confidenceIntervalWidth) {
        long t1;
        double confidenceWidth;
        long t2;
        double elapsed;
        VarianceStatistics stats = new VarianceStatistics();

        // measure
        do {
            for (int i = 0; i < numberOfTrials; i++) {
                t1 = System.nanoTime();
                func.get();
                t2 = System.nanoTime();
                elapsed = t2 - t1;
                stats.accept(elapsed);
            }
            confidenceWidth = Stats.confidence(1 - confidenceLevel, stats.getSampleStandardDeviation(), stats.getCount()) / stats.getAverage();
        } while (confidenceWidth > confidenceIntervalWidth);
        return stats;
    }

    private void printStatsAscii(List<String> lines, double volumeMB, String name, VarianceStatistics stats) {
        if (printConfidenceWidth) {
            double confidenceWidth = Stats.confidence(1 - MEASUREMENT_CONFIDENCE_LEVEL, stats.getSampleStandardDeviation(), stats.getCount()) / stats.getAverage();
            System.out.printf("%-30s :  %7.2f MB/s (+/-%4.1f %% stdv) (+/-%4.1f %% conf)  %7.2f Mfloat/s  %7.2f ns/f\n",
                    name,
                    volumeMB * 1e9 / stats.getAverage(),
                    stats.getSampleStandardDeviation() * 100 / stats.getAverage(),
                    100 * confidenceWidth,
                    lines.size() * (1e9 / 1_000_000) / stats.getAverage(),
                    stats.getAverage() / lines.size()
            );
        } else {
            System.out.printf("%-17s :  %7.2f MB/s (+/-%4.1f %%)  %7.2f Mfloat/s  %9.2f ns/f\n",
                    name,
                    volumeMB * 1e9 / stats.getAverage(),
                    stats.getSampleStandardDeviation() * 100 / stats.getAverage(),
                    lines.size() * (1e9 / 1_000_000) / stats.getAverage(),
                    stats.getAverage() / lines.size()
            );
        }
    }

    private void printStatsHeaderMarkdown() {
        System.out.println("|Method           | MB/s  |stdev|Mfloats/s| ns/f   | JDK    |");
        System.out.println("|-----------------|------:|-----:|------:|--------:|--------|");
    }

    private void printStatsMarkdown(List<String> lines, double volumeMB, String name, VarianceStatistics stats) {
        System.out.printf("|%-17s|%7.2f|%4.1f %%|%7.2f|%9.2f|%s|\n",
                name,
                volumeMB * 1e9 / stats.getAverage(),
                stats.getSampleStandardDeviation() * 100 / stats.getAverage(),
                lines.size() * (1e9 / 1_000_000) / stats.getAverage(),
                stats.getAverage() / lines.size(),
                System.getProperty("java.version")
        );
    }

    private void process(List<String> lines) {
        Map<String, BenchmarkFunction> functions = createBenchmarkFunctions(lines);

        double volumeMB = lines.stream().mapToInt(String::length).sum() / (1024. * 1024.);

        // Warm up
        System.out.println("Warming JVM up (code must be compiled by C2 compiler for optimal performance).");
        //System.out.printf("Warmup: Trying to reach a confidence level of %,.1f %% which only deviates by %,.0f %% from the average measured duration.\n",
        //       100 * WARMUP_CONFIDENCE_LEVEL, 100 * WARMUP_CONFIDENCE_INTERVAL_WIDTH);
        Map<String, VarianceStatistics> results = new LinkedHashMap<>();
        for (Map.Entry<String, BenchmarkFunction> entry : functions.entrySet()) {
            VarianceStatistics warmup = measure(entry.getValue().supplier, WARMUP_NUMBER_OF_TRIALS, WARMUP_CONFIDENCE_LEVEL, WARMUP_CONFIDENCE_INTERVAL_WIDTH);
            results.put(entry.getKey(), warmup);
            //System.out.println("  " + entry.getKey() + " " + warmup);
        }

        // Allow time for connecting with VisualVM
        sleep();

        // Measure
        System.out.printf("Trying to reach a confidence level of %,.1f %% which only deviates by %,.0f %% from the average measured duration.\n",
                100 * MEASUREMENT_CONFIDENCE_LEVEL, 100 * MEASUREMENT_CONFIDENCE_INTERVAL_WIDTH);
        for (Map.Entry<String, BenchmarkFunction> entry : functions.entrySet()) {
            VarianceStatistics stats = measure(entry.getValue().supplier, MEASUREMENT_NUMBER_OF_TRIALS, MEASUREMENT_CONFIDENCE_LEVEL, MEASUREMENT_CONFIDENCE_INTERVAL_WIDTH);
            results.put(entry.getKey(), stats);
            //System.out.println("  " + entry.getKey() + " " + stats);
        }

        // Print results
        System.out.println("Results:");
        if (markdown) {
            printStatsHeaderMarkdown();
        }
        for (Map.Entry<String, VarianceStatistics> entry : results.entrySet()) {
            String name = entry.getKey();
            VarianceStatistics stats = entry.getValue();
            if (markdown) {
                printStatsMarkdown(lines, volumeMB, name, stats);
            } else {
                printStatsAscii(lines, volumeMB, name, stats);
            }
        }

        // Print speedup versus reference implementation
        System.out.println();
        for (Map.Entry<String, VarianceStatistics> entry : results.entrySet()) {
            String name = entry.getKey();
            String reference = functions.get(name).reference;
            if (!reference.equals(name)) {
                VarianceStatistics referenceStats = results.get(reference);
                VarianceStatistics stats = entry.getValue();
                System.out.printf("Speedup %-17s vs %s: %,.2f\n", name, reference, referenceStats.getAverage() / stats.getAverage());

            }
        }
    }

    private void sleep() {
        if (sleep) {
            System.out.println("sleeping...");
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                //stop sleeping
            }
            System.out.println("done...");
        }
    }

    private double sumDoubleParseDouble(List<String> s) {
        double answer = 0;
        for (String st : s) {
            double x = Double.parseDouble(st);
            answer += x;
        }
        return answer;
    }

    private double sumFastDoubleFromCharSequence(List<String> s) {
        double answer = 0;
        for (String st : s) {
            double x = FastDoubleParser.parseDouble(st);
            answer += x;
        }
        return answer;
    }

    private double sumFastDoubleParserFromByteArray(List<byte[]> s) {
        double answer = 0;
        for (byte[] st : s) {
            double x = FastDoubleParser.parseDouble(st);
            answer += x;
        }
        return answer;
    }

    private double sumFastDoubleParserFromCharArray(List<char[]> s) {
        double answer = 0;
        for (char[] st : s) {
            double x = FastDoubleParser.parseDouble(st);
            answer += x;
        }
        return answer;
    }

    private float sumFastFloatFromCharSequence(List<String> s) {
        float answer = 0;
        for (String st : s) {
            float x = FastFloatParser.parseFloat(st);
            answer += x;
        }
        return answer;
    }

    private float sumFastFloatParserFromByteArray(List<byte[]> s) {
        float answer = 0;
        for (byte[] st : s) {
            float x = FastFloatParser.parseFloat(st);
            answer += x;
        }
        return answer;
    }

    private float sumFastFloatParserFromCharArray(List<char[]> s) {
        float answer = 0;
        for (char[] st : s) {
            float x = FastFloatParser.parseFloat(st);
            answer += x;
        }
        return answer;
    }

    private float sumFloatParseFloat(List<String> s) {
        float answer = 0;
        for (String st : s) {
            float x = Float.parseFloat(st);
            answer += x;
        }
        return answer;
    }

    private void validate(List<String> lines) {
        Map<String, BenchmarkFunction> map = createBenchmarkFunctions(lines);

        Number expectedDoubleValue = sumDoubleParseDouble(lines);
        Number expectedFloatValue = sumFloatParseFloat(lines);

        for (Map.Entry<String, BenchmarkFunction> entry : map.entrySet()) {
            String name = entry.getKey();
            Number actual = entry.getValue().supplier.get();

            if ((actual instanceof Double) && !expectedDoubleValue.equals(actual)
                    || (actual instanceof Float) && !expectedFloatValue.equals(actual)) {
                System.err.println(name + " has an error. expectedSum=" + expectedFloatValue + " actualSum=" + actual);

            }
        }
    }

    record BenchmarkFunction(String title, String reference,
                             Supplier<? extends Number> supplier) {
    }
}
