/*
 * @(#)CharSetOfFew.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.Set;

/**
 * A set for {@code char} with linear search.
 */
class CharSetOfFew implements CharSet {
    private final char[] chars;

    public CharSetOfFew(Set<Character> set) {
        this.chars = new char[set.size()];
        int i = 0;
        for (var ch : set) {
            chars[i++] = ch;
        }
    }

    public boolean contains(char ch) {
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            found |= chars[i] == ch;
        }
        return found;
    }
}
