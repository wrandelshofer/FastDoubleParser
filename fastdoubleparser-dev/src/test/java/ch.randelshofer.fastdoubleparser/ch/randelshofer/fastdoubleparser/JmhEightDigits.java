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
 * # VM version: JDK 20.0.1, OpenJDK 64-Bit Server VM, 20.0.1+9-29
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
 *
 * Benchmark                                      (str)     Mode  Cnt  Score   Error  Units
 * m01ByteArrayDecScalar                          12345678  avgt    4  6.187 ± 0.669  ns/op
 * m01ByteArrayDecScalar                          12345x78  avgt    4  4.854 ± 0.013  ns/op
 * m01ByteArrayDecScalarL                         12345678  avgt    4  6.665 ± 0.122  ns/op
 * m01ByteArrayDecScalarL                         12345x78  avgt    4  5.208 ± 0.069  ns/op
 * m01ByteArrayDecScalar_decDigit                 12345678  avgt    4  6.823 ± 0.072  ns/op
 * m01ByteArrayDecScalar_decDigit                 12345x78  avgt    4  4.601 ± 0.367  ns/op
 * m01ByteArrayDecScalar_isDigitInlined           12345678  avgt    4  5.781 ± 0.068  ns/op
 * m01ByteArrayDecScalar_isDigitInlined           12345x78  avgt    4  5.393 ± 0.031  ns/op
 * m02StringDecScalar                             12345678  avgt    4  7.651 ± 0.056  ns/op
 * m02StringDecScalar                             12345x78  avgt    4  6.807 ± 0.122  ns/op
 * m02StringDecScalar_isDigitInlined              12345678  avgt    4  7.093 ± 0.692  ns/op
 * m02StringDecScalar_isDigitInlined              12345x78  avgt    4  7.079 ± 0.410  ns/op
 * m03CharArrayDecScalar                          12345678  avgt    4  6.790 ± 0.177  ns/op
 * m03CharArrayDecScalar                          12345x78  avgt    4  5.896 ± 0.724  ns/op
 * m03CharArrayDecScalar_isDigitInlined           12345678  avgt    4  5.853 ± 0.022  ns/op
 * m03CharArrayDecScalar_isDigitInlined           12345x78  avgt    4  5.696 ± 0.103  ns/op
 *
 * m04ByteArrayHexScalar                          12345678  avgt    4  4.534 ± 0.058  ns/op
 * m04ByteArrayHexScalar                          12345x78  avgt    4  3.790 ± 0.300  ns/op
 * m05CharArrayHexScalar                          12345678  avgt    4  5.645 ± 0.030  ns/op
 * m05CharArrayHexScalar                          12345x78  avgt    4  5.523 ± 0.127  ns/op
 *
 * m11ByteArrayDecSwar                            12345678  avgt    4  2.043 ± 0.202  ns/op
 * m11ByteArrayDecSwar                            12345x78  avgt    4  0.945 ± 0.030  ns/op
 * m12CharArrayDecSwar                            12345678  avgt    4  2.703 ± 0.027  ns/op
 * m12CharArrayDecSwar                            12345x78  avgt    4  2.105 ± 0.230  ns/op
 * m13StringDecSwar                               12345678  avgt    4  3.957 ± 0.072  ns/op
 * m13StringDecSwar                               12345x78  avgt    4  3.236 ± 0.045  ns/op
 * m14ByteArrayHexSwar                            12345678  avgt    4  3.310 ± 0.273  ns/op
 * m14ByteArrayHexSwar                            12345x78  avgt    4  2.069 ± 0.043  ns/op
 * m15CharArrayHexSwar                            12345678  avgt    4  5.281 ± 0.067  ns/op
 * m15CharArrayHexSwar                            12345x78  avgt    4  4.091 ± 0.053  ns/op
 *
 * m21ByteArrayDecVector                          12345678  avgt    4  2.778 ± 0.325  ns/op
 * m21ByteArrayDecVector                          12345x78  avgt    4  1.595 ± 0.019  ns/op
 * m22CharArrayDecVector                          12345678  avgt    4  2.691 ± 0.131  ns/op
 * m22CharArrayDecVector                          12345x78  avgt    4  1.404 ± 0.163  ns/op
 * m23StringDecVector                             12345678  avgt    4  5.174 ± 0.097  ns/op
 * m23StringDecVector                             12345x78  avgt    4  3.096 ± 0.027  ns/op
 * m24ByteArrayHexVector                          12345678  avgt    4  4.588 ± 0.597  ns/op
 * m24ByteArrayHexVector                          12345x78  avgt    4  2.733 ± 0.026  ns/op
 * m25CharArrayHexVector                          12345678  avgt    4  4.191 ± 0.060  ns/op
 * m25CharArrayHexVector                          12345x78  avgt    4  2.872 ± 0.051  ns/op
 * </pre>
 */
