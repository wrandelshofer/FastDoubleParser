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
 * Benchmarks for selected long strings.
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 19-ea, OpenJDK 64-Bit Server VM, 19-ea+20-1369
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark                (str)  Mode  Cnt   Score   Error  Units
 * vectorByteArray                    0  avgt    2  33.423          ns/op
 * vectorByteArray                  365  avgt    2  32.738          ns/op
 * vectorByteArray                 -365  avgt    2  33.791          ns/op
 * vectorByteArray                 +365  avgt    2  32.755          ns/op
 * vectorByteArray     1234568790123456  avgt    2  14.059          ns/op
 * vectorByteArray     -123456879012345  avgt    2  37.385          ns/op
 *
 * scalarByteArray                    0  avgt    2   5.186          ns/op
 * scalarByteArray                  365  avgt    2   6.752          ns/op
 * scalarByteArray                 -365  avgt    2   6.738          ns/op
 * scalarByteArray                 +365  avgt    2   7.274          ns/op
 * scalarByteArray     1234568790123456  avgt    2  18.196          ns/op
 * scalarByteArray     -123456879012345  avgt    2  17.511          ns/op
 *
 * scalarCharSequence                 0  avgt    2   5.998          ns/op
 * scalarCharSequence               365  avgt    2   8.553          ns/op
 * scalarCharSequence              -365  avgt    2   8.271          ns/op
 * scalarCharSequence              +365  avgt    2   8.447          ns/op
 * scalarCharSequence  1234568790123456  avgt    2  19.709          ns/op
 * scalarCharSequence  -123456879012345  avgt    2  18.387          ns/op
 *
 * baseline                           0  avgt    2   6.471          ns/op
 * baseline                         365  avgt    2   9.099          ns/op
 * baseline                        -365  avgt    2   9.638          ns/op
 * baseline                        +365  avgt    2   9.835          ns/op
 * baseline            1234568790123456  avgt    2  22.893          ns/op
 * baseline            -123456879012345  avgt    2  22.919          ns/op
 * baseline         9223372036854775807  avgt    2  27.004          ns/op
 * baseline        -9223372036854775808  avgt    2  26.891          ns/op
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
public class VectorizedLongFromByteArrayJmh {


    @Param({
            "0",
            "365",
            "-365",
            "+365",
            "1234568790123456",
            "-123456879012345"
            //  ""+Long.MAX_VALUE,
            //  ""+Long.MIN_VALUE
    })
    public String str;
    private byte[] byteArray;

    @Setup
    public void prepare() {
        byteArray = str.getBytes(StandardCharsets.ISO_8859_1);
    }

    @Benchmark
    public double vectorByteArray() {
        return new LongFromByteArray().parseLongVectorized(byteArray, 0, byteArray.length);
    }

    @Benchmark
    public double scalarByteArray() {
        return new LongFromByteArray().parseLong(byteArray, 0, byteArray.length);
    }

    @Benchmark
    public double scalarCharSequence() {
        return new LongFromCharSequence().parseLong(str, 0, str.length());
    }

    @Benchmark
    public double dumb() {
        return new LongFromByteArray().parseLongDumb(byteArray, 0, byteArray.length);
    }

    @Benchmark
    public double baseline() {
        return Long.parseLong(str);
    }
}





