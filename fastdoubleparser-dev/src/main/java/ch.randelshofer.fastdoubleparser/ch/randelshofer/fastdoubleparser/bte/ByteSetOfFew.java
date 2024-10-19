/*
 * @(#)ByteSetOfFew.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;

import java.util.Set;

/**
 * A set of {@code char} with linear search.
 */
class ByteSetOfFew implements ByteSet {
    private final byte[] bytes;

    public ByteSetOfFew(Set<Character> set) {
        this.bytes = new byte[set.size()];
        int i = 0;
        for (char ch : set) {
            if (ch > 127)
                throw new IllegalArgumentException("can not map to a single byte. ch=" + ch + "' 0x" + Integer.toHexString(ch));
            this.bytes[i++] = (byte) ch;
        }
    }

    public boolean containsKey(byte ch) {
        boolean found = false;
        for (byte aChar : bytes) {
            found |= aChar == ch;
        }
        return found;
    }
}
