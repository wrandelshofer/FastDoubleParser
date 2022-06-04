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
 * # VM version: JDK 19-ea, OpenJDK 64-Bit Server VM, 19-ea+20-1369
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * VECTOR
 *
 * Benchmark             (str)  Mode  Cnt   Score   Error  Units
 * m    123.45678901234567e123  avgt    2  46.386          ns/op
 * m      123.4567890123456789  avgt    2  27.349          ns/op
 * m  123.4567890123456789e123  avgt    2  35.732          ns/op
 * m      -0.29235596393453456  avgt    2  26.483          ns/op
 *
 * SWAR
 *
 * Benchmark             (str)  Mode  Cnt   Score   Error  Units
 * m                         0  avgt    2   5.983          ns/op
 * m                       1.0  avgt    2  16.613          ns/op
 * m                       365  avgt    2  15.813          ns/op
 * m                      10.1  avgt    2  15.883          ns/op
 * m    123.45678901234567e123  avgt    2  39.784          ns/op
 * m      123.4567890123456789  avgt    2  26.901          ns/op
 * m  123.4567890123456789e123  avgt    2  32.918          ns/op
 * m      -0.29235596393453456  avgt    2  24.405          ns/op
 * m     0x123.456789abcdep123  avgt    2  31.260          ns/op
 * </pre>
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 17.0.1, OpenJDK 64-Bit Server VM, 17.0.1+12-jvmci-21.3-b05
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark             (str)  Mode  Cnt   Score   Error  Units
 * m                         0  avgt    2   6.189          ns/op
 * m                       365  avgt    2  14.710          ns/op
 * m                      10.1  avgt    2  16.301          ns/op
 * m    123.45678901234567e123  avgt    2  37.905          ns/op
 * m      123.4567890123456789  avgt    2  35.235          ns/op
 * m  123.4567890123456789e123  avgt    2  39.817          ns/op
 * m      -0.29235596393453456  avgt    2  33.384          ns/op
 * m     0x123.456789abcdep123  avgt    2  29.581          ns/op
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
        return FastDoubleParser.parseDouble(byteArray);
    }
}


