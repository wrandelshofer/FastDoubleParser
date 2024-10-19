/*
 * @(#)CharTrieOfFewIgnoreCase.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;


import java.util.Set;

/**
 * A trie for testing if a String is contained in a set of Strings.
 * <p>
 * This class only works if all characters of the String are in the ASCII range!
 */
class ByteTrieOfFewIgnoreCaseAscii implements ByteTrie {
    private ByteTrieNode root = new ByteTrieNode();

    public ByteTrieOfFewIgnoreCaseAscii(Set<String> set) {
        for (String str : set) {
            if (!str.isEmpty()) {
                add(str);
            }
        }
    }

    private void add(String str) {
        ByteTrieNode node = root;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch > 127) {
                throw new IllegalArgumentException("not an ascii char, char=" + ch);
            }
            node = node.insert((byte) convert((byte) ch));
        }
        node.setEnd();
    }

    private static byte convert(byte c) {
        // We have to convert to upper case and then to lower case
        // because of sophisticated writing systems, like Georgian script.
        return (byte) Character.toLowerCase(Character.toUpperCase((char) c));
    }

    @Override
    public int match(byte[] str, int startIndex, int endIndex) {
        ByteTrieNode node = root;
        int longestMatch = startIndex;
        for (int i = startIndex; i < endIndex; i++) {
            node = node.get(convert(str[i]));
            if (node == null) break;
            longestMatch = node.isEnd() ? i + 1 : longestMatch;
        }
        return longestMatch - startIndex;
    }

}
