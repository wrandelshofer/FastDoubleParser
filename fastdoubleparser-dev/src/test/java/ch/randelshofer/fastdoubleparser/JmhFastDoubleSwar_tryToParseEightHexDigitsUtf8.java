/*
 * @(#)JmhFastDoubleSwar_tryToParseEightHexDigitsUtf8.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"
        , "-Xmx24g"
})
@Measurement(iterations = 4, time = 1)
@Warmup(iterations = 4, time = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhFastDoubleSwar_tryToParseEightHexDigitsUtf8 {


    private long chunk = 0x31_31_31_31_31_31_31_31L; // eight ones

    @Benchmark
    public long proposed_tryToParseEightHexDigitsUtf8() {
        return FastDoubleSwar.tryToParseEightHexDigitsUtf8(chunk);
    }

    @Benchmark
    public long alreadyMerged_tryToParseEightHexDigitsUtf8() {
        return alreadyMerged_tryToParseEightHexDigitsUtf8(chunk);
    }

    // taken from commit a67caba
    private static long alreadyMerged_tryToParseEightHexDigitsUtf8(long chunk) {
        // The following code is based on the technique presented in the paper
        // by Leslie Lamport.

        // Create a predicate for all bytes which are greeter than '0' (0x30), where 0x30-0x1=0x2f;
        // The predicate is true if the hsb of a byte is set: (predicate & 0x80) != 0.
        long ge_0 = chunk + (0x2f_2f_2f_2f_2f_2f_2f_2fL ^ 0x7f_7f_7f_7f_7f_7f_7f_7fL);
        // We don't need to 'and' with 0x80…L here, because we do it in the if-statement below.
        //ge_0 &= 0x80_80_80_80_80_80_80_80L;

        // Create a predicate for all bytes which are smaller or equal than '9' (0x39), where 0x39 + 0x1 = 0x3a
        // The predicate is true if the hsb of a byte is set: (predicate & 0x80) != 0.
        long le_9 = 0x3a_3a_3a_3a_3a_3a_3a_3aL + (chunk ^ 0x7f_7f_7f_7f_7f_7f_7f_7fL);
        // We don't need to 'and' with 0x80…L here, because we do it in the if-statement below.
        //le_9 &= 0x80_80_80_80_80_80_80_80L;

        // Convert upper case characters to lower case by setting the 0x20 bit.
        long lowerCaseChunk = chunk | 0x20_20_20_20_20_20_20_20L;

        // Create a predicate for all bytes which are greater or equal than 'a' (0x61), where 0x61 - 0x1 = 0x60
        // The predicate is true if the hsb of a byte is set: (predicate & 0x80) != 0.
        long ge_a = lowerCaseChunk + (0x60_60_60_60_60_60_60_60L ^ 0x7f_7f_7f_7f_7f_7f_7f_7fL);
        // We must 'and' with 0x80…L, because we need the proper predicate bits further below in the code.
        ge_a &= 0x80_80_80_80_80_80_80_80L;

        // Create a predicate for all bytes which are smaller or equal than 'f' (0x66), where 0x66 + 0x1 = 0x67
        // The predicate is true if the hsb of a byte is set: (predicate & 0x80) != 0.
        long le_f = 0x67_67_67_67_67_67_67_67L + (lowerCaseChunk ^ 0x7f_7f_7f_7f_7f_7f_7f_7fL);
        // We don't need to 'and' with 0x80…L here, because we do it in the if-statement below.
        //le_f &= 0x80_80_80_80_80_80_80_80L;

        // A character must either be in the range from '0' to '9' or in the range from 'a' to 'f'
        if ((((ge_0 & le_9) ^ (ge_a & le_f)) & 0x80_80_80_80_80_80_80_80L) != 0x80_80_80_80_80_80_80_80L) {
            return -1;
        }

        // Expand the predicate to a byte mask
        long ge_a_mask = (ge_a >>> 7) * 0xffL;

        // Subtract character '0' (0x30) from each of the eight characters
        long vec = lowerCaseChunk - 0x30_30_30_30_30_30_30_30L;

        // Subtract 'a' - '0' + 10 = (0x27) from all bytes that are greater equal 'a'
        long v = vec & ~ge_a_mask | vec - (0x27272727_27272727L & ge_a_mask);

        // Compact all nibbles
        return Long.compress(v, 0x0f0f0f0f_0f0f0f0fL);// since Java 19
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JmhFastDoubleSwar_tryToParseEightHexDigitsUtf8.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}





