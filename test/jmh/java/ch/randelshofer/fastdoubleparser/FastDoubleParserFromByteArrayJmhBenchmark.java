/*
 * @(#)DoubleParserJmhBenchmark.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 16, OpenJDK 64-Bit Server VM, 16+36-2231
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark                                Mode  Cnt   Score   Error  Units
 * FromByteArrayZero                        avgt   25   2.713 ± 0.012  ns/op
 * FromByteArrayOnePointZero                avgt   25  11.731 ± 0.102  ns/op
 * FromByteArray3Digits                     avgt   25  10.991 ± 0.170  ns/op
 * FromByteArray3DigitsWithDecimalPoint     avgt   25  12.375 ± 0.059  ns/op
 * FromByteArray17DigitsWith3DigitExp       avgt   25  39.556 ± 2.835  ns/op
 * FromByteArray19DigitsWith3DigitExp       avgt   25  31.361 ± 0.439  ns/op
 * FromByteArray19DigitsWithoutExp          avgt   25  28.946 ± 0.377  ns/op
 * FromByteArrayNegative18DigitsWithoutExp  avgt   25  21.500 ± 0.581  ns/op
 * FromByteArray14HexDigitsWith3DigitExp    avgt   25  24.803 ± 0.103  ns/op
 * </pre>
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 11.0.8, Java HotSpot(TM) 64-Bit Server VM, 11.0.8+10-LTS
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark                                Mode  Cnt   Score   Error  Units
 * FromByteArrayZero                        avgt   25   2,741 ± 0,021  ns/op
 * FromByteArrayOnePointZero                avgt   25  14,546 ± 0,319  ns/op
 * FromByteArray3Digits                     avgt   25  12,483 ± 0,136  ns/op
 * FromByteArray3DigitsWithDecimalPoint     avgt   25  14,613 ± 0,196  ns/op
 * FromByteArrayNegative18DigitsWithoutExp  avgt   25  22,601 ± 0,113  ns/op
 * FromByteArray17DigitsWith3DigitExp       avgt   25  41,861 ± 2,239  ns/op
 * FromByteArray19DigitsWith3DigitExp       avgt   25  37,396 ± 0,461  ns/op
 * FromByteArray19DigitsWithoutExp          avgt   25  29,831 ± 0,247  ns/op
 * FromByteArray14HexDigitsWith3DigitExp    avgt   25  33,675 ± 0,696  ns/op
 * </pre>
 */
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

    /*
        @Benchmark
        @OutputTimeUnit(TimeUnit.NANOSECONDS)
        @BenchmarkMode(Mode.AverageTime)
        public void measureFromByteArray14HexDigitsWith3DigitExp() {
            FastDoubleParserFromByteArray.parseDouble(ISO_14_HEX_DIGITS_WITH_3_DIGIT_EXP);
        }
    */
    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFromByteArray17DigitsWith3DigitExp() {
        FastDoubleParserFromByteArray.parseDouble(ISO_17_DIGITS_WITH_3_DIGIT_EXP);
    }
/*
    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFromByteArray19DigitsWith3DigitExp() {
        FastDoubleParserFromByteArray.parseDouble(ISO_19_DIGITS_WITH_3_DIGIT_EX);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFromByteArray19DigitsWithoutExp() {
        FastDoubleParserFromByteArray.parseDouble(ISO_19_DIGITS_WITHOUT_EXP);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFromByteArray3Digits() {
        FastDoubleParserFromByteArray.parseDouble(ISO_3_DIGITS);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFromByteArray3DigitsWithDecimalPoint() {
        FastDoubleParserFromByteArray.parseDouble(ISO_3_DIGITS_WITH_DECIMAL_POINT);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFromByteArrayNegative18DigitsWithoutExp() {
        FastDoubleParserFromByteArray.parseDouble(ISO_18_DIGITS_WITHOUT_EXP);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFromByteArrayOnePointZero() {
        FastDoubleParserFromByteArray.parseDouble(ISO_ONE_POINT_ZERO);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFromByteArrayZero() {
        FastDoubleParserFromByteArray.parseDouble(ISO_ZERO);
    }*/
}


