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
import org.openjdk.jmh.annotations.Setup;
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
 * Benchmark    (str)  Mode  Cnt    Score   Error  Units
 * m                0  avgt    2    6.570          ns/op
 * m              365  avgt    2   14.525          ns/op
 * m             10.1  avgt    2   16.646          ns/op
 * m        3.1415927  avgt    2   24.022          ns/op
 * m    1.6162552E-35  avgt    2   28.465          ns/op
 * m  0x1.57bd4ep-116  avgt    2  336.391          ns/op
 * </pre>
 */

@Fork(value = 1)
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class FastFloatParserFromCharSequenceJmh {


    @Param({
            "0",
            "365",
            "10.1",
            "3.14159267",
            "1.6162552E-35",
            "0x1.57bd4ep-116"
    })
    public String str;

    @Setup
    public void setup() {
        System.out.println(str + "=" + JavaFloatParser.parseFloat(str));
    }

    @Benchmark
    public float m() {
        return JavaFloatParser.parseFloat(str);
    }
}





