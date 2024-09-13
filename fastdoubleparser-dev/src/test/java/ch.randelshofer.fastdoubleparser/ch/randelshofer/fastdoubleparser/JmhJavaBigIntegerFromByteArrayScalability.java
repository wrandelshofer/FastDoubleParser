/*
 * @(#)JmhJavaBigIntegerFromByteArrayScalability.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static ch.randelshofer.fastdoubleparser.Strings.repeat;

/**
 * Benchmarks for selected integer strings.
 * <pre>
 * # JMH version: 1.37
 * # VM version: JDK 22.0.1, OpenJDK 64-Bit Server VM, 22.0.1+8-16
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchm (digits)  Mode  Cnt             Score   Error  Units
 * dec           1  avgt    2             3.293          ns/op
 * dec          10  avgt    2            12.156          ns/op
 * dec         100  avgt    2           432.232          ns/op
 * dec        1000  avgt    2          4651.616          ns/op
 * dec       10000  avgt    2        154486.359          ns/op
 * dec      100000  avgt    2       4868793.278          ns/op
 * dec     1000000  avgt    2      76263104.519          ns/op
 * dec    10000000  avgt    2    1245637131.889          ns/op
 * dec   100000000  avgt    2   20964403409.000          ns/op
 * dec   646456993  avgt    2  204480626006.500          ns/op
 * dec  1292782621  avgt    2  190675819095.500          ns/op
 * hex           1  avgt    2            11.364          ns/op
 * hex          10  avgt    2            23.503          ns/op
 * hex         100  avgt    2            90.966          ns/op
 * hex        1000  avgt    2           747.841          ns/op
 * hex       10000  avgt    2          7732.226          ns/op
 * hex      100000  avgt    2         78843.403          ns/op
 * hex     1000000  avgt    2        759255.965          ns/op
 * hex    10000000  avgt    2       8093226.726          ns/op
 * hex   100000000  avgt    2     112561508.910          ns/op
 * hex   646456993  avgt    2     567704136.418          ns/op
 * hex  1292782621  avgt    2     508893087.985          ns/op
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
                + repeat('0', Math.max(0, digits - 646456993))
                + repeat("1234567890", (Math.min(646456993, digits) + 9) / 10).substring(0, Math.min(digits, 646456993));

        decLiteral = str.getBytes(StandardCharsets.ISO_8859_1);
        str = "-"
                + repeat('0', Math.max(0, digits - 536870912))
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





