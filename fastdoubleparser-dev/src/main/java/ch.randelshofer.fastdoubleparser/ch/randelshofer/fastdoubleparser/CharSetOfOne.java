/*
 * @(#)CharSetOfOne.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.Set;

public class CharSetOfOne implements CharSet {
    private final char ch;

    public CharSetOfOne(Set<?> set) {
        char c = 0;
        for (Object s : set) {
            c = s.toString().charAt(0);
        }
        this.ch = c;
    }

    public boolean contains(char ch) {
        return this.ch == ch;
    }
}
