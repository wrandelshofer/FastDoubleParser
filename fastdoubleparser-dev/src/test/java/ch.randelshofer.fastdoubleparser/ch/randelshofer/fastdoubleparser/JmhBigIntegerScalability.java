/*
 * @(#)JmhBigIntegerScalability.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import static ch.randelshofer.fastdoubleparser.Strings.repeat;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.35
 * # VM version: OpenJDK 64-Bit Server VM, Oracle Corporation, 20-ea+22-1594
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 *    (digits)  Mode  Cnt            Score   Error  Units
 * m         1  avgt    2           23.866          ns/op
 * m        10  avgt    2           64.853          ns/op
 * m       100  avgt    2          505.986          ns/op
 * m      1000  avgt    2        15322.876          ns/op
 * m     10000  avgt    2      1211620.965          ns/op
 * m    100000  avgt    2    117030135.430          ns/op
 * m   1000000  avgt    2  11883795509.500          ns/op
 * </pre>
 */

@Fork(value = 1)
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public final class JmhBigIntegerScalability {


    @Param({
            "1",
            "10",
            "100",
            "1000",
            "10000",
            "100000",
            "1000000",
            "10000000",
            "100000000",
            "646391315"
    })
    public int digits;
    private String str;

    @Setup(Level.Trial)
    public void setUp() {
        str = repeat("8808065258", (digits + 9) / 10).substring(0, digits);
    }

    @Benchmark
    public BigInteger m() {
        return new BigInteger(str);
    }
}





