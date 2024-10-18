/*
 * @(#)CharSet.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

import java.util.LinkedHashSet;
import java.util.Set;

public interface CharSet {
    boolean containsKey(char ch);

    static CharSet copyOf(Set<Character> set, boolean ignoreCase) {
        set = applyIgnoreCase(set, ignoreCase);
        switch (set.size()) {
            case 0:
                return new CharSetOfNone();
            case 1:
                return new CharSetOfOne(set);
            default:
                return set.size() < 5 ? new CharSetOfFew(set) : new CharToIntMap(set);
        }
    }

    static Set<Character> applyIgnoreCase(Set<Character> set, boolean ignoreCase) {
        if (ignoreCase) {
            LinkedHashSet<Character> convertedSet = new LinkedHashSet<Character>();
            for (Character ch : set) {
                // Add the original input char.
                convertedSet.add(ch);

                // Convert to lower case. This does not cover all cases.
                char lc = Character.toLowerCase(ch);

                // We have to convert to upper case and then to lower case
                // because of sophisticated writing systems, like Georgian script.
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
