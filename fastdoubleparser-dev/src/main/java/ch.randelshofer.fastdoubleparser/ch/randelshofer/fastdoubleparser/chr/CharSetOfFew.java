/*
 * @(#)CharSetOfFew.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

import java.util.Set;

/**
 * A set of {@code char} with linear search.
 */
final class CharSetOfFew implements CharSet {
    private final char[] chars;

    public CharSetOfFew(Set<Character> set) {
        this.chars = new char[set.size()];
        int i = 0;
        for (Character ch : set) {
            chars[i++] = ch;
        }
    }

    public boolean containsKey(char ch) {
        boolean found = false;
        for (char aChar : chars) {
            found |= aChar == ch;
        }
        return found;
    }
}
