/*
 * @(#)ByteTrie.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

import java.util.Set;

/**
 * Interface for a data retrieval tree (trie) of characters.
 */
public interface CharTrie {
    /**
     * Searches for the longest matching string in the trie
     * that matches the provided string.
     *
     * @param str a string in the form of a {@link CharSequence}
     * @return the length of the longest matching string, or 0 if no string matches
     */
    default int match(CharSequence str) {
        return match(str, 0, str.length());
    }

    /**
     * Searches for the longest matching string in the trie
     * that matches the provided string.
     *
     * @param str a string in the form of a char array
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


    /**
     * Creates a new {@link CharTrie} from the provided set.
     *
     * @param set        a set of strings
     * @param ignoreCase whether the {@link CharTrie} shall ignore the
     *                   case of the characters
     * @return a new {@link CharTrie} instance
     */
    static CharTrie copyOf(Set<String> set, boolean ignoreCase) {
        switch (set.size()) {
            case 0:
                return new CharTrieOfNone();
            case 1:
                if (set.iterator().next().length() == 1) {
                    return ignoreCase ? new CharTrieOfFewIgnoreCase(set) : new CharTrieOfOneSingleChar(set);
                }
                return ignoreCase ? new CharTrieOfFewIgnoreCase(set) : new CharTrieOfOne(set);
            default:
                return ignoreCase ? new CharTrieOfFewIgnoreCase(set) : new CharTrieOfFew(set);
        }
    }
}
