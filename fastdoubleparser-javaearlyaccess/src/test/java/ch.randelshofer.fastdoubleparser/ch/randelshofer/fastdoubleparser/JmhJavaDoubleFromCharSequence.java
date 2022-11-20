/*
 * @(#)JmhJavaDoubleFromCharSequence.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 19-ea, OpenJDK 64-Bit Server VM, 20-ea+22-1594
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 * </pre>
 *                       (str)  Mode  Cnt     Score    Error  Units
 *                           0  avgt    5     5.671 ±  0.200  ns/op
 *                         365  avgt    5    12.572 ±  0.129  ns/op
 *                        10.1  avgt    5    15.126 ±  0.235  ns/op
 *      123.45678901234567e123  avgt    5    36.075 ±  0.228  ns/op
 *        123.4567890123456789  avgt    5    25.596 ±  0.106  ns/op
 *    123.4567890123456789e123  avgt    5    36.864 ±  0.068  ns/op
 *        -0.29235596393453456  avgt    5    23.277 ±  0.275  ns/op
 *       0x123.456789abcdep123  avgt    5    27.080 ±  1.010  ns/op
 * 6295773983001708984375E-308  avgt    5  9398.083 ± 75.037  ns/op
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 19, OpenJDK 64-Bit Server VM, 19+36-2238
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark             (str)  Mode  Cnt     Score    Error  Units
 *                           0  avgt    5     5.566 ±  0.096  ns/op
 *                         1.0  avgt    5    14.634 ±  0.329  ns/op
 *                         365  avgt    5    13.348 ±  0.179  ns/op
 *                        10.1  avgt    5    15.175 ±  0.191  ns/op
 *      123.45678901234567e123  avgt    5    37.289 ±  0.355  ns/op
 *        123.4567890123456789  avgt    5    27.086 ±  0.191  ns/op
 *    123.4567890123456789e123  avgt    5    37.779 ±  0.286  ns/op
 *        -0.29235596393453456  avgt    5    24.239 ±  1.075  ns/op
 *       0x123.456789abcdep123  avgt    5    27.866 ± 0.735  ns/op
 * 6295773983001708984375E-308  avgt    5  9503.849 ± 93.268  ns/op
 *
 * Benchmark             (str)  Mode  Cnt     Score   Error  Units
 * m                         0  avgt    2     5.465          ns/op
 * m                       1.0  avgt    2    14.802          ns/op
 * m                       365  avgt    2    13.043          ns/op
 * m                      10.1  avgt    2    14.667          ns/op
 * m    123.45678901234567e123  avgt    2    38.379          ns/op
 * m      123.4567890123456789  avgt    2    27.434          ns/op
 * m  123.4567890123456789e123  avgt    2    33.667          ns/op
 * m      -0.29235596393453456  avgt    2    23.727          ns/op
 * m     0x123.456789abcdep123  avgt    2    27.099          ns/op
 *
 * m  2.225073858507...E-308    avgt    2  9668.358          ns/op (fallback to Double)
 * m  2.225073858507...E-308    avgt    2 52447.754          ns/op (fallback to BigDecimal)
 * m  2.225073858507...E-308    avgt    2 42281.529          ns/op (fallback to BigSignificand)
 * m  2.225073858507...E-308    avgt    2  8263.074          ns/op (fallback to BigSignificand + fix for
 *                                                                  https://bugs.openjdk.org/browse/JDK-8205592)
 * m  2.225073858507...E-308    avgt    2 15836.796          ns/op (fallback to BigDecimal + fix for
 *                                                                  https://bugs.openjdk.org/browse/JDK-8205592)
 * </pre>
 */
@Fork(value = 1, jvmArgsAppend = {"-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"
        , "-XX:+UnlockDiagnosticVMOptions"
        //,"-XX:PrintAssemblyOptions=intel", "-XX:CompileCommand=print,ch/randelshofer/fastdoubleparser/FastDoubleParser.*"
        //,"-XX:+PrintInlining"
})
@Measurement(iterations = 5)
@Warmup(iterations = 3)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhJavaDoubleFromCharSequence {
    @Param({
            "0",
            "365",
            "10.1",
            "123.45678901234567e123",
            "123.4567890123456789",
            "123.4567890123456789e123",
            "-0.29235596393453456",
            "0x123.456789abcdep123"
            , "2.22507385850720212418870147920222032907240528279439037814303133837435107319244194686754406432563881851382188218502438069999947733013005649884107791928741341929297200970481951993067993290969042784064731682041565926728632933630474670123316852983422152744517260835859654566319282835244787787799894310779783833699159288594555213714181128458251145584319223079897504395086859412457230891738946169368372321191373658977977723286698840356390251044443035457396733706583981055420456693824658413747607155981176573877626747665912387199931904006317334709003012790188175203447190250028061277777916798391090578584006464715943810511489154282775041174682194133952466682503431306181587829379004205392375072083366693241580002758391118854188641513168478436313080237596295773983001708984375E-308"

    })
    public String str;

    @Benchmark
    public double m() {
        return JavaDoubleParser.parseDouble(str);
    }
}


