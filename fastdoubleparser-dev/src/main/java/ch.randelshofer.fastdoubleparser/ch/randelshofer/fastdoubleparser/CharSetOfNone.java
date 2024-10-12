/*
 * @(#)CharSetOfOne.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.Set;

public class CharSetOfNone implements CharSet {

    public CharSetOfNone() {
    }

    public boolean contains(char ch) {
        return false;
    }
}
