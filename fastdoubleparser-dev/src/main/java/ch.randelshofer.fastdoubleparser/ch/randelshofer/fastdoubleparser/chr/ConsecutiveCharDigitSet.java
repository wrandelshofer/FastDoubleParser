/*
 * @(#)ConsecutiveByteDigitSet.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

final class ConsecutiveCharDigitSet implements CharDigitSet {
    private final char zeroChar;

    public ConsecutiveCharDigitSet(char zeroChar) {
        this.zeroChar = zeroChar;
    }

    @Override
    public char getZeroChar() {
        return zeroChar;
    }

    @Override
    public int toDigit(char ch) {
        return (char) (ch - zeroChar);
    }
}
