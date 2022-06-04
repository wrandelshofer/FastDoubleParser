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
 * for a confidence level of {@value CONFIDENCE_LEVEL}
 * is smaller than {@value CONFIDENCE_INTERVAL_WIDTH} of the average
 * measured time.
 * <p>
 * It then prints the average times, standard deviations and confidence intervals.
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
    private static final double CONFIDENCE_INTERVAL_WIDTH = 0.01;
    /**
     * One minus desired confidence level in percent.
     */
    private static final double CONFIDENCE_LEVEL = 0.998;

    private String filename = null;
    private boolean markdown = false;


    public static void main(String... args) throws Exception {
        System.out.println(SystemInfo.getSystemSummary());
        System.out.println();

        Main benchmark = new Main();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
            case "--markdown":
                benchmark.markdown = true;
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

    public void demo(int howmany) {
        System.out.println("parsing random doubles in the range [0,1)");
        List<String> lines = new Random().doubles(howmany).mapToObj(Double::toString)
                .collect(Collectors.toList());
        validate(lines);
        process(lines);
    }

    private double sumFastDoubleFromCharSequence(List<String> s) {
        double answer = 0;
        for (String st : s) {
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

    private double sumDoubleParseDouble(List<String> s) {
        double answer = 0;
        for (String st : s) {
            double x = Double.parseDouble(st);
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

    private void process(List<String> lines) {
        Map<String, Supplier<? extends Number>> functions = createMeasuringFunctions(lines);

        double volumeMB = lines.stream().mapToInt(String::length).sum() / (1024. * 1024.);

        // Measure
        System.out.printf("Trying to reach a confidence level of %,.1f %% which only deviates by %,.0f %% from the average measured duration.\n",
                100 * CONFIDENCE_LEVEL, 100 * CONFIDENCE_INTERVAL_WIDTH);
        Map<String, VarianceStatistics> results = new LinkedHashMap<>();
        for (Map.Entry<String, Supplier<? extends Number>> entry : functions.entrySet()) {
            VarianceStatistics warmup = measure(entry.getValue(), NUMBER_OF_TRIALS, CONFIDENCE_LEVEL, CONFIDENCE_INTERVAL_WIDTH);
            results.put(entry.getKey(), warmup);
        }
        for (Map.Entry<String, Supplier<? extends Number>> entry : functions.entrySet()) {
            VarianceStatistics stats = measure(entry.getValue(), NUMBER_OF_TRIALS, CONFIDENCE_LEVEL, CONFIDENCE_INTERVAL_WIDTH);
            results.put(entry.getKey(), stats);
        }

        // Print measurements
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
        String reference = "Double";
        VarianceStatistics referenceStats = results.get(reference);
        for (Map.Entry<String, VarianceStatistics> entry : results.entrySet()) {
            String name = entry.getKey();
            if (!reference.equals(name)) {
                VarianceStatistics stats = entry.getValue();
                System.out.printf("Speedup %-17s vs %s: %,.2f\n", name, reference, referenceStats.getAverage() / stats.getAverage());

            }
        }
    }

    private Map<String, Supplier<? extends Number>> createMeasuringFunctions(List<String> lines) {
        List<byte[]> byteArrayLines = lines.stream().map(l -> l.getBytes(StandardCharsets.ISO_8859_1)).collect(Collectors.toList());
        List<char[]> charArrayLines = lines.stream().map(String::toCharArray).collect(Collectors.toList());


        Map<String, Supplier<? extends Number>> functions = new LinkedHashMap<>();
        functions.put("FastDouble String", () -> sumFastDoubleFromCharSequence(lines));
        functions.put("FastDouble char[]", () -> sumFastDoubleParserFromCharArray(charArrayLines));
        functions.put("FastDouble byte[]", () -> sumFastDoubleParserFromByteArray(byteArrayLines));
        functions.put("Double", () -> sumDoubleParseDouble(lines));
        functions.put("FastFloat  String", () -> sumFastFloatFromCharSequence(lines));
        functions.put("FastFloat  char[]", () -> sumFastFloatParserFromCharArray(charArrayLines));
        functions.put("FastFloat  byte[]", () -> sumFastFloatParserFromByteArray(byteArrayLines));
        // functions.put("FastFloat  vector", () -> sumFastFloatParserFromVector(byteArrayLines));
        functions.put("Float", () -> sumFloatParseFloat(lines));
        return functions;
    }

    private void printStatsHeaderMarkdown() {
        System.out.println("Method           | MB/s  |stdev|Mfloats/s| ns/f   | JDK");
        System.out.println("-----------------|------:|-----:|------:|--------:|--------");
    }

    private void printStatsMarkdown(List<String> lines, double volumeMB, String name, VarianceStatistics stats) {
        System.out.printf("%-17s|%7.2f|%4.1f %%|%7.2f|%9.2f|%s\n",
                name,
                volumeMB * 1e9 / stats.getAverage(),
                stats.getSampleStandardDeviation() * 100 / stats.getAverage(),
                lines.size() * (1e9 / 1_000_000) / stats.getAverage(),
                stats.getAverage() / lines.size(),
                System.getProperty("java.version")
        );
        /*
        double confidenceWidth = Stats.confidence(1 - CONFIDENCE_LEVEL, stats.getSampleStandardDeviation(), stats.getCount()) / stats.getAverage();
        System.out.printf("%-30s :  %7.2f MB/s (+/-%4.1f %% stdv) (+/-%4.1f %% conf)  %7.2f Mfloat/s  %7.2f ns/f\n",
                name,
                volumeMB *1e9 / stats.getAverage(),
                stats.getSampleStandardDeviation() * 100 / stats.getAverage(),
                100*confidenceWidth,
                lines.size()*(1e9/1_000_000) / stats.getAverage(),
                stats.getAverage() / lines.size()
        );*/
    }

    private void printStatsAscii(List<String> lines, double volumeMB, String name, VarianceStatistics stats) {
        System.out.printf("%-17s :  %7.2f MB/s (+/-%4.1f %%)  %7.2f Mfloat/s  %9.2f ns/f\n",
                name,
                volumeMB * 1e9 / stats.getAverage(),
                stats.getSampleStandardDeviation() * 100 / stats.getAverage(),
                lines.size() * (1e9 / 1_000_000) / stats.getAverage(),
                stats.getAverage() / lines.size()
        );
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

    private void validate(List<String> lines) {
        Map<String, Supplier<? extends Number>> map = createMeasuringFunctions(lines);

        Number expectedDoubleValue = sumDoubleParseDouble(lines);
        Number expectedFloatValue = sumFloatParseFloat(lines);

        for (Map.Entry<String, Supplier<? extends Number>> entry : map.entrySet()) {
            String name = entry.getKey();
            Number actual = entry.getValue().get();

            if ((actual instanceof Double) && !expectedDoubleValue.equals(actual)
                    || (actual instanceof Float) && !expectedFloatValue.equals(actual)) {
                System.err.println(name + " has an error. expectedSum=" + expectedFloatValue + " actualSum=" + actual);

            }
        }
    }

    public void loadFile(String filename) throws IOException {
        Path path = FileSystems.getDefault().getPath(filename).toAbsolutePath();
        System.out.printf("parsing numbers in file %s\n", path);
        List<String> lines = Files.lines(path).collect(Collectors.toList());
        System.out.printf("read %d lines\n", lines.size());
        validate(lines);
        process(lines);
    }
}
