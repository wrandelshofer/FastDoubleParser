/*
 * @(#)FftMultiplierFft3Test.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ch.randelshofer.fastdoubleparser.JmhFftMultiplierFft3.Fft3Algorithm;
import ch.randelshofer.fastdoubleparser.FftMultiplier.ComplexVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class FftMultiplierFft3Test {
    private static final double SCALE = 1.0 / 3;
    private static final int NUMBER_OF_TESTED_RANDOM_VALUES = 10;

    @ParameterizedTest
    @MethodSource("someRandomParameters")
    public void compareWithOriginal(Fft3Algorithm algorithm,
                                    ComplexVector a0,
                                    ComplexVector a1,
                                    ComplexVector a2,
                                    int sign) {
        algorithm.fft3(a0, a1, a2, sign, SCALE);
    }

    private static Stream<Arguments> someRandomParameters() {
        List<Object[]> testCases = new ArrayList<>();
        Random rng = new Random(2023);
        for (int i = 0; i < NUMBER_OF_TESTED_RANDOM_VALUES; i++) {
            int n = 1 << (5 + i * 4 / NUMBER_OF_TESTED_RANDOM_VALUES);
            ComplexVector a0 = new ComplexVector(n);
            ComplexVector a1 = new ComplexVector(n);
            ComplexVector a2 = new ComplexVector(n);
            for (int j = 0; j < n; j++) {
                a0.set(j, rng.nextDouble(), rng.nextDouble());
                a1.set(j, rng.nextDouble(), rng.nextDouble());
                a2.set(j, rng.nextDouble(), rng.nextDouble());
            }
            int sign = i % 2 == 0 ? -1 : 1;
            testCases.add(new Object[]{a0, a1, a2, sign});
        }
        return combineWithAlgorithms(testCases);
    }

    private static Stream<Arguments> combineWithAlgorithms(List<Object[]> testCases) {
        List<Arguments> args = new ArrayList<>();
        for (JmhFftMultiplierFft3.Fft3Algorithm algorithm : JmhFftMultiplierFft3.Fft3Algorithm.values()) {
            if (!algorithm.equals(JmhFftMultiplierFft3.Fft3Algorithm.ORIGINAL)) {
                for (Object[] params : testCases) {
                    ComplexVector a0 = (ComplexVector) params[0];
                    ComplexVector a1 = (ComplexVector) params[1];
                    ComplexVector a2 = (ComplexVector) params[2];
                    int sign = (int) params[3];
                    args.add(Arguments.of(algorithm, a0, a1, a2, sign));
                }
            }
        }
        return args.stream();
    }
}