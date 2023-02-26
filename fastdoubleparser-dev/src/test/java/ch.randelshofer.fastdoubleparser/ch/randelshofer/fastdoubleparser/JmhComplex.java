/*
 * @(#)JmhComplex.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected integer strings.
 * <pre>
 * # JMH version: 1.35
 * # VM version: JDK 20-ea, OpenJDK 64-Bit Server VM, 20-ea+29-2280
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark           Mode  Cnt  Score   Error  Units
 * JmhComplex.mul      avgt    4  1.080 ± 0.117  ns/op
 * JmhComplex.mulFma1  avgt    4  0.995 ± 0.042  ns/op
 * JmhComplex.mulFma2  avgt    4  0.997 ± 0.028  ns/op
 * </pre>
 */
@Fork(value = 1, jvmArgsAppend = {
        "-XX:+UnlockExperimentalVMOptions", "--add-modules", "jdk.incubator.vector"
        , "--enable-preview"
        , "-Xmx24g"
        // , "--add-opens", "java.base/java.math=ALL-UNNAMED"

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
public class JmhComplex {


    private double ar, ai;
    private double br, bi;

    private double rr, ri;

    @Setup(Level.Trial)
    public void setUp() {
        Random rng = new Random();
        ar = rng.nextDouble();
        ai = rng.nextDouble();
        br = rng.nextDouble();
        bi = rng.nextDouble();
    }


    @Benchmark
    public void mul() {
        double real = ar;
        double imag = ai;
        rr = real * br - imag * bi;
        ri = real * bi + imag * br;
    }

    @Benchmark
    public void mulFma1() {
        double real = ar;
        double imag = ai;
        rr = real * br - imag * bi;
        ri = Math.fma(real, bi, imag * br);
    }

    @Benchmark
    public void mulFma2() {
        double real = ar;
        double imag = ai;
        rr = Math.fma(real, br, -imag * bi);
        ri = Math.fma(real, bi, imag * br);
    }

}





