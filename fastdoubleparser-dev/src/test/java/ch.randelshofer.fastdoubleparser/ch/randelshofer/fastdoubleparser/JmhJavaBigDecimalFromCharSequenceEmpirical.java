/*
 * @(#)JmhJavaBigDecimalFromCharSequence.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.28
 * # VM version: OpenJDK 64-Bit Server VM, Oracle Corporation, 19+36-2238
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Without self-made BigSignificand class
 *                         (str)  Mode  Cnt     Score    Error  Units
 * m                           0  avgt   25     9.164 ±  0.939  ns/op
 * m                         1.0  avgt   25    12.075 ±  0.051  ns/op
 * m                         365  avgt   25    10.583 ±  0.026  ns/op
 * m                        10.1  avgt   25    13.171 ±  0.060  ns/op
 *        123.45678901234567e123  avgt    5    33.040 ±  2.761  ns/op
 *          123.4567890123456789  avgt    5    48.920 ±  0.339  ns/op
 *      123.4567890123456789e123  avgt    5    56.127 ±  0.895  ns/op
 *          -0.29235596393453456  avgt    5    21.653 ±  0.993  ns/op
 *      588356425133246152117873  avgt    5   561.996 ± 13.278  ns/op
 *      5773983001708984375E-308  avgt    5  6980.994 ± 36.474  ns/op
 *
 * Benchmark               (str)  Mode  Cnt     Score   Error  Units
 * After refactoring:
 *                         (str)  Mode  Cnt     Score   Error  Units
 *                             0  avgt    2    13.386          ns/op
 *                           1.0  avgt    2    13.209          ns/op
 *                           365  avgt    2    11.097          ns/op
 *                          10.1  avgt    2    14.730          ns/op
 *        123.45678901234567e123  avgt    2    38.795          ns/op
 *          123.4567890123456789  avgt    2    22.519          ns/op
 *      123.4567890123456789e123  avgt    2    30.404          ns/op
 *          -0.29235596393453456  avgt    2    22.140          ns/op
 * 47928588356425133246152117873  avgt    2   261.176          ns/op
 * 596295773983001708984375E-308  avgt    2  5075.937          ns/op
 *
 * Before refactoring:
 * m                           0  avgt    2     5.102          ns/op
 * m                         1.0  avgt    2    13.881          ns/op
 * m                         365  avgt    2    12.272          ns/op
 * m                        10.1  avgt    2    15.358          ns/op
 *        123.45678901234567e123  avgt    2    51.550          ns/op
 *          123.4567890123456789  avgt    2    37.761          ns/op
 *      123.4567890123456789e123  avgt    2    47.188          ns/op
 *          -0.29235596393453456  avgt    2    26.997          ns/op
 * 47928588356425133246152117873  avgt    2   264.457          ns/op
 * 596295773983001708984375E-308  avgt    2  5205.855          ns/op
 * </pre>
 */

@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"})
//@Measurement(iterations = 2)
//@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhJavaBigDecimalFromCharSequenceEmpirical {


    @Param({
            "0",
            "1.0",
            "365",
            "10.1",
            "123.45678901234567e123",
            "123.4567890123456789",
            "123.4567890123456789e123",
            "-0.29235596393453456",
            "94950146746195022190939969168798551415894884143192300883495947928588356425133246152117873",
            "2.22507385850720212418870147920222032907240528279439037814303133837435107319244194686754406432563881851382188218502438069999947733013005649884107791928741341929297200970481951993067993290969042784064731682041565926728632933630474670123316852983422152744517260835859654566319282835244787787799894310779783833699159288594555213714181128458251145584319223079897504395086859412457230891738946169368372321191373658977977723286698840356390251044443035457396733706583981055420456693824658413747607155981176573877626747665912387199931904006317334709003012790188175203447190250028061277777916798391090578584006464715943810511489154282775041174682194133952466682503431306181587829379004205392375072083366693241580002758391118854188641513168478436313080237596295773983001708984375E-308"

    })
    public String str;

    @Benchmark
    public BigDecimal m() {
        return JavaBigDecimalParser.parseBigDecimal(str);
    }
}





