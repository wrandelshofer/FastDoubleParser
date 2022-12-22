/*
 * @(#)JmhJavaDoubleFromByteArray.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
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

import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 20-ea, OpenJDK 64-Bit Server VM, 20-ea+27-2213
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 *                       (str)  Mode  Cnt   Score   Error  Units
 * m                         0  avgt    5   3.942 ± 0.152  ns/op
 * m                       365  avgt    5  12.068 ± 0.521  ns/op
 * m                      10.1  avgt    5  14.133 ± 0.391  ns/op
 * m  -1.2345678901234568E-121  avgt    5  31.151 ± 0.656  ns/op
 * m      -0.29235596393453456  avgt    5  20.577 ± 0.256  ns/op
 * m     0x123.456789abcdep123  avgt    5  30.853 ± 0.381  ns/op
 * </pre>
 */
@Fork(value = 1, jvmArgsAppend = {
        "--enable-preview",

        // Options for analysis with https://github.com/AdoptOpenJDK/jitwatch
        "-XX:+UnlockDiagnosticVMOptions",
        "-Xlog:class+load=info",
        "-XX:+LogCompilation",
        "-XX:+PrintAssembly"

})
@Measurement(iterations = 5)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhJavaDoubleFromCharArray {
    @Param({
            //  "0"
            //,  "365"
            //,  "10.1"
            "-1.2345678901234568E-121"
            //, "-0.29235596393453456"
            //, "0x123.456789abcdep123"
    })
    public String str;
    private char[] charArray;

    @Setup
    public void prepare() {
        charArray = str.toCharArray();
    }

    @Benchmark
    public double m() {
        return JavaDoubleParser.parseDouble(charArray);
    }
}


