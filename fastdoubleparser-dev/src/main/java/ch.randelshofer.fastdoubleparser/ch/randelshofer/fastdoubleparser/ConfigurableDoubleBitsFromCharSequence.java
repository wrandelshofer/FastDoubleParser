/*
 * @(#)ConfigurableDoubleBitsFromCharSequence.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

/**
 * Parses a {@code double} from a {@link CharSequence} with configurable {@link NumberFormatSymbols}.
 */
final class ConfigurableDoubleBitsFromCharSequence extends AbstractConfigurableFloatingPointBitsFromCharSequence {
    /**
     * Creates a new instance.
     */
    public ConfigurableDoubleBitsFromCharSequence(NumberFormatSymbols symbols, boolean ignoreCase) {
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
    long valueOfFloatLiteral(CharSequence str, int startIndex, int endIndex, boolean isNegative,
                             long significand, int exponent, boolean isSignificandTruncated,
                             int exponentOfTruncatedSignificand) {
        double d = FastDoubleMath.tryDecFloatToDoubleTruncated(isNegative, significand, exponent, isSignificandTruncated,
                exponentOfTruncatedSignificand);
        return Double.doubleToRawLongBits(Double.isNaN(d)
                ? Double.parseDouble(filterInputString(str, startIndex, endIndex).toString())
                : d);
    }
}