/*
 * @(#)CharToIntMap.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

import java.util.Collection;

/**
 * A primitive map {@literal Map<char,int>}.
 */
final class CharToIntMap implements CharDigitSet, CharSet {

    private final char zeroChar;
    private final Node[] table;

    public CharToIntMap(Collection<Character> chars) {
        int n = (-1 >>> Integer.numberOfLeadingZeros(chars.size() * 2)) + 1;
        this.table = new Node[n];
        int i = 0;
        for (char ch : chars) {
            put(ch, i++);
        }
        zeroChar = chars.iterator().next();
    }


    @Override
    public boolean containsKey(char key) {
        return getOrDefault(key, -1) >= 0;
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

    @Override
    public char getZeroChar() {
        return zeroChar;
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
}
