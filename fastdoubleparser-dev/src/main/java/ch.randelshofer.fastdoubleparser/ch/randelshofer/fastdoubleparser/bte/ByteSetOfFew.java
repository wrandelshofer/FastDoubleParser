/*
 * @(#)ByteSetOfFew.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;

/**
 * A set of {@code char} with linear search.
 */
class ByteSetOfFew implements ByteSet {
    private final byte[] bytes;

    public ByteSetOfFew(Set<Character> set) {
        byte[] tmp = new byte[set.size() * 4];
        int i = 0;
        for (char ch : set) {
            for (byte b : new String(new char[]{ch}).getBytes(StandardCharsets.UTF_8)) {
                tmp[i++] = b;
            }
        }
        this.bytes = Arrays.copyOf(tmp, i);
    }

    public boolean containsKey(byte b) {
        boolean found = false;
        for (byte aChar : bytes) {
            found |= aChar == b;
        }
        return found;
    }
}
