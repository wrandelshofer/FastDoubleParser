/*
 * @(#)DoubleSum.java
 * Copyright © 2021 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.stats;

import java.util.function.DoubleConsumer;

import static java.lang.Math.abs;

/**
 * Computes the sum of doubles with the Neumaier compensation algorithm.
 * <p>
 * Usage with a double stream:
 * <pre>
 * DoubleSum stats = doubleStream.collect(DoubleSum::new,
 *                                               DoubleSum::accept,
 *                                               DoubleSum::combine);
 * </pre>
 * <p>
 * References:
 * <ul>
 *     <li>Neumaier Sum.<br>
 *         Wikipedia.<br>
 *     <a href="https://en.wikipedia.org/wiki/Kahan_summation_algorithm#Further_enhancements">link</a></li>
 * </ul>
 * </p>
 */
public class DoubleSum implements DoubleConsumer {
    private double sum = 0.0;
    private double c = 0.0;

    /**
     * Adds a value to the sample.
     *
     * @param value a new value
     */
    @Override
    public void accept(double value) {
        sumWithCompensation(value);
    }

    /**
     * Combines the state of another {@code VarianceStatistics} into this one.
     *
     * @param other another {@code VarianceStatistics}
     * @return this
     */
    public DoubleSum combine(DoubleSum other) {
        sumWithCompensation(other.sum);
        sumWithCompensation(other.c);
        return this;
    }

    /**
     * Returns the sum.
     *
     * @return the sum of square
     */
    public double getSum() {
        return sum + c;
    }

    /**
     * Performs the Neumaier Sum algorithm.
     *
     * @param input the new input value
     */
    private void sumWithCompensation(double input) {
        double t = sum + input;
        if (abs(sum) >= abs(input)) {
            c += (sum - t) + input;// If sum is bigger, low-order digits of input are lost.
        } else {
            c += (input - t) + sum;// Else low-order digits of sum are lost
        }
        sum = t;
    }
}
