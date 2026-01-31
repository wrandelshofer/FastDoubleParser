/*
 * @(#)CharDigitSet.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

import ch.randelshofer.fastdoubleparser.bte.ByteDigitSet;

import java.util.List;

/**
 * Interface for sets of digit characters.
 */
public interface CharDigitSet {
    char getZeroChar();

    /**
     * Returns a value in the range 0 to 9 if the specified character is a digit.
     * Otherwise, Returns a value greater than 9.
     *
     * @param ch a character
     * @return a value in the range 0 to Integer.MAX_VALUE.
     */
    int toDigit(char ch);


    /**
     * Creates a new {@link CharDigitSet} instead from the
     * specified list.
     * <p>
     * The list must contain characters for the digits 0 to 9.
     *
     * @param digits a list of digit characters
     * @return a new {@link ByteDigitSet} instance
     */
    @SuppressWarnings("SequencedCollectionMethodCanBeUsed")
    static CharDigitSet copyOf(List<Character> digits) {
        boolean consecutive = true;
        char zeroDigit = digits.get(0);
        for (int i = 1; i < 10; i++) {
            char current = digits.get(i);
            consecutive &= current == zeroDigit + i;
        }
        return consecutive ?
                new ConsecutiveCharDigitSet(digits.get(0)) :
                new CharToIntMap(digits);
    }
}
