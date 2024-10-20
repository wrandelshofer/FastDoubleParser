/*
 * @(#)ByteSetOfOne.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;

import java.util.Set;

class ByteSetOfOne implements ByteSet {
    private final byte ch;

    ByteSetOfOne(Set<Character> set) {
        if (set.size() != 1) throw new IllegalArgumentException("set size must be 1, size=" + set.size());
        char ch = set.iterator().next();
        if (ch > 127)
            throw new IllegalArgumentException("can not map to a single byte. ch='" + ch + "' 0x" + Integer.toHexString(ch));
        this.ch = (byte) ch;
    }

    @Override
    public boolean containsKey(byte b) {
        return this.ch == b;
    }
}
