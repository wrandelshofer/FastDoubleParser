/*
 * @(#)JmhParseIterativeThreshold.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.36
 * # VM version: JDK 20.0.1, OpenJDK 64-Bit Server VM, 20.0.1+9-29
 * # Intel(R) Core(TM) i5-6200U CPU @ 2.30GHz
 *
 * Benchmark   (digits)  Mode  Cnt   Score   Error  Units
 * bigInteger       300  avgt   10  23.413 ± 0.936  us/op
 * bigInteger       325  avgt   10  23.268 ± 0.153  us/op
 * bigInteger       350  avgt   10  23.266 ± 0.222  us/op
 * bigInteger       375  avgt   10  23.162 ± 0.099  us/op
 * bigInteger       400  avgt   10  23.069 ± 0.128  us/op
 * bigInteger       425  avgt   10  23.180 ± 0.148  us/op
 * bigInteger       450  avgt   10  23.049 ± 0.123  us/op
 * bigInteger       475  avgt   10  23.218 ± 0.184  us/op
 * bigInteger       500  avgt   10  24.597 ± 0.097  us/op
 *
 * Process finished with exit code 0
 * </pre>
 */

@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"
})
@Warmup(iterations = 4, time = 1)
@Measurement(iterations = 10, time = 1)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhParseIterativeThreshold {

    @Param({"300", "325", "350", "375", "400", "425", "450", "475", "500"})
    public int digits;
    public String s;

    @Setup(Level.Trial)
    public void setUp() {
        s = Strings.repeat("3", 1000);
        ParseDigitsTaskCharSequence.RECURSION_THRESHOLD = digits;
    }

    @Benchmark
    public void bigInteger(Blackhole blackhole) {
        blackhole.consume(JavaBigIntegerParser.parseBigInteger(s));
    }
}