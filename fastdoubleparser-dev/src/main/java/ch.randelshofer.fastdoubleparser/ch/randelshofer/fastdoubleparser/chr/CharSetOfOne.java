/*
 * @(#)CharSetOfOne.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

import java.util.Set;

class CharSetOfOne implements CharSet {
    private final char ch;

    CharSetOfOne(Set<Character> set) {
        if (set.size() != 1) throw new IllegalArgumentException("set size must be 1, size=" + set.size());
        this.ch = set.iterator().next();

    }

    public boolean containsKey(char ch) {
        return this.ch == ch;
    }
}
