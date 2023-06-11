/*
 * @(#)JmhJavaDoubleFromCharSequenceEmpirical.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 20-ea, OpenJDK 64-Bit Server VM, 20-ea+27-2213
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 * </pre>
 * (str)  Mode  Cnt     Score   Error  Units
 * m                         0  avgt    2     5.131          ns/op
 * m                       365  avgt    2    12.730          ns/op
 * m                      10.1  avgt    2    14.839          ns/op
 * m  -1.2345678901234568E-121  avgt    2    34.010          ns/op
 * m      -0.29235596393453456  avgt    2    25.021          ns/op
 * m   -0x123.456789abcdep-123  avgt    2    28.997          ns/op
 * m 95773983001708984375E-308  avgt    2  9455.148          ns/op
 * </pre>
 */
@Fork(value = 1, jvmArgsAppend = {"-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview",

        // Options for analysis with https://github.com/AdoptOpenJDK/jitwatch
        //"-XX:+UnlockDiagnosticVMOptions",
        //"-Xlog:class+load=info",
        //"-XX:+LogCompilation",
        //"-XX:+PrintAssembly"
})
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhJavaDoubleFromCharSequenceEmpirical {
    @Param({
            "0"
            , "365"
            , "10.1"
            , "-1.2345678901234568E-121"
            , "-0.29235596393453456"
            , "-0x123.456789abcdep-123"
            , "2.22507385850720212418870147920222032907240528279439037814303133837435107319244194686754406432563881851382188218502438069999947733013005649884107791928741341929297200970481951993067993290969042784064731682041565926728632933630474670123316852983422152744517260835859654566319282835244787787799894310779783833699159288594555213714181128458251145584319223079897504395086859412457230891738946169368372321191373658977977723286698840356390251044443035457396733706583981055420456693824658413747607155981176573877626747665912387199931904006317334709003012790188175203447190250028061277777916798391090578584006464715943810511489154282775041174682194133952466682503431306181587829379004205392375072083366693241580002758391118854188641513168478436313080237596295773983001708984375E-308"


    })
    public String str;

    @Benchmark
    public double m() {
        return JavaDoubleParser.parseDouble(str);
    }
}


