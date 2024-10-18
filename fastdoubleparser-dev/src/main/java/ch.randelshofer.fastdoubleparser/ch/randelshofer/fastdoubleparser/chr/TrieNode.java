/*
 * @(#)TrieNode.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

import java.util.Arrays;

class TrieNode {
    private char[] chars = new char[0];
    private TrieNode[] children = new TrieNode[0];
    private boolean isEnd;

    public final static TrieNode SENTINEL = new TrieNode();

    public TrieNode() {
    }

    /**
     * Insert a character into this node if it does not already exist.
     * Returns the child node corresponding to the char.
     *
     * @param ch the character
     * @return the child node corresponding to the char
     */
    public TrieNode insert(char ch) {
        int index = indexOf(ch);
        if (index < 0) {
            index = chars.length;
            chars = Arrays.copyOf(chars, chars.length + 1);
            children = Arrays.copyOf(children, children.length + 1);
            chars[index] = ch;
            children[index] = new TrieNode();
        }
        return children[index];
    }

    /**
     * Gets the child not for the given character, if it exists.
     *
     * @param ch the character
     * @return the child node corresponding to the char, or the sentinel node
     */
    public TrieNode get(char ch) {
        int index = indexOf(ch);
        return index < 0 ? SENTINEL : children[index];
    }

    /**
     * Returns the index of the specified character in this node.
     *
     * @param ch the character
     * @return the index or -1
     */
    private int indexOf(char ch) {
        // intentionally 'branchless' loop
        int index = -1;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ch) index = i;
        }
        return index;
    }

    public void setEnd() {
        isEnd = true;
    }

    public boolean isEnd() {
        return isEnd;
    }

}
