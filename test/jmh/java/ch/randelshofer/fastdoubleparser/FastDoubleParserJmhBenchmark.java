/*
 * @(#)DoubleParserJmhBenchmark.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 16, OpenJDK 64-Bit Server VM, 16+36-2231
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark                                  Mode  Cnt    Score   Error  Units
 * FastDoubleParserZero                       avgt   25   2.369 ± 0.008  ns/op
 * FastDoubleParserOnePointZero               avgt   25  12.851 ± 0.124  ns/op
 * FastDoubleParser3Digits                    avgt   25  11.227 ± 0.105  ns/op
 * FastDoubleParser14HexDigitsWith3DigitExp   avgt   25  21.629 ± 0.112  ns/op
 * FastDoubleParser17DigitsWith3DigitExp      avgt   25  31.923 ± 0.102  ns/op
 * FastDoubleParser19DigitsWith3DigitExp      avgt   25  33.912 ± 0.642  ns/op
 * FastDoubleParser19DigitsWithoutExp         avgt   25  29.793 ± 0.221  ns/op
 * FastDoubleParserNegative18DigitsWithoutExp avgt   25  28.023 ± 0.218  ns/op
 * </pre>
 */
public class FastDoubleParserJmhBenchmark {
    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFastDoubleParser14HexDigitsWith3DigitExp() {
        String str = "0x123.456789abcdep123";
        FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFastDoubleParser17DigitsWith3DigitExp() {
        String str = "123.45678901234567e123";
        FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFastDoubleParser19DigitsWith3DigitExp() {
        String str = "123.4567890123456789e123";
        FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFastDoubleParser19DigitsWithoutExp() {
        String str = "123.4567890123456789";
        FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFastDoubleParser3Digits() {
        String str = "365";
        FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFastDoubleParser3DigitsWithDecimalPoint() {
        String str = "10.1";
        FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFastDoubleParserNegative18DigitsWithoutExp() {
        String str = "-0.29235596393453456";
        FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFastDoubleParserOnePointZero() {
        String str = "1.0";
        FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureFastDoubleParserZero() {
        String str = "0";
        FastDoubleParser.parseDouble(str);
    }
}


