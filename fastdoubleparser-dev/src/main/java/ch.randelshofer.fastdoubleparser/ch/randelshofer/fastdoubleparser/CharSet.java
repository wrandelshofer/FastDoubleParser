/*
 * @(#)CharSet.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.LinkedHashSet;
import java.util.Set;

interface CharSet {
    boolean contains(char ch);

    public static CharSet copyOf(Set<Character> set) {
        switch (set.size()) {
            case 0:
                return new CharSetOfNone();
            case 1:
                return new CharSetOfOne(set);
            default:
                return new CharSetOfFew(set);
        }
    }

    public static CharSet copyOfFirstChar(Set<String> strSet) {
        LinkedHashSet<Character> set = new LinkedHashSet<Character>();
        for (String str : strSet) {
            set.add(str.charAt(0));
        }
        return copyOf(set);
    }
}
