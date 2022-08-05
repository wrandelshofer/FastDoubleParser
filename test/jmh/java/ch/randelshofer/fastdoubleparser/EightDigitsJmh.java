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
 * # VM version: JDK 19-ea, OpenJDK 64-Bit Server VM, 19-ea+31-2203
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
 *
 * Benchmark     (DigitsCharSequence)  Mode  Cnt   Score   Error  Units
 * 01ByteArrayDecScalar      12345678  avgt    5  8.977 ± 0.063  ns/op
 * 01ByteArrayDecScalar      12345x78  avgt    5  7.252 ± 0.107  ns/op
 * 01ByteArrayDecScalarMul10 12345678  avgt    5  8.653 ± 0.060  ns/op
 * 01ByteArrayDecScalarMul10 12345x78  avgt    5  6.621 ± 0.315  ns/op
 *
 * 02StringDecScalar         12345678  avgt    2  10.101          ns/op
 * 02StringDecScalar         12345x78  avgt    2   9.144          ns/op
 * 03CharArrayDecScalar      12345678  avgt    2   8.879          ns/op
 * 03CharArrayDecScalar      12345x78  avgt    2   6.655          ns/op
 *
 * 11ByteArrayDecSwar        12345678  avgt    2   3.687          ns/op
 * 11ByteArrayDecSwar        12345x78  avgt    2   3.216          ns/op
 * 12CharArrayDecSwar        12345678  avgt    2   4.616          ns/op
 * 12CharArrayDecSwar        12345x78  avgt    2   4.120          ns/op
 * 13StringDecSwar           12345678  avgt    2   6.208          ns/op
 * 13StringDecSwar           12345x78  avgt    2   4.847          ns/op
 * 14ByteArrayHexSwar        12345678  avgt    2   6.116          ns/op
 * 14ByteArrayHexSwar        12345x78  avgt    2   3.531          ns/op
 * 15CharArrayHexSwar        12345678  avgt    2   8.928          ns/op
 * 15CharArrayHexSwar        12345x78  avgt    2   7.679          ns/op
 *
 * 21ByteArrayDecVector      12345678  avgt    2   5.053          ns/op
 * 21ByteArrayDecVector      12345x78  avgt    2   3.231          ns/op
 * 22CharArrayDecVector      12345678  avgt    2   5.000          ns/op
 * 22CharArrayDecVector      12345x78  avgt    2   3.045          ns/op
 * 23StringDecVector         12345678  avgt    2   7.590          ns/op
 * 23StringDecVector         12345x78  avgt    2   5.064          ns/op
 * 24ByteArrayHexVector      12345678  avgt    2   6.611          ns/op
 * 24ByteArrayHexVector      12345x78  avgt    2   3.013          ns/op
 * 25CharArrayHexVector      12345678  avgt    2   6.433          ns/op
 * 25CharArrayHexVector      12345x78  avgt    2   2.990          ns/op
 * </pre>
 */
@Fork(value = 1, jvmArgsAppend = {"-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector",
        "--enable-preview"
        //,"-XX:+UnlockDiagnosticVMOptions", "-XX:PrintAssemblyOptions=intel", "-XX:CompileCommand=print,ch/randelshofer/fastdoubleparser/EightDigitsJmh.*"
})
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

    /**
     * Returns {@code a * 10}.
     * <p>
     * We compute {@code (a + a * 4) * 2}, which is {@code (a + (a << 2)) << 1}.
     * <p>
     * Expected assembly code on x64:
     * <pre>
     *         lea     eax, [rdi+rdi*4]
     *         add     eax, eax
     * </pre>
     * Expected assembly code on aarch64:
     * <pre>
     *         add     w0, w0, w0, lsl 2
     *         lsl     w0, w0, 1
     * </pre>
     */
    private static int mul10(int a) {
        return (a + (a << 2)) << 1;
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
        String str = this.eightDigitsCharSequence;
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

        return FastDoubleVector.tryToParseEightDigitsUtf16(first, second);
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
        return '0' <= c && c <= '9';
    }

}


