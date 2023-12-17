/*
 * @(#)JmhIsDigit.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;



/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.36
 * # VM version: JDK 20.0.1, OpenJDK 64-Bit Server VM, 20.0.1+9-29
 * # Intel(R) Core(TM) i5-6200U CPU @ 2.30GHz
 *
 * Benchmark                             Mode  Cnt  Score   Error  Units
 * isDigitCharConversion      avgt    5  1.221 ± 0.068  ns/op
 * isDigitCharConversionChar  avgt    5  1.219 ± 0.019  ns/op
 * isDigitIfs                 avgt    5  0.811 ± 0.013  ns/op
 * isDigitIfsChar             avgt    5  0.813 ± 0.005  ns/op
 *
 * Process finished with exit code 0
 * </pre>
 */

@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector",
        "--enable-preview"
})
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 5, time = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhIsDigit {


    public byte b = '0';
    public char c = '0';

    @Benchmark
    public boolean isDigitCharConversion() {
        return isDigitCharConversion(b);
    }
    @Benchmark
    public boolean isDigitIfs() {
        return isDigitIfs(b);
    }
    @Benchmark
    public boolean isDigitCharConversionChar() {
        return isDigitCharConversionChar(c);
    }
    @Benchmark
    public boolean isDigitIfsChar() {
        return isDigitIfsChar(c);
    }

    public static boolean isDigitCharConversionChar(char c) {
        return (char) (c - '0') < 10;
    }

    public static boolean isDigitIfsChar(char c) {
        return '0' <= c && c <= '9';
    }

    public static boolean isDigitCharConversion(byte b) {
        return (char) (b - '0') < 10;
    }

    public static boolean isDigitIfs(byte b) {
        return '0' <= b && b <= '9';
    }
}



