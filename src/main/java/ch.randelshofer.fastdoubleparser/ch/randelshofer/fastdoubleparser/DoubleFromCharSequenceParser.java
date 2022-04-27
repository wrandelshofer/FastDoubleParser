/*
 * @(#)FastDoubleParser.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

/**
 * Parses a double from a char array.
 */
public final class DoubleFromCharSequenceParser extends AbstractFloatValueFromCharSequenceParser {


    public DoubleFromCharSequenceParser() {

    }

    protected long negativeInfinity() {
        return Double.doubleToRawLongBits(Double.NEGATIVE_INFINITY);
    }

    protected long positiveInfinity() {
        return Double.doubleToRawLongBits(Double.POSITIVE_INFINITY);
    }

    protected long nan() {
        return Double.doubleToRawLongBits(Double.NaN);
    }

    /**
     * Convenience method for calling {@link #parse(CharSequence, int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     */
    public double parse(CharSequence str) throws NumberFormatException {
        return parse(str, 0, str.length());
    }

    /**
     * Parses a {@code FloatValue} from a {@link CharSequence} and converts it
     * into a {@code double} value.
     * <p>
     * See {@link ch.randelshofer.fastdoubleparser} for the syntax of {@code FloatValue}.
     *
     * @param str    the string to be parsed
     * @param offset the start offset of the {@code FloatValue} in {@code str}
     * @param length the length of {@code FloatValue} in {@code str}
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     */
    public double parse(CharSequence str, int offset, int length) throws NumberFormatException {
        return Double.longBitsToDouble(parseFloatValue(str, offset, length));
    }

    @Override
    protected long convertFloatLiteralToValue(CharSequence str, int startIndex, int endIndex, boolean isNegative,
                                              long digits, int exponent, boolean isSignificandTruncated,
                                              int exponentOfTruncatedSignificand) {
        double result = FastDoubleMath.decFloatLiteralToDouble(isNegative, digits, exponent, isSignificandTruncated,
                exponentOfTruncatedSignificand);
        return Double.isNaN(result) ? parseRestOfDecimalFloatLiteralTheHardWay(str, startIndex, endIndex) : Double.doubleToRawLongBits(result);
    }

    @Override
    protected long convertHexFloatLiteralToValue(int index, boolean isNegative, long digits, long exponent, int virtualIndexOfPoint, long exp_number, boolean isDigitsTruncated, int skipCountInTruncatedDigits, CharSequence str) {
        double d = FastDoubleMath.hexFloatLiteralToDouble(isNegative, digits, exponent, isDigitsTruncated,
                (virtualIndexOfPoint - index + skipCountInTruncatedDigits) * 4L + exp_number);
        return Double.doubleToRawLongBits(Double.isNaN(d) ? Double.parseDouble(str.toString()) : d);
    }

    private long parseRestOfDecimalFloatLiteralTheHardWay(CharSequence str, int startIndex, int endIndex) {
        return Double.doubleToRawLongBits(Double.parseDouble(str.subSequence(startIndex, endIndex).toString()));
    }
}