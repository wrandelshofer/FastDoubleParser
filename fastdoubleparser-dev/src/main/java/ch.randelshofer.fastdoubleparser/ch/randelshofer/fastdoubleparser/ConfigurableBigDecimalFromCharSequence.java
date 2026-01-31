/*
 * @(#)JavaBigDecimalFromCharSequence.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.chr.CharDigitSet;
import ch.randelshofer.fastdoubleparser.chr.CharSet;
import ch.randelshofer.fastdoubleparser.chr.CharTrie;

import java.math.BigDecimal;


/**
 * Parses a {@link BigDecimal} from a {@code byte} array.
 */
final class ConfigurableBigDecimalFromCharSequence extends AbstractBigDecimalParser {
    private final CharDigitSet digitSet;
    private final CharSet minusSignChar;
    private final CharSet plusSignChar;
    private final CharSet decimalSeparator;
    private final CharSet groupingSeparator;
    private final CharTrie exponentSeparatorTrie;

    /**
     * Creates a new instance.
     */
    public ConfigurableBigDecimalFromCharSequence(NumberFormatSymbols symbols, boolean ignoreCase) {
        this.decimalSeparator = CharSet.copyOf(symbols.decimalSeparator(), ignoreCase);
        this.groupingSeparator = CharSet.copyOf(symbols.groupingSeparator(), ignoreCase);
        this.digitSet = CharDigitSet.copyOf(symbols.digits());
        this.minusSignChar = CharSet.copyOf(symbols.minusSign(), ignoreCase);
        this.exponentSeparatorTrie = CharTrie.copyOf(symbols.exponentSeparator(), ignoreCase);
        this.plusSignChar = CharSet.copyOf(symbols.plusSign(), ignoreCase);
    }

    /**
     * Parses a {@code BigDecimalString} as specified in {@link JavaBigDecimalParser}.
     *
     * @param str    the input string
     * @param offset start of the input data
     * @param length length of the input data
     * @return the parsed {@link BigDecimal}
     * @throws NullPointerException     if str is null
     * @throws IllegalArgumentException if offset or length are illegal
     * @throws NumberFormatException    if the input string can not be parsed successfully
     */
    public BigDecimal parseBigDecimalString(CharSequence str, int offset, int length) {
        try {
            final int endIndex = checkBounds(str.length(), offset, length);
            if (hasManyDigits(length)) {
                return parseBigDecimalStringWithManyDigits(str, offset, length);
            }

            // Skip leading format characters
            // -------------------
            int index = skipFormatCharacters(str, offset, endIndex);
            if (index == endIndex) {
                throw new NumberFormatException(SYNTAX_ERROR);
            }
            char ch = str.charAt(index);

            // Parse optional sign
            // -------------------
            final boolean isNegative = minusSignChar.containsKey(ch);
            if (isNegative || plusSignChar.containsKey(ch)) {
                ch = charAt(str, ++index, endIndex);
                if (ch == 0) {
                    throw new NumberFormatException(SYNTAX_ERROR);
                }
            }

            // Parse significand
            long significand = 0;// significand is treated as an unsigned long
            final int significandStartIndex = index;
            int decimalSeparatorIndex = -1;
            int integerDigitCount = -1;
            int groupingCount = 0;
            boolean illegal = false;

            for (; index < endIndex; index++) {
                ch = str.charAt(index);
                int digit = digitSet.toDigit(ch);
                if (digit < 10) {
                    // This might overflow, we deal with it later.
                    significand = 10 * significand + digit;
                } else if (decimalSeparator.containsKey(ch)) {
                    illegal |= integerDigitCount >= 0;
                    decimalSeparatorIndex = index;
                    integerDigitCount = index - significandStartIndex - groupingCount;
                } else if (groupingSeparator.containsKey(ch)) {
                    illegal |= decimalSeparatorIndex != -1;
                    groupingCount++;
                } else {
                    break;
                }
            }
            final int digitCount;
            final int significandEndIndex = index;
            long exponent;
            if (integerDigitCount < 0) {
                digitCount = significandEndIndex - significandStartIndex - groupingCount;
                decimalSeparatorIndex = significandEndIndex;
                exponent = 0;
            } else {
                digitCount = significandEndIndex - significandStartIndex - 1 - groupingCount;
                exponent = integerDigitCount - digitCount;
            }
            illegal |= digitCount == 0 && significandEndIndex > significandStartIndex;

            // Parse exponent number
            // ---------------------
            long expNumber = 0;
            int exponentSeparatorIndex = -1;
            int count = exponentSeparatorTrie.match(str, index, endIndex);
            if (count > 0) {
                exponentSeparatorIndex = index;
                index += count;
                index = skipFormatCharacters(str, index, endIndex);
                ch = charAt(str, index, endIndex);
                boolean isExponentNegative = minusSignChar.containsKey(ch);
                if (isExponentNegative || plusSignChar.containsKey(ch)) {
                    ch = charAt(str, ++index, endIndex);
                }
                int digit = digitSet.toDigit(ch);
                illegal |= digit >= 10;
                do {
                    // Guard against overflow
                    if (expNumber < AbstractBigDecimalParser.OVERFLOW_EXPONENT_NUMBER) {
                        expNumber = 10 * expNumber + digit;
                    }
                    ch = charAt(str, ++index, endIndex);
                    digit = digitSet.toDigit(ch);
                } while (digit < 10);
                if (isExponentNegative) {
                    expNumber = -expNumber;
                }
                exponent += expNumber;
            }
            illegal |= digitCount == 0;
            checkParsedBigDecimalBounds(illegal, index, endIndex, digitCount, exponent);
            if (digitCount < 19) {
                return new BigDecimal(isNegative ? -significand : significand).scaleByPowerOfTen((int) exponent);
            }
            return parseBigDecimalStringWithManyDigits(str, offset, length);
        } catch (ArithmeticException e) {
            NumberFormatException nfe = new NumberFormatException(VALUE_EXCEEDS_LIMITS);
            nfe.initCause(e);
            throw nfe;
        }
    }

