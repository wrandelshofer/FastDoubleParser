/*
 * @(#)CharTrie.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.Set;

/**
 * A trie for testing if a String is contained in a set of Strings.
 */
class CharTrie {
    private TrieNode root = new TrieNode();

    public CharTrie(Set<String> set) {
        for (String str : set) {
            if (!str.isEmpty()) {
                add(str);
            }
        }
    }

    private void add(String str) {
        TrieNode node = root;
        for (int i = 0; i < str.length(); i++) {
            node = node.insert(str.charAt(i));
        }
        node.setEnd();
    }

    /**
     * Searches for the longest matching string in the trie
     * that matches the provided string.
     *
     * @param str a string
     * @return the length of the longest matching string, or 0 if no string matches
     */
    public int matchBranchless(CharSequence str) {
        return matchBranchless(str, 0, str.length());
    }

    /**
     * Searches for the longest matching string in the trie
     * that matches the provided string.
     *
     * @param str a string
     * @return the length of the longest matching string, or 0 if no string matches
     */
    public int matchBranchless(CharSequence str, int startIndex, int endIndex) {
        TrieNode node = root;
        int longestMatch = startIndex;
        for (int i = startIndex; i < endIndex; i++) {
            node = node.get(str.charAt(i));
            longestMatch = node.isEnd() ? i + 1 : longestMatch;
        }
        return longestMatch - startIndex;
    }

    /**
     * Searches for the longest matching string in the trie
     * that matches the provided string.
     *
     * @param str a string
     * @return the length of the longest matching string, or 0 if no string matches
     */
    public int match(CharSequence str) {
        return match(str, 0, str.length());
    }

    /**
     * Searches for the longest matching string in the trie
     * that matches the provided string.
     *
     * @param str        a string
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     * @return the length of the longest matching string, or 0 if no string matches
     */
    public int match(CharSequence str, int startIndex, int endIndex) {
        TrieNode node = root;
        int longestMatch = startIndex;
        for (int i = startIndex; i < endIndex; i++) {
            node = node.get(str.charAt(i));
            if (node == TrieNode.SENTINEL) {
                break;
            }
            if (node.isEnd()) {
                longestMatch = i + 1;
            }
        }
        return longestMatch - startIndex;
    }


}
