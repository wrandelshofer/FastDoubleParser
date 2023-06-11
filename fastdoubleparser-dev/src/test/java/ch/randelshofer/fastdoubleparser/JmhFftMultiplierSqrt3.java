/*
 * @(#)JmhFftMultiplierFft3.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@Fork(value = 1, jvmArgs = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"
        , "-Xmx4g"
})
@Measurement(iterations = 5, time = 1)
@Warmup(iterations = 4, time = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhFftMultiplierSqrt3 {
    int sign = -1;
    boolean signed = true;
    @Benchmark // 5x slower than the others
    public double fft3recompute() {
        return fft3recompute(sign);
    }

    @Benchmark
    public double fft3computeBranched() { return fft3computeBranched(signed); }

    @Benchmark
    public double fft3computeBranched2() { return fft3computeBranched2(signed); }

    @Benchmark
    public double fft3computeBranchedConst() {
        return fft3computeBranchedConst(signed);
    }

    private static final double sqrt32p = 0.5 * Math.sqrt(3);

    private static final double sqrt32m = -0.5 * Math.sqrt(3);
    static double fft3computeBranchedConst(boolean signed) {
        return signed ? sqrt32m : sqrt32p;
    }

    static double fft3recompute(int sign) { return sign * 0.5 * Math.sqrt(3); }

    static double fft3computeBranched2(boolean signed) {
        return signed ? -0.5 * Math.sqrt(3) : 0.5 * Math.sqrt(3);
    }

    // looks most suitable for the purpose
    static double fft3computeBranched(boolean signed) { return (signed ? -0.5 : 0.5) * Math.sqrt(3); }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JmhFftMultiplierSqrt3.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}