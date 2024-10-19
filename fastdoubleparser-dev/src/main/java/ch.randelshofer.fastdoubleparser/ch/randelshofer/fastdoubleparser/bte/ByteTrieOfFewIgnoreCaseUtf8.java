/*
 * @(#)CharTrieOfFewIgnoreCase.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;


import java.nio.charset.StandardCharsets;
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
 *            │   │    ┕─┬─┘
 *            │   │      └─→ node ['N','n']
 *            │   │                 │   │
 *            │   │                 ┕─┬─┘
 *            ┕─┬─┘                   └─→ node ['F','f']
 *              └─→ node ['A','a']
 *                         │   │
 *                         ┕─┬─┘
 *                           └─→ node ['N','N']
 * </pre>
 */
class ByteTrieOfFewIgnoreCaseUtf8 implements ByteTrie {
    private ByteTrieNode root = new ByteTrieNode();

    public ByteTrieOfFewIgnoreCaseUtf8(Set<String> set) {
        for (String str : set) {
            if (!str.isEmpty()) {
                add(str);
            }
        }
    }

    private void add(String str) {
        ByteTrieNode upperNode = root;
        ByteTrieNode lowerNode = root;
        String upperStr = str.toUpperCase();
        String lowerStr = upperStr.toLowerCase();
        for (int i = 0; i < str.length(); i++) {
            byte[] upper = upperStr.substring(i, i + 1).getBytes(StandardCharsets.UTF_8);
            byte[] lower = lowerStr.substring(i, i + 1).getBytes(StandardCharsets.UTF_8);
            for (int u = 0; u < upper.length; u++) {
                upperNode = upperNode.insert(upper[u]);
            }
            for (int l = 0; l < upper.length - 1; l++) {
                lowerNode = lowerNode.insert(lower[l]);
            }
            lowerNode = lowerNode.insert(lower[lower.length - 1], upperNode);

        }
        upperNode.setEnd();
    }


    @Override
    public int match(byte[] str, int startIndex, int endIndex) {
        ByteTrieNode node = root;
        int longestMatch = startIndex;
        for (int i = startIndex; i < endIndex; i++) {
            node = node.get(str[i]);
            if (node == null) break;
            longestMatch = node.isEnd() ? i + 1 : longestMatch;
        }
        return longestMatch - startIndex;
    }

}
