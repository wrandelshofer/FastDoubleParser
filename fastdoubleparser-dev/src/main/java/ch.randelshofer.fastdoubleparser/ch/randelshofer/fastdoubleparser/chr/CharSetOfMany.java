/*
 * @(#)CharSetOfMany.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

import java.util.Set;

/**
 * A set for {@code char} with linear search.
 */
class CharSetOfMany implements CharSet {
    private final Set<Character> set;

    public CharSetOfMany(Set<Character> set) {
        this.set = set;
    }

    public boolean containsKey(char ch) {
        return set.contains(ch);
    }
}
