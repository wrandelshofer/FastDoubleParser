/*
 * @(#)CharTrieOfFewIgnoreCase.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

import java.util.Set;

/**
 * A trie for testing if a String is contained in a set of Strings.
 */
class CharTrieOfFewIgnoreCase implements CharTrie {
    private CharTrieNode root = new CharTrieNode();

    public CharTrieOfFewIgnoreCase(Set<String> set) {
        for (String str : set) {
            if (!str.isEmpty()) {
                add(str);
            }
        }
    }

    private void add(String str) {
        CharTrieNode node = root;
        for (int i = 0; i < str.length(); i++) {
            node = node.insert(convert(str.charAt(i)));
        }
        node.setEnd();
    }

    private static char convert(char c) {
        // We have to convert to upper case and then to lower case
        // because of sophisticated writing systems, like Georgian script.
        return Character.toLowerCase(Character.toUpperCase(c));
    }

    @Override
    public int match(CharSequence str, int startIndex, int endIndex) {
        CharTrieNode node = root;
        int longestMatch = startIndex;
        for (int i = startIndex; i < endIndex; i++) {
            node = node.get(convert(str.charAt(i)));
            if (node == null) break;
            longestMatch = node.isEnd() ? i + 1 : longestMatch;
        }
        return longestMatch - startIndex;
    }

    @Override
    public int match(char[] str, int startIndex, int endIndex) {
        CharTrieNode node = root;
        int longestMatch = startIndex;
        for (int i = startIndex; i < endIndex; i++) {
            node = node.get(convert(str[i]));
            if (node == null) break;
            longestMatch = node.isEnd() ? i + 1 : longestMatch;
        }
        return longestMatch - startIndex;
    }

}
