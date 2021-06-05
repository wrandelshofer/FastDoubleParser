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
 * DoubleZero                                 avgt   25    7.371 ± 0.079  ns/op
 * DoubleOnePointZero                         avgt   25   13.672 ± 0.101  ns/op
 * Double3Digits                              avgt   25   13.951 ± 0.184  ns/op
 * Double3DigitsWithDecimalPoint              avgt   25   16.973 ± 0.122  ns/op
 * Double14HexDigitsWith3DigitExp             avgt   25  356.834 ± 3.108  ns/op
 * Double17DigitsWith3DigitExp                avgt   25  233.695 ± 1.434  ns/op
 * Double19DigitsWithoutExp                   avgt   25  424.996 ± 3.733  ns/op
 * DoubleNegative18DigitsWithoutExp           avgt   25  177.604 ± 5.411  ns/op
 * </pre>
 */
public class DoubleJmhBenchmark {
    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureDouble14HexDigitsWith3DigitExp() {
        String str = "0x123.456789abcdep123";
        Double.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureDouble17DigitsWith3DigitExp() {
        String str = "123.45678901234567e123";
        Double.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureDouble19DigitsWith3DigitExp() {
        String str = "123.4567890123456789e123";
        Double.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureDouble19DigitsWithoutExp() {
        String str = "123.4567890123456789";
        Double.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureDouble3Digits() {
        String str = "365";
        Double.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureDouble3DigitsWithDecimalPoint() {
        String str = "10.1";
        Double.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureDoubleNegative18DigitsWithoutExp() {
        String str = "-0.29235596393453456";
        Double.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureDoubleOnePointZero() {
        String str = "1.0";
        Double.parseDouble(str);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measureDoubleZero() {
        String str = "0";
        Double.parseDouble(str);
    }


}


