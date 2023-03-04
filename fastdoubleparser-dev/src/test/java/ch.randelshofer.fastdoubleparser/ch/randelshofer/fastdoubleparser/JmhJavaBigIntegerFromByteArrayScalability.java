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
 *        (digits)  Mode  Cnt     _        Score             Error  Units
 * dec           1  avgt    4     _        3.895 ±           0.051  ns/op
 * dec          10  avgt    4     _       13.073 ±           1.012  ns/op
 * dec         100  avgt    4     _      417.791 ±           7.421  ns/op
 * dec        1000  avgt    4     _     4713.890 ±          95.678  ns/op
 * dec       10000  avgt    4     _   159283.207 ±        5076.778  ns/op
 * dec      100000  avgt    4     _  5148743.967 ±      269250.312  ns/op
 * dec     1000000  avgt    4     _ 82326733.043 ±      988263.342  ns/op
 * dec    10000000  avgt    4    1_359363201.768 ±   306440228.295  ns/op
 * dec   100000000  avgt    4   22_241605723.500 ±  1486815357.018  ns/op
 * dec   646456993  avgt    4  202_298930337.000 ± 10730148276.875  ns/op
 * dec  1292782621  avgt    4  197_858301116.000 ±  5372926664.595  ns/op
 * hex           1  avgt    4     _       15.576 ±           0.693  ns/op
 * hex          10  avgt    4     _       27.551 ±           9.898  ns/op
 * hex         100  avgt    4     _      121.339 ±           7.258  ns/op
 * hex        1000  avgt    4     _     1043.819 ±          28.706  ns/op
 * hex       10000  avgt    4     _    10741.632 ±         258.920  ns/op
 * hex      100000  avgt    4     _   112710.224 ±        2946.230  ns/op
 * hex     1000000  avgt    4     _  1145607.433 ±       37200.668  ns/op
 * hex    10000000  avgt    4     _ 12940545.545 ±      182941.922  ns/op
 * hex   100000000  avgt    4     _133010989.979 ±     9262778.811  ns/op
 * hex   646456993  avgt    4     _786577513.250 ±    86855481.927  ns/op
 * hex  1292782621  avgt    4     _881336008.671 ±   248831282.555  ns/op
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





