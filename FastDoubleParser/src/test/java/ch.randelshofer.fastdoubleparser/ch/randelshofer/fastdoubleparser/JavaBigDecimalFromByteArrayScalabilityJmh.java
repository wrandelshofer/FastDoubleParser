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
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.28
 * # VM version: OpenJDK 64-Bit Server VM, Oracle Corporation, 19+36-2238
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 *    (digits)  Mode  Cnt          Score   Error  Units
 * i         1  avgt    2     _   _  7.703          ns/op
 * i        10  avgt    2     _   _ 13.072          ns/op
 * i       100  avgt    2     _   _512.098          ns/op
 * i      1000  avgt    2     _  6_691.866          ns/op
 * i     10000  avgt    2     _216_947.857          ns/op
 * i    100000  avgt    2    7_660_540.711          ns/op
 * i   1000000  avgt    2  255_927_837.600          ns/op
 *
 *    (digits)  Mode  Cnt          Score   Error  Units     Factor
 * f         1  avgt    2     _   _ 12.077          ns/op    1
 * f        10  avgt    2     _   _ 17.682          ns/op    1.5
 * f       100  avgt    2     _   _475.559          ns/op   25.9
 * f      1000  avgt    2     _  6_353.706          ns/op   13.3
 * f     10000  avgt    2     _211_366.945          ns/op   33.3
 * f    100000  avgt    2    7_470_872.529          ns/op   35.3
 * f   1000000  avgt    2  251_240_461.688          ns/op   33.6
 * </pre>
 */

@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"

        //       ,"-XX:+UnlockDiagnosticVMOptions", "-XX:PrintAssemblyOptions=intel", "-XX:CompileCommand=print,ch/randelshofer/fastdoubleparser/JavaBigDecimalParser.*"

})
@Measurement(iterations = 2)
@Warmup(iterations = 3)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JavaBigDecimalFromByteArrayScalabilityJmh {


    @Param({
            "1"
            , "10"
            , "100"
            , "1000"
            , "10000"
            , "100000"
            , "1000000"
    })
    public int digits;
    private byte[] integerPart;
    private byte[] fractionalPart;

    @Setup(Level.Trial)
    public void setUp() {
        String str = "9806543217".repeat((digits + 9) / 10).substring(0, digits);
        integerPart = str.getBytes(StandardCharsets.ISO_8859_1);
        fractionalPart = ("0." + str).getBytes(StandardCharsets.ISO_8859_1);
    }

    /*
        @Benchmark
        public BigDecimal i() {
            return JavaBigDecimalParser.parseBigDecimal(integerPart);
        }
    */
    @Benchmark
    public BigDecimal f() {
        return JavaBigDecimalParser.parseBigDecimal(fractionalPart);
    }
}





