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
 *    (digits)  Mode  Cnt       _   _  Score   Error  Units
 * m    _   _100  avgt    2       _   _756.939          ns/op
 * m    _  1_000  avgt    2       _  9_989.409          ns/op
 * m    _ 10_000  avgt    2       _265_456.893          ns/op
 * m    _100_000  avgt    2      9_130_925.801          ns/op
 * m   1_000_000  avgt    2    303_638_502.013          ns/op
 * m  10_000_000  avgt    2 10_162_931_172.500          ns/op
 * </pre>
 */

@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"
        , "-XX:+UnlockDiagnosticVMOptions"
        // "-XX:PrintAssemblyOptions=intel", "-XX:CompileCommand=print,ch/randelshofer/fastdoubleparser/*.*"
        , "-XX:-PrintInlining"

})
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JavaBigDecimalFromCharSequenceScalabilityJmh {


    @Param({
            // "1",
            // "10",
            // "100",
            // "1000",
            // "10000",
            // "100000",
            // "1000000",
            "10000000"
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





