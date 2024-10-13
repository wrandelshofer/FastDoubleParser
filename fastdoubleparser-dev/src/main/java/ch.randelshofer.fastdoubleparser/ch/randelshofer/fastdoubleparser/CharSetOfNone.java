/*
 * @(#)CharSetOfNone.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

class CharSetOfNone implements CharSet {

    CharSetOfNone() {
    }

    public boolean contains(char ch) {
        return false;
    }
}
