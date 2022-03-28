/*
 * @(#)DoubleParserJmhBenchmark.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static ch.randelshofer.fastdoubleparser.FastDoubleSimd.tryToParseEightDigitsUtf8Swar;


/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 17.0.1, OpenJDK 64-Bit Server VM, 17.0.1+12-jvmci-21.3-b05
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
 *
 * Benchmark                              Mode  Cnt      Score   Error  Units
 * m04SwarDependentMultiplications        avgt           3.087          ns/op
 * m05SwarIndependentMultiplications      avgt           3.106          ns/op
 * m07SimdFromCharArray                   avgt          76.830          ns/op
 * m08SimdFromByteArray                   avgt          62.394          ns/op
 * m14SwarDependentFromByteArrayInLoop    avgt        2449.812          ns/op
 * m15SwarIndependentFromByteArrayInLoop  avgt        2510.852          ns/op
 * m17SimdFromCharArrayInLoop             avgt       70393.986          ns/op
 * m18SimdFromByteArrayInLoop             avgt       42270.765          ns/op
 * </pre>
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 18-ea, OpenJDK 64-Bit Server VM, 18-ea+30-2029
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
 *
 *
 * Benchmark                              Mode  Cnt     Score   Error  Units
 * m04SwarDependentMultiplications        avgt          4.338          ns/op
 * m05SwarIndependentMultiplications      avgt          3.645          ns/op
 * m07SimdFromCharArray                   avgt          5.222          ns/op
 * m08SimdFromByteArray                   avgt          4.864          ns/op
 * m14SwarDependentFromByteArrayInLoop    avgt       3018.542          ns/op
 * m15SwarIndependentFromByteArrayInLoop  avgt       3345.844          ns/op
 * m17SimdFromCharArrayInLoop             avgt       3267.633          ns/op
 * m18SimdFromByteArrayInLoop             avgt       3143.888          ns/op
 * </pre>
 */
@Fork(value = 1, jvmArgsAppend = {"-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"})
@Measurement(iterations = 1)
@Warmup(iterations = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class EightDigitsJmh {
    final static char[] eightDigitsCharArray = "12345678".toCharArray();
    final static byte[] eightDigitsByteArray = "12345678".getBytes(StandardCharsets.UTF_8);
    private final static int[] numbers = new int[1000];
    final static long[] longs = new long[numbers.length];
    final static char[][] charArrays = new char[numbers.length][0];
    final static byte[][] byteArrays = new byte[numbers.length][0];
    private final static VarHandle readLongFromByteArray =
            MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);

    static {
        Random rng = new Random(0);
        for (int i = 0; i < numbers.length; i++) {
            int n = rng.nextInt(9999_9999);
            numbers[i] = n;
            String s = String.format("%08d", n);
            charArrays[i] = s.toCharArray();
            byteArrays[i] = s.getBytes(StandardCharsets.UTF_8);
            longs[i] = (long) readLongFromByteArray.get(byteArrays[i], 0);

            assert n == tryToParseEightDigitsUtf8Swar(byteArrays[i], 0);
            assert n == FastDoubleSimd.tryToParseEightDigitsUtf16Simd(charArrays[i], 0);
        }
    }

    @Benchmark
    public long m05Swar() {
        return tryToParseEightDigitsUtf8Swar(eightDigitsByteArray, 0);
    }

    @Benchmark
    public long m07SimdFromCharArray() {
        return FastDoubleSimd.tryToParseEightDigitsUtf16Simd(eightDigitsCharArray, 0);
    }

    @Benchmark
    public long m08SimdFromByteArray() {
        return FastDoubleSimd.tryToParseEightDigitsUtf8Simd(eightDigitsByteArray, 0);
    }

    @Benchmark
    public long m14SwarLoop() {
        int sum = 0;
        for (byte[] l : byteArrays) {
            sum += tryToParseEightDigitsUtf8Swar(l, 0);
        }
        return sum;
    }

    @Benchmark
    public long m17SimdFromCharArrayInLoop() {
        int sum = 0;
        for (char[] l : charArrays) {
            sum += FastDoubleSimd.tryToParseEightDigitsUtf16Simd(l, 0);
        }
        return sum;
    }

    @Benchmark
    public long m18SimdFromByteArrayInLoop() {
        int sum = 0;
        for (byte[] l : byteArrays) {
            sum += FastDoubleSimd.tryToParseEightDigitsUtf8Simd(l, 0);
        }
        return sum;
    }
}


