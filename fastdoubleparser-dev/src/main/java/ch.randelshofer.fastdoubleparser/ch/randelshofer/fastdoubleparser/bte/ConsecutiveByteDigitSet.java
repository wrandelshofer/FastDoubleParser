/*
 * @(#)ConsecutiveByteDigitSet.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;

public class ConsecutiveByteDigitSet implements ByteDigitSet {
    private final byte zeroDigit;

    public ConsecutiveByteDigitSet(char zeroDigit) {
        if (zeroDigit > 127) {
            throw new IllegalArgumentException("can not map to a single byte. zeroDigit=" + zeroDigit + "' 0x" + Integer.toHexString(zeroDigit));
        }
        this.zeroDigit = (byte) zeroDigit;
    }

    @Override
    public int toDigit(byte ch) {
        return (char) (ch - zeroDigit);
    }
}
