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
 * # VM version: JDK 17, OpenJDK 64-Bit Server VM, 17+35-2724
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark                       Mode  Cnt   Score   Error  Units
 * FromZero                        avgt   25   3.362 ± 0.130  ns/op
 * FromOnePointZero                avgt   25  12.784 ± 0.250  ns/op
 * From3Digits                     avgt   25  11.910 ± 0.292  ns/op
 * From3DigitsWithDecimalPoint     avgt   25  13.544 ± 0.308  ns/op
 * From17DigitsWith3DigitExp       avgt   25  34.346 ± 0.436  ns/op
 * From19DigitsWith3DigitExp       avgt   25  35.628 ± 0.441  ns/op
 * From19DigitsWithoutExp          avgt   25  29.889 ± 0.451  ns/op
 * FromNegative18DigitsWithoutExp  avgt   25  27.912 ± 0.088  ns/op
 * From14HexDigitsWith3DigitExp    avgt   25  25.812 ± 0.511  ns/op
 * </pre>
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 1.8.0_261, Java HotSpot(TM) 64-Bit Server VM, 25.261-b12
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark                       Mode  Cnt   Score   Error  Units
 * FromZero                        avgt   25   3.687 ± 0.019  ns/op
 * FromOnePointZero                avgt   25  14.691 ± 0.279  ns/op
 * From3Digits                     avgt   25  13.264 ± 0.069  ns/op
 * From3DigitsWithDecimalPoint     avgt   25  14.557 ± 0.156  ns/op
 * From17DigitsWith3DigitExp       avgt   25  41.830 ± 0.097  ns/op
 * From19DigitsWith3DigitExp       avgt   25  42.775 ± 0.881  ns/op
 * From19DigitsWithoutExp          avgt   25  34.215 ± 1.184  ns/op
 * FromNegative18DigitsWithoutExp  avgt   25  33.949 ± 0.078  ns/op
 * From14HexDigitsWith3DigitExp    avgt   25  36.575 ± 0.412  ns/op
 * </pre>
 */
public class FastDoubleParserJmhBenchmark {
    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public double measureFrom14HexDigitsWith3DigitExp() {
        String str = "0x123.456789abcdep123";
        return FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public double measureFrom17DigitsWith3DigitExp() {
        String str = "123.45678901234567e123";
        return FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public double measureFrom19DigitsWith3DigitExp() {
        String str = "123.4567890123456789e123";
        return FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public double measureFrom19DigitsWithoutExp() {
        String str = "123.4567890123456789";
        return FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public double measureFrom3Digits() {
        String str = "365";
        return FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public double measureFrom3DigitsWithDecimalPoint() {
        String str = "10.1";
        return FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public double measureFromNegative18DigitsWithoutExp() {
        String str = "-0.29235596393453456";
        return FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public double measureFromOnePointZero() {
        String str = "1.0";
        return FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public double measureFromZero() {
        String str = "0";
        return FastDoubleParser.parseDouble(str);
    }
}


