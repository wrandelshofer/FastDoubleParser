/*
 * @(#)ByteTrie.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

import java.util.Set;

public interface CharTrie {
    /**
     * Searches for the longest matching string in the trie
     * that matches the provided string.
     *
     * @param str a string
     * @return the length of the longest matching string, or 0 if no string matches
     */
    default int match(CharSequence str) {
        return match(str, 0, str.length());
    }

    /**
     * Searches for the longest matching string in the trie
     * that matches the provided string.
     *
     * @param str a string
     * @return the length of the longest matching string, or 0 if no string matches
     */
    default int match(char[] str) {
        return match(str, 0, str.length);
    }


    /**
     * Searches for the longest matching string in the trie
     * that matches the provided string.
     *
     * @param str a string
     * @param startIndex start index (inclusive)
     * @param endIndex end index (exclusive)
     * @return the length of the longest matching string, or 0 if no string matches
     */
    int match(CharSequence str, int startIndex, int endIndex);

    /**
     * Searches for the longest matching string in the trie
     * that matches the provided string.
     *
     * @param str a string
     * @param startIndex start index (inclusive)
     * @param endIndex end index (exclusive)
     * @return the length of the longest matching string, or 0 if no string matches
     */
    int match(char[] str, int startIndex, int endIndex);

    public static CharTrie of(Set<String> set, boolean ignoreCase) {
        switch (set.size()) {
            case 0:
                return new CharTrieOfNone();
            case 1:
                return ignoreCase ? new CharTrieOfOneIgnoreCase(set) : new CharTrieOfOne(set);
            default:
                return ignoreCase ? new CharTrieOfManyIgnoreCase(set) : new CharTrieOfMany(set);
        }
    }
}
