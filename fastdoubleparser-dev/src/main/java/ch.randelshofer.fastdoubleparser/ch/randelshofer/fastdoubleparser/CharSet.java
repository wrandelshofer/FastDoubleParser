/*
 * @(#)CharSet.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.LinkedHashSet;
import java.util.Set;

interface CharSet {
    boolean contains(char ch);

    static CharSet copyOf(Set<Character> set, boolean ignoreCase) {
        set = applyIgnoreCase(set, ignoreCase);
        switch (set.size()) {
            case 0:
                return new CharSetOfNone();
            case 1:
                return new CharSetOfOne(set);
            default:
                return new CharSetOfFew(set);
        }
    }

    static Set<Character> applyIgnoreCase(Set<Character> set, boolean ignoreCase) {
        if (ignoreCase) {
            LinkedHashSet<Character> convertedSet = new LinkedHashSet<Character>();
            for (Character ch : set) {
                convertedSet.add(ch);
                char lc = Character.toLowerCase(ch);
                char uc = Character.toUpperCase(ch);
                char uclc = Character.toLowerCase(uc);
                convertedSet.add(lc);
                convertedSet.add(uc);
                convertedSet.add(uclc);
            }
            set = convertedSet;
        }
        return set;
    }

}
