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
 * # JMH version: 1.35
 * # VM version: JDK 19, OpenJDK 64-Bit Server VM, 19+36-2238
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark             (str)  Mode  Cnt   Score   Error  Units
 * m                         0  avgt    2   5.465          ns/op
 * m                       1.0  avgt    2  14.802          ns/op
 * m                       365  avgt    2  13.043          ns/op
 * m                      10.1  avgt    2  14.667          ns/op
 * m    123.45678901234567e123  avgt    2  38.379          ns/op
 * m      123.4567890123456789  avgt    2  27.434          ns/op
 * m  123.4567890123456789e123  avgt    2  33.667          ns/op
 * m      -0.29235596393453456  avgt    2  23.727          ns/op
 * m     0x123.456789abcdep123  avgt    2  27.099          ns/op
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
        return JavaDoubleParser.parseDouble(str);
    }
}


