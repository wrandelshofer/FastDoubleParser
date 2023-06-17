/*
 * @(#)JmhTryToParseUpTo7Digits.java
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
 * Benchmark  (digits)  Mode  Cnt   Score   Error  Units
 * optimized         1  avgt   10  29.423 ± 0.161  ns/op
 * optimized         2  avgt   10  30.638 ± 0.105  ns/op
 * optimized         3  avgt   10  31.101 ± 0.095  ns/op
 * optimized         4  avgt   10  30.859 ± 0.206  ns/op
 * optimized         5  avgt   10  33.399 ± 0.136  ns/op
 * optimized         6  avgt   10  34.185 ± 0.126  ns/op
 * optimized         7  avgt   10  34.763 ± 0.139  ns/op
 * original          1  avgt   10  29.462 ± 0.201  ns/op
 * original          2  avgt   10  31.425 ± 0.144  ns/op
 * original          3  avgt   10  31.423 ± 0.747  ns/op
 * original          4  avgt   10  31.536 ± 0.085  ns/op
 * original          5  avgt   10  32.786 ± 0.162  ns/op
 * original          6  avgt   10  34.021 ± 0.303  ns/op
 * original          7  avgt   10  35.614 ± 0.189  ns/op
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
public class JmhTryToParseUpTo7Digits {

    @Param({"1", "2", "3", "4", "5", "6", "7"})
    public int digits;
    private byte[] b;

    @Setup(Level.Invocation)
    public void setUp() {
        b = new byte[digits];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) (0x31 + i);
        }
    }

    @Benchmark
    public void original(Blackhole blackhole) {
        blackhole.consume(FastDoubleSwar.tryToParseUpTo7Digits(b, 0, digits));
    }

    @Benchmark
    public void optimized1(Blackhole blackhole) {
        blackhole.consume(tryToParseUpTo7Digits1(b, 0, digits));
    }

    @Benchmark
    public void optimized2(Blackhole blackhole) {
        blackhole.consume(tryToParseUpTo7Digits2(b, 0, digits));
    }

    @Benchmark
    public void optimized3(Blackhole blackhole) {
        blackhole.consume(tryToParseUpTo7Digits3(b, 0, digits));
    }

    // use of the method isDigit() avoided
    public static int tryToParseUpTo7Digits1(byte[] str, int from, int to) {
        int result = 0;
        boolean success = true;
        for (; from < to; from++) {
            char digit = (char) (str[from] - '0');
            success &= digit < 10;
            result = 10 * result + digit;
        }
        return success ? result : -1;
    }

    // bad character detection via negative integer value
    public static int tryToParseUpTo7Digits2(byte[] str, int from, int to) {
        int result = 0;
        int success = 0;
        for (; from < to; from++) {
            char digit = (char) (str[from] - '0');
            success |= digit - 9;
            result = 10 * result + digit;
        }
        return success < 0 ? result : -1;
    }

    // boundary check replaced by chock, not so effective for that small number of iterations however
    public static int tryToParseUpTo7Digits3(byte[] str, int from, int to) {
        int result = 0;
        byte temp = str[to - 1];
        str[to - 1] = 'Z';
        char digit = 0;
        for (; digit < 10; from++) {
            digit = (char) (str[from] - '0');
            result = 10 * result + digit;
        }
        str[to - 1] = temp;
        digit = (char) (temp - '0');
        if (from < to - 1 || digit > 9) {
            return -1;
        } else {
            return result * 10 + digit;
        }
    }
}





