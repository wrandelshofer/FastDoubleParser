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
 * Benchmark                                   Mode  Cnt   Score   Error  Units
 * FastDoubleParserZero                        avgt   25   1.818 ± 0.007  ns/op
 * FastDoubleParserOnePointZero                avgt   25  11.265 ± 0.043  ns/op
 * FastDoubleParser3Digits                     avgt   25  10.102 ± 0.097  ns/op
 * FastDoubleParser3DigitsWithDecimalPoint     avgt   25  11.932 ± 0.106  ns/op
 * FastDoubleParser17DigitsWith3DigitExp       avgt   25  31.393 ± 0.116  ns/op
 * FastDoubleParser19DigitsWith3DigitExp       avgt   25  32.941 ± 0.153  ns/op
 * FastDoubleParser19DigitsWithoutExp          avgt   25  28.314 ± 0.292  ns/op
 * FastDoubleParserNegative18DigitsWithoutExp  avgt   25  25.988 ± 0.102  ns/op
 * FastDoubleParser14HexDigitsWith3DigitExp    avgt   25  21.606 ± 0.197  ns/op
 * </pre>
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 11.0.8, Java HotSpot(TM) 64-Bit Server VM, 11.0.8+10-LTS
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark                                   Mode  Cnt   Score   Error  Units
 * FastDoubleParserZero                        avgt   25   1,822 ± 0,010  ns/op
 * FastDoubleParserOnePointZero                avgt   25  11,889 ± 0,076  ns/op
 * FastDoubleParser3Digits                     avgt   25  10,324 ± 0,128  ns/op
 * FastDoubleParser3DigitsWithDecimalPoint     avgt   25  12,437 ± 0,324  ns/op
 * FastDoubleParser17DigitsWith3DigitExp       avgt   25  37,022 ± 0,339  ns/op
 * FastDoubleParserNegative18DigitsWithoutExp  avgt   25  28,039 ± 0,192  ns/op
 * FastDoubleParser19DigitsWithoutExp          avgt   25  29,121 ± 0,728  ns/op
 * FastDoubleParser19DigitsWith3DigitExp       avgt   25  38,763 ± 0,219  ns/op
 * FastDoubleParser14HexDigitsWith3DigitExp    avgt   25  23,396 ± 0,314  ns/op
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


