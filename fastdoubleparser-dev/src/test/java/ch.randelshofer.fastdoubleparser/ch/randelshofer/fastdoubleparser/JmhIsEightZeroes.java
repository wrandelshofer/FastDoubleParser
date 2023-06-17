/*
 * @(#)java
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
 * optimized1  00000000  avgt   10  4.063 ± 0.025  ns/op
 * optimized1  12345678  avgt   10  1.628 ± 0.010  ns/op
 * optimized2  00000000  avgt   10  4.877 ± 0.021  ns/op
 * optimized2  12345678  avgt   10  5.050 ± 0.060  ns/op
 * original    00000000  avgt   10  3.940 ± 0.023  ns/op
 * original    12345678  avgt   10  3.955 ± 0.050  ns/op
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
public class JmhIsEightZeroes {

    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    @Param({"00000000", "12345678"})
    private String ch;

    @Benchmark
    public void original(Blackhole blackhole) {
        blackhole.consume(FastDoubleSwar.isEightZeroes(ch, 0));
    }

    @Benchmark
    public void optimized1(Blackhole blackhole) {
        blackhole.consume(isEightZeroes1(ch, 0));
    }

    @Benchmark
    public void optimized2(Blackhole blackhole) {
        blackhole.consume(isEightZeroes2(ch, 0));
    }


    // enrolled version
    public static boolean isEightZeroes1(CharSequence a, int offset) {
        return a.charAt(offset) == '0' &&
                a.charAt(offset + 1) == '0' &&
                a.charAt(offset + 2) == '0' &&
                a.charAt(offset + 3) == '0' &&
                a.charAt(offset + 4) == '0' &&
                a.charAt(offset + 5) == '0' &&
                a.charAt(offset + 6) == '0' &&
                a.charAt(offset + 7) == '0';
    }

    // enrolled range check conditions
    public static boolean isEightZeroes2(CharSequence a, int offset) {
        return 0 == (
                (char)(a.charAt(offset + 0) - '0') |
                (char)(a.charAt(offset + 1) - '0') |
                (char)(a.charAt(offset + 2) - '0') |
                (char)(a.charAt(offset + 3) - '0') |
                (char)(a.charAt(offset + 4) - '0') |
                (char)(a.charAt(offset + 5) - '0') |
                (char)(a.charAt(offset + 6) - '0') |
                (char)(a.charAt(offset + 7) - '0')
        );
    }
}