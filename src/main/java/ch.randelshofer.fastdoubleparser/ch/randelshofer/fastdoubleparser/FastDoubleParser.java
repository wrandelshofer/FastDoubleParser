/*
 * @(#)FastDoubleParser2.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

/**
 * Parses a double from a char sequence.
 */
public class FastDoubleParser {


    public static final FloatFromCharSequenceParser FLOAT_P = new FloatFromCharSequenceParser();
    public static final DoubleFromCharSequenceParser DOUBLE_P = new DoubleFromCharSequenceParser();

    /**
     * Don't let anyone instantiate this class.
     */
    private FastDoubleParser() {

    }

    /**
     * Convenience method for calling {@link #parseDouble(CharSequence, int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static double parseDouble(CharSequence str) throws NumberFormatException {
        return parseDouble(str, 0, str.length());
    }

    /**
     * Parses a {@code FloatValue} from a {@link CharSequence} and converts it
     * into a {@code double} value.
     * <p>
     * See {@link ch.randelshofer.fastdoubleparser} for the syntax of {@code FloatValue}.
     *
     * @param str the string to be parsed
     * @param offset the start offset of the {@code FloatValue} in {@code str}
     * @param length the length of {@code FloatValue} in {@code str}
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static double parseDouble(CharSequence str, int offset, int length) throws NumberFormatException {
        return DOUBLE_P.parse(str, offset,length);
    }

    /**
     * Convenience method for calling {@link #parseFloat(CharSequence, int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static float parseFloat(CharSequence str) throws NumberFormatException {
        return parseFloat(str, 0, str.length());
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
    public static float parseFloat(CharSequence str, int offset, int length) throws NumberFormatException {
        return FLOAT_P.parse(str, offset, length);
    }

}