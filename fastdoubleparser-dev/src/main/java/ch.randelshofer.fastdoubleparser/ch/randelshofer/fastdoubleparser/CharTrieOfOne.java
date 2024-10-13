/*
 * @(#)CharTrieOfOne.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.Set;

class CharTrieOfOne implements CharTrie {
    private final char[] chars;

    public CharTrieOfOne(Set<String> set) {
        if (set.size() != 1) throw new IllegalArgumentException("set size must be 1, size=" + set.size());
        chars = set.iterator().next().toCharArray();
    }

    public CharTrieOfOne(char[] chars) {
        this.chars = chars;
    }

    @Override
    public int match(CharSequence str) {
        return match(str, 0, str.length());
    }

    @Override
    public int match(CharSequence str, int startIndex, int endIndex) {
        int i = 0;
        int limit = Math.min(endIndex - startIndex, chars.length);
        while (i < limit && str.charAt(i + startIndex) == chars[i]) {
            i++;
        }
        return i == chars.length ? chars.length : 0;
    }

    @Override
    public int match(char[] str) {
        return match(str, 0, str.length);
    }

    @Override
    public int match(char[] str, int startIndex, int endIndex) {
        int i = 0;
        int limit = Math.min(endIndex - startIndex, chars.length);
        while (i < limit && str[i + startIndex] == chars[i]) {
            i++;
        }
        return i == chars.length ? chars.length : 0;
    }
}
