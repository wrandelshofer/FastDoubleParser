/*
 * @(#)ByteTrieOfOne.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;

import java.nio.charset.StandardCharsets;
import java.util.Set;

final class ByteTrieOfOneSingleByte implements ByteTrie {
    private final byte ch;

    public ByteTrieOfOneSingleByte(Set<String> set) {
        if (set.size() != 1) throw new IllegalArgumentException("set size must be 1, size=" + set.size());
        byte[] chars = set.iterator().next().getBytes(StandardCharsets.UTF_8);
        if (chars.length != 1) throw new IllegalArgumentException("char size must be 1, size=" + set.size());
        ch = chars[0];
    }

    public ByteTrieOfOneSingleByte(byte ch) {
        this.ch = ch;
    }


    @Override
    public int match(byte[] str) {
        return match(str, 0, str.length);
    }

    @Override
    public int match(byte[] str, int startIndex, int endIndex) {
        return startIndex < endIndex && str[startIndex] == ch ? 1 : 0;
    }
}
