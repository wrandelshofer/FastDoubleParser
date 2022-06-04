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
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;


/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 17.0.1, OpenJDK 64-Bit Server VM, 17+35-2724
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
 *
 * Benchmark     (eightDigitsCharSequence)  Mode  Cnt   Score   Error  Units
 * m01ByteArrayDecScalar          12345678  avgt    2   9.477          ns/op
 * m02CharArrayDecScalar          12345678  avgt    2  10.896          ns/op
 * m03StringDecScalar             12345678  avgt    2   9.324          ns/op
 *
 * m11ByteArrayDecSwar            12345678  avgt    2   3.885          ns/op
 * m12CharArrayDecSwar            12345678  avgt    2   5.581          ns/op
 * m13StringDecSwar               12345678  avgt    2   7.074          ns/op
 * m14ByteArrayHexSwar            12345678  avgt    2   6.584          ns/op
 * m15CharArrayHexSwar            12345678  avgt    2   9.392          ns/op
 *
 * m21ByteArrayDecVector          12345678  avgt    2   5.057          ns/op
 * m22CharArrayDecVector          12345678  avgt    2   5.155          ns/op
 * m23StringDecVector             12345678  avgt    2   9.128          ns/op
 * m24ByteArrayHexVector          12345678  avgt    2   6.941          ns/op
 * m25CharArrayHexVector          12345678  avgt    2   6.912          ns/op
 *
 * Benchmark     (eightDigitsCharSequence)  Mode  Cnt  Score   Error  Units
 * m01ByteArrayDecScalar          12345x78  avgt    2  7.523          ns/op
 * m02CharArrayDecScalar          12345x78  avgt    2  9.974          ns/op
 * m03StringDecScalar             12345x78  avgt    2  7.271          ns/op
 *
 * m11ByteArrayDecSwar            12345x78  avgt    2  3.081          ns/op
 * m12CharArrayDecSwar            12345x78  avgt    2  4.565          ns/op
 * m13StringDecSwar               12345x78  avgt    2  5.373          ns/op
 * m14ByteArrayHexSwar            12345x78  avgt    2  4.235          ns/op
 * m15CharArrayHexSwar            12345x78  avgt    2  8.052          ns/op
 *
 * m21ByteArrayDecVector          12345x78  avgt    2  3.226          ns/op
 * m22CharArrayDecVector          12345x78  avgt    2  3.293          ns/op
 * m23StringDecVector             12345x78  avgt    2  6.671          ns/op
 * m24ByteArrayHexVector          12345x78  avgt    2  3.222          ns/op
 * m25CharArrayHexVector          12345x78  avgt    2  3.302          ns/op
 * </pre>
 */
@Fork(value = 1, jvmArgsAppend = {"-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"})
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class EightDigitsJmh {
    @Param({"12345678", "12345x78"})
    public String eightDigitsCharSequence;
    public char[] eightDigitsCharArray;
    public byte[] eightDigitsByteArray;

    @Setup
    public void prepare() {
        eightDigitsCharArray = eightDigitsCharSequence.toCharArray();
        eightDigitsByteArray = eightDigitsCharSequence.getBytes(StandardCharsets.UTF_8);
    }

    @Benchmark
    public int m00ByteArray7DecDigitsSwar() {
        return FastDoubleSwar.tryToParseSevenDigitsUtf8(eightDigitsByteArray, 1);
    }


    @Benchmark
    public int m01ByteArrayDecScalar() {
        int value = 0;
        for (int i = 0; i < eightDigitsByteArray.length; i++) {
            byte ch = eightDigitsByteArray[i];
            if (isDigit(ch)) {
                value = value * 10 + ch - '0';
            } else {
                return -1;
            }
        }
        return value;
    }

    @Benchmark
    public int m02CharArrayDecScalar() {
        int value = 0;
        for (int i = 0, n = eightDigitsCharSequence.length(); i < n; i++) {
            char ch = eightDigitsCharSequence.charAt(i);
            if (isDigit(ch)) {
                value = value * 10 + ch - '0';
            } else {
                return -1;
            }
        }
        return value;
    }

    @Benchmark
    public int m03StringDecScalar() {
        int value = 0;
        for (int i = 0; i < eightDigitsCharArray.length; i++) {
            char ch = eightDigitsCharArray[i];
            if (isDigit(ch)) {
                value = value * 10 + ch - '0';
            } else {
                return -1;
            }
        }
        return value;
    }

    @Benchmark
    public int m11ByteArrayDecSwar() {
        return FastDoubleSwar.tryToParseEightDigitsUtf8(eightDigitsByteArray, 0);
    }

    @Benchmark
    public int m12CharArrayDecSwar() {
        return FastDoubleSwar.tryToParseEightDigitsUtf16(eightDigitsCharArray, 0);
    }

    @Benchmark
    public int m13StringDecSwar() {
        String str = eightDigitsCharSequence;
        int offset = 0;

        // Performance: We extract the chars in two steps so that we
        //              can benefit from out of order execution in the CPU.
        long first = str.charAt(offset)
                | (long) str.charAt(offset + 1) << 16
                | (long) str.charAt(offset + 2) << 32
                | (long) str.charAt(offset + 3) << 48;

        long second = str.charAt(offset + 4)
                | (long) str.charAt(offset + 5) << 16
                | (long) str.charAt(offset + 6) << 32
                | (long) str.charAt(offset + 7) << 48;

        return FastDoubleSwar.tryToParseEightDigitsUtf16(first, second);
    }

    @Benchmark
    public long m14ByteArrayHexSwar() {
        return FastDoubleSwar.tryToParseEightHexDigitsUtf8(eightDigitsByteArray, 0);
    }

    @Benchmark
    public long m15CharArrayHexSwar() {
        return FastDoubleSwar.tryToParseEightHexDigitsUtf16(eightDigitsCharArray, 0);
    }

    private static boolean isDigit(byte c) {
        return '0' <= c && c <= '9';
    }

    private static boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }
}


