/*
 * @(#)ByteSetOfNone.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;

class ByteSetOfNone implements ByteSet {

    ByteSetOfNone() {
    }

    public boolean containsKey(byte ch) {
        return false;
    }
}
