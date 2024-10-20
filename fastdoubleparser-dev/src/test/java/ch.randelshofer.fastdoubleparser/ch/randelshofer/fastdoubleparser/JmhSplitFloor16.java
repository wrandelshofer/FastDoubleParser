/*
 * @(#)JmhSplitFloor16.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * # JMH version: 1.37
 * # VM version: JDK 22-ea, OpenJDK 64-Bit Server VM, 22-ea+34-2360
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark                        Mode  Cnt   Score   Error  Units
 * JmhSplitFloor16.oldSplitFloor16  avgt    2  58.752          ns/op
 * JmhSplitFloor16.splitFloor16     avgt    2  42.323          ns/op
 * </pre>
 */
@Fork(value = 1)
@Measurement(iterations = 2, time = 1)
@Warmup(iterations = 2, time = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public final class JmhSplitFloor16 {
    private static final int TO_MAX = 1024 + 1;
    private static final int FROM_MAX = 100;
    private static final int DATA_LENGTH = TO_MAX - FROM_MAX;

    @Benchmark
    @OperationsPerInvocation(DATA_LENGTH)
    public void oldSplitFloor16(Blackhole blackhole) {
        for (int i = 0; i < FROM_MAX; i++) {
            for (int j = i; j < TO_MAX; j++) {
                blackhole.consume(oldSplitFloor16(i, j));
            }
        }
    }

    @Benchmark
    @OperationsPerInvocation(DATA_LENGTH)
    public void splitFloor16(Blackhole blackhole) {
        for (int i = 0; i < FROM_MAX; i++) {
            for (int j = i; j < TO_MAX; j++) {
                blackhole.consume(splitFloor16(i, j));
            }
        }
    }

    static int oldSplitFloor16(int from, int to) {
        int mid = (from + to) >>> 1;// split in half
        mid = to - (((to - mid + 15) >> 4) << 4);// make numDigits of low a multiple of 16
        return mid;
    }

    static int splitFloor16(int from, int to) {
        // divide length by 2 as we want the middle, round up range half to multiples of 16
        int range = (((to - from + 31) >>> 5) << 4);
        return to - range;
    }
}