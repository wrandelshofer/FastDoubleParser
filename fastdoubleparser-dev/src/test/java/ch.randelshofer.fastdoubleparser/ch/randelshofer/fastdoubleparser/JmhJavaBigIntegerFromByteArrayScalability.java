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
 *           (digits)  Mode  Cnt    _        Score   Error  Units
 * hex              1  avgt         _       21.791          ns/op
 * hex             10  avgt         _       28.969          ns/op
 * hex            100  avgt         _      132.085          ns/op
 * hex           1000  avgt         _     1136.079          ns/op
 * hex          10000  avgt         _    10960.841          ns/op
 * hex         100000  avgt         _   107655.529          ns/op
 * hex        1000000  avgt         _  1159378.097          ns/op
 * hex       10000000  avgt         _ 13527667.547          ns/op
 * hex      100000000  avgt         _136392967.149          ns/op
 * parDec           1  avgt         _        3.672          ns/op
 * parDec          10  avgt         _       14.365          ns/op
 * parDec         100  avgt         _      451.139          ns/op
 * parDec        1000  avgt         _     4822.375          ns/op
 * parDec       10000  avgt         _   110501.414          ns/op
 * parDec      100000  avgt         _  2868811.338          ns/op
 * parDec     1000000  avgt         _ 37864639.087          ns/op
 * parDec    10000000  avgt         _655854712.875          ns/op
 * parDec   100000000  avgt       10_396544430.000          ns/op
 * parDec   646456993  avgt      105_021966151.000          ns/op
 * parDec  1292782621  avgt      109_162700507.000          ns/op
 * seqDec           1  avgt         _        3.636          ns/op
 * seqDec          10  avgt         _       14.796          ns/op
 * seqDec         100  avgt         _      452.669          ns/op
 * seqDec        1000  avgt         _     5070.600          ns/op
 * seqDec       10000  avgt         _   171745.829          ns/op
 * seqDec      100000  avgt         _  5439038.952          ns/op
 * seqDec     1000000  avgt         _ 87288729.661          ns/op
 * seqDec    10000000  avgt        1_533647605.714          ns/op
 * seqDec   100000000  avgt       23_473318663.000          ns/op
 * seqDec   646456993  avgt      229_532516121.000          ns/op
 * seqDec  1292782621  avgt      228_312615257.000          ns/op
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
@Measurement(iterations = 1)
@Warmup(iterations = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhJavaBigIntegerFromByteArrayScalability {


    @Param({
            // "1"
            // , "10"
            // , "100"
            // , "1000"
            // , "10000"
            // , "100000"
            // , "1000000"
            // , "10000000"
            "100000000"
            // , "646456993"
            //    "1292782621"

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

    /*
        @Benchmark
        public BigInteger hex() {
            return JavaBigIntegerParser.parseBigInteger(decLiteral, 16);
        }
    */
    @Benchmark
    public BigInteger dec() {
        return JavaBigIntegerParser.parseBigInteger(decLiteral);
    }
}





