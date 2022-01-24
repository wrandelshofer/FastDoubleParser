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
 * Benchmark                       Mode  Cnt   Score   Error  Units Suspicious
 * FromZero                        avgt    4   2.067 ± 0.130  ns/op ?
 * FromOnePointZero                avgt    4   2.304 ± 0.030  ns/op ?
 * From3Digits                     avgt    4   2.291 ± 0.093  ns/op ?
 * From3DigitsWithDecimalPoint     avgt    4   2.285 ± 0.031  ns/op ?
 * From17DigitsWith3DigitExp       avgt    4  32.242 ± 0.728  ns/op
 * From19DigitsWithoutExp          avgt    4  30.658 ± 0.366  ns/op
 * From19DigitsWith3DigitExp       avgt    4  34.448 ± 0.801  ns/op
 * FromNegative18DigitsWithoutExp  avgt    4  29.039 ± 1.615  ns/op
 * From14HexDigitsWith3DigitExp    avgt    4  25.044 ± 0.687  ns/op
 * </pre>
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 18-ea, OpenJDK 64-Bit Server VM, 18-ea+30-2029
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 * Benchmark                       Mode  Cnt   Score   Error  Units
 * FromZero                        avgt    4   4.248 ± 2.656  ns/op
 * FromOnePointZero                avgt    4  14.417 ± 1.936  ns/op
 * From3Digits                     avgt    4  12.971 ± 2.486  ns/op
 *
 * independent multiplications:
 * From3DigitsWithDecimalPoint     avgt    4  15.068 ± 3.471  ns/op
 * From17DigitsWith3DigitExp       avgt    4  35.870 ± 4.861  ns/op
 * From19DigitsWith3DigitExp       avgt    4  38.046 ± 3.203  ns/op
 * From19DigitsWithoutExp          avgt    4  32.201 ± 4.246  ns/op
 * FromNegative18DigitsWithoutExp  avgt    4  30.095 ± 2.193  ns/op
 * From14HexDigitsWith3DigitExp    avgt    4  26.513 ± 3.116  ns/op
 * </pre>
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
 */
@Fork(value = 2, jvmArgsAppend = {"-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"})
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class FastDoubleParserJmhBenchmark {
    /*
    @Benchmark
    public double m01FromZero() {
        String str = "0";
        return FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    public double m02FromOnePointZero() {
        String str = "1.0";
        return FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    public double m03From3Digits() {
        String str = "365";
        return FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    public double m04From3DigitsWithDecimalPoint() {
        String str = "10.1";
        return FastDoubleParser.parseDouble(str);
    }
*/
    @Benchmark
    public double m05From17DigitsWith3DigitExp() {
        String str = "123.45678901234567e123";
        return FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    public double m06From19DigitsWithoutExp() {
        String str = "123.4567890123456789";
        return FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    public double m07From19DigitsWith3DigitExp() {
        String str = "123.4567890123456789e123";
        return FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    public double m08FromNegative18DigitsWithoutExp() {
        String str = "-0.29235596393453456";
        return FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    public double m09From14HexDigitsWith3DigitExp() {
        String str = "0x123.456789abcdep123";
        return FastDoubleParser.parseDouble(str);
    }
}


