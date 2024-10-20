/*
 * @(#)CharSetOfNone.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

final class CharSetOfNone implements CharSet {

    CharSetOfNone() {
    }

    public boolean containsKey(char ch) {
        return false;
    }
}
