/*
 * @(#)ConfigurableFloatBitsFromCharSequence.java
 * Copyright © 2025 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

/**
 * Parses a {@code float} from a {@link CharSequence} with configurable {@link NumberFormatSymbols}.
 */
final class ConfigurableFloatBitsFromCharSequence extends AbstractConfigurableFloatingPointBitsFromCharSequence {
    /**
     * Creates a new instance.
     */
    public ConfigurableFloatBitsFromCharSequence(NumberFormatSymbols symbols, boolean ignoreCase) {
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
        float d = FastFloatMath.tryDecFloatToFloatTruncated(isSignificandNegative, significand, exponent, isSignificandTruncated,
                exponentOfTruncatedSignificand);
        return Float.floatToRawIntBits(Float.isNaN(d) ?
                slowPathToFloat(str, integerStartIndex, integerEndIndex, fractionStartIndex, fractionEndIndex, isSignificandNegative, exponentValue) :
                d);
    }

    private float slowPathToFloat(CharSequence str, int integerStartIndex, int integerEndIndex, int fractionStartIndex, int fractionEndIndex, boolean isSignificandNegative, int exponentValue) {
        return SlowFloatConversionPath.toFloat(str, digitSet, integerStartIndex, integerEndIndex, fractionStartIndex, fractionEndIndex, isSignificandNegative, exponentValue);
    }
}