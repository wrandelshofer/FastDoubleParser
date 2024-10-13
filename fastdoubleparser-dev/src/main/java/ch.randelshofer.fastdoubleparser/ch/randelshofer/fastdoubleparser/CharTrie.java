/*
 * @(#)CharTrie.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

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
     * @return the length of the longest matching string, or 0 if no string matches
     */
    int match(CharSequence str, int startIndex, int endIndex);

    /**
     * Searches for the longest matching string in the trie
     * that matches the provided string.
     *
     * @param str a string
     * @return the length of the longest matching string, or 0 if no string matches
     */
    int match(char[] str, int startIndex, int endIndex);

    public static CharTrie of(Set<String> set) {
        switch (set.size()) {
            case 0:
                return new CharTrieOfNone();
            case 1:
                return new CharTrieOfOne(set);
            default:
                return new CharTrieOfMany(set);
        }
    }
}
