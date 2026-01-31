/*
 * @(#)CharToIntMapTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;


import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class ByteToIntMapTest {
    @Test
    public void shouldFindDigit() {
        List<Character> digits = Arrays.asList('h', 'a', 'm', 'b', 'u', 'r', 'g', 'e', 'f', 'o');
        ByteToIntMap charMap = new ByteToIntMap(digits.size());
        for (int i = 0, digitsSize = digits.size(); i < digitsSize; i++) {
            charMap.put((byte) (char) digits.get(i), i);
        }

        for (int i = 0, digitsSize = digits.size(); i < digitsSize; i++) {
            byte d = (byte) (char) digits.get(i);
            int actual = charMap.getOrDefault(d, 10);
            assertEquals(i, actual, "for d=" + d);
        }
    }
}
