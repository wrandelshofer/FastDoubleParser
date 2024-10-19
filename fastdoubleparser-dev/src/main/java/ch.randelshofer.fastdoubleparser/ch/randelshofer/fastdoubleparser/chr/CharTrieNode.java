/*
 * @(#)CharTrieNode.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

import java.util.Arrays;

class CharTrieNode {
    private char[] chars = new char[0];
    private CharTrieNode[] children = new CharTrieNode[0];
    private boolean isEnd;



    public CharTrieNode() {
    }

    /**
     * Insert a character into this node if it does not already exist.
     * Returns the child node corresponding to the char.
     *
     * @param ch the character
     * @return the child node corresponding to the char
     */
    public CharTrieNode insert(char ch) {
        int index = indexOf(ch);
        if (index < 0) {
            index = chars.length;
            chars = Arrays.copyOf(chars, chars.length + 1);
            children = Arrays.copyOf(children, children.length + 1);
            chars[index] = ch;
            children[index] = new CharTrieNode();
        }
        return children[index];
    }

    /**
     * Gets the child not for the given character, if it exists.
     *
     * @param ch the character
     * @return the child node corresponding to the char, or the sentinel node
     */
    public CharTrieNode get(char ch) {
        int index = indexOf(ch);
        return index < 0 ? null : children[index];
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
