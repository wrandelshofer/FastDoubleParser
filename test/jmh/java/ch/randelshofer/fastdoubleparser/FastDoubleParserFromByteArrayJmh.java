/*
 * @(#)DoubleParserJmhBenchmark.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
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
 * # JMH version: 1.28
 * # VM version: JDK 19-ea, OpenJDK 64-Bit Server VM, 19-ea+33-2224
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark             (str)  Mode  Cnt   Score   Error  Units
 * m                         0  avgt    2   5.693          ns/op
 * m                       365  avgt    2  12.696          ns/op
 * m                      10.1  avgt    2  15.071          ns/op
 * m    123.45678901234567e123  avgt    2  34.540          ns/op
 * m      123.4567890123456789  avgt    2  23.182          ns/op
 * m  123.4567890123456789e123  avgt    2  29.311          ns/op
 * m      -0.29235596393453456  avgt    2  20.927          ns/op
 * m     0x123.456789abcdep123  avgt    2  29.703          ns/op
 * </pre>
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 17, OpenJDK 64-Bit Server VM, 17+35-2724
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark             (str)  Mode  Cnt   Score   Error  Units
 * m                         0  avgt    2   5.817          ns/op
 * m                       365  avgt    2  13.326          ns/op
 * m                      10.1  avgt    2  15.387          ns/op
 * m    123.45678901234567e123  avgt    2  36.986          ns/op
 * m      123.4567890123456789  avgt    2  24.286          ns/op
 * m  123.4567890123456789e123  avgt    2  33.748          ns/op
 * m      -0.29235596393453456  avgt    2  23.058          ns/op
 * m     0x123.456789abcdep123  avgt    2  30.115          ns/op
 * </pre>
 */
@Fork(value = 1, jvmArgsAppend = {"-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        //       ,"-XX:+UnlockDiagnosticVMOptions", "-XX:PrintAssemblyOptions=intel", "-XX:CompileCommand=print,ch/randelshofer/fastdoubleparser/FastDoubleParser.*"

})
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class FastDoubleParserFromByteArrayJmh {
    @Param({
            "0",
            "365",
            "10.1",
            "123.45678901234567e123",
            "123.4567890123456789",
            "123.4567890123456789e123",
            "-0.29235596393453456",
            "0x123.456789abcdep123"
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


