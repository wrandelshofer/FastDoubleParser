/*
 * @(#)JmhJavaDoubleFromByteArrayEmpirical.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.36
 * # VM version: JDK 20.0.1, OpenJDK 64-Bit Server VM, 20.0.1+9-29
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * FastDoubleSwar.isDigits() with return '0' <= c && c <= '9';
 * Benchmark                                                 (str)  Mode  Cnt   Score   Error  Units
 * JmhJavaDoubleFromByteArrayEmpirical.m                         0  avgt    4   4.018 ± 0.111  ns/op
 * JmhJavaDoubleFromByteArrayEmpirical.m                       365  avgt    4   8.465 ± 0.090  ns/op
 * JmhJavaDoubleFromByteArrayEmpirical.m                      10.1  avgt    4  11.933 ± 1.421  ns/op
 * JmhJavaDoubleFromByteArrayEmpirical.m  -1.2345678901234568E-121  avgt    4  26.296 ± 0.211  ns/op
 * JmhJavaDoubleFromByteArrayEmpirical.m      -0.29235596393453456  avgt    4  19.544 ± 0.035  ns/op
 * JmhJavaDoubleFromByteArrayEmpirical.m     0x123.456789abcdep123  avgt    4  23.105 ± 2.042  ns/op
 *
 * FastDoubleSwar.isDigits() with return (char) (c - '0') < 10;
 * Benchmark                                                 (str)  Mode  Cnt   Score   Error  Units
 * JmhJavaDoubleFromByteArrayEmpirical.m                         0  avgt    4   4.067 ± 0.107  ns/op
 * JmhJavaDoubleFromByteArrayEmpirical.m                       365  avgt    4   9.001 ± 0.118  ns/op
 * JmhJavaDoubleFromByteArrayEmpirical.m                      10.1  avgt    4  12.328 ± 0.070  ns/op
 * JmhJavaDoubleFromByteArrayEmpirical.m  -1.2345678901234568E-121  avgt    4  26.345 ± 3.242  ns/op
 * JmhJavaDoubleFromByteArrayEmpirical.m      -0.29235596393453456  avgt    4  18.962 ± 0.329  ns/op
 * JmhJavaDoubleFromByteArrayEmpirical.m     0x123.456789abcdep123  avgt    4  22.333 ± 0.437  ns/op
 * </pre>
 */
@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector",
        "--enable-preview",

        // Options for analysis with https://github.com/AdoptOpenJDK/jitwatch
        //"-XX:+UnlockDiagnosticVMOptions",
        //"-Xlog:class+load=info",
        //"-XX:+LogCompilation",
        //"-XX:+PrintAssembly"
})
@Measurement(iterations = 4)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhJavaDoubleFromByteArrayEmpirical {
    @Param({
            "0"
            , "365"
            , "10.1"
            , "-1.2345678901234568E-121"
            , "-0.29235596393453456"
            , "0x123.456789abcdep123"
    })
    public String str;
    private byte[] byteArray;

    @Setup
    public void prepare() {
        byteArray = str.getBytes(StandardCharsets.ISO_8859_1);
    }

    @Benchmark
    public double m() {
        return JavaDoubleParser.parseDouble(byteArray);
    }
}


