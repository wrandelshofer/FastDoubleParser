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
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.37
 * # VM version: JDK 23, OpenJDK 64-Bit Server VM, 23+37
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 * </pre>
 * (str)  Mode  Cnt   Score   Error  Units
 * m  7.3177701707893310e15  avgt    2  21.365          ns/op
 * m    9007199254740991e22  avgt    2   1.686          ns/op
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
public class JmhUseBigDecimalForSlowPath {
    @Param({
            "7.3177701707893310e15"//Outside Clinger fast path, bail-out in semi-fast path, 7.3177701707893310e+15
            , "9007199254740991e22"//Inside Clinger fast path (max_clinger_significand, max_clinger_exponent), 9007199254740991e22


    })
    public String str;
    public BigDecimal bigDecimal;

    @Setup
    public void setup() {
        BigSignificand b = new BigSignificand(FastIntegerMath.estimateNumBits(str.length()));
        int integerStartIndex = Character.isDigit(str.charAt(0)) ? 0 : 1;
        boolean isNegative = str.charAt(0) == '-';
        int integerEndIndex = str.indexOf('.');
        int fractionStartIndex = integerEndIndex + 1;
        int fractionEndIndex = str.indexOf('e');
        for (int i = integerStartIndex; i < integerEndIndex; i++) {
            char ch = str.charAt(i);
            int digit = (char) (ch - '0');
            b.fma(10, digit);
            System.out.println(digit + "  " + b.toBigInteger());
        }
        for (int i = fractionStartIndex; i < fractionEndIndex; i++) {
            char ch = str.charAt(i);
            int digit = (char) (ch - '0');
            b.fma(10, digit);
            System.out.println(digit + "  " + b.toBigInteger());
        }
        int exponentValue = Integer.parseInt(str.substring(fractionEndIndex + 1));
        int exponent = exponentValue - fractionEndIndex + fractionStartIndex;

        System.out.println("str=" + str);
        BigInteger bigInteger = b.toBigInteger();
        if (isNegative) bigInteger = bigInteger.negate();
        System.out.println("significand=" + bigInteger);
        System.out.println("exponent=" + exponent);
        System.out.println(bigDecimal = new BigDecimal(bigInteger, exponent));
        System.out.println();

        assert bigDecimal.doubleValue() == Double.parseDouble(str);
    }

    @Benchmark
    public double mConversion() {
        return bigDecimal.doubleValue();
    }
}


