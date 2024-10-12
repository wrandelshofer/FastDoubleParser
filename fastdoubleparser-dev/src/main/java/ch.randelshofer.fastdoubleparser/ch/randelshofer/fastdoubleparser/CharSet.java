/*
 * @(#)CharSet.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.LinkedHashSet;
import java.util.Set;

public interface CharSet {
    boolean contains(char ch);

    public static CharSet copyOf(Set<Character> set) {
        return switch (set.size()) {
            case 0 -> new CharSetOfNone();
            case 1 -> new CharSetOfOne(set);
            default -> new CharSetOfFew(set);
        };
    }

    public static CharSet copyOfFirstChar(Set<String> strSet) {
        var set = new LinkedHashSet<Character>();
        for (var str : strSet) {
            set.add(str.charAt(0));
        }
        return copyOf(set);
    }
}
