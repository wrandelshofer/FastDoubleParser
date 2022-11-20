/*
 * @(#)JmhJavaBigDecimalFromCharSequenceScalability.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
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
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 20-ea, OpenJDK 64-Bit Server VM, 20-ea+22-1594
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 *      (digits)  Mode  Cnt              Score   Error  Units
 * f    _   _ 10  avgt    2   _   _   _ 18.488          ns/op
 * f    _   _100  avgt    2   _   _   _254.483          ns/op
 * f    _  1_000  avgt    2   _   _  5_485.980          ns/op
 * f    _ 10_000  avgt    2   _   _139_287.776          ns/op
 * f    _100_000  avgt    2   _  2_620_321.357          ns/op
 * f   1_000_000  avgt    2   _ 69_274_168.383          ns/op
 * f  10_000_000  avgt    2  1_952_061_947.250          ns/op
 *
 * # Benchmark: ch.randelshofer.fastdoubleparser.JavaBigDecimalFromCharSequenceScalabilityJmh.f
 * # Parameters: (digits = 1292782621)
 *
 * </pre>
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 1.8.0_281, Java HotSpot(TM) 64-Bit Server VM, 25.281-b09
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Java 8 does not have method {@link BigInteger#parallelMultiply}.
 *
 *      (digits)  Mode  Cnt      _   _  Score   Error  Units
 * f    _   _ 10  avgt    2      _   _ 32.109          ns/op
 * f    _   _100  avgt    2      _   _431.013          ns/op
 * f    _  1_000  avgt    2      _  6_552.813          ns/op
 * f    _ 10_000  avgt    2      _166_715.977          ns/op
 * f    _100_000  avgt    2     4_821_497.950          ns/op
 * f   1_000_000  avgt    2   143_244_763.043          ns/op
 * </pre>
 */

@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"
        //, "-XX:+UnlockDiagnosticVMOptions"
        // "-XX:PrintAssemblyOptions=intel", "-XX:CompileCommand=print,ch/randelshofer/fastdoubleparser/*.*"
        //, "-XX:-PrintInlining"

})
@Measurement(iterations = 1)
@Warmup(iterations = 0)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhJavaBigDecimalFromCharSequenceScalability {


    @Param({
            //"1"
            //  "10"
            //  , "100"
            //  , "1000"
            //  , "10000"
            //  , "100000"
            //  , "1000000"
            //  , "10000000"
            // "100000000"
            //  ,"1000000000"
            "1200000000"
    })
    public int digits;
    private String integerPart;
    private String fractionalPart;

    @Setup(Level.Trial)
    public void setUp() {
        String str = "9806543217".repeat((digits + 9) / 10).substring(0, digits);
        integerPart = str;
        fractionalPart = ("." + str);
    }


    //   @Benchmark
    //   public BigDecimal i() {
    //       return JavaBigDecimalParser.parseBigDecimal(integerPart);
    //   }

    @Benchmark
    public BigDecimal f() {
        return JavaBigDecimalParser.parallelParseBigDecimal(fractionalPart);
    }

}





