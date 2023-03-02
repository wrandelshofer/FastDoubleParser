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
 * # JMH version: 1.35
 * # VM version: JDK 20-ea, OpenJDK 64-Bit Server VM, 20-ea+29-2280
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 *        (digits)  Mode  Cnt     _        Score            Error  Units
 * dec           1  avgt    4     _        3.880 ±          0.516  ns/op
 * dec          10  avgt    4     _       13.327 ±          0.182  ns/op
 * dec         100  avgt    4     _      431.815 ±         17.828  ns/op
 * dec        1000  avgt    4     _     4723.527 ±        140.066  ns/op
 * dec       10000  avgt    4     _   160260.630 ±       4473.628  ns/op
 * dec      100000  avgt    4     _  5115129.545 ±      30648.873  ns/op
 * dec     1000000  avgt    4     _ 84585203.925 ±    1717849.243  ns/op
 * dec    10000000  avgt    4    1_322781727.969 ±   30016267.997  ns/op
 * dec   100000000  avgt    4   22_319001493.000 ±  151861005.500  ns/op
 * dec   646456993  avgt    4  203_270000609.750 ± 9549048639.338  ns/op
 * dec  1292782621  avgt    4  209_153677323.500 ± 4978317262.414  ns/op
 * hex           1  avgt    4     _       15.976 ±          1.003  ns/op
 * hex          10  avgt    4     _       26.896 ±          0.218  ns/op
 * hex         100  avgt    4     _      131.585 ±          0.756  ns/op
 * hex        1000  avgt    4     _     1046.923 ±         24.097  ns/op
 * hex       10000  avgt    4     _    10789.885 ±        184.191  ns/op
 * hex      100000  avgt    4     _   113695.942 ±       3368.431  ns/op
 * hex     1000000  avgt    4     _  1155096.199 ±      25860.593  ns/op
 * hex    10000000  avgt    4     _ 13046375.887 ±     102391.425  ns/op
 * hex   100000000  avgt    4     _130816711.539 ±     814238.405  ns/op
 * </pre>
 */
@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"
        , "-Xmx14g"
        //, "--add-opens", "java.base/java.math=ALL-UNNAMED"

        // Options for analysis with https://github.com/AdoptOpenJDK/jitwatch
        //,"-XX:+UnlockDiagnosticVMOptions"
        //,"-Xlog:class+load=info"
        //,"-XX:+LogCompilation"
        //,"-XX:+PrintAssembly"

})
@Measurement(iterations = 4)
@Warmup(iterations = 4)
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

    @Setup(Level.Trial)
    public void setUp() {
        String str =
                "-"
                        + repeat("0", Math.max(0, digits - 646456993))
                        + repeat("1234567890", (Math.min(646456993, digits) + 9) / 10).substring(0, Math.min(digits, 646456993));

        decLiteral = str.getBytes(StandardCharsets.ISO_8859_1);
    }


    @Benchmark
        public BigInteger hex() {
            return JavaBigIntegerParser.parseBigInteger(decLiteral, 16);
        }

    @Benchmark
    public BigInteger dec() {
        return JavaBigIntegerParser.parseBigInteger(decLiteral);
    }
}





