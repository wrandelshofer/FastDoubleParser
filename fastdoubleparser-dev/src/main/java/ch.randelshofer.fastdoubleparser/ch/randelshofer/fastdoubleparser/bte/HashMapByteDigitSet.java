/*
 * @(#)HashMapByteDigitSet.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class HashMapByteDigitSet implements ByteDigitSet {
    private final Map<Byte, Integer> map;

    public HashMapByteDigitSet(List<Character> digits) {
        this.map = new HashMap<>(20);
        for (int i = 0; i < 10; i++) {
            char ch = digits.get(i);
            if (ch > 127) throw new IllegalArgumentException("can not map to a single byte. ch=" + ch);
            map.put((byte) ch, i);
        }
    }

    @Override
    public int toDigit(byte ch) {
        Integer i = map.get(ch);
        return i == null ? 10 : i;
    }
}
