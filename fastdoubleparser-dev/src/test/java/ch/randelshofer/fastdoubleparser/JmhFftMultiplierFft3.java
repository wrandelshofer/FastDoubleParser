/*
 * @(#)JmhFftMultiplierFft3.java
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
public class JmhFftMultiplierFft3 {

    private static final int N = 1 << 10;
    private static final double SCALE = 1.0 / 3;

    public int sign = -1;
    FftMultiplier.ComplexVector a0 = new FftMultiplier.ComplexVector(N);
    FftMultiplier.ComplexVector a1 = new FftMultiplier.ComplexVector(N);
    FftMultiplier.ComplexVector a2 = new FftMultiplier.ComplexVector(N);

    @Setup(Level.Iteration)
    public void setUp() {
        for (int i = 0; i < a0.length(); i++) {
            // just some values, but zeroes
            a0.real(i, i);
            a0.imag(i, i * 200 >>> 8);
            a1.real(i, i + 4);
            a1.imag(i, i * 250 >>> 7);
            a2.real(i, 2 * i + 3);
            a2.imag(i, i * 230 >>> 8);
        }
    }

    public void run(Fft3Algorithm algorithm, Blackhole blackhole) {
        algorithm.fft3(a0, a1, a2, sign, SCALE);
        blackhole.consume(a0);
        blackhole.consume(a1);
        blackhole.consume(a2);
    }

    /**
     * Dummy work, because any benchmark which run first was slower for any JMH settings.
     */
    // @formatter:off
    @Benchmark
    public void _warmUp(Blackhole blackhole) { run(Fft3Algorithm.A2_IS_ZERO, blackhole); }
    @Benchmark
    public void a2isZero(Blackhole blackhole) { run(Fft3Algorithm.A2_IS_ZERO, blackhole); }
    @Benchmark
    public void original(Blackhole blackhole) { run(Fft3Algorithm.ORIGINAL, blackhole); }
    @Benchmark
    public void scaleStandalone(Blackhole blackhole) { run(Fft3Algorithm.SCALE_STANDALONE, blackhole); }
    @Benchmark
    public void scaleStandaloneSums(Blackhole blackhole) { run(Fft3Algorithm.SCALE_STANDALONE_SUMS, blackhole); }
    @Benchmark
    public void scaleStandaloneSumsOneIntermediateVariableCdMiddle(Blackhole blackhole) { run(Fft3Algorithm.SCALE_STANDALONE_SUMS_ONE_INTERMEDIATE_VARIABLE_CD_MIDDLE, blackhole); }
    @Benchmark
    public void scaleStandaloneSumsOneIntermediateVariableCdLast(Blackhole blackhole) { run(Fft3Algorithm.SCALE_STANDALONE_SUMS_ONE_INTERMEDIATE_VARIABLE_CD_LAST, blackhole); }
    @Benchmark
    public void scaleStandaloneSumsOneIntermediateVariableCdFirst(Blackhole blackhole) { run(Fft3Algorithm.SCALE_STANDALONE_SUMS_ONE_INTERMEDIATE_VARIABLE_CD_FIRST, blackhole); }
    @Benchmark
    public void scaleStandaloneOneIntermediateVariable(Blackhole blackhole) { run(Fft3Algorithm.SCALE_STANDALONE_ONE_INTERMEDIATE_VARIABLE, blackhole); }
    @Benchmark
    public void sums(Blackhole blackhole) { run(Fft3Algorithm.SUMS, blackhole); }
    @Benchmark
    public void sums2(Blackhole blackhole) { run(Fft3Algorithm.SUMS2, blackhole); }
    @Benchmark
    public void sums3(Blackhole blackhole) { run(Fft3Algorithm.SUMS3, blackhole); }
    @Benchmark
    public void sumsReused1Var(Blackhole blackhole) { run(Fft3Algorithm.SUMS_REUSED_1VAR, blackhole); }
    @Benchmark
    public void oneIntermediateVariable(Blackhole blackhole) { run(Fft3Algorithm.ONE_INTERMEDIATE_VARIABLE, blackhole); }
    @Benchmark
    public void sumsReusedOneIntermediateVar(Blackhole blackhole) { run(Fft3Algorithm.SUMS_REUSED_ONE_INTERMEDIATE_VAR, blackhole); }
    @Benchmark
    public void sumsReused2Var(Blackhole blackhole) { run(Fft3Algorithm.SUMS_REUSED_2VAR, blackhole); }
    // @formatter:on

    private enum Fft3Algorithm {
        A2_IS_ZERO {
            @Override
            void fft3(FftMultiplier.ComplexVector a0, FftMultiplier.ComplexVector a1, FftMultiplier.ComplexVector a2, int sign, double scale) {
                double omegaImag = sign * -0.5 * Math.sqrt(3);   // imaginary part of omega for n=3: sin(sign*(-2)*pi*1/3)
                for (int i = 0; i < a0.length(); i++) {
                    double c = -omegaImag * a1.imag(i);
                    double d =  omegaImag * a1.real(i);

                    double e = 0.5 * a1.real(i);
                    double f = 0.5 * a1.imag(i);

                    double a0Real = a0.real(i) + a1.real(i);
                    double a0Imag = a0.imag(i) + a1.imag(i);
                    double a1Real = a0.real(i) - e + c;
                    double a1Imag = a0.imag(i) + d - f;
                    double a2Real = a0.real(i) - e - c;
                    double a2Imag = a0.imag(i) - d - f;

                    a0.real(i, a0Real * scale);
                    a0.imag(i, a0Imag * scale);
                    a1.real(i, a1Real * scale);
                    a1.imag(i, a1Imag * scale);
                    a2.real(i, a2Real * scale);
                    a2.imag(i, a2Imag * scale);
                }
            }
        },
        SUMS {
            @Override
            void fft3(FftMultiplier.ComplexVector a0, FftMultiplier.ComplexVector a1, FftMultiplier.ComplexVector a2, int sign, double scale) {
                double omegaImag = sign * -0.5 * Math.sqrt(3);   // imaginary part of omega for n=3: sin(sign*(-2)*pi*1/3)
                for (int i = 0; i < a0.length(); i++) {
                    double c = omegaImag * (a2.imag(i) - a1.imag(i));
                    double d = omegaImag * (a1.real(i) - a2.real(i));

                    double a12real = a1.real(i) + a2.real(i);
                    double a12imag = a1.imag(i) + a2.imag(i);
                    double a02Real = a0.real(i) + a12real;
                    double a02Imag = a0.imag(i) + a12imag;

                    double e = 0.5 * a12real;
                    double f = 0.5 * a12imag;
                    double a1Real = a0.real(i) - e + c;
                    double a1Imag = a0.imag(i) + d - f;
                    double a2Real = a0.real(i) - e - c;
                    double a2Imag = a0.imag(i) - d - f;

                    a0.real(i, a02Real * scale);
                    a0.imag(i, a02Imag * scale);
                    a1.real(i, a1Real * scale);
                    a1.imag(i, a1Imag * scale);
                    a2.real(i, a2Real * scale);
                    a2.imag(i, a2Imag * scale);
                }
            }
        },
        SUMS2 {
            @Override
            void fft3(FftMultiplier.ComplexVector a0, FftMultiplier.ComplexVector a1, FftMultiplier.ComplexVector a2, int sign, double scale) {
                double omegaImag = sign * -0.5 * Math.sqrt(3);   // imaginary part of omega for n=3: sin(sign*(-2)*pi*1/3)
                for (int i = 0; i < a0.length(); i++) {
                    double c = omegaImag * (a2.imag(i) - a1.imag(i));
                    double d = omegaImag * (a1.real(i) - a2.real(i));

                    double a12real = a1.real(i) + a2.real(i);
                    double a12imag = a1.imag(i) + a2.imag(i);

                    double e = 0.5 * a12real;
                    double f = 0.5 * a12imag;
                    double a1Real = a0.real(i) - e + c;
                    double a1Imag = a0.imag(i) + d - f;
                    double a2Real = a0.real(i) - e - c;
                    double a2Imag = a0.imag(i) - d - f;

                    a0.real(i, (a0.real(i) + a12real) * scale);
                    a0.imag(i, (a0.imag(i) + a12imag) * scale);
                    a1.real(i, a1Real * scale);
                    a1.imag(i, a1Imag * scale);
                    a2.real(i, a2Real * scale);
                    a2.imag(i, a2Imag * scale);
                }
            }
        },
        SUMS3 {
            @Override
            void fft3(FftMultiplier.ComplexVector a0, FftMultiplier.ComplexVector a1, FftMultiplier.ComplexVector a2, int sign, double scale) {
                double omegaImag = sign * -0.5 * Math.sqrt(3);   // imaginary part of omega for n=3: sin(sign*(-2)*pi*1/3)
                for (int i = 0; i < a0.length(); i++) {
                    double c = omegaImag * (a2.imag(i) - a1.imag(i));
                    double d = omegaImag * (a1.real(i) - a2.real(i));

                    double a12real = a1.real(i) + a2.real(i);
                    double a12imag = a1.imag(i) + a2.imag(i);

                    double e = 0.5 * a12real;
                    double f = 0.5 * a12imag;
                    a12real += a0.real(i);
                    a12imag += a0.imag(i);
                    double a1Real = a0.real(i) - e + c;
                    double a1Imag = a0.imag(i) + d - f;
                    double a2Real = a0.real(i) - e - c;
                    double a2Imag = a0.imag(i) - d - f;

                    a0.real(i, a12real * scale);
                    a0.imag(i, a12imag * scale);
                    a1.real(i, a1Real * scale);
                    a1.imag(i, a1Imag * scale);
                    a2.real(i, a2Real * scale);
                    a2.imag(i, a2Imag * scale);
                }
            }
        },
        SUMS_REUSED_1VAR {
            @Override
            void fft3(FftMultiplier.ComplexVector a0, FftMultiplier.ComplexVector a1, FftMultiplier.ComplexVector a2, int sign, double scale) {
                double omegaImag = sign * -0.5 * Math.sqrt(3);   // imaginary part of omega for n=3: sin(sign*(-2)*pi*1/3)
                for (int i = 0; i < a0.length(); i++) {
                    double c = omegaImag * (a2.imag(i) - a1.imag(i));
                    double d = omegaImag * (a1.real(i) - a2.real(i));

                    double e = a1.real(i) + a2.real(i);
                    double f = a1.imag(i) + a2.imag(i);
                    double a02Real = a0.real(i) + e;
                    double a02Imag = a0.imag(i) + f;

                    e *= 0.5;
                    f *= 0.5;
                    double a1Real = a0.real(i) - e + c;
                    double a1Imag = a0.imag(i) + d - f;
                    double a2Real = a0.real(i) - e - c;
                    double a2Imag = a0.imag(i) - d - f;

                    a0.real(i, a02Real * scale);
                    a0.imag(i, a02Imag * scale);
                    a1.real(i, a1Real * scale);
                    a1.imag(i, a1Imag * scale);
                    a2.real(i, a2Real * scale);
                    a2.imag(i, a2Imag * scale);
                }
            }
        },
        SUMS_REUSED_2VAR {
            @Override
            void fft3(FftMultiplier.ComplexVector a0, FftMultiplier.ComplexVector a1, FftMultiplier.ComplexVector a2, int sign, double scale) {
                double omegaImag = sign * -0.5 * Math.sqrt(3);   // imaginary part of omega for n=3: sin(sign*(-2)*pi*1/3)
                for (int i = 0; i < a0.length(); i++) {
                    double c = omegaImag * (a2.imag(i) - a1.imag(i));
                    double d = omegaImag * (a1.real(i) - a2.real(i));
                    double e = a1.real(i) + a2.real(i);
                    double f = a1.imag(i) + a2.imag(i);

                    double a02Real = a0.real(i) + e;
                    double a02Imag = a0.imag(i) + f;


                    e *= 0.5;
                    f *= 0.5;
                    double a1Real = a0.real(i) - e + c;
                    double a1Imag = a0.imag(i) + d - f;
                    e = a0.real(i) - e - c;
                    f = a0.imag(i) - d - f;

                    a0.real(i, a02Real * scale);
                    a0.imag(i, a02Imag * scale);
                    a1.real(i, a1Real * scale);
                    a1.imag(i, a1Imag * scale);
                    a2.real(i, e * scale);
                    a2.imag(i, f * scale);
                }
            }
        },
        SUMS_REUSED_ONE_INTERMEDIATE_VAR {
            @Override
            void fft3(FftMultiplier.ComplexVector a0, FftMultiplier.ComplexVector a1, FftMultiplier.ComplexVector a2, int sign, double scale) {
                double omegaImag = sign * -0.5 * Math.sqrt(3);   // imaginary part of omega for n=3: sin(sign*(-2)*pi*1/3)
                for (int i = 0; i < a0.length(); i++) {
                    double c = omegaImag * (a2.imag(i) - a1.imag(i));
                    double d = omegaImag * (a1.real(i) - a2.real(i));
                    double e = a1.real(i) + a2.real(i);
                    double f = a1.imag(i) + a2.imag(i);

                    double a0real = a0.real(i) + e;
                    double a0imag = a0.imag(i) + f;

                    e *= 0.5;
                    f *= 0.5;

                    a1.real(i, (a0.real(i) - e + c) * scale);
                    a1.imag(i, (a0.imag(i) + d - f) * scale);
                    a2.real(i, (a0.real(i) - e - c) * scale);
                    a2.imag(i, (a0.imag(i) - d - f) * scale);

                    a0.real(i, a0real * scale);
                    a0.imag(i, a0imag * scale);
                }
            }
        },

        ORIGINAL {
            @Override
            void fft3(FftMultiplier.ComplexVector a0, FftMultiplier.ComplexVector a1, FftMultiplier.ComplexVector a2, int sign, double scale) {
                double omegaImag = sign * -0.5 * Math.sqrt(3);   // imaginary part of omega for n=3: sin(sign*(-2)*pi*1/3)
                for (int i = 0; i < a0.length(); i++) {
                    double c = omegaImag * (a2.imag(i) - a1.imag(i));
                    double d = omegaImag * (a1.real(i) - a2.real(i));
                    double e = 0.5 * (a1.real(i) + a2.real(i));
                    double f = 0.5 * (a1.imag(i) + a2.imag(i));
                    double a02Real = a0.real(i) + a1.real(i) + a2.real(i);
                    double a02Imag = a0.imag(i) + a1.imag(i) + a2.imag(i);
                    double a1Real = a0.real(i) - e + c;
                    double a1Imag = a0.imag(i) + d - f;
                    double a2Real = a0.real(i) - e - c;
                    double a2Imag = a0.imag(i) - d - f;
                    a0.real(i, a02Real * scale);
                    a0.imag(i, a02Imag * scale);
                    a1.real(i, a1Real * scale);
                    a1.imag(i, a1Imag * scale);
                    a2.real(i, a2Real * scale);
                    a2.imag(i, a2Imag * scale);
                }
            }
        },

        SCALE_STANDALONE {
            @Override
            void fft3(FftMultiplier.ComplexVector a0, FftMultiplier.ComplexVector a1, FftMultiplier.ComplexVector a2, int sign, double scale) {
                double omegaImag = sign * -0.5 * Math.sqrt(3);   // imaginary part of omega for n=3: sin(sign*(-2)*pi*1/3)
                for (int i = 0; i < a0.length(); i++) {
                    double c = omegaImag * (a2.imag(i) - a1.imag(i));
                    double d = omegaImag * (a1.real(i) - a2.real(i));
                    double e = 0.5 * (a1.real(i) + a2.real(i));
                    double f = 0.5 * (a1.imag(i) + a2.imag(i));

                    double a02Real = a0.real(i) + a1.real(i) + a2.real(i);
                    double a02Imag = a0.imag(i) + a1.imag(i) + a2.imag(i);
                    double a1Real = a0.real(i) - e + c;
                    double a1Imag = a0.imag(i) + d - f;
                    double a2Real = a0.real(i) - e - c;
                    double a2Imag = a0.imag(i) - d - f;

                    a0.real(i, a02Real);
                    a0.imag(i, a02Imag);
                    a1.real(i, a1Real);
                    a1.imag(i, a1Imag);
                    a2.real(i, a2Real);
                    a2.imag(i, a2Imag);
                }
                a0.scale(scale);
                a1.scale(scale);
                a2.scale(scale);
            }
        },

        SCALE_STANDALONE_SUMS {
            @Override
            void fft3(FftMultiplier.ComplexVector a0, FftMultiplier.ComplexVector a1, FftMultiplier.ComplexVector a2,
                      int sign, double scale) {
                double omegaImag = sign * -0.5 * Math.sqrt(3);   // imaginary part of omega for n=3: sin(sign*(-2)*pi*1/3)
                for (int i = 0; i < a0.length(); i++) {
                    double a12real = a1.real(i) + a2.real(i);
                    double a12imag = a1.imag(i) + a2.imag(i);
                    double c = omegaImag * (a2.imag(i) - a1.imag(i));
                    double d = omegaImag * (a1.real(i) - a2.real(i));
                    double e = 0.5 * a12real;
                    double f = 0.5 * a12imag;

                    double a02Real = a0.real(i) + a12real;
                    double a02Imag = a0.imag(i) + a12imag;
                    double a1Real = a0.real(i) - e + c;
                    double a1Imag = a0.imag(i) + d - f;
                    double a2Real = a0.real(i) - e - c;
                    double a2Imag = a0.imag(i) - d - f;

                    a0.real(i, a02Real);
                    a0.imag(i, a02Imag);
                    a1.real(i, a1Real);
                    a1.imag(i, a1Imag);
                    a2.real(i, a2Real);
                    a2.imag(i, a2Imag);
                }
                a0.scale(scale);
                a1.scale(scale);
                a2.scale(scale);
            }
        },

        SCALE_STANDALONE_ONE_INTERMEDIATE_VARIABLE {
            @Override
            void fft3(FftMultiplier.ComplexVector a0, FftMultiplier.ComplexVector
                    a1, FftMultiplier.ComplexVector a2, int sign, double scale) {
                double omegaImag = sign * -0.5 * Math.sqrt(3);   // imaginary part of omega for n=3: sin(sign*(-2)*pi*1/3)
                for (int i = 0; i < a0.length(); i++) {
                    double c = omegaImag * (a2.imag(i) - a1.imag(i));
                    double d = omegaImag * (a1.real(i) - a2.real(i));
                    double e = 0.5 * (a1.real(i) + a2.real(i));
                    double f = 0.5 * (a1.imag(i) + a2.imag(i));

                    double a12real = a0.real(i) + a1.real(i) + a2.real(i);
                    double a12imag = a0.imag(i) + a1.imag(i) + a2.imag(i);
                    a1.real(i, a0.real(i) - e + c);
                    a1.imag(i, a0.imag(i) + d - f);
                    a2.real(i, a0.real(i) - e - c);
                    a2.imag(i, a0.imag(i) - d - f);
                    a0.real(i, a12real);
                    a0.imag(i, a12imag);
                }
                a0.scale(scale);
                a1.scale(scale);
                a2.scale(scale);
            }
        },

        SCALE_STANDALONE_SUMS_ONE_INTERMEDIATE_VARIABLE_CD_MIDDLE {
            @Override
            void fft3(FftMultiplier.ComplexVector a0, FftMultiplier.ComplexVector a1, FftMultiplier.ComplexVector a2, int sign, double scale) {
                double omegaImag = sign * -0.5 * Math.sqrt(3);   // imaginary part of omega for n=3: sin(sign*(-2)*pi*1/3)
                for (int i = 0; i < a0.length(); i++) {
                    double a12real = a1.real(i) + a2.real(i);
                    double a12imag = a1.imag(i) + a2.imag(i);
                    double c = omegaImag * (a2.imag(i) - a1.imag(i));
                    double d = omegaImag * (a1.real(i) - a2.real(i));
                    double e = 0.5 * a12real;
                    double f = 0.5 * a12imag;

                    a2.real(i, a0.real(i) - e - c);
                    a2.imag(i, a0.imag(i) - d - f);
                    a1.real(i, a0.real(i) - e + c);
                    a1.imag(i, a0.imag(i) + d - f);
                    a0.addReal(i, a12real);
                    a0.addImag(i, a12imag);
                }
                a0.scale(scale);
                a1.scale(scale);
                a2.scale(scale);
            }
        },

        SCALE_STANDALONE_SUMS_ONE_INTERMEDIATE_VARIABLE_CD_FIRST {
            @Override
            void fft3(FftMultiplier.ComplexVector a0, FftMultiplier.ComplexVector a1, FftMultiplier.ComplexVector a2, int sign, double scale) {
                double omegaImag = sign * -0.5 * Math.sqrt(3);   // imaginary part of omega for n=3: sin(sign*(-2)*pi*1/3)
                for (int i = 0; i < a0.length(); i++) {
                    double c = omegaImag * (a2.imag(i) - a1.imag(i));
                    double d = omegaImag * (a1.real(i) - a2.real(i));
                    double a12real = a1.real(i) + a2.real(i);
                    double a12imag = a1.imag(i) + a2.imag(i);
                    double e = 0.5 * a12real;
                    double f = 0.5 * a12imag;

                    a2.real(i, a0.real(i) - e - c);
                    a2.imag(i, a0.imag(i) - d - f);
                    a1.real(i, a0.real(i) - e + c);
                    a1.imag(i, a0.imag(i) + d - f);
                    a0.addReal(i, a12real);
                    a0.addImag(i, a12imag);
                }
                a0.scale(scale);
                a1.scale(scale);
                a2.scale(scale);
            }
        },

        SCALE_STANDALONE_SUMS_ONE_INTERMEDIATE_VARIABLE_CD_LAST {
            @Override
            void fft3(FftMultiplier.ComplexVector a0, FftMultiplier.ComplexVector a1, FftMultiplier.ComplexVector a2, int sign, double scale) {
                double omegaImag = sign * -0.5 * Math.sqrt(3);   // imaginary part of omega for n=3: sin(sign*(-2)*pi*1/3)
                for (int i = 0; i < a0.length(); i++) {
                    double a12real = a1.real(i) + a2.real(i);
                    double a12imag = a1.imag(i) + a2.imag(i);
                    double e = 0.5 * a12real;
                    double f = 0.5 * a12imag;
                    double c = omegaImag * (a2.imag(i) - a1.imag(i));
                    double d = omegaImag * (a1.real(i) - a2.real(i));

                    a2.real(i, a0.real(i) - e - c);
                    a2.imag(i, a0.imag(i) - d - f);
                    a1.real(i, a0.real(i) - e + c);
                    a1.imag(i, a0.imag(i) + d - f);
                    a0.addReal(i, a12real);
                    a0.addImag(i, a12imag);
                }
                a0.scale(scale);
                a1.scale(scale);
                a2.scale(scale);
            }
        },

        ONE_INTERMEDIATE_VARIABLE {
            @Override
            void fft3(FftMultiplier.ComplexVector a0, FftMultiplier.ComplexVector a1, FftMultiplier.ComplexVector a2, int sign, double scale) {
                double omegaImag = sign * -0.5 * Math.sqrt(3);   // imaginary part of omega for n=3: sin(sign*(-2)*pi*1/3)
                for (int i = 0; i < a0.length(); i++) {
                    double c = omegaImag * (a2.imag(i) - a1.imag(i));
                    double d = omegaImag * (a1.real(i) - a2.real(i));
                    double a12real = a1.real(i) + a2.real(i);
                    double a12imag = a1.imag(i) + a2.imag(i);
                    double e = 0.5 * a12real;
                    double f = 0.5 * a12imag;

                    a12real = a0.real(i) + a12real;
                    a12imag = a0.imag(i) + a12imag;
                    a1.real(i, (a0.real(i) - e + c) * scale);
                    a1.imag(i, (a0.imag(i) + d - f) * scale);
                    a2.real(i, (a0.real(i) - e - c) * scale);
                    a2.imag(i, (a0.imag(i) - d - f) * scale);
                    a0.real(i, a12real * scale);
                    a0.imag(i, a12imag * scale);
                }
            }
        };

        @SuppressWarnings("SameParameterValue")
        abstract void fft3(FftMultiplier.ComplexVector a0, FftMultiplier.ComplexVector a1, FftMultiplier.ComplexVector a2, int sign, double scale);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JmhFftMultiplierFft3.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}