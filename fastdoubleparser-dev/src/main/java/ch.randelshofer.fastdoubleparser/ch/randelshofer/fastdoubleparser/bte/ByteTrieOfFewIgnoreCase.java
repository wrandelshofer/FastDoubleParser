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
 * This trie is a directed acyclic graph. The trie contains UTF-8 encoded characters.
 * <p>
 * <pre>
 *     Given: the strings: "NaN" in latin alphabet,
 *                         "інф" in cyrillic alphabet.
 *
 *     The latin alphabet is encoded with one byte per character.
 *     The cyrillic alphabet is encoded with 2 bytes per character.
 *
 *     "NAN" upper case bytes:  { 0x4e, 0x41, 0x4e }
 *     "nan" lower case bytes:  { 0x6e, 0x61, 0x6e }
 *     "ІНФ" upper case bytes:  { 0xd0, 0x86, 0xd0, 0x9d, 0xd0, 0xa4 }
 *     "інф" lower case bytes:  { 0xd1, 0x96, 0xd0, 0xbd, 0xd1, 0x84 }
 *
 *     The trie will have the following structure:
 *
 *     root [0xd0,    0xd1, 'N'0x4e,'n'0x6e]
 *    ┌───────┘        │        └─────┬─┘
 *    ↓                │              ↓
 *  node ['І'0x86]     ↓            node ['A'0x41,'a'0x61]
 *            │      node ['і'0x96]            └───┬───┘
 *            └─┬─────────────┘                   ↓
 *              ↓                               node ['N'0x4e,'n'0x6e]
 *            node [0xd0]
 *     ┌────────────┴─┐
 *     ↓              ↓
 *  node ['Н'0x9d]   node ['н'0xbd]
 *            └─┬─────────────┘
 *              ↓
 *            node [0xd0, 0xd1]
 *    ┌──────────────┘ ┌───┘
 *    ↓                ↓
 *  node ['Ф'0xa4]   node ['ф'0x84]
 * </pre>
 */
class ByteTrieOfFewIgnoreCase implements ByteTrie {
    private ByteTrieNode root = new ByteTrieNode();

    public ByteTrieOfFewIgnoreCase(Set<String> set) {
        for (String str : set) {
            if (!str.isEmpty()) {
                add(str);
            }
        }
    }

    private void add(String str) {
        // We have to convert to upper case and then to lower case
        // because of sophisticated writing systems, like Georgian script.
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
