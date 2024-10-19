/*
 * @(#)CharTrieOfMany.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

import java.util.Set;

/**
 * A trie for testing if a String is contained in a set of Strings.
 */
class CharTrieOfMany implements CharTrie {
    private CharTrieNode root = new CharTrieNode();

    public CharTrieOfMany(Set<String> set) {
        for (String str : set) {
            if (!str.isEmpty()) {
                add(str);
            }
        }
    }

    private void add(String str) {
        CharTrieNode node = root;
        for (int i = 0; i < str.length(); i++) {
            node = node.insert(str.charAt(i));
        }
        node.setEnd();
    }


    @Override
    public int match(CharSequence str, int startIndex, int endIndex) {
        CharTrieNode node = root;
        int longestMatch = startIndex;
        for (int i = startIndex; i < endIndex; i++) {
            node = node.get(str.charAt(i));
            longestMatch = node.isEnd() ? i + 1 : longestMatch;
        }
        return longestMatch - startIndex;
    }

    @Override
    public int match(char[] str, int startIndex, int endIndex) {
        CharTrieNode node = root;
        int longestMatch = startIndex;
        for (int i = startIndex; i < endIndex; i++) {
            node = node.get(str[i]);
            longestMatch = node.isEnd() ? i + 1 : longestMatch;
        }
        return longestMatch - startIndex;
    }
}
