/*
 * @(#)JmhJavaDoubleFromByteArrayScalability.java
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

import java.util.concurrent.TimeUnit;

import static ch.randelshofer.fastdoubleparser.Strings.repeat;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 20-ea, OpenJDK 64-Bit Server VM, 20-ea+27-2213
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 *      (digits)  Mode  Cnt           Score   Error  Units
 * m          24  avgt    2          51.190          ns/op
 * m           1  avgt    2          11.600          ns/op
 * m          10  avgt    2          16.603          ns/op
 * m         100  avgt    2         106.407          ns/op
 * m        1000  avgt    2        1217.225          ns/op
 * m       10000  avgt    2       12067.423          ns/op
 * m      100000  avgt    2      149914.570          ns/op
 * m     1000000  avgt    2     1485587.121          ns/op
 * m    10000000  avgt    2    15262129.361          ns/op
 * m   100000000  avgt    2   153397857.242          ns/op
 * m  1000000000  avgt    2  1528759668.000          ns/op
 * m  2147483643  avgt    2  3289661698.125          ns/op
 *
 * m  2147483643  avgt    2  4350359642.500          ns/op
 * </pre>
 */

@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"
        , "-Xmx10g"

        //       ,"-XX:+UnlockDiagnosticVMOptions", "-XX:PrintAssemblyOptions=intel", "-XX:CompileCommand=print,ch/randelshofer/fastdoubleparser/FastDoubleParser.*"

})
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhJavaDoubleFromCharSequenceScalability {


    @Param({
            //"24" // Double.toString() never produces more than 24 characters
            //, "1"
            //, "10"
            //, "100"
            //, "1000"
            //, "10000"
            //, "100000"
            //, "1000000"
            //, "10000000"
            //, "100000000"
            //, "1000000000"
            "2147483643" // Integer.MAX_VALUE - 4 = the largest support array size
    })
    public int digits;
    private String str;

    @Setup(Level.Trial)
    public void setUp() {
        str = repeat("7", digits - 2);
    }

    @Benchmark
    public double m() {
        return JavaDoubleParser.parseDouble(str);
    }
}





