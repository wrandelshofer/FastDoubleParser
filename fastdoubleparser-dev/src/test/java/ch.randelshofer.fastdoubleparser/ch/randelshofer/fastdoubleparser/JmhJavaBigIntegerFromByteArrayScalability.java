/*
 * @(#)JmhJavaBigIntegerFromByteArrayScalability.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static ch.randelshofer.fastdoubleparser.Strings.repeat;

/**
 * Benchmarks for selected integer strings.
 * <pre>
 * # JMH version: 1.36
 * # VM version: JDK 21-ea, OpenJDK 64-Bit Server VM, 21-ea+24-2086
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchm (digits)  Mode  Cnt             Score   Error  Units
 * dec           1  avgt    2             3.830          ns/op
 * dec          10  avgt    2            12.742          ns/op
 * dec         100  avgt    2           490.141          ns/op
 * dec        1000  avgt    2          4702.757          ns/op
 * dec       10000  avgt    2        155614.143          ns/op
 * dec      100000  avgt    2       5090985.731          ns/op
 * dec     1000000  avgt    2      80625344.892          ns/op
 * dec    10000000  avgt    2    1298521324.813          ns/op
 * dec   100000000  avgt    2   21618066446.000          ns/op
 * dec   646456993  avgt    2  193819477092.000          ns/op
 * dec  1292782621  avgt    2  216033005512.000          ns/op
 * hex           1  avgt    2            16.569          ns/op
 * hex          10  avgt    2            26.783          ns/op
 * hex         100  avgt    2           139.187          ns/op
 * hex        1000  avgt    2          1217.598          ns/op
 * hex       10000  avgt    2         12751.188          ns/op
 * hex      100000  avgt    2        128000.498          ns/op
 * hex     1000000  avgt    2       1272110.362          ns/op
 * hex    10000000  avgt    2      13750114.163          ns/op
 * hex   100000000  avgt    2     193441331.309          ns/op
 * hex   646456993  avgt    2    1036792579.470          ns/op
 * hex  1292782621  avgt    2     991057402.000          ns/op
 *
 * Process finished with exit code 0
 * </pre>
 */
@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"
        , "-Xmx24g"
        //, "--add-opens", "java.base/java.math=ALL-UNNAMED"

        // Options for analysis with https://github.com/AdoptOpenJDK/jitwatch
        //,"-XX:+UnlockDiagnosticVMOptions"
        //,"-Xlog:class+load=info"
        //,"-XX:+LogCompilation"
        //,"-XX:+PrintAssembly"

})
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhJavaBigIntegerFromByteArrayScalability {


    @Param({
            "1"
            , "10"
            , "100"
            , "1000"
            , "10000"
            , "100000"
            , "1000000"
            , "10000000"
            , "100000000"
            , "646456993"
            , "1292782621"

    })
    public int digits;
    private byte[] decLiteral;
    private byte[] hexLiteral;

    @Setup(Level.Trial)
    public void setUp() {
        String str = "-"
                + repeat("0", Math.max(0, digits - 646456993))
                + repeat("1234567890", (Math.min(646456993, digits) + 9) / 10).substring(0, Math.min(digits, 646456993));

        decLiteral = str.getBytes(StandardCharsets.ISO_8859_1);
        str = "-"
                + repeat("0", Math.max(0, digits - 536870912))
                + repeat("12b4c6d7e0", (Math.min(536870912, digits) + 9) / 10).substring(0, Math.min(digits, 536870912));

        hexLiteral = str.getBytes(StandardCharsets.ISO_8859_1);
    }

    @Benchmark
    public BigInteger hex() {
        return JavaBigIntegerParser.parseBigInteger(hexLiteral, 16);
    }

    @Benchmark
    public BigInteger dec() {
        return JavaBigIntegerParser.parseBigInteger(decLiteral);
    }
}





