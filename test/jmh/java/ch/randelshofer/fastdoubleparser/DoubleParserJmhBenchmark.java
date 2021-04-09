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
 * Benchmark                                  Mode  Cnt    Score   Error  Units
 * DoubleZero                                 avgt   25    7.371 ± 0.079  ns/op
 * DoubleOnePointZero                         avgt   25   13.672 ± 0.101  ns/op
 * Double3Digits                              avgt   25   13.951 ± 0.184  ns/op
 * Double3DigitsWithDecimalPoint              avgt   25   16.973 ± 0.122  ns/op
 * Double14HexDigitsWith3DigitExp             avgt   25  356.834 ± 3.108  ns/op
 * Double17DigitsWith3DigitExp                avgt   25  233.695 ± 1.434  ns/op
 * Double19DigitsWithoutExp                   avgt   25  424.996 ± 3.733  ns/op
 * DoubleNegative18DigitsWithoutExp           avgt   25  177.604 ± 5.411  ns/op
 *
 * FastDoubleParserZero                       avgt   25   2.369 ± 0.008  ns/op
 * FastDoubleParserOnePointZero               avgt   25  12.851 ± 0.124  ns/op
 * FastDoubleParser3Digits                    avgt   25  11.227 ± 0.105  ns/op
 * FastDoubleParser14HexDigitsWith3DigitExp   avgt   25  21.629 ± 0.112  ns/op
 * FastDoubleParser17DigitsWith3DigitExp      avgt   25  31.923 ± 0.102  ns/op
 * FastDoubleParser19DigitsWith3DigitExp      avgt   25  33.912 ± 0.642  ns/op
 * FastDoubleParser19DigitsWithoutExp         avgt   25  29.793 ± 0.221  ns/op
 * FastDoubleParserNegative18DigitsWithoutExp avgt   25  28.023 ± 0.218  ns/op
 *
 * Benchmark                                  Mode  Cnt   Score   Error  Units
 * FromByteArrayZero                          avgt   25   2.847 ± 0.029  ns/op
 * FromByteArrayOnePointZero                  avgt   25  12.890 ± 0.092  ns/op
 * FromByteArray3Digits                       avgt   25  12.288 ± 0.131  ns/op
 * FromByteArray14HexDigitsWith3DigitExp      avgt   25  25.375 ± 0.348  ns/op
 * FromByteArray17DigitsWith3DigitExp         avgt   25  36.262 ± 2.224  ns/op
 * FromByteArray19DigitsWith3DigitExp         avgt   25  33.603 ± 0.106  ns/op
 * FromByteArray19DigitsWithoutExp            avgt   25  31.576 ± 0.321  ns/op
 * FromByteArray3DigitsWithDecimalPoint       avgt   25  14.278 ± 0.238  ns/op
 * FromByteArrayNegative18DigitsWithoutExp    avgt   25  22.149 ± 0.254  ns/op
 *
 * </pre>
 */
