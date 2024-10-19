/*
 * @(#)ByteTrieOfManyIgnoreCase.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;

import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * A trie for testing if a String is contained in a set of Strings.
 */
class ByteTrieOfManyIgnoreCase implements ByteTrie {
    private ByteTrieNode root = new ByteTrieNode();

    public ByteTrieOfManyIgnoreCase(Set<String> set) {
        for (String str : set) {
            if (!str.isEmpty()) {
                add(str);
            }
        }
    }

    private void add(String str) {
        ByteTrieNode node = root;
        byte[] strBytes = convert(str).getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < strBytes.length; i++) {
            node = node.insert(strBytes[i]);
        }
        node.setEnd();
    }

    private static byte convert(byte c) {
        // We have to convert to upper case and then to lower case
        // because of sophisticated writing systems, like Georgian script.
        return (byte) Character.toLowerCase(Character.toUpperCase(c));
    }

    private static String convert(String str) {
        // We have to convert to upper case and then to lower case
        // because of sophisticated writing systems, like Georgian script.
        return str.toUpperCase().toLowerCase();
    }


    @Override
    public int match(byte[] str, int startIndex, int endIndex) {
        ByteTrieNode node = root;
        int longestMatch = startIndex;
        for (int i = startIndex; i < endIndex; i++) {
            node = node.get(convert(str[i]));
            longestMatch = node.isEnd() ? i + 1 : longestMatch;
        }
        return longestMatch - startIndex;
    }
}
