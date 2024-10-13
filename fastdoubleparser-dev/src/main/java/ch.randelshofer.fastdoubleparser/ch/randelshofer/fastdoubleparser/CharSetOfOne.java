/*
 * @(#)CharSetOfOne.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.Set;

public class CharSetOfOne implements CharSet {
    private final char ch;

    public CharSetOfOne(Set<Character> set) {
        if (set.size() != 1) throw new IllegalArgumentException("set size must be 1, size=" + set.size());
        this.ch = set.iterator().next();

    }

    public boolean contains(char ch) {
        return this.ch == ch;
    }
}
