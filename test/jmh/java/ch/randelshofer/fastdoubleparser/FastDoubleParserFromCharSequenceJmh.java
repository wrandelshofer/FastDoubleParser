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
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 17.0.4, OpenJDK 64-Bit Server VM, 17.0.4+8-jvmci-22.2-b06
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark             (str)  Mode  Cnt   Score   Error  Units
 * m                         0  avgt    2   3.301          ns/op
 * m                       1.0  avgt    2   8.832          ns/op
 * m                       365  avgt    2   7.611          ns/op
 * m                      10.1  avgt    2  15.050          ns/op
 * m    123.45678901234567e123  avgt    2  47.701          ns/op
 * m      123.4567890123456789  avgt    2  32.883          ns/op
 * m  123.4567890123456789e123  avgt    2  36.771          ns/op
 * m      -0.29235596393453456  avgt    2  27.865          ns/op
 * m     0x123.456789abcdep123  avgt    2  29.254          ns/op
 * </pre>
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 17.0.1, OpenJDK 64-Bit Server VM, 17.0.1+12-jvmci-21.3-b05
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark             (str)  Mode  Cnt   Score   Error  Units
 * m                         0  avgt    2   6.842          ns/op
 * m                       1.0  avgt    2  15.523          ns/op
 * m                       365  avgt    2  14.116          ns/op
 * m                      10.1  avgt    2  16.245          ns/op
 * m    123.45678901234567e123  avgt    2  46.071          ns/op (regression)
 * m      123.4567890123456789  avgt    2  28.477          ns/op
 * m  123.4567890123456789e123  avgt    2  35.852          ns/op
 * m      -0.29235596393453456  avgt    2  24.824          ns/op
 * m     0x123.456789abcdep123  avgt    2  28.582          ns/op
 * </pre>
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 17.0.1, OpenJDK 64-Bit Server VM, 17.0.1+12-jvmci-21.3-b05
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark             (str)  Mode  Cnt   Score   Error  Units
 * m                         0  avgt    2   6.189          ns/op
 * m                       1.0  avgt    2  15.507          ns/op
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
public class FastDoubleParserFromCharSequenceJmh {
    @Param({
            "0",
            "1.0",
            "365",
            "10.1",
            "123.45678901234567e123",
            "123.4567890123456789",
            "123.4567890123456789e123",
            "-0.29235596393453456",
            "0x123.456789abcdep123"
    })
    public String str;

    @Benchmark
    public double m() {
        return FastDoubleParser.parseDouble(str);
    }
}


