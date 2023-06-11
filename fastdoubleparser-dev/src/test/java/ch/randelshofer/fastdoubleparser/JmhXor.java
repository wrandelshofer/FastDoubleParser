/*
 * @(#)JmhXor.java
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
public class JmhXor {
    private long significand = Long.MAX_VALUE;

    @Benchmark
    public void changed(Blackhole blackhole) {
        blackhole.consume(changed(significand));
    }

    private static int changed(long upperbit) {
        int lz = 2;

        lz += (int) (1 ^ upperbit);
        return lz;
    }

    @Benchmark
    public void original(Blackhole blackhole) {
        blackhole.consume(original(significand));
    }

    private static int original(long upperbit) {
        int lz = 2;
        lz += (int) (1 ^ upperbit);
        return lz;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JmhXor.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
