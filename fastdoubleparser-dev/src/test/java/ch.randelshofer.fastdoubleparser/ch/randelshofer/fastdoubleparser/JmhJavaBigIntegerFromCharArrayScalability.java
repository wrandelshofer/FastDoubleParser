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
import java.util.concurrent.TimeUnit;

import static ch.randelshofer.fastdoubleparser.Strings.repeat;

/**
 * Benchmarks for selected integer strings.
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 20-ea, OpenJDK 64-Bit Server VM, 20-ea+29-2280
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 *          (digits)  Mode  Cnt     _        Score   Error  Units
 * hex             1  avgt          _       20.936          ns/op
 * hex            10  avgt          _       31.734          ns/op
 * hex           100  avgt          _      159.260          ns/op
 * hex          1000  avgt          _     1408.135          ns/op
 * hex         10000  avgt          _    14324.334          ns/op
 * hex        100000  avgt          _   141958.011          ns/op
 * hex       1000000  avgt          _  1446624.174          ns/op
 * hex      10000000  avgt          _ 16437162.558          ns/op
 * hex     100000000  avgt          _163623055.290          ns/op
 * parDec          1  avgt          _        3.048          ns/op
 * parDec         10  avgt          _       17.486          ns/op
 * parDec        100  avgt          _      435.753          ns/op
 * parDec       1000  avgt          _     4892.866          ns/op
 * parDec      10000  avgt          _   111659.249          ns/op
 * parDec     100000  avgt          _  2402003.316          ns/op
 * parDec    1000000  avgt          _ 68541837.137          ns/op
 * parDec   10000000  avgt         2_405123965.800          ns/op
 * parDec  100000000  avgt        60_993703656.000          ns/op
 * parDec  646391315  avgt       936_806558929.000          ns/op
 * seqDec          1  avgt          _        3.065          ns/op
 * seqDec         10  avgt          _       15.378          ns/op
 * seqDec        100  avgt          _      448.063          ns/op
 * seqDec       1000  avgt          _     5161.490          ns/op
 * seqDec      10000  avgt          _   165607.185          ns/op
 * seqDec     100000  avgt          _  6283289.137          ns/op
 * seqDec    1000000  avgt          _205836024.143          ns/op
 * seqDec   10000000  avgt         4_112426090.000          ns/op
 * seqDec  100000000  avgt        97_338750044.000          ns/op
 * seqDec  646391315  avgt       601_801982449.000          ns/op
 * </pre>
 */
@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"
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
public class JmhJavaBigIntegerFromCharArrayScalability {


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
            , "646391315"

    })
    public int digits;
    private char[] decLiteral;

    @Setup(Level.Trial)
    public void setUp() {
        String str = repeat("9806543217", (digits + 9) / 10).substring(0, digits);
        decLiteral = str.toCharArray();
    }

    @Benchmark
    public BigInteger hex() {
        return JavaBigIntegerParser.parseBigInteger(decLiteral, 16);
    }

    @Benchmark
    public BigInteger seqDec() {
        return JavaBigIntegerParser.parseBigInteger(decLiteral);
    }

    @Benchmark
    public BigInteger parDec() {
        return JavaBigIntegerParser.parallelParseBigInteger(decLiteral);
    }
}





