/*
 * @(#)JmhJsonDoubleFromByteArray.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 19-ea, OpenJDK 64-Bit Server VM, 20-ea+22-1594
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 *                        (str)  Mode  Cnt   Score   Error  Units
 * m                         0  avgt    5   4.719 ± 1.005  ns/op
 * m                       365  avgt    5  11.517 ± 0.119  ns/op
 * m                      10.1  avgt    5  13.060 ± 0.101  ns/op
 * m    123.45678901234567e123  avgt    5  28.984 ± 0.207  ns/op
 * m      123.4567890123456789  avgt    5  24.307 ± 0.358  ns/op
 * m  123.4567890123456789e123  avgt    5  31.024 ± 0.190  ns/op
 * m      -0.29235596393453456  avgt    5  20.588 ± 0.077  ns/op
 * </pre>
 */
@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"
        , "-XX:+UnlockDiagnosticVMOptions"
        // "-XX:PrintAssemblyOptions=intel", "-XX:CompileCommand=print,ch/randelshofer/fastdoubleparser/*.*"
        //,"-XX:+PrintInlining"

})
@Measurement(iterations = 5)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhJsonDoubleFromByteArray {
    @Param({
            "0"
            , "365"
            , "10.1"
            , "123.45678901234567e123"
            , "123.4567890123456789"
            , "123.4567890123456789e123"
            , "-0.29235596393453456"
    })
    public String str;
    private byte[] byteArray;

    @Setup
    public void prepare() {
        byteArray = str.getBytes(StandardCharsets.ISO_8859_1);
    }

    @Benchmark
    public double m() {
        return JsonDoubleParser.parseDouble(byteArray);
    }
}


