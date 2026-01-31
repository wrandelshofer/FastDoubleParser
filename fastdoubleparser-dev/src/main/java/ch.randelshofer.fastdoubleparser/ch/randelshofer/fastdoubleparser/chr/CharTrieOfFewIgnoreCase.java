/*
 * @(#)CharTrieOfFewIgnoreCase.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

import java.util.Set;

/**
 * A trie for testing if a String is contained in a set of Strings.
 * <p>
 * This trie is a directed acyclic graph.
 * <p>
 * <pre>
 *     Given: the strings: "NaN", "Inf"
 *     The trie will have the following structure:
 *
 *     root ['N','n', 'I','i']
 *            │   │    │   │
 *            │   │    └─┬─┘
 *            │   │      └─→ node ['N','n']
 *            │   │                 │   │
 *            │   │                 └─┬─┘
 *            └─┬─┘                   └─→ node ['F','f']
 *              └─→ node ['A','a']
 *                         │   │
 *                         └─┬─┘
 *                           └─→ node ['N','N']
 * </pre>
 */
final class CharTrieOfFewIgnoreCase implements CharTrie {
    private CharTrieNode root = new CharTrieNode();

    public CharTrieOfFewIgnoreCase(Set<String> set) {
        for (String str : set) {
            if (!str.isEmpty()) {
                add(str);
            }
        }
    }

    private void add(String str) {
        // We have to convert to upper case and then to lower case
        // because of sophisticated writing systems, like Georgian script.
        CharTrieNode upperNode = root;
        CharTrieNode lowerNode = root;
        String upperStr = str.toUpperCase();
        String lowerStr = upperStr.toLowerCase();
        for (int i = 0; i < str.length(); i++) {
            char upper = upperStr.charAt(i);
            char lower = lowerStr.charAt(i);
            upperNode = upperNode.insert(upper);
            lowerNode = lowerNode.insert(lower, upperNode);

        }
        upperNode.setEnd();
    }


    @Override
    public int match(char[] str, int startIndex, int endIndex) {
        CharTrieNode node = root;
        int longestMatch = startIndex;
        for (int i = startIndex; i < endIndex; i++) {
            node = node.get(str[i]);
            if (node == null) break;
            longestMatch = node.isEnd() ? i + 1 : longestMatch;
        }
        return longestMatch - startIndex;
    }

    @Override
    public int match(CharSequence str, int startIndex, int endIndex) {
        CharTrieNode node = root;
        int longestMatch = startIndex;
        for (int i = startIndex; i < endIndex; i++) {
            node = node.get(str.charAt(i));
            if (node == null) break;
            longestMatch = node.isEnd() ? i + 1 : longestMatch;
        }
        return longestMatch - startIndex;
    }

}
