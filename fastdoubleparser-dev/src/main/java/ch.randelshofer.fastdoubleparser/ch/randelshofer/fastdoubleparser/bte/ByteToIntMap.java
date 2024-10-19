/*
 * @(#)ByteToIntMap.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;

import java.util.Collection;

/**
 * A primitive map {@literal Map<char,int>}.
 */
class ByteToIntMap implements ByteDigitSet, ByteSet {


    public ByteToIntMap(Collection<Character> chars) {
        this(chars.size());
        int i = 0;
        for (char ch : chars) {
            if (ch > 127) throw new IllegalArgumentException("can not map to a single byte. ch=" + ch);
            put((byte) ch, i++);
        }
    }

    @Override
    public boolean containsKey(byte key) {
        return getOrDefault(key, -1) >= 0;
    }

    @Override
    public int toDigit(byte ch) {
        return getOrDefault(ch, 10);
    }

    private static class Node {
        byte key;
        int value;
        Node next;

        public Node(byte key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node[] table;

    public ByteToIntMap(int maxSize) {
        // int n = BigInteger.valueOf(maxSize*2).nextProbablePrime().intValue();
        int n = (-1 >>> Integer.numberOfLeadingZeros(maxSize * 2)) + 1;
        this.table = new Node[n];
    }

    public void put(byte key, int value) {
        int index = getIndex(key);
        Node found = table[index];
        if (found == null) {
            table[index] = new Node(key, value);
        } else {
            while (found.next != null && found.key != key) {
                found = found.next;
            }
            if (found.key == key) {
                found.value = value;
            } else {
                found.next = new Node(key, value);
            }
        }
    }

    private int getIndex(byte key) {
        return key & (table.length - 1);
    }

    public int getOrDefault(byte key, int defaultValue) {
        int index = getIndex(key);
        Node found = table[index];
        while (found != null) {
            if (found.key == key) return found.value;
            found = found.next;
        }
        return defaultValue;
    }
}
