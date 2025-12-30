/*
 * @(#)CharSetOfNone.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

public final class CharSetOfNone implements CharSet {

    public CharSetOfNone() {
    }

    public boolean containsKey(char ch) {
        return false;
    }
}
