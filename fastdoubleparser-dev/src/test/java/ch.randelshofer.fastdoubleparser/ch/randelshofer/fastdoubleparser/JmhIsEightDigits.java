/*
 * @(#)JmhIsEightDigits.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.36
 * # VM version: JDK 20.0.1, OpenJDK 64-Bit Server VM, 20.0.1+9-29
 * # Intel(R) Core(TM) i5-6200U CPU @ 2.30GHz
 *
 * Benchmark   Mode  Cnt  Score   Error  Units
 * optimized1  avgt   10  4.621 ± 0.381  ns/op
 * optimized2  avgt   10  4.452 ± 0.023  ns/op
 * optimized3  avgt   10  5.274 ± 0.028  ns/op
 * original    avgt   10  5.677 ± 0.026  ns/op
 *
 * Process finished with exit code 0
 *
 * Process finished with exit code 0
 * </pre>
 */
@Fork(value = 1, jvmArgsAppend = {"--enable-preview"})
@Measurement(iterations = 10, time = 1)
@Warmup(iterations = 5, time = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhIsEightDigits {

    @SuppressWarnings("FieldMayBeFinal")
    private String ch = "12345678";

    @Benchmark
    public void original(Blackhole blackhole) {
        blackhole.consume(FastDoubleSwar.isEightDigits(ch, 0));
    }

    @Benchmark
    public void optimized1(Blackhole blackhole) {
        blackhole.consume(isEightDigits1(ch, 0));
    }

    @Benchmark
    public void optimized2(Blackhole blackhole) {
        blackhole.consume(isEightDigits2(ch, 0));
    }

    @Benchmark
    public void optimized3(Blackhole blackhole) {
        blackhole.consume(isEightDigits3(ch, 0));
    }

    // method isDigit() replaced with range check conditions
    public static boolean isEightDigits1(CharSequence a, int offset) {
        boolean success = true;
        for (int i = 0; i < 8; i++) {
            char ch = a.charAt(i + offset);
            success &= '0' <= ch && ch <= '9';
        }
        return success;
    }

    // enrolled range check conditions
    public static boolean isEightDigits2(CharSequence a, int offset) {
        return '0' <= a.charAt(offset) && a.charAt(offset) <= '9' &&
                '0' <= a.charAt(offset + 1) && a.charAt(offset + 1) <= '9' &&
                '0' <= a.charAt(offset + 2) && a.charAt(offset + 2) <= '9' &&
                '0' <= a.charAt(offset + 3) && a.charAt(offset + 3) <= '9' &&
                '0' <= a.charAt(offset + 4) && a.charAt(offset + 4) <= '9' &&
                '0' <= a.charAt(offset + 5) && a.charAt(offset + 5) <= '9' &&
                '0' <= a.charAt(offset + 6) && a.charAt(offset + 6) <= '9' &&
                '0' <= a.charAt(offset + 7) && a.charAt(offset + 7) <= '9';
    }

    // enrolled char conversion trick
    public static boolean isEightDigits3(CharSequence a, int offset) {
        return (char) (a.charAt(offset) - '0') < 10 |
                (char) (a.charAt(offset + 1) - '0') < 10 |
                (char) (a.charAt(offset + 2) - '0') < 10 |
                (char) (a.charAt(offset + 3) - '0') < 10 |
                (char) (a.charAt(offset + 4) - '0') < 10 |
                (char) (a.charAt(offset + 5) - '0') < 10 |
                (char) (a.charAt(offset + 6) - '0') < 10 |
                (char) (a.charAt(offset + 7) - '0') < 10;
    }
}