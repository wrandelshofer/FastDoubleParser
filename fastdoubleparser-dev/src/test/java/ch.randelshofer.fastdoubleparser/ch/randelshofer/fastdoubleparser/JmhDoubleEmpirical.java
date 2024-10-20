/*
 * @(#)JmhDoubleEmpirical.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
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
 * # JMH version: 1.36
 * # VM version: JDK 16, OpenJDK 64-Bit Server VM, 20+36-2344
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 * </pre>
 * (str)  Mode  Cnt    Score   Error  Units
 * 0x123.456789abcdep123  avgt    2  368.869          ns/op
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 16, OpenJDK 64-Bit Server VM, 16+36-2231
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark             (str)  Mode  Cnt     Score   Error  Units
 * m                         0  avgt    2     9.682          ns/op
 * m                       1.0  avgt    2    15.788          ns/op
 * m                       365  avgt    2    16.584          ns/op
 * m                      10.1  avgt    2    18.746          ns/op
 * m    123.45678901234567e123  avgt    2   234.406          ns/op
 * m      123.4567890123456789  avgt    2   413.116          ns/op
 * m  123.4567890123456789e123  avgt    2   286.112          ns/op
 * m      -0.29235596393453456  avgt    2   175.137          ns/op
 * m     0x123.456789abcdep123  avgt    2   334.153          ns/op
 * m  949501467461950221909...  avgt    2   270.113          ns/op
 * m  2.22507385850720...E-308  avgt    2  9342.667          ns/op
 * m  -1.7976931348623157E-323  avgt    2  181.399          ns/op
 * </pre>
 */

@Fork(value = 1)
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public final class JmhDoubleEmpirical {


    @Param({
            "0"
            , "1.0"
            , "365"
            , "10.1"
            , "123.45678901234567e123"
            , "123.4567890123456789"
            , "123.4567890123456789e123"
            , "-1.7976931348623157E-323"
            , "-0.29235596393453456"
            , "0x123.456789abcdep123"
            , "94950146746195022190939969168798551415894884143192300883495947928588356425133246152117873"
            , "2.22507385850720212418870147920222032907240528279439037814303133837435107319244194686754406432563881851382188218502438069999947733013005649884107791928741341929297200970481951993067993290969042784064731682041565926728632933630474670123316852983422152744517260835859654566319282835244787787799894310779783833699159288594555213714181128458251145584319223079897504395086859412457230891738946169368372321191373658977977723286698840356390251044443035457396733706583981055420456693824658413747607155981176573877626747665912387199931904006317334709003012790188175203447190250028061277777916798391090578584006464715943810511489154282775041174682194133952466682503431306181587829379004205392375072083366693241580002758391118854188641513168478436313080237596295773983001708984375E-308"
    })
    public String str;

    @Benchmark
    public double m() {
        return Double.parseDouble(str);
    }
}





