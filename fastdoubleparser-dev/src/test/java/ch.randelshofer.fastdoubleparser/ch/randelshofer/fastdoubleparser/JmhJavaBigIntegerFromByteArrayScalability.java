/*
 * @(#)JmhJavaBigIntegerFromByteArrayScalability.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
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
 *           (digits)  Mode  Cnt      _        Score   Error  Units
 * hex              1  avgt           _       21.791          ns/op
 * hex             10  avgt           _       28.969          ns/op
 * hex            100  avgt           _      132.085          ns/op
 * hex           1000  avgt           _     1136.079          ns/op
 * hex          10000  avgt           _    10960.841          ns/op
 * hex         100000  avgt           _   107655.529          ns/op
 * hex        1000000  avgt           _  1159378.097          ns/op
 * hex       10000000  avgt           _ 13527667.547          ns/op
 * hex      100000000  avgt           _136392967.149          ns/op
 * parDec           1  avgt           _        3.309          ns/op
 * parDec          10  avgt           _       14.114          ns/op
 * parDec         100  avgt           _      431.813          ns/op
 * parDec        1000  avgt           _     4776.129          ns/op
 * parDec       10000  avgt           _   109625.327          ns/op
 * parDec      100000  avgt           _  4569606.572          ns/op
 * parDec     1000000  avgt           _ 56500481.191          ns/op
 * parDec    10000000  avgt          1_637870034.143          ns/op
 *           (digits)  Mode  Cnt      _        Score          Error  Units
 * seqDec           1  avgt    4      _        3.733 ±        0.349  ns/op
 * seqDec          10  avgt    4      _       17.959 ±        1.761  ns/op
 * seqDec         100  avgt    4      _      505.845 ±      111.849  ns/op
 * seqDec        1000  avgt    4      _     5527.022 ±      199.217  ns/op
 * seqDec       10000  avgt    4      _   197207.502 ±     6519.489  ns/op
 * seqDec      100000  avgt    4      _  6707270.184 ±   382960.495  ns/op
 * seqDec     1000000  avgt    4      _111331188.763 ± 63617989.241  ns/op
 * seqDec    10000000  avgt    4     1_610442339.750 ± 48623222.687  ns/op
 * seqDec   100000000  avgt         26_460563123.000          ns/op
 * seqDec   646391315  avgt        241_777155009.000          ns/op
 * seqDec  1292782621  avgt        395_434726445.000          ns/op
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
@Warmup(iterations = 0)
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
            //"100000000"
            //, "646391315"
            "646456993"
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

    /*
    @Benchmark
    public BigInteger hex() {
        return JavaBigIntegerParser.parseBigInteger(decLiteral, 16);
    }
*/
    @Benchmark
    public BigInteger seqDec() {
        return JavaBigIntegerParser.parseBigInteger(decLiteral);
    }
/*
    @Benchmark
    public BigInteger parDec() {
        return JavaBigIntegerParser.parallelParseBigInteger(decLiteral);
    }

 */
}





