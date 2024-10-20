/*
 * @(#)ByteSetOfNone.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;

final class ByteSetOfNone implements ByteSet {

    ByteSetOfNone() {
    }

    public boolean containsKey(byte b) {
        return false;
    }
}
