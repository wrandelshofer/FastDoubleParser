/*
 * @(#)JmhJsonDoubleFromCharArray.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 20-ea, OpenJDK 64-Bit Server VM, 20-ea+27-2213
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 *                       (str)  Mode  Cnt   Score   Error  Units
 * m                         0  avgt    5   3.440 ± 0.113  ns/op
 * m                       365  avgt    5  10.405 ± 0.162  ns/op
 * m                      10.1  avgt    5  12.453 ± 1.461  ns/op
 * m  -1.2345678901234568E-121  avgt    5  26.581 ± 0.256  ns/op
 * m      -0.29235596393453456  avgt    5  18.163 ± 0.140  ns/op
 * </pre>
 */
@Fork(value = 1, jvmArgsAppend = {
        "--enable-preview",

        // Options for analysis with https://github.com/AdoptOpenJDK/jitwatch
        // "-XX:+UnlockDiagnosticVMOptions",
        // "-Xlog:class+load=info",
        // "-XX:+LogCompilation",
        // "-XX:+PrintAssembly"

})
@Measurement(iterations = 5)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhJsonDoubleFromCharArray {
    @Param({
            "0"
            , "365"
            , "10.1"
            , "-1.2345678901234568E-121"
            , "-0.29235596393453456"
    })
    public String str;
    private char[] charArray;

    @Setup
    public void prepare() {
        charArray = str.toCharArray();
    }

    @Benchmark
    public double m() {
        return JsonDoubleParser.parseDouble(charArray);
    }
}


