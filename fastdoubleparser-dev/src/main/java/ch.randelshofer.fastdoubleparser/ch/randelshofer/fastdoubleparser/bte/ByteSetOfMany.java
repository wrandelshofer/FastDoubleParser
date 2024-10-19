/*
 * @(#)ByteSetOfMany.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;

import java.util.HashSet;
import java.util.Set;

/**
 * A set for {@code char} with linear search.
 */
class ByteSetOfMany implements ByteSet {
    private final Set<Byte> set;

    public ByteSetOfMany(Set<Character> set) {
        this.set = new HashSet<>(set.size() * 2);
        for (char ch : set) {
            if (ch > 127) throw new IllegalArgumentException("can not map to a single byte. ch=" + ch);
            this.set.add((byte) ch);
        }
    }

    @Override
    public boolean containsKey(byte ch) {
        return set.contains(ch);
    }
}
