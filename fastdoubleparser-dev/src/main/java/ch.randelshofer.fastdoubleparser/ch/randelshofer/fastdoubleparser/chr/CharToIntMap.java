/*
 * @(#)CharToIntMap.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

import java.util.Collection;

/**
 * A primitive map {@literal Map<char,int>}.
 */
class CharToIntMap implements CharDigitSet, CharSet {


    public CharToIntMap(Collection<Character> chars) {
        this(chars.size());
        int i = 0;
        for (char ch : chars) {
            put(ch, i++);
        }
    }

    @Override
    public boolean containsKey(char key) {
        return getOrDefault(key, -1) >= 0;
    }

    @Override
    public int toDigit(char ch) {
        return getOrDefault(ch, 10);
    }

    private static class Node {
        char key;
        int value;
        Node next;

        public Node(char key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node[] table;

    public CharToIntMap(int maxSize) {
        // int n = BigInteger.valueOf(maxSize*2).nextProbablePrime().intValue();
        int n = (-1 >>> Integer.numberOfLeadingZeros(maxSize * 2)) + 1;
        this.table = new Node[n];
    }

    public void put(char key, int value) {
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

    private int getIndex(char key) {
        return key & (table.length - 1);
    }

    public int getOrDefault(char key, int defaultValue) {
        int index = getIndex(key);
        Node found = table[index];
        while (found != null) {
            if (found.key == key) return found.value;
            found = found.next;
        }
        return defaultValue;
    }
}
