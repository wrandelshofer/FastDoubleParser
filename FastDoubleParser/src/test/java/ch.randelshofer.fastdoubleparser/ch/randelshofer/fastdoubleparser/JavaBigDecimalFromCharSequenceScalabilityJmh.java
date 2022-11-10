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

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.28
 * # VM version: OpenJDK 64-Bit Server VM, Oracle Corporation, 19+36-2238
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark    Mode  Cnt    _   _   _  Score   Error  Units
 *
 * Recursive Algo with recursion base at 18 digits
 *    (digits)  Mode  Cnt    _   _   _  Score   Error  Units
 * m         1  avgt    2    _   _   _  9.229  WRONG           ns/op
 * m        10  avgt    2    _   _   _ 15.465  WRONG       ns/op
 * m       100  avgt    2    _   _   _569.899  WRONG       ns/op
 * m      1000  avgt    2    _   _  7_972.836  WRONG       ns/op
 * m     10000  avgt    2    _   _224_364.423  WRONG       ns/op
 * m    100000  avgt    2    _  7_684_976.688  WRONG       ns/op
 * m   1000000  avgt    2    _255_832_902.938  WRONG       ns/op
 * ___
 * Linear Algo___
 * m         1  avgt    2    _   _   _ 10.219          ns/op
 * m        10  avgt    2    _   _   _ 16.585          ns/op
 * m       100  avgt    2    _   _   _347.669          ns/op  *21
 * m      1000  avgt    2    _   _  7_029.623          ns/op  *20
 * m     10000  avgt    2    _   _537_926.302          ns/op  *76
 * m    100000  avgt    2    _ 47_848_870.339          ns/op  *88
 * m   1000000  avgt    2   4_943_568_603.167          ns/op *103
 * </pre>
 */

@Fork(value = 1)
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JavaBigDecimalFromCharSequenceScalabilityJmh {


    @Param({
            // "1",
            // "10",
            "100",
            "1000",
            "10000",
            "100000",
            "1000000"
    })
    public int digits;
    private String str;

    @Setup(Level.Trial)
    public void setUp() {
        str = "9806543217".repeat((digits + 9) / 10).substring(0, digits);
    }

    @Benchmark
    public BigDecimal m() {
        return JavaBigDecimalParser.parseBigDecimal(str);
    }
}





