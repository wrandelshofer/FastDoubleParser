/*
 * @(#)Strings.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Strings {
    public static StringBuilder repeatStringBuilder(String str, int count) {
        StringBuilder b = new StringBuilder(str.length() * count);
        while (count-- > 0) {
            b.append(str);
        }
        return b;
    }

    public static String repeat(String str, int count) {
        return repeatStringBuilder(str, count).toString();
    }

    public static String repeat(char ch, int count) {
        if (ch < 256) {
            byte[] chars = new byte[count];
            Arrays.fill(chars, (byte) ch);
            return new String(chars, StandardCharsets.ISO_8859_1);
        }
        char[] chars = new char[count];
        Arrays.fill(chars, ch);
        return String.copyValueOf(chars);
    }
}
