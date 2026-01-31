/*
 * @(#)ConfigurableFloatMatcherFromCharSequence.java
 * Copyright © 2025 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

/**
 * Parses a {@code float} from a {@link CharSequence} with configurable {@link NumberFormatSymbols}.
 */
final class ConfigurableFloatMatcherFromCharSequence extends AbstractConfigurableFloatingPointBitsFromCharSequence {
    /**
     * Creates a new instance.
     */
    public ConfigurableFloatMatcherFromCharSequence(NumberFormatSymbols symbols, boolean ignoreCase) {
        super(symbols, ignoreCase);
    }

    @Override
    long nan() {
        return Float.floatToRawIntBits(Float.NaN);
    }

    @Override
    long negativeInfinity() {
        return Float.floatToRawIntBits(Float.NEGATIVE_INFINITY);
    }

    @Override
    long positiveInfinity() {
        return Float.floatToRawIntBits(Float.POSITIVE_INFINITY);
    }

    @Override
    long valueOfFloatLiteral(CharSequence str, int integerStartIndex, int integerEndIndex, int fractionStartIndex, int fractionEndIndex, boolean isSignificandNegative,
                             long significand, int exponent, boolean isSignificandTruncated,
                             int exponentOfTruncatedSignificand, int exponentValue, int startIndex, int endIndex) {
        return Float.floatToRawIntBits(1.0f);
    }

}