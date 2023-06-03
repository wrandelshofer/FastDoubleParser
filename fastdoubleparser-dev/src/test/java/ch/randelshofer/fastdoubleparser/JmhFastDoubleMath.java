/*
 * @(#)JmhFastDoubleMath.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@Fork(value = 1)
@Measurement(iterations = 10, time = 1)
@Warmup(iterations = 2, time = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhFastDoubleMath {
    private boolean negative = true;
    private long significand = 344_123_233_324_850L;
    private int exponent = 13;

    @Benchmark
    public void tryHexFloatToDoubleTruncated(Blackhole blackhole) {
        blackhole.consume(FastDoubleMath.tryHexFloatToDoubleTruncated(negative, significand, exponent, false, 0));
    }
}
