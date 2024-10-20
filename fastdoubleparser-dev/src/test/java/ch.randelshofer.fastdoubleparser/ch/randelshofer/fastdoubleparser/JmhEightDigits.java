/*
 * @(#)JmhEightDigits.java
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

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static ch.randelshofer.fastdoubleparser.AbstractNumberParser.lookupHex;


/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.37
 * VM version: JDK 22.0.1, OpenJDK 64-Bit Server VM, 22.0.1+8-16
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
 *
 * Benchmark                                               (eightDigitsCharSequence)  Mode  Cnt   Score   Error  Units
 * JmhEightDigits.m01ByteArrayDecScalarL                                    12345678  avgt    2   7.037          ns/op
 * JmhEightDigits.m01ByteArrayDecScalarL                                    12345x78  avgt    2   5.966          ns/op
 * JmhEightDigits.m01ByteArrayDecScalarMul10                                12345678  avgt    2  13.816          ns/op
 * JmhEightDigits.m01ByteArrayDecScalarMul10                                12345x78  avgt    2   4.808          ns/op
 * JmhEightDigits.m01ByteArrayDecScalarMul10L                               12345678  avgt    2   6.620          ns/op
 * JmhEightDigits.m01ByteArrayDecScalarMul10L                               12345x78  avgt    2   5.705          ns/op
 * JmhEightDigits.m01ByteArrayDecScalarWithIsDigitCall                      12345678  avgt    2   5.753          ns/op
 * JmhEightDigits.m01ByteArrayDecScalarWithIsDigitCall                      12345x78  avgt    2   4.777          ns/op
 * JmhEightDigits.m01ByteArrayDecScalarWithIsDigitCallBranchfree            12345678  avgt    2   5.784          ns/op
 * JmhEightDigits.m01ByteArrayDecScalarWithIsDigitCallBranchfree            12345x78  avgt    2   7.159          ns/op
 * JmhEightDigits.m01ByteArrayDecScalarWithIsDigitInlined                   12345678  avgt    2   6.669          ns/op
 * JmhEightDigits.m01ByteArrayDecScalarWithIsDigitInlined                   12345x78  avgt    2   5.197          ns/op
 * JmhEightDigits.m01ByteArrayDecScalarWithIsDigitInlinedBranchfree         12345678  avgt    2   6.138          ns/op
 * JmhEightDigits.m01ByteArrayDecScalarWithIsDigitInlinedBranchfree         12345x78  avgt    2   6.925          ns/op
 * JmhEightDigits.m02StringDecScalar                                        12345678  avgt    2   7.011          ns/op
 * JmhEightDigits.m02StringDecScalar                                        12345x78  avgt    2   5.997          ns/op
 * JmhEightDigits.m03CharArrayDecScalar                                     12345678  avgt    2   6.916          ns/op
 * JmhEightDigits.m03CharArrayDecScalar                                     12345x78  avgt    2   5.100          ns/op
 * JmhEightDigits.m04ByteArrayHexScalar                                     12345678  avgt    2   4.253          ns/op
 * JmhEightDigits.m04ByteArrayHexScalar                                     12345x78  avgt    2   4.366          ns/op
 * JmhEightDigits.m05CharArrayHexScalar                                     12345678  avgt    2   5.416          ns/op
 * JmhEightDigits.m05CharArrayHexScalar                                     12345x78  avgt    2   5.073          ns/op
 * JmhEightDigits.m11ByteArrayDecSwar                                       12345678  avgt    2   2.017          ns/op
 * JmhEightDigits.m11ByteArrayDecSwar                                       12345x78  avgt    2   0.867          ns/op
 * JmhEightDigits.m12CharArrayDecSwar                                       12345678  avgt    2   2.525          ns/op
 * JmhEightDigits.m12CharArrayDecSwar                                       12345x78  avgt    2   1.388          ns/op
 * JmhEightDigits.m13StringDecSwar                                          12345678  avgt    2   3.900          ns/op
 * JmhEightDigits.m13StringDecSwar                                          12345x78  avgt    2   3.038          ns/op
 * JmhEightDigits.m14ByteArrayHexSwar                                       12345678  avgt    2   4.235          ns/op
 * JmhEightDigits.m14ByteArrayHexSwar                                       12345x78  avgt    2   1.994          ns/op
 * JmhEightDigits.m15CharArrayHexSwar                                       12345678  avgt    2   5.274          ns/op
 * JmhEightDigits.m15CharArrayHexSwar                                       12345x78  avgt    2   3.952          ns/op
 * JmhEightDigits.m21ByteArrayDecVector                                     12345678  avgt    2   2.544          ns/op
 * JmhEightDigits.m21ByteArrayDecVector                                     12345x78  avgt    2   1.334          ns/op
 * JmhEightDigits.m22CharArrayDecVector                                     12345678  avgt    2   2.425          ns/op
 * JmhEightDigits.m22CharArrayDecVector                                     12345x78  avgt    2   1.156          ns/op
 * JmhEightDigits.m23StringDecVector                                        12345678  avgt    2   4.686          ns/op
 * JmhEightDigits.m23StringDecVector                                        12345x78  avgt    2   2.895          ns/op
 * JmhEightDigits.m24ByteArrayHexVector                                     12345678  avgt    2   4.061          ns/op
 * JmhEightDigits.m24ByteArrayHexVector                                     12345x78  avgt    2   2.512          ns/op
 * JmhEightDigits.m25CharArrayHexVector                                     12345678  avgt    2   4.040          ns/op
 * JmhEightDigits.m25CharArrayHexVector                                     12345x78  avgt    2   2.508          ns/op
 *
 * Process finished with exit code 0
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
public final class JmhEightDigits {
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
    public int m01ByteArrayDecScalarWithIsDigitCall() {
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
    public int m01ByteArrayDecScalarWithIsDigitInlined() {
        int value = 0;
        for (int i = 0; i < eightDigitsByteArray.length; i++) {
            byte ch = eightDigitsByteArray[i];
            int digit = (char) (ch - '0');
            if (digit < 10) {
                value = value * 10 + digit;
            } else {
                return -1;
            }
        }
        return value;
    }

    @Benchmark
    public int m01ByteArrayDecScalarWithIsDigitCallBranchfree() {
        int value = 0;
        boolean success = true;
        for (int i = 0; i < eightDigitsByteArray.length; i++) {
            byte ch = eightDigitsByteArray[i];
            success &= isDigit(ch);
            value = value * 10 + ch - '0';
        }
        return success ? value : -1;
    }

    @Benchmark
    public int m01ByteArrayDecScalarWithIsDigitInlinedBranchfree() {
        int value = 0;
        boolean failed = false;
        for (int i = 0; i < eightDigitsByteArray.length; i++) {
            byte ch = eightDigitsByteArray[i];
            int digit = (char) (ch - '0');
            failed |= digit > 9;
            value = value * 10 + digit;
        }
        return failed ? -1 : value;
    }

    @Benchmark
    public int m01ByteArrayDecScalarMul10() {
        int value = 0;
        for (int i = 0; i < eightDigitsByteArray.length; i++) {
            byte ch = eightDigitsByteArray[i];
            char digit = (char) (ch - '0');
            if (digit < 10) {
                value = mul10(value) + digit;
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
            char digit = (char) (ch - '0');
            if (digit < 10) {
                value = value * 10 + digit;
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
            char digit = (char) (ch - '0');
            if (digit < 10) {
                value = mul10L(value) + digit;
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
            char digit = (char) (ch - '0');
            if (digit < 10) {
                value = value * 10 + digit;
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
            char digit = (char) (ch - '0');
            if (digit < 10) {
                value = value * 10 + digit;
            } else {
                return -1;
            }
        }
        return value;
    }

    @Benchmark
    public int m04ByteArrayHexScalar() {
        int value = 0;
        for (int i = 0; i < eightDigitsByteArray.length; i++) {
            byte ch = eightDigitsByteArray[i];
            int h = lookupHex(ch);
            if (h >= 0) {
                value = value << 4 + h;
            } else {
                return -1;
            }
        }
        return value;
    }

    @Benchmark
    public int m05CharArrayHexScalar() {
        int value = 0;
        for (int i = 0; i < eightDigitsCharArray.length; i++) {
            char ch = eightDigitsCharArray[i];
            int h = lookupHex(ch);
            if (h >= 0) {
                value = value << 4 + h;
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


