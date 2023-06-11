/*
 * @(#)JmhBigDecimalScalability.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static ch.randelshofer.fastdoubleparser.Strings.repeat;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 20-ea, OpenJDK 64-Bit Server VM, 20-ea+27-2213
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark    Mode     Cnt                    Score   Error   Units
 * m         24  avgt       2                    116.786          ns/op
 * m          1  avgt       2                     12.473          ns/op
 * m         10  avgt       2                     20.776          ns/op
 * m        100  avgt       2                    480.817          ns/op
 * m       1000  avgt       2                  14832.646          ns/op
 * m      10000  avgt       2                1200632.862          ns/op
 * m     100000  avgt       2              117587913.424          ns/op
 * m    1000000  avgt       2            12027995151.000          ns/op
 * m   10000000  avgt       1          1281686999490.000          ns/op
 * m  100000000  avgt       ?    128_168_699_949_000.000          ns/op
 * m  646391315  avgt       ?  5_355_166_821_464_850.000          ns/op
 * </pre>
 */

@Fork(value = 1, jvmArgsAppend = {
        "-Xmx16g"
})
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhBigDecimalScalability {


    @Param({
            //  "24"
            //  , "1"
            //  , "10"
            //  , "100"
            //  , "1000"
            //  , "10000"
            //  , "100000"
            //  , "1000000"
            //  , "10000000"
            "100000000"
            , "646391315"// The maximal number non-zero digits in the significand

    })
    public int digits;
    private String str;

    @Setup(Level.Trial)
    public void setUp() {
        str = repeat("7", digits);
    }

    @Benchmark
    public BigDecimal m() {
        return new BigDecimal(str);
    }
}





