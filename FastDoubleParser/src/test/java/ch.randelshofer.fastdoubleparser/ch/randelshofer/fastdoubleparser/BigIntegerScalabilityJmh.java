/*
 * @(#)BigDecimalParserJmhBenchmark.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
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

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.35
 * # VM version: OpenJDK 64-Bit Server VM, Oracle Corporation, 20-ea+22-1594
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 *    (digits)  Mode  Cnt    _   _   _  Score   Error  Units
 * m    _   _  1  avgt    2    _   _   _ 23.866          ns/op
 * m    _   _ 10  avgt    2    _   _   _ 64.853          ns/op
 * m    _   _100  avgt    2    _   _   _505.986          ns/op
 * m    _  1_000  avgt    2    _   _ 15_322.876          ns/op
 * m    _ 10_000  avgt    2    _  1_211_620.965          ns/op
 * m    _100_000  avgt    2    _117_030_135.430          ns/op
 * m   1_000_000  avgt    2  11_883_795_509.500          ns/op
 * </pre>
 */

@Fork(value = 1)
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class BigIntegerScalabilityJmh {


    @Param({
            "1",
            "10",
            "100",
            "1000",
            "10000",
            "100000",
            "1000000"
            //"1292782621"
    })
    public int digits;
    private String str;

    @Setup(Level.Trial)
    public void setUp() {
        str = "9806543217".repeat((digits + 9) / 10).substring(0, digits);
    }

    @Benchmark
    public BigInteger m() {
        return new BigInteger(str);
    }
}





