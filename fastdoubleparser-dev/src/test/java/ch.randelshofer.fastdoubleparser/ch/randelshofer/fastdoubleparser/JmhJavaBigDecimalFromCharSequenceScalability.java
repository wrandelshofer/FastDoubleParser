/*
 * @(#)JmhJavaBigDecimalFromCharSequenceScalability.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static ch.randelshofer.fastdoubleparser.Strings.repeat;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.36
 * # VM version: JDK 20.0.1, OpenJDK 64-Bit Server VM, 20.0.1+9-29
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 *     (digits)  Mode  Cnt            Score   Error  Units
 * f          1  avgt    2           11.791          ns/op
 * f         10  avgt    2           19.076          ns/op
 * f        100  avgt    2          277.271          ns/op
 * f       1000  avgt    2         5422.912          ns/op
 * f      10000  avgt    2       180604.928          ns/op
 * f     100000  avgt    2      5534607.161          ns/op
 * f    1000000  avgt    2     86172567.073          ns/op
 * f   10000000  avgt    2   1411668617.188          ns/op
 * f  100000000  avgt    2  23719563715.500          ns/op
 * </pre>
 */

@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"
        //, "-XX:+UnlockDiagnosticVMOptions"
        // "-XX:PrintAssemblyOptions=intel", "-XX:CompileCommand=print,ch/randelshofer/fastdoubleparser/*.*"
        //, "-XX:-PrintInlining"

})
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhJavaBigDecimalFromCharSequenceScalability {


    @Param({
            "1"
            , "10"
            , "100"
            , "1000"
            , "10000"
            , "100000"
            , "1000000"
            , "10000000"
            , "100000000"
            // , "646391315"// The maximal number non-zero digits in the significand
    })
    public int digits;
    private String integerPart;
    private String fractionalPart;

    @Setup(Level.Trial)
    public void setUp() {
        String str = repeat("9806543217", (digits + 9) / 10).substring(0, digits);
        integerPart = str;
        fractionalPart = ("." + str);
    }


    //   @Benchmark
    //   public BigDecimal i() {
    //       return JavaBigDecimalParser.parseBigDecimal(integerPart);
    //   }

    @Benchmark
    public BigDecimal f() {
        return JavaBigDecimalParser.parseBigDecimal(fractionalPart);
    }

}





