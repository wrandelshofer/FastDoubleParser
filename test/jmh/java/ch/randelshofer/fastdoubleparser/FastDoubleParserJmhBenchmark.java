/*
 * @(#)DoubleParserJmhBenchmark.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 17.0.1, OpenJDK 64-Bit Server VM, 17.0.1+12-jvmci-21.3-b05
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * with Vector and singleton:
 *
 * Benchmark                       Mode  Cnt   Score   Error  Units
 * FromZero                        avgt   25   4.553 ± 1.350  ns/op
 * FromOnePointZero                avgt   25  13.631 ± 0.081  ns/op
 * From3Digits                     avgt   25  12.385 ± 0.146  ns/op
 * From3DigitsWithDecimalPoint     avgt   25  14.460 ± 0.254  ns/op
 * From17DigitsWith3DigitExp       avgt   25  55.069 ± 4.114  ns/op//regression
 * From19DigitsWithoutExp          avgt   25  31.342 ± 0.406  ns/op
 * From19DigitsWith3DigitExp       avgt   25  39.758 ± 0.978  ns/op
 * FromNegative18DigitsWithoutExp  avgt   25  29.071 ± 0.151  ns/op
 * From14HexDigitsWith3DigitExp    avgt   25  28.406 ± 0.341  ns/op
 *
 * Current version with SWAR:
 *
 * Benchmark                       Mode  Cnt   Score   Error  Units
 * From17DigitsWith3DigitExp       avgt   25  46.076 ± 1.663  ns/op (regression)
 * From19DigitsWithoutExp          avgt   25  30.204 ± 0.668  ns/op
 * From19DigitsWith3DigitExp       avgt   25  38.357 ± 0.410  ns/op (regression)
 * FromNegative18DigitsWithoutExp  avgt   25  26.533 ± 0.470  ns/op
 * From14HexDigitsWith3DigitExp    avgt   25  26.959 ± 1.069  ns/op
 *
 * Previous version without SWAR:
 *
 * Benchmark                       Mode  Cnt   Score   Error  Units
 * FromZero                        avgt    4   2.067 ± 0.130  ns/op
 * FromOnePointZero                avgt    4   2.304 ± 0.030  ns/op
 * From3Digits                     avgt    4   2.291 ± 0.093  ns/op
 * From3DigitsWithDecimalPoint     avgt    4   2.285 ± 0.031  ns/op
 * From17DigitsWith3DigitExp       avgt    4  32.242 ± 0.728  ns/op
 * From19DigitsWithoutExp          avgt    4  30.658 ± 0.366  ns/op
 * From19DigitsWith3DigitExp       avgt    4  34.448 ± 0.801  ns/op
 * FromNegative18DigitsWithoutExp  avgt    4  29.039 ± 1.615  ns/op
 * From14HexDigitsWith3DigitExp    avgt    4  25.044 ± 0.687  ns/op
 * </pre>
 */
@Fork(value = 5, jvmArgsAppend = {"-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        //       ,"-XX:+UnlockDiagnosticVMOptions", "-XX:PrintAssemblyOptions=intel", "-XX:CompileCommand=print,ch/randelshofer/fastdoubleparser/FastDoubleParser.*"

})
@Measurement(iterations = 5)
@Warmup(iterations = 4)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class FastDoubleParserJmhBenchmark {

    @Benchmark
    public double m01FromZero() {
        String str = "0";
        return FastDoubleParser.parseDouble((CharSequence) str);
    }

    @Benchmark
    public double m02FromOnePointZero() {
        String str = "1.0";
        return FastDoubleParser.parseDouble((CharSequence) str);
    }

    @Benchmark
    public double m03From3Digits() {
        String str = "365";
        return FastDoubleParser.parseDouble((CharSequence) str);
    }

    @Benchmark
    public double m04From3DigitsWithDecimalPoint() {
        String str = "10.1";
        return FastDoubleParser.parseDouble((CharSequence) str);
    }

    @Benchmark
    public double m05From17DigitsWith3DigitExp() {
        String str = "123.45678901234567e123";
        return FastDoubleParser.parseDouble((CharSequence) str);
    }

    @Benchmark
    public double m06From19DigitsWithoutExp() {
        String str = "123.4567890123456789";
        return FastDoubleParser.parseDouble((CharSequence) str);
    }

    @Benchmark
    public double m07From19DigitsWith3DigitExp() {
        String str = "123.4567890123456789e123";
        return FastDoubleParser.parseDouble((CharSequence) str);
    }

    @Benchmark
    public double m08FromNegative18DigitsWithoutExp() {
        String str = "-0.29235596393453456";
        return FastDoubleParser.parseDouble((CharSequence) str);
    }

    @Benchmark
    public double m09From14HexDigitsWith3DigitExp() {
        String str = "0x123.456789abcdep123";
        return FastDoubleParser.parseDouble((CharSequence) str);
    }

}


