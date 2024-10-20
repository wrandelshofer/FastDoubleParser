/*
 * @(#)CharToIntMapTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CharToIntMapTest {
    @Test
    public void shouldFindDigit() {
        List<Character> digits = Arrays.asList('〇', '一', '二', '三', '四', '五', '六', '七', '八', '九');
        CharToIntMap charMap = new CharToIntMap(digits.size());
        for (int i = 0, digitsSize = digits.size(); i < digitsSize; i++) {
            charMap.put(digits.get(i), i);
        }

        for (int i = 0, digitsSize = digits.size(); i < digitsSize; i++) {
            char d = digits.get(i);
            int actual = charMap.getOrDefault(d, 10);
            assertEquals(i, actual, "for d=" + d);
        }
    }
}
