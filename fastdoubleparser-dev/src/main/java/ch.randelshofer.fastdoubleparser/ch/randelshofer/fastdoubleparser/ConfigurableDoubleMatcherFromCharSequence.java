/*
 * @(#)ConfigurableDoubleBitsFromCharSequence.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

/**
 * Parses a {@code double} from a {@link CharSequence} with configurable {@link NumberFormatSymbols}.
 */
final class ConfigurableDoubleMatcherFromCharSequence extends AbstractConfigurableFloatingPointBitsFromCharSequence {
    /**
     * Creates a new instance.
     */
    public ConfigurableDoubleMatcherFromCharSequence(NumberFormatSymbols symbols, boolean ignoreCase) {
        super(symbols, ignoreCase);
    }

    @Override
    long nan() {
        return Double.doubleToRawLongBits(Double.NaN);
    }

    @Override
    long negativeInfinity() {
        return Double.doubleToRawLongBits(Double.NEGATIVE_INFINITY);
    }

    @Override
    long positiveInfinity() {
        return Double.doubleToRawLongBits(Double.POSITIVE_INFINITY);
    }

    @Override
    long valueOfFloatLiteral(CharSequence str, int integerStartIndex, int integerEndIndex, int fractionStartIndex, int fractionEndIndex, boolean isSignificandNegative,
                             long significand, int exponent, boolean isSignificandTruncated,
                             int exponentOfTruncatedSignificand, int exponentValue, int startIndex, int endIndex) {
        return Double.doubleToRawLongBits(1.0);
    }

}