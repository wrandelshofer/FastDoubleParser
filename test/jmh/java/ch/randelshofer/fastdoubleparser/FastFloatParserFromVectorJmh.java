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
 * Benchmark  (str)  Mode  Cnt   Score   Error  Units
 * m              0  avgt    2  33.584          ns/op
 * m            365  avgt    2  36.287          ns/op
 * m           10.1  avgt    2  46.346          ns/op
 * m      3.1415927  avgt    2  47.454          ns/op
 * m  1.6162552e-35  avgt    2  64.859          ns/op
 * </pre>
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 17, OpenJDK 64-Bit Server VM, 17+35-2724
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark  (str)  Mode  Cnt    Score   Error  Units
 * m              0  avgt    2   51.447          ns/op
 * m            365  avgt    2   59.366          ns/op
 * m           10.1  avgt    2   75.002          ns/op
 * m      3.1415927  avgt    2   77.392          ns/op
 * m  1.6162552e-35  avgt    2  103.139          ns/op
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
public class FastFloatParserFromVectorJmh {


    @Param({
            "0",
            "365",
            "10.1",
            "3.1415927",
            "1.6162552e-35",
    })
    public String str;
    private byte[] byteArray;

    @Setup
    public void prepare() {
        byteArray = str.getBytes(StandardCharsets.ISO_8859_1);
    }

    @Benchmark
    public double m() {
        return new VectorizedFloatFromByteArray().parseFloat(byteArray, 0, byteArray.length);
    }
}





