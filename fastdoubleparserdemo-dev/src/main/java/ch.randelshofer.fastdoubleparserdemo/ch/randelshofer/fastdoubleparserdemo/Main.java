/*
 * @(#)Main.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparserdemo;

import ch.randelshofer.fastdoubleparser.JavaBigDecimalParser;
import ch.randelshofer.fastdoubleparser.JavaDoubleParser;
import ch.randelshofer.fastdoubleparser.JavaFloatParser;
import ch.randelshofer.fastdoubleparser.JsonDoubleParser;
import ch.randelshofer.fastdoubleparser.LenientDoubleParser;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This benchmark for {@link ch.randelshofer.fastdoubleparser.JavaDoubleParser} aims to provide results that
 * can be compared easily with the benchmark of Daniel Lemire's fast_double_parser.
 * <p>
 * Most of the code in this class stems from
 * <a href="https://github.com/lemire/fast_double_parser/blob/master/benchmarks/benchmark.cpp">github.com/lemire/fast_double_parser</a>
 * <p>
 * The code runs the benchmark multiple times, until the confidence interval
 * for a confidence level of {@value MEASUREMENT_CONFIDENCE_LEVEL}
 * is smaller than {@value MEASUREMENT_CONFIDENCE_INTERVAL_WIDTH} of the average
 * measured time.
 * <p>
 * It then prints the average times, standard deviations and confidence intervals.
 * <p>
 * References:
 * <dl>
 *     <dt>Daniel Lemire, fast_float number parsing library: 4x faster than strtod.
 *     <a href="https://github.com/fastfloat/fast_float/blob/cc1e01e9eee74128e48d51488a6b1df4a767a810/LICENSE-MIT">MIT License</a>.</dt>
 *     <dd><a href="https://github.com/fastfloat/fast_float">github.com</a></dd>
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
     * the test samples.
     * <p>
     * Must be large enough so that the JVM hits the C2 compiler.
     */
    public static final int WARMUP_NUMBER_OF_TRIALS = 256;

    /**
     * Desired confidence interval width in percent of the average value.
     */
    private static final double MEASUREMENT_CONFIDENCE_INTERVAL_WIDTH = 0.02;

    /**
     * Desired confidence interval width in percent of the average value.
     */
    private static final double WARMUP_CONFIDENCE_INTERVAL_WIDTH = 0.02;

    /**
     * One minus desired confidence level in percent.
     */
    private static final double MEASUREMENT_CONFIDENCE_LEVEL = 0.99;

    /**
     * One minus desired confidence level in percent.
     */
    private static final double WARMUP_CONFIDENCE_LEVEL = 0.99;
    /**
     * Must be large enough, so that Java hits the C2 compiler.
     */
    private static final int WARMUP_MIN_TRIALS = 256;
    private String filename = null;
    private boolean markdown = false;
    private boolean sleep = false;
    private Locale locale = Locale.ENGLISH;
    private boolean printConfidence = false;

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
                benchmark.printConfidence = true;
                break;
                case "--locale":
                    benchmark.locale = Locale.forLanguageTag(args[++i]);
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
        List<BenchmarkFunction> benchmarkFunctions = Arrays.asList(
                new BenchmarkFunction("java.lang.Double", "java.lang.Double", () -> sumJavaLangDouble(lines)),
                new BenchmarkFunction("java.lang.Float", "java.lang.Float", () -> sumJavaLangFloat(lines)),
                new BenchmarkFunction("java.math.BigDecimal", "java.math.BigDecimal", () -> sumJavaLangBigDecimal(lines)),
                new BenchmarkFunction("java.text.NumberFormat", "java.text.NumberFormat", () -> sumJavaTextNumberFormat(lines, locale)),

                new BenchmarkFunction("JavaDoubleParser CharSequence", "java.lang.Double", () -> sumFastDoubleFromCharSequence(lines)),
                new BenchmarkFunction("JavaDoubleParser String", "java.lang.Double", () -> sumFastDoubleFromString(lines)),
                new BenchmarkFunction("JavaDoubleParser char[]", "java.lang.Double", () -> sumFastDoubleParserFromCharArray(charArrayLines)),
                new BenchmarkFunction("JavaDoubleParser byte[]", "java.lang.Double", () -> sumFastDoubleParserFromByteArray(byteArrayLines)),

                new BenchmarkFunction("JsonDoubleParser String", "java.lang.Double", () -> sumJsonDoubleFromString(lines)),
                new BenchmarkFunction("JsonDoubleParser CharSequence", "java.lang.Double", () -> sumJsonDoubleFromCharSequence(lines)),
                new BenchmarkFunction("JsonDoubleParser char[]", "java.lang.Double", () -> sumJsonDoubleParserFromCharArray(charArrayLines)),
                new BenchmarkFunction("JsonDoubleParser byte[]", "java.lang.Double", () -> sumJsonDoubleParserFromByteArray(byteArrayLines)),

                new BenchmarkFunction("JavaFloatParser  CharSequence", "java.lang.Float", () -> sumFastFloatFromCharSequence(lines)),
                new BenchmarkFunction("JavaFloatParser  String", "java.lang.Float", () -> sumFastFloatFromString(lines)),
                new BenchmarkFunction("JavaFloatParser  char[]", "java.lang.Float", () -> sumFastFloatParserFromCharArray(charArrayLines)),
                new BenchmarkFunction("JavaFloatParser  byte[]", "java.lang.Float", () -> sumFastFloatParserFromByteArray(byteArrayLines)),

                new BenchmarkFunction("JavaBigDecimalParser CharSequence", "java.math.BigDecimal", () -> sumFastBigDecimalFromCharSequence(lines)),
                new BenchmarkFunction("JavaBigDecimalParser char[]", "java.math.BigDecimal", () -> sumFastBigDecimalFromCharArray(charArrayLines)),
                new BenchmarkFunction("JavaBigDecimalParser byte[]", "java.math.BigDecimal", () -> sumFastBigDecimalFromByteArray(byteArrayLines)),
                new BenchmarkFunction("LenientDoubleParser CharSequence", "java.text.NumberFormat", () -> sumLenientDoubleFromCharSequence(lines)),
                new BenchmarkFunction("LenientDoubleParser String", "java.text.NumberFormat", () -> sumLenientDoubleFromString(lines))

        );
        for (BenchmarkFunction b : benchmarkFunctions) {
            functions.put(b.title, b);
        }

        return functions;
    }

    public void demo(int howMany) {
        System.out.println("Parsing random doubles in the range [0,1).");
        List<String> lines = new Random().doubles(howMany).mapToObj(Double::toString)
                .collect(Collectors.toList());
        Map<String, BenchmarkFunction> validated = validate(lines);
        process(lines, validated);
    }

    public void loadFile(String filename) throws IOException {
        Path path = FileSystems.getDefault().getPath(filename).toAbsolutePath();
        System.out.printf("Parsing numbers in file %s\n", path);
        List<String> lines = Files.lines(path).collect(Collectors.toList());
        System.out.printf("Read %d lines\n", lines.size());
        Map<String, BenchmarkFunction> validated = validate(lines);
        process(lines, validated);
    }

    private VarianceStatistics measure(Supplier<? extends Number> func, int numberOfTrials,
                                       double confidenceLevel, double confidenceIntervalWidth, int minTrials) {
        long t1;
        double confidenceWidth;
        long t2;
        double elapsed;
        VarianceStatistics stats = new VarianceStatistics();

        // measure
        int trials = 0;
        do {
            for (int i = 0; i < numberOfTrials; i++) {
                t1 = System.nanoTime();
                func.get();
                t2 = System.nanoTime();
                elapsed = t2 - t1;
                stats.accept(elapsed);
            }
            confidenceWidth = Stats.confidence(1 - confidenceLevel, stats.getSampleStandardDeviation(), stats.getCount()) / stats.getAverage();
            trials += numberOfTrials;
        } while (trials < minTrials || confidenceWidth > confidenceIntervalWidth);
        return stats;
    }

    private void printStatsAscii(List<String> lines, double volumeMB, String name, VarianceStatistics stats, double confidenceLevel, Map<String, BenchmarkFunction> functions, Map<String, VarianceStatistics> results) {
        double speedup = computeSpeedup(name, stats, functions, results);
        if (printConfidence) {
            double confidenceWidth = Stats.confidence(1 - confidenceLevel, stats.getSampleStandardDeviation(), stats.getCount()) / stats.getAverage();

            System.out.printf("%-33s :  %7.2f MB/s (+/-%4.1f %% stdv) (+/-%4.1f %% conf, %6d trials)  %7.2f Mfloat/s  %7.2f ns/f  %4.2f speedup\n",
                    name,
                    volumeMB * 1e9 / stats.getAverage(),
                    stats.getSampleStandardDeviation() * 100 / stats.getAverage(),
                    100 * confidenceWidth,
                    stats.getCount(),
                    lines.size() * (1e9 / 1_000_000) / stats.getAverage(),
                    stats.getAverage() / lines.size(),
                    speedup
            );
        } else {
            System.out.printf("%-33s :  %7.2f MB/s (+/-%4.1f %%)  %7.2f Mfloat/s  %9.2f ns/f  %7.2f speedup\n",
                    name,
                    volumeMB * 1e9 / stats.getAverage(),
                    stats.getSampleStandardDeviation() * 100 / stats.getAverage(),
                    lines.size() * (1e9 / 1_000_000) / stats.getAverage(),
                    stats.getAverage() / lines.size(),
                    speedup
            );
        }
    }

    private static double computeSpeedup(String name, VarianceStatistics stats, Map<String, BenchmarkFunction> functions, Map<String, VarianceStatistics> results) {
        double speedup;
        String reference = functions.get(name).reference;
        if (!reference.equals(name)) {
            VarianceStatistics referenceStats = results.get(reference);
            speedup = referenceStats == null ? 1 : referenceStats.getAverage() / stats.getAverage();
        } else {
            speedup = 1;
        }
        return speedup;
    }

    private void printStatsHeaderMarkdown() {
        System.out.println("|Method                           | MB/s  |stdev|Mfloats/s| ns/f   | speedup | JDK    |");
        System.out.println("|---------------------------------|------:|-----:|------:|--------:|--------:|--------|");
    }

    private void printStatsMarkdown(List<String> lines, double volumeMB, String name, VarianceStatistics stats, Map<String, BenchmarkFunction> functions, Map<String, VarianceStatistics> results) {
        double speedup = computeSpeedup(name, stats, functions, results);
        System.out.printf("|%-33s|%7.2f|%4.1f %%|%7.2f|%9.2f|%9.2f|%-8s|\n",
                name,
                volumeMB * 1e9 / stats.getAverage(),
                stats.getSampleStandardDeviation() * 100 / stats.getAverage(),
                lines.size() * (1e9 / 1_000_000) / stats.getAverage(),
                stats.getAverage() / lines.size(),
                speedup,
                System.getProperty("java.version")
        );
    }

    private void process(List<String> lines, Map<String, BenchmarkFunction> functions) {
        double volumeMB = lines.stream().mapToInt(String::length).sum() / (1024. * 1024.);

        // Warm up
        System.out.printf("Warming Up: Trying to reach a confidence level of %,.1f %% which only deviates by %,.0f %% from the average measured duration.\n",
                100 * WARMUP_CONFIDENCE_LEVEL, 100 * WARMUP_CONFIDENCE_INTERVAL_WIDTH);
        Map<String, VarianceStatistics> warmupResults = new LinkedHashMap<>();
        for (Map.Entry<String, BenchmarkFunction> entry : functions.entrySet()) {
            //System.out.println("  " + entry.getKey() + " ...");
            System.out.print(".");
            System.out.flush();
            VarianceStatistics warmup = measure(entry.getValue().supplier, WARMUP_NUMBER_OF_TRIALS, WARMUP_CONFIDENCE_LEVEL, WARMUP_CONFIDENCE_INTERVAL_WIDTH, WARMUP_MIN_TRIALS);
            warmupResults.put(entry.getKey(), warmup);
            //System.out.println("  " + entry.getKey() + " " + warmup);
        }
        System.out.println();

        sleep();

        System.out.printf("Measuring: Trying to reach a confidence level of %,.1f %% which only deviates by %,.0f %% from the average measured duration.\n",
                100 * MEASUREMENT_CONFIDENCE_LEVEL, 100 * MEASUREMENT_CONFIDENCE_INTERVAL_WIDTH);
        Map<String, VarianceStatistics> results = new LinkedHashMap<>();
        for (Map.Entry<String, BenchmarkFunction> entry : functions.entrySet()) {
            //System.out.println("  " + entry.getKey() + " ...");
            System.out.print(".");
            System.out.flush();

            VarianceStatistics stats = measure(entry.getValue().supplier, MEASUREMENT_NUMBER_OF_TRIALS, MEASUREMENT_CONFIDENCE_LEVEL, MEASUREMENT_CONFIDENCE_INTERVAL_WIDTH, 1);
            results.put(entry.getKey(), stats);
            //System.out.println("  " + entry.getKey() + " " + stats);
        }
        System.out.println();

        // Print results
        //printResults("Warmup results:", lines, volumeMB, warmupResults, WARMUP_CONFIDENCE_LEVEL, functions);
        printResults("Measurement results:", lines, volumeMB, results, MEASUREMENT_CONFIDENCE_LEVEL, functions);
        System.out.println();

    }

    private void printResults(String title, List<String> lines, double volumeMB, Map<String, VarianceStatistics> results, double confidenceLevel, Map<String, BenchmarkFunction> functions) {
        System.out.println();
        System.out.println(title);
        if (markdown) {
            printStatsHeaderMarkdown();
        }
        for (Map.Entry<String, VarianceStatistics> entry : results.entrySet()) {
            String name = entry.getKey();
            VarianceStatistics stats = entry.getValue();
            if (markdown) {
                printStatsMarkdown(lines, volumeMB, name, stats, functions, results);
            } else {
                printStatsAscii(lines, volumeMB, name, stats, confidenceLevel, functions, results);
            }
        }
    }

    private void sleep() {
        if (sleep) {
            System.out.println("sleeping for 10 seconds...");
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                //stop sleeping
            }
            System.out.println("done sleeping...");
        }
    }

    private double sumJavaLangDouble(List<String> s) {
        double answer = 0;
        for (String st : s) {
            double x = Double.parseDouble(st);
            answer += x;
        }
        return answer;
    }

    private int sumJavaLangBigDecimal(List<String> s) {
        int answer = 0;
        for (String st : s) {
            BigDecimal x = new BigDecimal(st);
            answer += x.scale();
        }
        return answer;
    }

    private double sumJavaTextNumberFormat(List<String> s, Locale locale) {
        double answer = 0;
        NumberFormat fmt = NumberFormat.getNumberInstance(locale);
        ParsePosition pos = new ParsePosition(0);
        for (String st : s) {
            pos.setIndex(0);
            double x = fmt.parse(st, pos).doubleValue();
            answer += x;
        }
        return answer;
    }


    private int sumFastBigDecimalFromCharSequence(List<String> s) {
        int answer = 0;
        for (String st : s) {
            BigDecimal x = JavaBigDecimalParser.parseBigDecimal(st);
            answer += x.scale();
        }
        return answer;
    }

    private int sumFastBigDecimalFromByteArray(List<byte[]> s) {
        int answer = 0;
        for (byte[] st : s) {
            BigDecimal x = JavaBigDecimalParser.parseBigDecimal(st);
            answer += x.scale();
        }
        return answer;
    }

    private int sumFastBigDecimalFromCharArray(List<char[]> s) {
        int answer = 0;
        for (char[] st : s) {
            BigDecimal x = JavaBigDecimalParser.parseBigDecimal(st);
            answer += x.scale();
        }
        return answer;
    }

    private double sumFastDoubleFromCharSequence(List<String> s) {
        double answer = 0;
        for (String st : s) {
            double x = JavaDoubleParser.parseDouble((CharSequence) st);
            answer += x;
        }
        return answer;
    }

    private double sumFastDoubleFromString(List<String> s) {
        double answer = 0;
        for (String st : s) {
            double x = JavaDoubleParser.parseDouble(st);
            answer += x;
        }
        return answer;
    }


    private double sumFastDoubleParserFromByteArray(List<byte[]> s) {
        double answer = 0;
        for (byte[] st : s) {
            double x = JavaDoubleParser.parseDouble(st);
            answer += x;
        }
        return answer;
    }

    private double sumFastDoubleParserFromCharArray(List<char[]> s) {
        double answer = 0;
        for (char[] st : s) {
            double x = JavaDoubleParser.parseDouble(st);
            answer += x;
        }
        return answer;
    }

    private float sumFastFloatFromCharSequence(List<String> s) {
        float answer = 0;
        for (String st : s) {
            float x = JavaFloatParser.parseFloat((CharSequence) st);
            answer += x;
        }
        return answer;
    }

    private float sumFastFloatFromString(List<String> s) {
        float answer = 0;
        for (String st : s) {
            float x = JavaFloatParser.parseFloat(st);
            answer += x;
        }
        return answer;
    }

    private float sumFastFloatParserFromByteArray(List<byte[]> s) {
        float answer = 0;
        for (byte[] st : s) {
            float x = JavaFloatParser.parseFloat(st);
            answer += x;
        }
        return answer;
    }

    private float sumFastFloatParserFromCharArray(List<char[]> s) {
        float answer = 0;
        for (char[] st : s) {
            float x = JavaFloatParser.parseFloat(st);
            answer += x;
        }
        return answer;
    }

    private double sumLenientDoubleFromCharSequence(List<String> s) {
        double answer = 0;
        LenientDoubleParser p = new LenientDoubleParser(new DecimalFormat().getDecimalFormatSymbols());
        for (String st : s) {
            double x = p.parseDouble((CharSequence) st);
            answer += x;
        }
        return answer;
    }

    private double sumLenientDoubleFromString(List<String> s) {
        double answer = 0;
        LenientDoubleParser p = new LenientDoubleParser(new DecimalFormat().getDecimalFormatSymbols());
        for (String st : s) {
            double x = p.parseDouble(st);
            answer += x;
        }
        return answer;
    }

    private float sumJavaLangFloat(List<String> s) {
        float answer = 0;
        for (String st : s) {
            float x = Float.parseFloat(st);
            answer += x;
        }
        return answer;
    }

    private double sumJsonDoubleFromCharSequence(List<String> s) {
        double answer = 0;
        for (String st : s) {
            double x = JsonDoubleParser.parseDouble((CharSequence) st);
            answer += x;
        }
        return answer;
    }

    private double sumJsonDoubleFromString(List<String> s) {
        double answer = 0;
        for (String st : s) {
            double x = JsonDoubleParser.parseDouble(st);
            answer += x;
        }
        return answer;
    }

    private double sumJsonDoubleParserFromByteArray(List<byte[]> s) {
        double answer = 0;
        for (byte[] st : s) {
            double x = JsonDoubleParser.parseDouble(st);
            answer += x;
        }
        return answer;
    }

    private double sumJsonDoubleParserFromCharArray(List<char[]> s) {
        double answer = 0;
        for (char[] st : s) {
            double x = JsonDoubleParser.parseDouble(st);
            answer += x;
        }
        return answer;
    }


    private Map<String, BenchmarkFunction> validate(List<String> lines) {
        Map<String, BenchmarkFunction> map = createBenchmarkFunctions(lines);

        Map<String, Number> results = new LinkedHashMap<>();

        for (Iterator<Map.Entry<String, BenchmarkFunction>> iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, BenchmarkFunction> entry = iterator.next();
            String name = entry.getKey();
            try {
                BenchmarkFunction function = entry.getValue();
                Number actual = function.supplier.get();
                results.put(function.title, actual);
            } catch (NumberFormatException e) {
                System.err.println(name + " has encountered an error: " + e);
                iterator.remove();
            }
        }

        // Check results
        for (Iterator<Map.Entry<String, BenchmarkFunction>> iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, BenchmarkFunction> entry = iterator.next();
            BenchmarkFunction function = entry.getValue();
            Number expected = results.get(function.reference);
            Number actual = results.get(function.title);
            if (!Objects.equals(expected, actual)) {
                System.err.println(function.title + " has computed the wrong sum: expectedSum=" + expected + " actualSum=" + actual);
                iterator.remove();
            }
        }

        return map;
    }

    /*
    record BenchmarkFunction(String title, String reference,
                             Supplier<? extends Number> supplier) {
    }
    */

    static final class BenchmarkFunction {
        private final String title;
        private final String reference;
        private final Supplier<? extends Number> supplier;

        BenchmarkFunction(String title, String reference,
                          Supplier<? extends Number> supplier) {
            this.title = title;
            this.reference = reference;
            this.supplier = supplier;
        }

        public String title() {
            return title;
        }

        public String reference() {
            return reference;
        }

        public Supplier<? extends Number> supplier() {
            return supplier;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            BenchmarkFunction that = (BenchmarkFunction) obj;
            return Objects.equals(this.title, that.title) &&
                    Objects.equals(this.reference, that.reference) &&
                    Objects.equals(this.supplier, that.supplier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, reference, supplier);
        }

        @Override
        public String toString() {
            return "BenchmarkFunction[" +
                    "title=" + title + ", " +
                    "reference=" + reference + ", " +
                    "supplier=" + supplier + ']';
        }

    }
}
