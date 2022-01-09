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
 * # VM version: JDK 18-ea, OpenJDK 64-Bit Server VM, 18-ea+30-2029
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz, SIMD-256
 *
 * Benchmark                                Mode  Cnt   Score   Error  Units  Regression
 * FromCharArrayZero                        avgt   25   4.801 ± 0.055  ns/op  +
 * FromCharArrayOnePointZero                avgt   25  14.022 ± 0.107  ns/op  +
 * FromCharArray3Digits                     avgt   25  12.483 ± 0.163  ns/op  +
 * FromCharArray3DigitsWithDecimalPoint     avgt   25  14.811 ± 0.284  ns/op  +
 * FromCharArray17DigitsWith3DigitExp       avgt   25  40.581 ± 1.916  ns/op  +
 * FromCharArray19DigitsWithoutExp          avgt   25  32.341 ± 0.246  ns/op  +
 * FromCharArray19DigitsWith3DigitExp       avgt   25  33.872 ± 0.892  ns/op  +
 * FromCharArrayNegative18DigitsWithoutExp  avgt   25  25.057 ± 0.309  ns/op  -
 * FromCharArray14HexDigitsWith3DigitExp    avgt   25  30.508 ± 1.785  ns/op  +
 * </pre>
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 17, OpenJDK 64-Bit Server VM, 17+35-2724
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark                                Mode  Cnt   Score   Error  Units
 * FromCharArrayZero                        avgt   25   2.934 ± 0.009  ns/op
 * FromCharArrayOnePointZero                avgt   25  11.503 ± 0.048  ns/op
 * FromCharArray3Digits                     avgt   25  10.661 ± 0.246  ns/op
 * FromCharArray3DigitsWithDecimalPoint     avgt   25  12.176 ± 0.054  ns/op
 * FromCharArray17DigitsWith3DigitExp       avgt   25  33.858 ± 0.428  ns/op
 * FromCharArray19DigitsWithoutExp          avgt   25  30.264 ± 0.461  ns/op
 * FromCharArray19DigitsWith3DigitExp       avgt   25  35.607 ± 0.543  ns/op
 * FromCharArrayNegative18DigitsWithoutExp  avgt   25  29.087 ± 0.132  ns/op
 * FromCharArray14HexDigitsWith3DigitExp    avgt   25  26.870 ± 0.174  ns/op
 * </pre>
 */
@Fork(value = 5, jvmArgsAppend = {"-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"})
@Measurement(iterations = 5)
@Warmup(iterations = 4)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class FastDoubleParserFromCharArrayJmhBenchmark {
    private final static char[] CHARS_ZERO = "0".toCharArray();
    private final static char[] CHARS_ONE_POINT_ZERO = "1.0".toCharArray();
    private final static char[] CHARS_18_DIGITS_WITHOUT_EXP = "-0.29235596393453456".toCharArray();
    private final static char[] CHARS_3_DIGITS_WITH_DECIMAL_POINT = "10.1".toCharArray();
    private final static char[] CHARS_3_DIGITS = "365".toCharArray();
    private final static char[] CHARS_19_DIGITS_WITHOUT_EXP = "123.4567890123456789".toCharArray();
    private final static char[] CHARS_19_DIGITS_WITH_3_DIGIT_EX = "123.4567890123456789e123".toCharArray();
    private final static char[] CHARS_17_DIGITS_WITH_3_DIGIT_EXP = "123.45678901234567e123".toCharArray();
    private final static char[] CHARS_14_HEX_DIGITS_WITH_3_DIGIT_EXP = "0x123.456789abcdep123".toCharArray();

    @Benchmark
    public double m01FromCharArrayZero() {
        return FastDoubleParserFromCharArray.parseDouble(CHARS_ZERO);
    }

    @Benchmark
    public double m02FromCharArrayOnePointZero() {
        return FastDoubleParserFromCharArray.parseDouble(CHARS_ONE_POINT_ZERO);
    }

    @Benchmark
    public double m03FromCharArray3Digits() {
        return FastDoubleParserFromCharArray.parseDouble(CHARS_3_DIGITS);
    }

    @Benchmark
    public double m04FromCharArray3DigitsWithDecimalPoint() {
        return FastDoubleParserFromCharArray.parseDouble(CHARS_3_DIGITS_WITH_DECIMAL_POINT);
    }

    @Benchmark
    public double m05FromCharArray17DigitsWith3DigitExp() {
        return FastDoubleParserFromCharArray.parseDouble(CHARS_17_DIGITS_WITH_3_DIGIT_EXP);
    }

    @Benchmark
    public double m06FromCharArray19DigitsWithoutExp() {
        return FastDoubleParserFromCharArray.parseDouble(CHARS_19_DIGITS_WITHOUT_EXP);
    }

    @Benchmark
    public double m07FromCharArray19DigitsWith3DigitExp() {
        return FastDoubleParserFromCharArray.parseDouble(CHARS_19_DIGITS_WITH_3_DIGIT_EX);
    }

    @Benchmark
    public double m08FromCharArrayNegative18DigitsWithoutExp() {
        return FastDoubleParserFromCharArray.parseDouble(CHARS_18_DIGITS_WITHOUT_EXP);
    }

    @Benchmark
    public double m09FromCharArray14HexDigitsWith3DigitExp() {
        return FastDoubleParserFromCharArray.parseDouble(CHARS_14_HEX_DIGITS_WITH_3_DIGIT_EXP);
    }
}


