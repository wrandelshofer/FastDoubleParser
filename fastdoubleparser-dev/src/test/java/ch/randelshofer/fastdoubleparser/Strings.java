/*
 * @(#)Strings.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.Arrays;

public class Strings {
    public static String repeat(String str, int count) {
        StringBuilder b = new StringBuilder(str.length() * count);
        while (count-- > 0) {
            b.append(str);
        }
        return b.toString();
    }

    public static String repeat(char c, int count) {
        char[] chars = new char[count];
        Arrays.fill(chars, c);
        return new String(chars);
    }
}