    /**
     * Parses a big decimal string that has many digits.
     */
    BigDecimal parseBigDecimalStringWithManyDigits(CharSequence str, int offset, int length) {
        byte[] digits = new byte[length];

        final int endIndex = checkBounds(str.length(), offset, length);

        // Skip leading format characters
        // -------------------
        int index = skipFormatCharacters(str, offset, endIndex);
        if (index == endIndex) {
            throw new NumberFormatException(SYNTAX_ERROR);
        }
        char ch = str.charAt(index);

        // Parse optional sign
        // -------------------
        final boolean isNegative = minusSignChar.containsKey(ch);
        if (isNegative || plusSignChar.containsKey(ch)) {
            ch = charAt(str, ++index, endIndex);
            if (ch == 0) {
                throw new NumberFormatException(SYNTAX_ERROR);
            }
        }

        // Parse significand
        final int significandStartIndex = index;
        int decimalSeparatorIndex = -1;
        int integerDigitCount = -1;
        int groupingCount = 0;
        boolean illegal = false;
        int digitCount = 0;
        for (; index < endIndex; index++) {
            ch = str.charAt(index);
            int digit = digitSet.toDigit(ch);
            if (digit < 10) {
                digits[digitCount++] = (byte) ('0' + digit);
            } else if (decimalSeparator.containsKey(ch)) {
                illegal |= integerDigitCount >= 0;
                decimalSeparatorIndex = index;
                integerDigitCount = index - significandStartIndex - groupingCount;
            } else if (groupingSeparator.containsKey(ch)) {
                illegal |= decimalSeparatorIndex != -1;
                groupingCount++;
            } else {
                break;
            }
        }
        final int significandEndIndex = index;
        long exponent;
        if (integerDigitCount < 0) {
            decimalSeparatorIndex = significandEndIndex;
            exponent = 0;
        } else {
            exponent = integerDigitCount - digitCount;
        }
        illegal |= digitCount == 0 && significandEndIndex > significandStartIndex;

        // Parse exponent number
        // ---------------------
        long expNumber = 0;
        int count = exponentSeparatorTrie.match(str, index, endIndex);
        if (count > 0) {
            index += count;
            index = skipFormatCharacters(str, index, endIndex);
            ch = charAt(str, index, endIndex);
            boolean isExponentNegative = minusSignChar.containsKey(ch);
            if (isExponentNegative || plusSignChar.containsKey(ch)) {
                ch = charAt(str, ++index, endIndex);
            }
            int digit = digitSet.toDigit(ch);
            illegal |= digit >= 10;
            do {
                // Guard against overflow
                if (expNumber < AbstractBigDecimalParser.OVERFLOW_EXPONENT_NUMBER) {
                    expNumber = 10 * expNumber + digit;
                }
                ch = charAt(str, ++index, endIndex);
                digit = digitSet.toDigit(ch);
            } while (digit < 10);
            if (isExponentNegative) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        }
        illegal |= digitCount == 0;
        checkParsedBigDecimalBounds(illegal, index, endIndex, digitCount, exponent);

        return JavaBigDecimalFromByteArray.valueOfBigDecimalString(digits, 0, 0, 0, digitCount, isNegative, (int) exponent);
    }

    /**
     * Skips all format characters.
     *
     * @param str      a string
     * @param index    start index (inclusive) of the optional white space
     * @param endIndex end index (exclusive) of the optional white space
     * @return index after the optional format character
     */
    private static int skipFormatCharacters(CharSequence str, int index, int endIndex) {
        while (index < endIndex && Character.getType(str.charAt(index)) == Character.FORMAT) {
            index++;
        }
        return index;
    }
}