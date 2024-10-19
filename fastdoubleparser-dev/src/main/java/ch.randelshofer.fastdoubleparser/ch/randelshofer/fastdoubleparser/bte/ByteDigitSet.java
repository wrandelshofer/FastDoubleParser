/*
 * @(#)ByteDigitSet.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;

import java.util.List;

public interface ByteDigitSet {
    /**
     * Returns a value in the range 0 to 9 if the specified character is a digit.
     * Otherwise, Returns a value greater than 9.
     *
     * @param ch a character
     * @return a value in the range 0 to Integer.MAX_VALUE.
     */
    int toDigit(byte ch);

    @SuppressWarnings("SequencedCollectionMethodCanBeUsed")
    static ByteDigitSet copyOf(List<Character> digits) {
        boolean consecutive = true;
        char zeroDigit = digits.get(0);
        for (int i = 1; i < 10; i++) {
            char current = digits.get(i);
            consecutive &= current == zeroDigit + i;
        }
        return consecutive ?
                new ConsecutiveByteDigitSet(digits.get(0)) :
                new ByteToIntMap(digits);
        //new HashMapByteDigitSet(digits);
        //new SwarByteDigitSet(digits);
        //new LookupCharDigitSet(digits);
    }
}
