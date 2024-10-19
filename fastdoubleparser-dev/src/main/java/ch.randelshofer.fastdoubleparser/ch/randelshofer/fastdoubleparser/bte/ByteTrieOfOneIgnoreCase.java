/*
 * @(#)CharTrieOfOneIgnoreCase.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;


import java.nio.charset.StandardCharsets;
import java.util.Set;

class ByteTrieOfOneIgnoreCase implements ByteTrie {
    private final byte[] chars;

    public ByteTrieOfOneIgnoreCase(Set<String> set) {
        this(set.iterator().next().getBytes(StandardCharsets.UTF_8));
        if (set.size() != 1) throw new IllegalArgumentException("set size must be 1, size=" + set.size());
    }

    public ByteTrieOfOneIgnoreCase(byte[] chars) {
        this.chars = chars;
        for (int i = 0; i < chars.length; i++) {
            chars[i] = convert(chars[i]);
        }
    }


    private static byte convert(byte c) {
        // We have to convert to upper case and then to lower case
        // because of sophisticated writing systems, like Georgian script.
        return (byte) Character.toLowerCase(Character.toUpperCase(c));
    }

    @Override
    public int match(byte[] str) {
        return match(str, 0, str.length);
    }

    @Override
    public int match(byte[] str, int startIndex, int endIndex) {
        int i = 0;
        int limit = Math.min(endIndex - startIndex, chars.length);
        while (i < limit && convert(str[i + startIndex]) == chars[i]) {
            i++;
        }
        return i == chars.length ? chars.length : 0;
    }
}
