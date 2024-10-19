/*
 * @(#)ByteTrieOfOne.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;

import java.nio.charset.StandardCharsets;
import java.util.Set;

class ByteTrieOfOne implements ByteTrie {
    private final byte[] chars;

    public ByteTrieOfOne(Set<String> set) {
        if (set.size() != 1) throw new IllegalArgumentException("set size must be 1, size=" + set.size());
        chars = set.iterator().next().getBytes(StandardCharsets.UTF_8);
    }


    @Override
    public int match(byte[] str) {
        return match(str, 0, str.length);
    }

    @Override
    public int match(byte[] str, int startIndex, int endIndex) {
        /*
        int mismatch = Arrays.mismatch(chars, 0, chars.length, str, startIndex,startIndex+ Math.min(endIndex - startIndex, chars.length));
        return mismatch<0?chars.length:0;
        */
        int i = 0;
        int limit = Math.min(endIndex - startIndex, chars.length);
        while (i < limit && str[i + startIndex] == chars[i]) {
            i++;
        }
        return i == chars.length ? chars.length : 0;
    }
}
