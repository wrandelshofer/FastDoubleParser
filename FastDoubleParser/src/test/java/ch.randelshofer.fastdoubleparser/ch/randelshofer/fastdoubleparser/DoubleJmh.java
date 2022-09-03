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
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 16, OpenJDK 64-Bit Server VM, 16+36-2231
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark             (str)  Mode  Cnt    Score   Error  Units
 * m                         0  avgt    2    9.682          ns/op
 * m                       1.0  avgt    2   15.788          ns/op
 * m                       365  avgt    2   16.584          ns/op
 * m                      10.1  avgt    2   18.746          ns/op
 * m    123.45678901234567e123  avgt    2  234.406          ns/op
 * m      123.4567890123456789  avgt    2  413.116          ns/op
 * m  123.4567890123456789e123  avgt    2  286.112          ns/op
 * m      -0.29235596393453456  avgt    2  175.137          ns/op
 * m     0x123.456789abcdep123  avgt    2  334.153          ns/op
 * </pre>
 */

@Fork(value = 1)
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class DoubleJmh {


    @Param({
            "0",
            "1.0",
            "365",
            "10.1",
            "123.45678901234567e123",
            "123.4567890123456789",
            "123.4567890123456789e123",
            "-0.29235596393453456",
            "0x123.456789abcdep123"
    })
    public String str;

    @Benchmark
    public double m() {
        return Double.parseDouble(str);
    }
}





