/*
 * @(#)ByteTrieOfNone.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;

final class ByteTrieOfNone implements ByteTrie {


    @Override
    public int match(byte[] str) {
        return 0;
    }

    @Override
    public int match(byte[] str, int startIndex, int endIndex) {
        return 0;
    }
}
