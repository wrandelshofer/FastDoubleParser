/*
 * @(#)FastDoubleParser.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

/**
 * Parses a double from a char array.
 */
public class FloatFromCharSequenceParser extends AbstractFloatValueFromCharSequenceParser {


    public FloatFromCharSequenceParser() {

    }

    protected long negativeInfinity() {
        return Float.floatToRawIntBits(Float.NEGATIVE_INFINITY);
    }

    protected long positiveInfinity() {
        return Float.floatToRawIntBits(Float.POSITIVE_INFINITY);
    }

    protected long nan() {
        return Float.floatToRawIntBits(Float.NaN);
    }

    /**
     * Convenience method for calling {@link #parse(CharSequence, int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     */
    public float parse(CharSequence str) throws NumberFormatException {
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
    public float parse(CharSequence str, int offset, int length) throws NumberFormatException {
        return Float.intBitsToFloat((int) parseFloatValue(str, offset, length));
    }

    @Override
    protected long convertFloatLiteralToValue(CharSequence str, int startIndex, int endIndex, boolean isNegative,
                                              long digits, int exponent, boolean isDigitsTruncated,
                                              int exponentOfTruncatedDigits) {
        float result = FastFloatMath.decFloatLiteralToFloat(isNegative, digits, exponent, isDigitsTruncated, exponentOfTruncatedDigits);
        return Float.isNaN(result) ? parseRestOfDecimalFloatLiteralTheHardWay(str, startIndex, endIndex) : Float.floatToRawIntBits(result);
    }

    @Override
    protected long convertHexFloatLiteralToValue(int index, boolean isNegative, long digits, long exponent, int virtualIndexOfPoint, long exp_number, boolean isDigitsTruncated, int skipCountInTruncatedDigits, CharSequence str) {
        float d = FastFloatMath.hexFloatLiteralToFloat(index, isNegative, digits, exponent, virtualIndexOfPoint, exp_number, isDigitsTruncated, skipCountInTruncatedDigits);
        return Float.floatToRawIntBits(Float.isNaN(d) ? Float.parseFloat(str.toString()) : d);
    }

    protected long parseRestOfDecimalFloatLiteralTheHardWay(CharSequence str, int startIndex, int endIndex) {
        return Float.floatToRawIntBits(Float.parseFloat(str.subSequence(startIndex, endIndex).toString()));
    }
}