/*
 * @(#)CharTrieOfOneIgnoreCase.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

import java.util.Set;

class CharTrieOfOneIgnoreCase implements CharTrie {
    private final char[] chars;

    public CharTrieOfOneIgnoreCase(Set<String> set) {
        this(set.iterator().next().toCharArray());
        if (set.size() != 1) throw new IllegalArgumentException("set size must be 1, size=" + set.size());
    }

    public CharTrieOfOneIgnoreCase(char[] chars) {
        this.chars = chars;
        for (int i = 0; i < chars.length; i++) {
            chars[i] = convert(chars[i]);
        }
    }

    @Override
    public int match(CharSequence str) {
        return match(str, 0, str.length());
    }

    @Override
    public int match(CharSequence str, int startIndex, int endIndex) {
        int i = 0;
        int limit = Math.min(endIndex - startIndex, chars.length);
        while (i < limit && convert(str.charAt(i + startIndex)) == chars[i]) {
            i++;
        }
        return i == chars.length ? chars.length : 0;
    }

    private static char convert(char c) {
        // We have to convert to upper case and then to lower case
        // because of sophisticated writing systems, like Georgian script.
        return Character.toLowerCase(Character.toUpperCase(c));
    }

    @Override
    public int match(char[] str) {
        return match(str, 0, str.length);
    }

    @Override
    public int match(char[] str, int startIndex, int endIndex) {
        int i = 0;
        int limit = Math.min(endIndex - startIndex, chars.length);
        while (i < limit && convert(str[i + startIndex]) == chars[i]) {
            i++;
        }
        return i == chars.length ? chars.length : 0;
    }
}
