/*
 * @(#)SwarByteDigitSet.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;

import java.util.List;

/**
 * References:
 * <dl>
 *     <dt>Wojciech Muła, SWAR find any byte from set</dt>
 *     <dd><a href="http://0x80.pl/notesen/2023-03-06-swar-find-any.html">0x80.pl</a></dd>
 * </dl>
 */
class SwarByteDigitSet implements ByteDigitSet {
    /**
     * Digits 0,1,2,3,4,5,6,7
     */
    private final long a;
    /**
     * Digits 8,9
     */
    private final long c;

    public SwarByteDigitSet(List<Character> digits) {
        long ta = 0;
        for (int i = 0; i < 8; i++) {
            ta = (ta << 8) | digits.get(i);
        }
        a = ta;
        c = (long) ((digits.get(8)) << 8 | digits.get(9)) << 48;
    }

    @Override
    public int toDigit(byte ch) {
        long broadcast = 0x01_01_01_01_01_01_01_01L * ch;
        int x = lookup(a, broadcast);
        if (x >= 0) {
            return x;
        }
        x = lookup(c, broadcast);
        if (x >= 0) {
            return x + 8;
        }
        return 10;
    }

    private static int lookup(long a, long broadcast) {
        long x = (((a ^ broadcast) + 0x7f_7f_7f_7f_7f_7f_7f_7fL) & 0x80_80_80_80_80_80_80_80L) ^ (0x80_80_80_80_80_80_80_80L);
        if (x != 0) {
            return (Long.numberOfLeadingZeros(x) >>> 3);
        } else {
            return -1;
        }
    }
}
