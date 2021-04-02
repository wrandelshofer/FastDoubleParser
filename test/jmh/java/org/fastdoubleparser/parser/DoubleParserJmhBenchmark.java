/*
 * @(#)DoubleParserJmhBenchmark.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package org.fastdoubleparser.parser;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <p>
 * FIXME Add benchmarks for floating point strings that trigger
 * slow paths in {@link FastDoubleMath} once we have implement them.
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
 * FastDoubleParser3DigitsWithDecimalPoint    avgt   25  13.606 ± 0.043  ns/op
 * FastDoubleParser14HexDigitsWith3DigitExp   avgt   25  39.805 ± 0.586  ns/op
 * FastDoubleParser17DigitsWith3DigitExp      avgt   25  31.923 ± 0.102  ns/op
 * FastDoubleParser19DigitsWith3DigitExp      avgt   25  33.912 ± 0.642  ns/op
 * FastDoubleParser19DigitsWithoutExp         avgt   25  29.793 ± 0.221  ns/op
 * FastDoubleParserNegative18DigitsWithoutExp avgt   25  28.023 ± 0.218  ns/op
 * </pre>
 */
public class DoubleParserJmhBenchmark {
    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmarkDouble14HexDigitsWith3DigitExp() {
        String str = "0x123.456789abcdep123";
        Double.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmarkDouble17DigitsWith3DigitExp() {
        String str = "123.45678901234567e123";
        Double.parseDouble(str);
    }

    //  @Benchmark @OutputTimeUnit(TimeUnit.NANOSECONDS)  @BenchmarkMode(Mode.AverageTime)
    public void benchmarkDouble19DigitsWith3DigitExp() {
        String str = "123.4567890123456789e123";
        Double.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmarkDouble19DigitsWithoutExp() {
        String str = "123.4567890123456789";
        Double.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmarkDouble3Digits() {
        String str = "365";
        Double.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmarkDouble3DigitsWithDecimalPoint() {
        String str = "10.1";
        Double.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmarkDoubleNegative18DigitsWithoutExp() {
        String str = "-0.29235596393453456";
        Double.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmarkDoubleOnePointZero() {
        String str = "1.0";
        Double.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmarkDoubleZero() {
        String str = "0";
        Double.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmarkFastDoubleParser14HexDigitsWith3DigitExp() {
        String str = "0x123.456789abcdep123";
        FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmarkFastDoubleParser17DigitsWith3DigitExp() {
        String str = "123.45678901234567e123";
        FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmarkFastDoubleParser19DigitsWith3DigitExp() {
        String str = "123.4567890123456789e123";
        FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmarkFastDoubleParser19DigitsWithoutExp() {
        String str = "123.4567890123456789";
        FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmarkFastDoubleParser3Digits() {
        String str = "365";
        FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmarkFastDoubleParser3DigitsWithDecimalPoint() {
        String str = "10.1";
        FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmarkFastDoubleParserNegative18DigitsWithoutExp() {
        String str = "-0.29235596393453456";
        FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmarkFastDoubleParserOnePointZero() {
        String str = "1.0";
        FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmarkFastDoubleParserZero() {
        String str = "0";
        FastDoubleParser.parseDouble(str);
    }
}


