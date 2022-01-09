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

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 18-ea, OpenJDK 64-Bit Server VM, 18-ea+30-2029
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
 *
 * FromByteArrayZero                        avgt   25   5.017 ± 0.031  ns/op
 * FromByteArrayOnePointZero                avgt   25  13.668 ± 0.122  ns/op
 * FromByteArray3Digits                     avgt   25  12.307 ± 0.253  ns/op
 * FromByteArray3DigitsWithDecimalPoint     avgt   25  14.550 ± 0.114  ns/op
 * FromByteArray17DigitsWith3DigitExp       avgt   25  37.845 ± 0.383  ns/op
 * FromByteArray19DigitsWithoutExp          avgt   25  30.804 ± 0.367  ns/op
 * FromByteArray19DigitsWith3DigitExp       avgt   25  33.502 ± 0.183  ns/op
 * FromByteArrayNegative18DigitsWithoutExp  avgt   25  23.563 ± 0.308  ns/op
 * FromByteArray14HexDigitsWith3DigitExp    avgt   25  30.711 ± 0.475  ns/op
 * </pre>
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 17, OpenJDK 64-Bit Server VM, 17+35-2724
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark                                Mode  Cnt   Score   Error  Units
 * FromByteArrayZero                        avgt   25   2.718 ± 0.010  ns/op
 * FromByteArrayOnePointZero                avgt   25  12.087 ± 0.190  ns/op
 * FromByteArray3Digits                     avgt   25  10.803 ± 0.117  ns/op
 * FromByteArray3DigitsWithDecimalPoint     avgt   25  12.853 ± 0.156  ns/op
 * FromByteArray17DigitsWith3DigitExp       avgt   25  35.246 ± 0.248  ns/op
 * FromByteArray19DigitsWith3DigitExp       avgt   25  31.426 ± 0.296  ns/op
 * FromByteArray19DigitsWithoutExp          avgt   25  27.941 ± 0.132  ns/op
 * FromByteArrayNegative18DigitsWithoutExp  avgt   25  21.638 ± 0.225  ns/op
 * FromByteArray14HexDigitsWith3DigitExp    avgt   25  27.389 ± 0.479  ns/op
 * </pre>
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 11.0.8, Java HotSpot(TM) 64-Bit Server VM, 11.0.8+10-LTS
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark                                Mode  Cnt   Score   Error  Units
 * FromByteArrayZero                        avgt   25   2,721 ± 0,010  ns/op
 * FromByteArrayOnePointZero                avgt   25  14,717 ± 0,060  ns/op
 * FromByteArray3Digits                     avgt   25  12,088 ± 0,180  ns/op
 * FromByteArray3DigitsWithDecimalPoint     avgt   25  14,393 ± 0,215  ns/op
 * FromByteArray17DigitsWith3DigitExp       avgt   25  41,526 ± 2,092  ns/op
 * FromByteArray19DigitsWith3DigitExp       avgt   25  38,262 ± 0,783  ns/op
 * FromByteArray19DigitsWithoutExp          avgt   25  31,012 ± 1,736  ns/op
 * FromByteArrayNegative18DigitsWithoutExp  avgt   25  23,366 ± 0,511  ns/op
 * FromByteArray14HexDigitsWith3DigitExp    avgt   25  32,841 ± 0,152  ns/op
 * </pre>
 */
@Fork(value = 5, jvmArgsAppend = {"-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"})
@Measurement(iterations = 5)
@Warmup(iterations = 4)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class FastDoubleParserFromByteArrayJmhBenchmark {
    private final static byte[] ISO_ZERO = "0".getBytes(StandardCharsets.ISO_8859_1);
    private final static byte[] ISO_ONE_POINT_ZERO = "1.0".getBytes(StandardCharsets.ISO_8859_1);
    private final static byte[] ISO_18_DIGITS_WITHOUT_EXP = "-0.29235596393453456".getBytes(StandardCharsets.ISO_8859_1);
    private final static byte[] ISO_3_DIGITS_WITH_DECIMAL_POINT = "10.1".getBytes(StandardCharsets.ISO_8859_1);
    private final static byte[] ISO_3_DIGITS = "365".getBytes(StandardCharsets.ISO_8859_1);
    private final static byte[] ISO_19_DIGITS_WITHOUT_EXP = "123.4567890123456789".getBytes(StandardCharsets.ISO_8859_1);
    private final static byte[] ISO_19_DIGITS_WITH_3_DIGIT_EX = "123.4567890123456789e123".getBytes(StandardCharsets.ISO_8859_1);
    private final static byte[] ISO_17_DIGITS_WITH_3_DIGIT_EXP = "123.45678901234567e123".getBytes(StandardCharsets.ISO_8859_1);
    private final static byte[] ISO_14_HEX_DIGITS_WITH_3_DIGIT_EXP = "0x123.456789abcdep123".getBytes(StandardCharsets.ISO_8859_1);

    @Benchmark
    public double m01FromByteArrayZero() {
        return FastDoubleParserFromByteArray.parseDouble(ISO_ZERO);
    }

    @Benchmark
    public double m02FromByteArrayOnePointZero() {
        return FastDoubleParserFromByteArray.parseDouble(ISO_ONE_POINT_ZERO);
    }

    @Benchmark
    public double m03FromByteArray3Digits() {
        return FastDoubleParserFromByteArray.parseDouble(ISO_3_DIGITS);
    }

    @Benchmark
    public double m04FromByteArray3DigitsWithDecimalPoint() {
        return FastDoubleParserFromByteArray.parseDouble(ISO_3_DIGITS_WITH_DECIMAL_POINT);
    }

    @Benchmark
    public double m05FromByteArray17DigitsWith3DigitExp() {
        return FastDoubleParserFromByteArray.parseDouble(ISO_17_DIGITS_WITH_3_DIGIT_EXP);
    }

    @Benchmark
    public double m06FromByteArray19DigitsWithoutExp() {
        return FastDoubleParserFromByteArray.parseDouble(ISO_19_DIGITS_WITHOUT_EXP);
    }

    @Benchmark
    public double m07FromByteArray19DigitsWith3DigitExp() {
        return FastDoubleParserFromByteArray.parseDouble(ISO_19_DIGITS_WITH_3_DIGIT_EX);
    }

    @Benchmark
    public double m08FromByteArrayNegative18DigitsWithoutExp() {
        return FastDoubleParserFromByteArray.parseDouble(ISO_18_DIGITS_WITHOUT_EXP);
    }

    @Benchmark
    public double m09FromByteArray14HexDigitsWith3DigitExp() {
        return FastDoubleParserFromByteArray.parseDouble(ISO_14_HEX_DIGITS_WITH_3_DIGIT_EXP);
    }
}


