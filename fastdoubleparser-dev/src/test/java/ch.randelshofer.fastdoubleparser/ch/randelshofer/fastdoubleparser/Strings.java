/*
 * @(#)Strings.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

public class Strings {
    public static String repeat(String str, int count) {
        StringBuilder b = new StringBuilder(str.length() * count);
        while (count-- > 0) {
            b.append(str);
        }
        return b.toString();
    }
}
