/*
 * @(#)VarianceStatistics.java
 * Copyright © 2021 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.stats;

import java.util.DoubleSummaryStatistics;

import static java.lang.Math.sqrt;

/**
 * This collector computes sample variance and population variance in
 * addition to the values computed by {@link DoubleSummaryStatistics}.
 * <p>
 * Usage with a double stream:
 * <pre>
 * VarianceStatistics stats = doubleStream.collect(VarianceStatistics::new,
 *                                               VarianceStatistics::accept,
 *                                               VarianceStatistics::combine);
 * </pre>
 *
 * <p>
 * References:
 * <ul>
 * <li>Algorithms for calculating variance.<br>
 * Wikipedia.
 * <a href="https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#Computing_shifted_data">link</a>
 * </li>
 * </ul>
 * </p>
 */
public class VarianceStatistics extends DoubleSummaryStatistics {
    /**
     * We use e DoubleSummaryStatistics here, because it can sum
     * doubles with compensation.
     */
    private final DoubleSum sumOfSquare = new DoubleSum();

    /**
     * Adds a value to the sample.
     *
     * @param value a new value
     */
    @Override
    public void accept(double value) {
        super.accept(value);
        sumOfSquare.accept(value * value);
    }

    /**
     * Combines the state of another {@code VarianceStatistics} into this one.
     *
     * @param other another {@code VarianceStatistics}
     * @return this
     */
    public VarianceStatistics combine(VarianceStatistics other) {
        super.combine(other);
        sumOfSquare.combine(other.sumOfSquare);
        return this;
    }

    /**
     * Returns the sum of square of the sample.
     *
     * @return the sum of square
     */
    public double getSumOfSquare() {
        return sumOfSquare.getSum();
    }

    /**
     * Returns the (unbiased) variance {@code s^2} of the sample.
     *
     * @return the variance of the sample
     */
    public double getSampleVariance() {
        double avg = getAverage();
        long n = getCount();
        return n > 0 ? (getSumOfSquare() - avg * avg * n) / (n - 1) : 0.0d;
    }

    /**
     * Returns the standard deviation {@code stdev} of the sample.
     *
     * @return the standard deviation of the sample
     */
    public double getSampleStandardDeviation() {
        return sqrt(getSampleVariance());
    }

    /**
     * Returns the variance {@code s^2} of the population.
     * <p>
     * Use this method only if the entire population has been sampled.
     *
     * @return the variance of the population
     */
    public double getPopulationVariance() {
        double avg = getAverage();
        long n = getCount();
        return n > 0 ? (getSumOfSquare() / n) - avg * avg : 0.0d;
    }

    /**
     * Returns the standard deviation {@code stdev} of the population.
     * <p>
     * Use this method only if the entire population has been sampled.
     *
     * @return the standard deviation of the population
     */
    public double getPopulationStandardDeviation() {
        return sqrt(getPopulationVariance());
    }


    @Override
    public String toString() {
        return String.format(
                "%s{count=%d, sum=%f, min=%f, avg=%f, max=%f, stdevs=%f, stdevp=%f}",
                this.getClass().getSimpleName(),
                getCount(),
                getSum(),
                getMin(),
                getAverage(),
                getMax(),
                getSampleStandardDeviation(),
                getPopulationStandardDeviation()
        );
    }
}