@Fork(value = 1, jvmArgsAppend = {"-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector",
        "--enable-preview"
        //,"-XX:+UnlockDiagnosticVMOptions", "-XX:PrintAssemblyOptions=intel", "-XX:CompileCommand=print,ch/randelshofer/fastdoubleparser/EightDigitsJmh.*"

        // Options for analysis with https://github.com/AdoptOpenJDK/jitwatch
        //, "-XX:+UnlockDiagnosticVMOptions"
        //, "-Xlog:class+load=info"
        //, "-XX:+LogCompilation"
        //, "-XX:+PrintAssembly"

})
@Measurement(iterations = 4)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhEightDigits {
    @Param({"12345678", "12345x78"})
    public String str;
    public char[] strCharArray;
    public byte[] strByteArray;

    @Setup
    public void prepare() {
        strCharArray = str.toCharArray();
        strByteArray = str.getBytes(StandardCharsets.UTF_8);
    }


    @Benchmark
    public int m01ByteArrayDecScalar() {
        int value = 0;
        for (int i = 0; i < strByteArray.length; i++) {
            byte ch = strByteArray[i];
            if (isDigit(ch)) {
                value = value * 10 + ch - '0';
            } else {
                return -1;
            }
        }
        return value;
    }

    @Benchmark
    public int m01ByteArrayDecScalar_isDigitInlined() {
        int value = 0;
        for (int i = 0; i < strByteArray.length; i++) {
            byte ch = strByteArray[i];
            int digit = ch - '0';
            if (0 <= digit && digit <= 10) {
                value = value * 10 + digit;
            } else {
                return -1;
            }
        }
        return value;
    }

    @Benchmark
    public int m01ByteArrayDecScalar_decDigit() {
        int value = 0;
        for (int i = 0; i < strByteArray.length; i++) {
            byte ch = strByteArray[i];
            int digit = decDigit(ch);
            if (digit >= 0) {
                value = value * 10 + digit;
            } else {
                return -1;
            }
        }
        return value;
    }
/*
    @Benchmark
    public long m01ByteArrayDecScalarL() {
        long value = 0;
        for (int i = 0; i < strByteArray.length; i++) {
            byte ch = strByteArray[i];
            if (isDigit(ch)) {
                value = value * 10 + ch - '0';
            } else {
                return -1;
            }
        }
        return value;
    }

    @Benchmark
    public int m02StringDecScalar() {
        int value = 0;
        for (int i = 0, n = str.length(); i < n; i++) {
            char ch = str.charAt(i);
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
        for (int i = 0; i < strCharArray.length; i++) {
            char ch = strCharArray[i];
            if (isDigit(ch)) {
                value = value * 10 + ch - '0';
            } else {
                return -1;
            }
        }
        return value;
    }

    @Benchmark
    public int m02StringDecScalar_isDigitInlined() {
        int value = 0;
        for (int i = 0, n = str.length(); i < n; i++) {
            char ch = str.charAt(i);
            int digit = (ch - '0');
            if (0<=digit&&digit <= 10) {
                value = value * 10 + digit;
            } else {
                return -1;
            }
        }
        return value;
    }

    @Benchmark
    public int m03CharArrayDecScalar_isDigitInlined() {
        int value = 0;
        for (int i = 0; i < strCharArray.length; i++) {
            char ch = strCharArray[i];
            int digit = ch - '0';
            if (0<=digit&&digit <= 10) {
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
        for (int i = 0; i < strByteArray.length; i++) {
            byte ch = strByteArray[i];
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
        for (int i = 0; i < strCharArray.length; i++) {
            char ch = strCharArray[i];
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
        return FastDoubleSwar.tryToParseEightDigitsUtf8(strByteArray, 0);
    }

    @Benchmark
    public int m12CharArrayDecSwar() {
        return FastDoubleSwar.tryToParseEightDigits(strCharArray, 0);
    }

    @Benchmark
    public int m13StringDecSwar() {
        return FastDoubleSwar.tryToParseEightDigits(str, 0);
    }

    @Benchmark
    public long m14ByteArrayHexSwar() {
        return FastDoubleSwar.tryToParseEightHexDigits(strByteArray, 0);
    }

    @Benchmark
    public long m15CharArrayHexSwar() {
        return FastDoubleSwar.tryToParseEightHexDigits(strCharArray, 0);
    }


    @Benchmark
    public int m21ByteArrayDecVector() {
        return FastDoubleVector.tryToParseEightDigitsUtf8(strByteArray, 0);
    }

    @Benchmark
    public int m22CharArrayDecVector() {
        return FastDoubleVector.tryToParseEightDigitsUtf16(strCharArray, 0);
    }


    @Benchmark
    public int m23StringDecVector() {
        return FastDoubleVector.tryToParseEightDigits(str, 0);
    }


    @Benchmark
    public long m24ByteArrayHexVector() {
        return FastDoubleVector.tryToParseEightHexDigitsUtf8(strByteArray, 0);
    }

    @Benchmark
    public long m25CharArrayHexVector() {
        return FastDoubleVector.tryToParseEightHexDigitsUtf16(strCharArray, 0);
    }
*/

    private static boolean isDigit(byte c) {
        return '0' <= c && c <= '9';
    }

    private static boolean isDigit(char c) {
        // We check if '0' <= c && c <= '9'.
        // We take advantage of the fact that char is an unsigned value:
        // subtracted values wrap around.
        return (char) (c - '0') <= (char) ('9' - '0');
    }

    protected static int decDigit(byte c) {
        int digit = c - '0';
        return digit > 10 ? -1 : digit;
    }
}


