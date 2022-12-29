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
 * hex             1  avgt          _       28.118          ns/op
 * hex            10  avgt          _       38.880          ns/op
 * hex           100  avgt          _      178.967          ns/op
 * hex          1000  avgt          _     1599.581          ns/op
 * hex         10000  avgt          _    17330.320          ns/op
 * hex        100000  avgt          _   170223.337          ns/op
 * hex       1000000  avgt          _  1987982.711          ns/op
 * hex      10000000  avgt          _ 18958389.212          ns/op
 * hex     100000000  avgt          _186365188.556          ns/op
 * parDec          1  avgt          _        3.861          ns/op
 * parDec         10  avgt          _       17.894          ns/op
 * parDec        100  avgt          _      471.581          ns/op
 * parDec       1000  avgt          _     5452.639          ns/op
 * parDec      10000  avgt          _   118939.665          ns/op
 * parDec     100000  avgt          _  2542289.835          ns/op
 * parDec    1000000  avgt          _ 69882443.306          ns/op
 * parDec   10000000  avgt         2_604154877.000          ns/op
 * parDec  100000000  avgt        64_185023916.000          ns/op
 * parDec  646391315  avgt       979_539101414.000          ns/op
 * seqDec          1  avgt          _        3.547          ns/op
 * seqDec         10  avgt          _       16.847          ns/op
 * seqDec        100  avgt          _      449.925          ns/op
 * seqDec       1000  avgt          _     5279.527          ns/op
 * seqDec      10000  avgt          _   167100.851          ns/op
 * seqDec     100000  avgt          _  6301831.420          ns/op
 * seqDec    1000000  avgt          _207809933.796          ns/op
 * seqDec   10000000  avgt         4_120704527.667          ns/op
 * seqDec  100000000  avgt        96_521550616.000          ns/op
 * seqDec  646391315  avgt       614_960001037.000          ns/op
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
public class JmhJavaBigIntegerFromCharSequenceScalability {


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
    private String decLiteral;

    @Setup(Level.Trial)
    public void setUp() {
        String str = repeat("9806543217", (digits + 9) / 10).substring(0, digits);
        decLiteral = str;
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





