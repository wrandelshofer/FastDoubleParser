/*
 * @(#)JmhEightDigits.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;


/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 19-ea, OpenJDK 64-Bit Server VM, 19+36-2238
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
 *
 * Benchmark(eightDigitsCharSequence)  Mode  Cnt  Score   Error  Units
 * 01ByteArrayDecScalar       12345678  avgt   16  6.478 ± 0.038  ns/op
 * 01ByteArrayDecScalar       12345x78  avgt   16  5.098 ± 0.011  ns/op
 * 01ByteArrayDecScalarMul10  12345678  avgt   16  5.648 ± 0.011  ns/op
 * 01ByteArrayDecScalarMul10  12345x78  avgt   16  4.600 ± 0.046  ns/op
 * 01ByteArrayDecScalarL      12345678  avgt    4  7.333 ± 0.180  ns/op
 * 01ByteArrayDecScalarL      12345x78  avgt    4  5.324 ± 0.070  ns/op
 * 01ByteArrayDecScalarMul10L 12345678  avgt    4  6.672 ± 0.067  ns/op
 * 01ByteArrayDecScalarMul10L 12345x78  avgt    4  5.042 ± 0.167  ns/op
 * 02StringDecScalar          12345678  avgt   16  7.316 ± 0.072  ns/op
 * 02StringDecScalar          12345x78  avgt   16  5.840 ± 0.236  ns/op
 * 03CharArrayDecScalar       12345678  avgt   16  6.274 ± 0.027  ns/op
 * 03CharArrayDecScalar       12345x78  avgt   16  4.742 ± 0.028  ns/op
 *
 * 11ByteArrayDecSwar         12345678  avgt   16  2.026 ± 0.011  ns/op
 * 11ByteArrayDecSwar         12345x78  avgt   16  1.229 ± 0.008  ns/op
 * 12CharArrayDecSwar         12345678  avgt   16  2.528 ± 0.019  ns/op
 * 12CharArrayDecSwar         12345x78  avgt   16  2.073 ± 0.020  ns/op
 * 13StringDecSwar            12345678  avgt   16  3.740 ± 0.013  ns/op
 * 13StringDecSwar            12345x78  avgt   16  3.131 ± 0.014  ns/op
 * 14ByteArrayHexSwar         12345678  avgt    4  2.782 ± 0.027  ns/op
 * 14ByteArrayHexSwar         12345x78  avgt    4  1.901 ± 0.022  ns/op
 * 15CharArrayHexSwar         12345678  avgt    4  4.637 ± 0.123  ns/op
 * 15CharArrayHexSwar         12345x78  avgt    4  3.638 ± 0.039  ns/op
 *
 * 21ByteArrayDecVector       12345678  avgt   16  2.845 ± 0.055  ns/op
 * 21ByteArrayDecVector       12345x78  avgt   16  1.460 ± 0.007  ns/op
 * 22CharArrayDecVector       12345678  avgt   16  2.701 ± 0.018  ns/op
 * 22CharArrayDecVector       12345x78  avgt   16  1.300 ± 0.012  ns/op
 * 23StringDecVector          12345678  avgt   16  5.055 ± 0.047  ns/op
 * 23StringDecVector          12345x78  avgt   16  3.277 ± 0.011  ns/op
 * 24ByteArrayHexVector       12345678  avgt   16  3.919 ± 0.036  ns/op
 * 24ByteArrayHexVector       12345x78  avgt   16  1.630 ± 0.010  ns/op
 * 25CharArrayHexVector       12345678  avgt   16  3.607 ± 0.177  ns/op
 * 25CharArrayHexVector       12345x78  avgt   16  1.373 ± 0.024  ns/op
 *
 * Process finished with exit code 0
 * </pre>
 */
