/*
 * @(#)JmhEightDigits.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static ch.randelshofer.fastdoubleparser.AbstractNumberParser.lookupHex;


/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 19-ea, OpenJDK 64-Bit Server VM, 19+36-2238
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
 *
 * Benchmark                   (eightDigitsCharSequence)  Mode  Cnt  Score   Error  Units
 * 01ByteArrayDecScalar                         12345678  avgt    2  6.006          ns/op
 * 01ByteArrayDecScalar                         12345x78  avgt    2  4.989          ns/op
 * 01ByteArrayDecScalarL                        12345678  avgt    2  6.822          ns/op
 * 01ByteArrayDecScalarL                        12345x78  avgt    2  5.616          ns/op
 * 01ByteArrayDecScalarMul10                    12345678  avgt    2  5.604          ns/op
 * 01ByteArrayDecScalarMul10                    12345x78  avgt    2  4.669          ns/op
 * 01ByteArrayDecScalarMul10L                   12345678  avgt    2  6.086          ns/op
 * 01ByteArrayDecScalarMul10L                   12345x78  avgt    2  4.777          ns/op
 *
 * 02StringDecScalar                            12345678  avgt    2  7.808          ns/op
 * 02StringDecScalar                            12345x78  avgt    2  6.977          ns/op
 * 03CharArrayDecScalar                         12345678  avgt    2  6.404          ns/op
 * 03CharArrayDecScalar                         12345x78  avgt    2  5.330          ns/op
 * 04ByteArrayHexScalar                         12345678  avgt    2  4.633          ns/op
 * 04ByteArrayHexScalar                         12345x78  avgt    2  3.889          ns/op
 * 05CharArrayHexScalar                         12345678  avgt    2  6.233          ns/op
 * 05CharArrayHexScalar                         12345x78  avgt    2  5.639          ns/op
 *
 * 11ByteArrayDecSwar                           12345678  avgt    2  1.995          ns/op
 * 11ByteArrayDecSwar                           12345x78  avgt    2  0.966          ns/op
 * 12CharArrayDecSwar                           12345678  avgt    2  2.921          ns/op
 * 12CharArrayDecSwar                           12345x78  avgt    2  1.660          ns/op
 * 13StringDecSwar                              12345678  avgt    2  4.204          ns/op
 * 13StringDecSwar                              12345x78  avgt    2  3.232          ns/op
 * 14ByteArrayHexSwar                           12345678  avgt    2  4.056          ns/op
 * 14ByteArrayHexSwar                           12345x78  avgt    2  2.072          ns/op
 * 15CharArrayHexSwar                           12345678  avgt    2  5.296          ns/op
 * 15CharArrayHexSwar                           12345x78  avgt    2  4.081          ns/op
 *
 * 21ByteArrayDecVector                         12345678  avgt    2  2.562          ns/op
 * 21ByteArrayDecVector                         12345x78  avgt    2  1.380          ns/op
 * 22CharArrayDecVector                         12345678  avgt    2  2.513          ns/op
 * 22CharArrayDecVector                         12345x78  avgt    2  1.182          ns/op
 * 23StringDecVector                            12345678  avgt    2  4.740          ns/op
 * 23StringDecVector                            12345x78  avgt    2  2.963          ns/op
 * 24ByteArrayHexVector                         12345678  avgt    2  4.202          ns/op
 * 24ByteArrayHexVector                         12345x78  avgt    2  2.711          ns/op
 * 25CharArrayHexVector                         12345678  avgt    2  4.061          ns/op
 * 25CharArrayHexVector                         12345x78  avgt    2  2.485          ns/op
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

    /*
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
    */
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

    /*
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
    */
    @Benchmark
    public long m15CharArrayHexSwar() {
        return FastDoubleSwar.tryToParseEightHexDigits(eightDigitsCharArray, 0);
    }

    /*

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
    */
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


