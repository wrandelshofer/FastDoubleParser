/*
 * @(#)JmhJavaBigDecimalFromByteArray.java
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
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.28
 * # VM version: OpenJDK 64-Bit Server VM, Oracle Corporation, 19+36-2238
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Without self-made class BigSignificand.
 *                    (str)  Mode  Cnt     Score     Error  Units
 *                        0  avgt    5     9.468 ±   5.638  ns/op
 *                      1.0  avgt    5    11.863 ±   0.158  ns/op
 *                      365  avgt    5     9.781 ±   0.114  ns/op
 *                     10.1  avgt    5    12.471 ±   0.143  ns/op
 *   123.45678901234567e123  avgt    5    32.458 ±   0.420  ns/op
 *     123.4567890123456789  avgt    5    38.686 ±   0.527  ns/op
 * 123.4567890123456789e123  avgt    5    40.715 ±   0.407  ns/op
 *     -0.29235596393453456  avgt    5    17.845 ±   0.091  ns/op
 * 588356425133246152117873  avgt    5   510.067 ±   8.775  ns/op
 * 5773983001708984375E-308  avgt    5  6556.212 ± 123.193  ns/op
 *
 * Benchmark             (str)  Mode  Cnt       Score   Error  Units
 * m                         0  avgt    2     7.287          ns/op
 * m                       1.0  avgt    2    15.359          ns/op
 * m                       365  avgt    2    10.047          ns/op
 * m                      10.1  avgt    2    13.428          ns/op
 * m    123.45678901234567e123  avgt    2    30.263          ns/op
 * m      123.4567890123456789  avgt    2    38.777          ns/op
 * m  123.4567890123456789e123  avgt    2    38.467          ns/op
 * m      -0.29235596393453456  avgt    2    17.960          ns/op
 *                  ...2117873  avgt    2   523.903          ns/op
 *                  ...75E-308  avgt    2  6781.365          ns/op
 * </pre>
 */

@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"

        //       ,"-XX:+UnlockDiagnosticVMOptions", "-XX:PrintAssemblyOptions=intel", "-XX:CompileCommand=print,ch/randelshofer/fastdoubleparser/FastDoubleParser.*"

})
@Measurement(iterations = 5)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhJavaBigDecimalFromByteArray {


    @Param({

            "0",
            "1.0",
            "365",
            "10.1",
            "123.45678901234567e123",
            "123.4567890123456789",
            "123.4567890123456789e123",
            "-0.29235596393453456",
            //"0x123.456789abcdep123",

            "94950146746195022190939969168798551415894884143192300883495947928588356425133246152117873",
            "2.22507385850720212418870147920222032907240528279439037814303133837435107319244194686754406432563881851382188218502438069999947733013005649884107791928741341929297200970481951993067993290969042784064731682041565926728632933630474670123316852983422152744517260835859654566319282835244787787799894310779783833699159288594555213714181128458251145584319223079897504395086859412457230891738946169368372321191373658977977723286698840356390251044443035457396733706583981055420456693824658413747607155981176573877626747665912387199931904006317334709003012790188175203447190250028061277777916798391090578584006464715943810511489154282775041174682194133952466682503431306181587829379004205392375072083366693241580002758391118854188641513168478436313080237596295773983001708984375E-308"

    })
    public String str;
    private byte[] byteArray;

    @Setup
    public void prepare() {
        byteArray = str.getBytes(StandardCharsets.ISO_8859_1);
    }

    @Benchmark
    public BigDecimal m() {
        return JavaBigDecimalParser.parallelParseBigDecimal(byteArray);
    }
}





