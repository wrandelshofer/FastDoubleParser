/*
 * @(#)DoubleParserJmhBenchmark.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
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
 * FromCharArray19DigitsWith3DigitExp       avgt   25  35.607 ± 0.543  ns/op
 * FromCharArray19DigitsWithoutExp          avgt   25  30.264 ± 0.461  ns/op
 * FromCharArrayNegative18DigitsWithoutExp  avgt   25  29.087 ± 0.132  ns/op
 * FromCharArray14HexDigitsWith3DigitExp    avgt   25  26.870 ± 0.174  ns/op
 * </pre>
 */
@Fork(jvmArgsAppend = {"-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"})
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
    public double measureFromCharArray14HexDigitsWith3DigitExp() {
        return FastDoubleParserFromCharArray.parseDouble(CHARS_14_HEX_DIGITS_WITH_3_DIGIT_EXP);
    }

    @Benchmark
    public double measureFromCharArray17DigitsWith3DigitExp() {
        return FastDoubleParserFromCharArray.parseDouble(CHARS_17_DIGITS_WITH_3_DIGIT_EXP);
    }

    @Benchmark
    public double measureFromCharArray19DigitsWith3DigitExp() {
        return FastDoubleParserFromCharArray.parseDouble(CHARS_19_DIGITS_WITH_3_DIGIT_EX);
    }

    @Benchmark
    public double measureFromCharArray19DigitsWithoutExp() {
        return FastDoubleParserFromCharArray.parseDouble(CHARS_19_DIGITS_WITHOUT_EXP);
    }

    @Benchmark
    public double measureFromCharArray3Digits() {
        return FastDoubleParserFromCharArray.parseDouble(CHARS_3_DIGITS);
    }

    @Benchmark
    public double measureFromCharArray3DigitsWithDecimalPoint() {
        return FastDoubleParserFromCharArray.parseDouble(CHARS_3_DIGITS_WITH_DECIMAL_POINT);
    }

    @Benchmark
    public double measureFromCharArrayNegative18DigitsWithoutExp() {
        return FastDoubleParserFromCharArray.parseDouble(CHARS_18_DIGITS_WITHOUT_EXP);
    }

    @Benchmark
    public double measureFromCharArrayOnePointZero() {
        return FastDoubleParserFromCharArray.parseDouble(CHARS_ONE_POINT_ZERO);
    }

    @Benchmark
    public double measureFromCharArrayZero() {
        return FastDoubleParserFromCharArray.parseDouble(CHARS_ZERO);
    }
}


