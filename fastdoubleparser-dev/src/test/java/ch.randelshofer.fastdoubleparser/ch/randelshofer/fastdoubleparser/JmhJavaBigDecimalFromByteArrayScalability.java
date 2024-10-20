/*
 * @(#)JmhJavaBigDecimalFromByteArrayScalability.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
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

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static ch.randelshofer.fastdoubleparser.Strings.repeatStringBuilder;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.37
 * # VM version: JDK 21-ea, OpenJDK 64-Bit Server VM, 21-ea+20-1677
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 *     (digits)  Mode  Cnt            Score   Error  Units
 * m          1  avgt                10.688          ns/op
 * m         10  avgt                19.903          ns/op
 * m        100  avgt               551.403          ns/op
 * m       1000  avgt              5999.290          ns/op
 * m      10000  avgt            221929.429          ns/op
 * m     100000  avgt           5881864.021          ns/op
 * m    1000000  avgt         110878102.879          ns/op
 * m   10000000  avgt        1649224533.143          ns/op
 * m  100000000  avgt       25932859264.000          ns/op
 * </pre>
 */

@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"
        , "-Xmx24g"

        //       ,"-XX:+UnlockDiagnosticVMOptions", "-XX:PrintAssemblyOptions=intel", "-XX:CompileCommand=print,ch/randelshofer/fastdoubleparser/JavaBigDecimalParser.*"

})
@Measurement(iterations = 1)
@Warmup(iterations = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public final class JmhJavaBigDecimalFromByteArrayScalability {
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
            // , "646391315"// The maximal number of non-zero digits in the significand
    })

    public int digits;
    private byte[] input;

    @Setup(Level.Trial)
    public void setUp() {
        StringBuilder str = repeatStringBuilder("9", digits + 1);
        str.setCharAt(digits / 3, '.');
        input = str.toString().getBytes(StandardCharsets.UTF_8);
    }


    @Benchmark
    public BigDecimal m() {
        return JavaBigDecimalParser.parseBigDecimal(input);
    }
}