/*
 * @(#)ByteTrieOfFew.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;

import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * A trie for testing if a String is contained in a set of Strings.
 */
final class ByteTrieOfFew implements ByteTrie {
    private ByteTrieNode root = new ByteTrieNode();

    public ByteTrieOfFew(Set<String> set) {
        for (String str : set) {
            if (!str.isEmpty()) {
                add(str);
            }
        }
    }

    private void add(String str) {
        ByteTrieNode node = root;
        byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < strBytes.length; i++) {
            node = node.insert(strBytes[i]);
        }
        node.setEnd();
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