public class DoubleParserJmhBenchmark {
    private final static byte[] ISO_ZERO = "0".getBytes(StandardCharsets.ISO_8859_1);
    private final static byte[] ISO_ONE_POINT_ZERO = "1.0".getBytes(StandardCharsets.ISO_8859_1);
    private final static byte[] ISO_18_DIGITS_WITHOUT_EXP = "-0.29235596393453456".getBytes(StandardCharsets.ISO_8859_1);
    private final static byte[] ISO_3_DIGITS_WITH_DECIMAL_POINT = "10.1".getBytes(StandardCharsets.ISO_8859_1);
    private final static byte[] ISO_3_DIGITS = "365".getBytes(StandardCharsets.ISO_8859_1);
    private final static byte[] ISO_19_DIGITS_WITHOUT_EXP = "123.4567890123456789".getBytes(StandardCharsets.ISO_8859_1);
    private final static byte[] ISO_19_DIGITS_WITH_3_DIGIT_EX = "123.4567890123456789e123".getBytes(StandardCharsets.ISO_8859_1);
    private final static byte[] ISO_17_DIGITS_WITH_3_DIGIT_EXP = "123.45678901234567e123".getBytes(StandardCharsets.ISO_8859_1);
    private final static byte[] ISO_14_HEX_DIGITS_WITH_3_DIGIT_EXP = "0x123.456789abcdep123".getBytes(StandardCharsets.ISO_8859_1);

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureDouble14HexDigitsWith3DigitExp() {
        String str = "0x123.456789abcdep123";
        Double.parseDouble(str);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureDouble17DigitsWith3DigitExp() {
        String str = "123.45678901234567e123";
        Double.parseDouble(str);
    }

    //@Benchmark @OutputTimeUnit(TimeUnit.NANOSECONDS)  @BenchmarkMode(Mode.AverageTime)
    public void measureDouble19DigitsWith3DigitExp() {
        String str = "123.4567890123456789e123";
        Double.parseDouble(str);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureDouble19DigitsWithoutExp() {
        String str = "123.4567890123456789";
        Double.parseDouble(str);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureDouble3Digits() {
        String str = "365";
        Double.parseDouble(str);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureDouble3DigitsWithDecimalPoint() {
        String str = "10.1";
        Double.parseDouble(str);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureDoubleNegative18DigitsWithoutExp() {
        String str = "-0.29235596393453456";
        Double.parseDouble(str);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureDoubleOnePointZero() {
        String str = "1.0";
        Double.parseDouble(str);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureDoubleZero() {
        String str = "0";
        Double.parseDouble(str);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFastDoubleParser14HexDigitsWith3DigitExp() {
        String str = "0x123.456789abcdep123";
        FastDoubleParser.parseDouble(str);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFastDoubleParser17DigitsWith3DigitExp() {
        String str = "123.45678901234567e123";
        FastDoubleParser.parseDouble(str);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFastDoubleParser19DigitsWith3DigitExp() {
        String str = "123.4567890123456789e123";
        FastDoubleParser.parseDouble(str);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFastDoubleParser19DigitsWithoutExp() {
        String str = "123.4567890123456789";
        FastDoubleParser.parseDouble(str);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFastDoubleParser3Digits() {
        String str = "365";
        FastDoubleParser.parseDouble(str);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFastDoubleParser3DigitsWithDecimalPoint() {
        String str = "10.1";
        FastDoubleParser.parseDouble(str);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFromByteArray14HexDigitsWith3DigitExp() {
        FastDoubleParserFromByteArray.parseDouble(ISO_14_HEX_DIGITS_WITH_3_DIGIT_EXP);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFromByteArray17DigitsWith3DigitExp() {
        FastDoubleParserFromByteArray.parseDouble(ISO_17_DIGITS_WITH_3_DIGIT_EXP);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFromByteArray19DigitsWith3DigitExp() {
        FastDoubleParserFromByteArray.parseDouble(ISO_19_DIGITS_WITH_3_DIGIT_EX);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFromByteArray19DigitsWithoutExp() {
        FastDoubleParserFromByteArray.parseDouble(ISO_19_DIGITS_WITHOUT_EXP);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFromByteArray3Digits() {
        FastDoubleParserFromByteArray.parseDouble(ISO_3_DIGITS);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFromByteArray3DigitsWithDecimalPoint() {
        FastDoubleParserFromByteArray.parseDouble(ISO_3_DIGITS_WITH_DECIMAL_POINT);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFromByteArrayNegative18DigitsWithoutExp() {
        FastDoubleParserFromByteArray.parseDouble(ISO_18_DIGITS_WITHOUT_EXP);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFromByteArrayOnePointZero() {
        FastDoubleParserFromByteArray.parseDouble(ISO_ONE_POINT_ZERO);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFromByteArrayZero() {
        FastDoubleParserFromByteArray.parseDouble(ISO_ZERO);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFastDoubleParserNegative18DigitsWithoutExp() {
        String str = "-0.29235596393453456";
        FastDoubleParser.parseDouble(str);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFastDoubleParserOnePointZero() {
        String str = "1.0";
        FastDoubleParser.parseDouble(str);
    }

    //@Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFastDoubleParserZero() {
        String str = "0";
        FastDoubleParser.parseDouble(str);
    }


}


