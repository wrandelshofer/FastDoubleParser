/*
 * @(#)JmhLookupHex.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@Fork(value = 1)
@Measurement(iterations = 10, time = 1)
@Warmup(iterations = 2, time = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhLookupHex {
    @Param({"A","X"})
    public char c;

    @Benchmark
    public void changed(Blackhole blackhole) {
        blackhole.consume(changed(c));
    }

    private static int changed(char c) {
        return AbstractFloatValueParser.lookupHex(c);
    }

    @Benchmark
    public void original(Blackhole blackhole) {
        blackhole.consume(original(c));
    }

    private static int original(char c) {
        return lookupHexOld(c);
    }

    static int lookupHexOld(char ch) {
        // The branchy code is faster than the branch-less code.
        // Branch-less code: return CHAR_TO_HEX_MAP[ch & 127] | (127 - ch) >> 31;
        return ch < 128 ? AbstractFloatValueParser.CHAR_TO_HEX_MAP[ch] : -1;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JmhLookupHex.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
