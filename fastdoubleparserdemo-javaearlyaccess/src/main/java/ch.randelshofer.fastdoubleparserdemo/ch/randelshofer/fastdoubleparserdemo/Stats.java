/*
 * @(#)Stats.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparserdemo;

import java.util.Arrays;

import static java.lang.Math.sqrt;

/**
 * Provides utility methods for statistics calculations.
 */
public class Stats {
    private Stats() {
        // prevent instantiation
    }

    /**
     * Quantiles of the normal distribution function for
     * expected value = 0 and variance = 1.
     * <p>
     * The values below have been computed with NORMINV(p,0,1) on OpenOffice.
     * <p>
     * References
     * <ul>
     * <li><a href="https://de.wikipedia.org/wiki/Standardnormalverteilungstabelle">wikipedia</a></li>
     * </ul>
     * </p>
     */
    private final static double[][] z1 = {
            // The first row contains the percentage "p".
            {0.9, 0.95, 0.96, 0.975, 0.98, 0.99, 0.995, 0.999, 0.9995},
            {1.2815515655, 1.644853627, 1.7506860713, 1.9599639845, 2.0537489106, 2.326347874, 2.5758293035, 3.0902323062, 3.2905267315},
    };

    /**
     * Evaluates the inverse of the cumulative normal distribution function
     * of {@code expected value = 0, variance = 1} for the given confidence level.
     *
     * @param p the confidence level in [0, 1].
     * @return the value that yields the percentage {@code p}.
     */
    private static double z1(double p) {
        int col = Arrays.binarySearch(z1[0], p);
        if (col <= 0) {
            throw new AssertionError("Cannot compute z1 for confidence level: " + p);
        }
        return z1[1][col];
    }

    /**
     * Same as Arrays.binarySearch but with double[][] type.
     */
    private static int binarySearch(double[][] a,
                                    double key) {
        int low = 0;
        int high = a.length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            double[] midVal = a[mid];
            int cmp = Double.compare(midVal[0], key);
            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid; // key found
            }
        }
        return -(low + 1);  // key not found.
    }


    /**
     * Returns the confidence value for a population mean using a
     * Normal distribution.
     * <p>
     * This distribution should be used if the standard deviation of
     * the population is known. If it is unknown, then it should
     * be used for large sample sizes {@code size ≥ 30} only.
     * For smaller sample sizes with unknown standard deviation of the population
     * use {@code #confidenceT}.
     *
     * @param alpha the significance level.
     *              The confidence level equals {@code 1 - alpha}.
     *              An alpha of 0.05 indicates a 95 percent confidence.
     *              Supported values: 0.05, 0.02, 0.01, 0.001.
     * @param stdev the standard deviation of the population
     * @param size  the sample size
     * @return the value {@code c} for constructing the confidence interval
     * {@code [ mean - c , mean + c ] }.
     */
    public static double confidenceNorm(double alpha, double stdev, long size) {
        return z1(1.0 - alpha / 2) * stdev / sqrt(size);
    }

    /**
     * Returns the confidence value for a population mean using
     * the Student's t distribution for small samples and the
     * Normal distribution for large samples.
     * <p>
     * For sample sizes &lt; 30 the Student's t distribution is used.
     * For sample sizes ≥ 30 the Normal distribution is used.
     * <p>
     * The confidence interval can be constructed by
     * subtracting and adding the returned value {@code c} from the mean
     * value: {@code [mean - c, mean + c]}.
     * <p>
     *
     * @param alpha the significance level.
     *              The confidence level equals {@code 1 - alpha}.
     *              An alpha of 0.05 indicates a 95 percent confidence level.
     *              Supported values: 0.05, 0.02, 0.01, 0.001.
     * @param stdev the sample standard deviation
     * @param size  the sample size
     * @return the confidence value {@code c}
     */
    public static double confidence(double alpha, double stdev, long size) {
        if (size >= 30) {
            return Stats.confidenceNorm(alpha, stdev, size);
        } else {
            throw new UnsupportedOperationException("StudentT distribution is not included in this distro.");
        }
    }
}
