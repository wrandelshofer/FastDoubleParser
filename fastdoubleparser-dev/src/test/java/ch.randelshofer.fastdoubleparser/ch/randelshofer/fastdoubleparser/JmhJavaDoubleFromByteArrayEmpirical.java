/*
 * @(#)JmhJavaDoubleFromByteArrayEmpirical.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
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
 * # VM version: JDK 20-ea, OpenJDK 64-Bit Server VM, 20-ea+27-2213
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
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
@Measurement(iterations = 5)
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


