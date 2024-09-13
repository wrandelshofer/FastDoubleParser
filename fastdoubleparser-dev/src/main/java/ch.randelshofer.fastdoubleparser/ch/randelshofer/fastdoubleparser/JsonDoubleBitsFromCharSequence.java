/*
 * @(#)JsonDoubleBitsFromCharSequence.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

/**
 * Parses a {@code double} from a {@link CharSequence}.
 */
final class JsonDoubleBitsFromCharSequence extends AbstractJsonFloatingPointBitsFromCharSequence {

    /**
     * Creates a new instance.
     */
    public JsonDoubleBitsFromCharSequence() {

    }

    @Override
    long valueOfFloatLiteral(CharSequence str, int startIndex, int endIndex, boolean isNegative,
                             long significand, int exponent, boolean isSignificandTruncated,
                             int exponentOfTruncatedSignificand) {
        double d = FastDoubleMath.tryDecFloatToDoubleTruncated(isNegative, significand, exponent, isSignificandTruncated,
                exponentOfTruncatedSignificand);
        return Double.doubleToRawLongBits(Double.isNaN(d) ? Double.parseDouble(str.subSequence(startIndex, endIndex).toString()) : d);
    }
}