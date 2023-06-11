/*
 * @(#)AbstractBigIntegerParser.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;
public abstract class AbstractBigIntegerParser extends AbstractNumberParser {
    private final static int MAX_INPUT_LENGTH = 1_292_782_622;

    /**
     * The resulting value must fit into {@code 2^31 - 1} bits.
     * The decimal representation of {@code 2^31 - 1} bits has 646,456,993 digits.
     */
    private static final int MAX_DECIMAL_DIGITS = 646_456_993;

    /**
     * The resulting value must fit into {@code 2^31 - 1} bits.
     * The hexadecimal representation of {@code 2^31 - 1} bits has 536,870,912 digits.
     */
    private static final int MAX_HEX_DIGITS = 536_870_912;

    protected static boolean hasManyDigits(int length) {
        return length > 18;
    }

    protected static int checkBigIntegerBounds(int size, int offset, int length) {
        return AbstractNumberParser.checkBounds(size, offset, length, MAX_INPUT_LENGTH);
    }

    protected static void checkHexBigIntegerBounds(int numDigits) {
        if (numDigits > MAX_HEX_DIGITS) {
            throw new NumberFormatException(AbstractNumberParser.VALUE_EXCEEDS_LIMITS);
        }
    }

    protected static void checkDecBigIntegerBounds(int numDigits) {
        if (numDigits > MAX_DECIMAL_DIGITS) {
            throw new NumberFormatException(AbstractNumberParser.VALUE_EXCEEDS_LIMITS);
        }
    }
}
