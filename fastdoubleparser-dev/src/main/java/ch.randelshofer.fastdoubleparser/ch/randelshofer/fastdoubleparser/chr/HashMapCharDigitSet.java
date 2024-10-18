/*
 * @(#)HashMapCharDigitSet.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class HashMapCharDigitSet implements CharDigitSet {
    private final Map<Character, Integer> map;

    public HashMapCharDigitSet(List<Character> digits) {
        this.map = new HashMap<>(20);
        for (int i = 0; i < 10; i++) {
            map.put(digits.get(i), i);
        }
    }

    @Override
    public int toDigit(char ch) {
        Integer i = map.get(ch);
        return i == null ? 10 : i;
    }
}
