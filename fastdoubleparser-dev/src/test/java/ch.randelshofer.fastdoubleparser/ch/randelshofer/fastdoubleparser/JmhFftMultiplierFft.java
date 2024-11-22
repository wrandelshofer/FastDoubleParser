/*
 * @(#)JmhFftMultiplierFft.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.FftMultiplier.ComplexVector;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * # JMH version: 1.36
 * # VM version: JDK 20.0.1, OpenJDK 64-Bit Server VM, 20.0.1+9-29
 * # Intel(R) Core(TM) i5-6200U CPU @ 2.30GHz
 *
 * Benchmark                           Mode  Cnt      Score     Error  Units
 * original                            avgt    5  16084.425 ± 163.063  ns/op
 * sums                                avgt    5  17349.120 ±  86.367  ns/op
 * sums_less_variables                 avgt    5  14540.706 ± 145.691  ns/op
 * sums_less_variables_original_index  avgt    5  14694.344 ± 416.344  ns/op
 *
 * Process finished with exit code 0
 * </pre>
 */
@Fork(value = 1, jvmArgs = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"
        //, "-Xmx4g"
})
@Measurement(iterations = 5, time = 1)
@Warmup(iterations = 4, time = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhFftMultiplierFft {

    private static final int K = 10;
    private static final int N = 1 << K;

    ComplexVector a = new ComplexVector(N);
    ComplexVector[] roots = new ComplexVector[K];

    @Setup(Level.Iteration)
    public void setUp() {
        for (int i = 0; i < N; i++) {
            // just some values, but zeroes
            a.real(i, i);
            a.imag(i, i * 200 >>> 8);
        }
        for (int i = 0; i < K; i++) {
            roots[i] = new ComplexVector(N);
            for (int j = 0; j < N; j++) {
                roots[i].real(j, j + i + 4);
                roots[i].imag(j, j * 250 >>> 7);
            }
        }
    }

    // @formatter:off
    @Benchmark
    public void original(Blackhole blackhole) { run(FftAlgorithm.ORIGINAL, blackhole); }
    @Benchmark
    public void sums(Blackhole blackhole) { run(FftAlgorithm.OPTIMIZED, blackhole); }
    @Benchmark
    public void sums_less_variables(Blackhole blackhole) { run(FftAlgorithm.OPTIMIZED_LESS_VARIABLES, blackhole); }
    @Benchmark
    public void sums_less_variables_original_index(Blackhole blackhole) { run(FftAlgorithm.OPTIMIZED_VARIABLE_REDUCED_ORIGINAL_INDEX, blackhole); }
    // @formatter:on

    private enum FftAlgorithm {
        ORIGINAL {
            @Override
            void fft(ComplexVector a, ComplexVector[] roots) {
                FftMultiplier.fftOriginal(a, roots);
            }
        },
        OPTIMIZED {
            @Override
            void fft(ComplexVector a, ComplexVector[] roots) {
                FftMultiplier.fftOptimized(a, roots);
            }
        },
        OPTIMIZED_LESS_VARIABLES {
            @Override
            void fft(ComplexVector a, ComplexVector[] roots) {
                FftMultiplier.fftOptimizedLessVariables(a, roots);
            }
        },
        OPTIMIZED_VARIABLE_REDUCED_ORIGINAL_INDEX {
            @Override
            void fft(ComplexVector a, ComplexVector[] roots) {
                FftMultiplier.fftOptimizedLessVariablesOriginalIndex(a, roots);
            }
        };

        @SuppressWarnings("SameParameterValue")
        abstract void fft(ComplexVector a, ComplexVector[] roots);
    }

    private void run(FftAlgorithm algorithm, Blackhole blackhole) {
        algorithm.fft(a, roots);
        blackhole.consume(a);
    }
}