@Fork(value = 1, jvmArgsAppend = {"-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector",
        "--enable-preview"
        //,"-XX:+UnlockDiagnosticVMOptions", "-XX:PrintAssemblyOptions=intel", "-XX:CompileCommand=print,ch/randelshofer/fastdoubleparser/EightDigitsJmh.*"
})
@Measurement(iterations = 1)
@Warmup(iterations = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhEightDigits {
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
    public int m01ByteArrayDecScalarMul10() {
        int value = 0;
        for (int i = 0; i < eightDigitsByteArray.length; i++) {
            byte ch = eightDigitsByteArray[i];
            if (isDigit(ch)) {
                value = mul10(value) + ch - '0';
            } else {
                return -1;
            }
        }
        return value;
    }

    @Benchmark
    public long m01ByteArrayDecScalarL() {
        long value = 0;
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
    public long m01ByteArrayDecScalarMul10L() {
        long value = 0;
        for (int i = 0; i < eightDigitsByteArray.length; i++) {
            byte ch = eightDigitsByteArray[i];
            if (isDigit(ch)) {
                value = mul10L(value) + ch - '0';
            } else {
                return -1;
            }
        }
        return value;
    }


    @Benchmark
    public int m02StringDecScalar() {
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
    public int m03CharArrayDecScalar() {
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
        return FastDoubleSwar.tryToParseEightDigits(eightDigitsCharArray, 0);
    }

    @Benchmark
    public int m13StringDecSwar() {
        return FastDoubleSwar.tryToParseEightDigits(eightDigitsCharSequence, 0);
    }

    @Benchmark
    public long m14ByteArrayHexSwar() {
        return FastDoubleSwar.tryToParseEightHexDigits(eightDigitsByteArray, 0);
    }

    @Benchmark
    public long m15CharArrayHexSwar() {
        return FastDoubleSwar.tryToParseEightHexDigits(eightDigitsCharArray, 0);
    }


    @Benchmark
    public int m21ByteArrayDecVector() {
        return FastDoubleVector.tryToParseEightDigitsUtf8(eightDigitsByteArray, 0);
    }

    @Benchmark
    public int m22CharArrayDecVector() {
        return FastDoubleVector.tryToParseEightDigitsUtf16(eightDigitsCharArray, 0);
    }


    @Benchmark
    public int m23StringDecVector() {
        return FastDoubleVector.tryToParseEightDigits(eightDigitsCharSequence, 0);
    }


    @Benchmark
    public long m24ByteArrayHexVector() {
        return FastDoubleVector.tryToParseEightHexDigitsUtf8(eightDigitsByteArray, 0);
    }

    @Benchmark
    public long m25CharArrayHexVector() {
        return FastDoubleVector.tryToParseEightHexDigitsUtf16(eightDigitsCharArray, 0);
    }


    private static boolean isDigit(byte c) {
        return '0' <= c && c <= '9';
    }

    private static boolean isDigit(char c) {
        // We check if '0' <= c && c <= '9'.
        // We take advantage of the fact that char is an unsigned value:
        // subtracted values wrap around.
        return (char) (c - '0') <= (char) ('9' - '0');
    }

    /**
     * Returns {@code a * 10}.
     * <p>
     * We compute {@code (a + a * 4) * 2}, which is {@code (a + (a << 2)) << 1}.
     * <p>
     * Expected assembly code on x64:
     * <pre>
     * lea     rax, [rdi+rdi*4]
     * add     rax, rax
     * </pre>
     * Expected assembly code on aarch64:
     * <pre>
     * add     x0, x0, x0, lsl 2
     * lsl     x0, x0, 1
     * </pre>
     */
    public static long mul10L(long a) {
        return (a + (a << 2)) << 1;
    }

    /**
     * Returns {@code a * 10}.
     * <p>
     * We compute {@code (a + a * 4) * 2}, which is {@code (a + (a << 2)) << 1}.
     * <p>
     * Expected assembly code on x64:
     * <pre>
     * lea     eax, [rdi+rdi*4]
     * add     eax, eax
     * </pre>
     * Expected assembly code on aarch64:
     * <pre>
     * add     w0, w0, w0, lsl 2
     * lsl     w0, w0, 1
     * </pre>
     */
    public static int mul10(int a) {
        return (a + (a << 2)) << 1;
    }
}


