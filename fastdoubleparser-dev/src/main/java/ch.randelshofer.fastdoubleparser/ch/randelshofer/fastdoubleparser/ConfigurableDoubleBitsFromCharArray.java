/*
 * @(#)ConfigurableDoubleBitsFromCharArray.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

/**
 * Parses a {@code double} from a {@code char[]} with configurable {@link NumberFormatSymbols}.
 */
final class ConfigurableDoubleBitsFromCharArray extends AbstractConfigurableFloatingPointBitsFromCharArray {
    /**
     * Creates a new instance.
     */
    public ConfigurableDoubleBitsFromCharArray(NumberFormatSymbols symbols, boolean ignoreCase) {
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
    long valueOfFloatLiteral(char[] str, int integerStartIndex, int integerEndIndex, int fractionStartIndex, int fractionEndIndex, boolean isSignificandNegative,
                             long significand, int exponent, boolean isSignificandTruncated,
                             int exponentOfTruncatedSignificand, int exponentValue) {
        double d = FastDoubleMath.tryDecFloatToDoubleTruncated(isSignificandNegative, significand, exponent, isSignificandTruncated,
                exponentOfTruncatedSignificand);
        return Double.doubleToRawLongBits(Double.isNaN(d) ?
                // Double.parseDouble(filterInputString(str, startIndex, endIndex).toString())
                slowPathToDouble(str, integerStartIndex, integerEndIndex, fractionStartIndex, fractionEndIndex, isSignificandNegative, exponentValue) :
                d);
    }
}