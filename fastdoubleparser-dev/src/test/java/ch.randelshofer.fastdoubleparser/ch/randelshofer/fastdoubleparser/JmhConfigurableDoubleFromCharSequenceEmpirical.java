/*
 * @(#)JmhJavaDoubleFromCharSequenceEmpirical.java
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
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.37
 * # VM version: JDK 23, OpenJDK 64-Bit Server VM, 23+37
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 * </pre>
 * (str)  Mode  Cnt    Score   Error  Units
 * mBigDecimal                            -7.3177701707893310e+15  avgt    2   37.462          ns/op
 * mBigDecimal                                9007199254740991e22  avgt    2    1.772          ns/op
 * mConfigurableDoubleParserCharArray     -7.3177701707893310e+15  avgt    2  161.468          ns/op
 * mConfigurableDoubleParserCharArray         9007199254740991e22  avgt    2   33.743          ns/op
 * mConfigurableDoubleParserCharSequence  -7.3177701707893310e+15  avgt    2  161.913          ns/op
 * mConfigurableDoubleParserCharSequence      9007199254740991e22  avgt    2   34.766          ns/op
 * mJavaDoubleParserCharArray             -7.3177701707893310e+15  avgt    2  128.115          ns/op
 * mJavaDoubleParserCharArray                 9007199254740991e22  avgt    2   21.152          ns/op
 * mJavaDoubleParserCharSequence          -7.3177701707893310e+15  avgt    2  124.387          ns/op
 * mJavaDoubleParserCharSequence              9007199254740991e22  avgt    2   21.240          ns/op
 * mJavaLangDouble                        -7.3177701707893310e+15  avgt    2   75.791          ns/op
 * mJavaLangDouble                            9007199254740991e22  avgt    2  106.108          ns/op
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
public final class JmhConfigurableDoubleFromCharSequenceEmpirical {
    @Param({
            "-7.3177701707893310e+15"//Outside Clinger fast path, bail-out in semi-fast path, 7.3177701707893310e+15
            , "9007199254740991e22"//Inside Clinger fast path (max_clinger_significand, max_clinger_exponent), 9007199254740991e22


    })
    public String str;
    public BigDecimal bigDecimal;
    private char[] charArray;

    @Setup
    public void setup() {
        charArray = str.toCharArray();
        bigDecimal = new BigDecimal(str);
    }

    private ConfigurableDoubleParser cdp = new ConfigurableDoubleParser();

    @Benchmark
    public double mJavaDoubleParserCharSequence() {
        return JavaDoubleParser.parseDouble(str);
    }

    @Benchmark
    public double mBigDecimal() {
        return bigDecimal.doubleValue();
    }

    @Benchmark
    public double mJavaDoubleParserCharArray() {
        return JavaDoubleParser.parseDouble(charArray);
    }

    @Benchmark
    public double mJavaLangDouble() {
        return Double.parseDouble(str);
    }

    @Benchmark
    public double mConfigurableDoubleParserCharSequence() {
        return cdp.parseDouble(str);
    }

    @Benchmark
    public double mConfigurableDoubleParserCharArray() {
        return cdp.parseDouble(charArray);
    }
}


