/*
 * @(#)JmhFastFloatMath.java
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
public class JmhFastFloatMath {
    /**
     * Bias used in the exponent of a float.
     */
    private static final int FLOAT_EXPONENT_BIAS = 127;
    private final static int FLOAT_MIN_EXPONENT_POWER_OF_TWO = Float.MIN_EXPONENT;
    private final static int FLOAT_MAX_EXPONENT_POWER_OF_TWO = Float.MAX_EXPONENT;
    /**
     * The number of bits in the significand, including the implicit bit.
     */
    private static final int FLOAT_SIGNIFICAND_WIDTH = 24;
    private boolean negative = true;
    private long significand = 324565667850L;
    private int exponent = 13;

    @Benchmark
    public void tryHexFloatToFloatTruncated(Blackhole blackhole) {
        blackhole.consume(FastFloatMath.tryHexFloatToFloatTruncated(negative, significand, exponent, false, 0));
    }

    @Benchmark
    public void customTryHexFloatToFloatTruncated(Blackhole blackhole) {
        blackhole.consume(tryHexFloatToFloatTruncated(negative, significand, exponent, false, 0));
    }

    static float powerOfTwo(boolean isNegative, int exponent) {
        int floatSign = isNegative ? 1 << 31 : 0;
        int floatExponent = (exponent + FLOAT_EXPONENT_BIAS) << (FLOAT_SIGNIFICAND_WIDTH - 1);
        int floatValue = floatSign | floatExponent;
        return Float.intBitsToFloat(floatValue);
    }

    static float tryHexFloatToFloatTruncated(boolean isNegative, long significand, int exponent,
                                             boolean isSignificandTruncated,
                                             int exponentOfTruncatedSignificand) {
        int power = isSignificandTruncated ? exponentOfTruncatedSignificand : exponent;
        if (FLOAT_MIN_EXPONENT_POWER_OF_TWO <= power && power <= FLOAT_MAX_EXPONENT_POWER_OF_TWO) {
            // Convert the significand into a float.
            // The cast will round the significand if necessary.
            // We use Math.abs here, because we treat the significand as an unsigned long.

            // Scale the significand by the power.
            // This only works if power is within the supported range, so that
            // we do not underflow or overflow.
            return scalb(significand, isNegative, power);
        } else {
            return Float.NaN;
        }
    }
    static float scalb(long number, boolean isNegative, int exponent) {
        return number * powerOfTwo(isNegative, exponent);
    }
}
