/*
 * @(#)SwarCharDigitSet.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

import java.util.List;

/**
 * References:
 * <dl>
 *     <dt>Wojciech Muła, SWAR find any byte from set</dt>
 *     <dd><a href="http://0x80.pl/notesen/2023-03-06-swar-find-any.html">0x80.pl</a></dd>
 * </dl>
 */
class SwarCharDigitSet implements CharDigitSet {
    /**
     * Digits 0,1,2,3
     */
    private final long a;
    /**
     * Digits 4,5,6,7
     */
    private final long b;
    /**
     * Digits 8,9
     */
    private final long c;

    public SwarCharDigitSet(List<Character> digits) {
        long ta = 0, tb = 0;
        for (int i = 0; i < 4; i++) {
            ta = (ta << 16) | digits.get(i);
            tb = (tb << 16) | digits.get(i + 4);
        }
        a = ta;
        b = tb;
        c = (long) ((digits.get(8)) << 16 | digits.get(9)) << 32;
    }

    @Override
    public int toDigit(char ch) {
        long broadcast = 0x0001_0001_0001_0001L * ch;
        //long broadcast = ch|(long)ch<<16|(long)ch<<32|(long)ch<<48;
        int x = lookup(a, broadcast);
        if (x >= 0) {
            return x;
        }
        x = lookup(b, broadcast);
        if (x >= 0) {
            return x + 4;
        }
        x = lookup(c, broadcast);
        if (x >= 0) {
            return x + 8;
        }
        return 10;
    }

    private static int lookup(long a, long broadcast) {
        long x = (((a ^ broadcast) + 0x7fff_7fff_7fff_7fffL) & 0x8000_8000_8000_8000L) ^ (0x8000_8000_8000_8000L);
        if (x != 0) {
            return (Long.numberOfLeadingZeros(x) >>> 4);
        } else {
            return -1;
        }
    }
}
