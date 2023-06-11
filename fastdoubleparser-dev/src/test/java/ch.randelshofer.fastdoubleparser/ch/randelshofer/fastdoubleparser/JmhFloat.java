/*
 * @(#)JmhFloat.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 17, OpenJDK 64-Bit Server VM, 17+35-2724
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark    (str)  Mode  Cnt    Score   Error  Units
 * m                0  avgt    2    9.607          ns/op
 * m              365  avgt    2   15.991          ns/op
 * m             10.1  avgt    2   18.379          ns/op
 * m        3.1415927  avgt    2   77.434          ns/op
 * m    1.6162552E-35  avgt    2   89.729          ns/op
 * m  0x1.57bd4ep-116  avgt    2  290.443          ns/op
 * </pre>
 */

@Fork(value = 1)
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhFloat {


    @Param({
            "0",
            "365",
            "10.1",
            "3.1415927",
            "1.6162552E-35",
            "0x1.57bd4ep-116"
    })
    public String str;

    @Benchmark
    public float m() {
        return Float.parseFloat(str);
    }
}





