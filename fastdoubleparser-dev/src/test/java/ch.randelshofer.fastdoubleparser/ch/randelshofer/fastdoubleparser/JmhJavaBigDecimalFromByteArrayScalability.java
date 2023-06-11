/*
 * @(#)JmhJavaBigDecimalFromByteArrayScalability.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static ch.randelshofer.fastdoubleparser.Strings.repeat;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.36
 * # VM version: JDK 21-ea, OpenJDK 64-Bit Server VM, 21-ea+20-1677
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * via Double.parseDouble
 *    (digits)  Mode  Cnt     Score   Error  Units
 * m         1  avgt         26.139          ns/op
 * m        10  avgt         31.454          ns/op
 * m       100  avgt        244.925          ns/op
 * m       200  avgt        506.917          ns/op
 * m       300  avgt        912.334          ns/op
 * m       400  avgt       1365.712          ns/op
 * m       500  avgt       2082.934          ns/op
 * m       600  avgt       2902.877          ns/op
 * m       700  avgt       3537.485          ns/op
 * m       800  avgt       4446.817          ns/op
 *
 * via BigDecimal:
 *
 *    (digits)  Mode  Cnt     Score   Error  Units
 * m         1  avgt         25.626          ns/op
 * m        10  avgt         31.454          ns/op
 * m       100  avgt        255.092          ns/op
 * m       200  avgt        506.333          ns/op
 * m       300  avgt        887.673          ns/op
 * m       400  avgt       1423.838          ns/op
 * m       500  avgt       2114.062          ns/op
 * m       600  avgt       2928.280          ns/op
 * m       700  avgt       3567.702          ns/op
 * m       800  avgt       4362.583          ns/op
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
public class JmhJavaBigDecimalFromByteArrayScalability {
    @Param({
            "1"
            , "10"
            , "100"
            , "200"
            , "300"
            , "400"
            , "500"
            , "600"
            , "700"
            , "800"
            // , "10000"
            // , "100000"
            // , "1000000"
            //,"10000000"
            //, "100000000"
            //, "646456993"
            //, "1292782621"

    })
    public int digits;
    private byte[] decLiteral;

    @Setup(Level.Trial)
    public void setUp() {
        String str =
                "-"
                        + repeat("0", Math.max(0, digits - 646456993))
                        + repeat("1234567890", (Math.min(646456993, digits) + 9) / 10).substring(0, Math.min(digits, 646456993))
                        + "e"
                        + (Integer.MIN_VALUE + 1);

        decLiteral = str.getBytes(StandardCharsets.ISO_8859_1);
    }

    @Benchmark
    public BigDecimal m() {
        return JavaBigDecimalParser.parseBigDecimal(decLiteral);
    }


}











