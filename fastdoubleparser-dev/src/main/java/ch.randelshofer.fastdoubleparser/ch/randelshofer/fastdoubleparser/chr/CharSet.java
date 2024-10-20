/*
 * @(#)ByteSet.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

import ch.randelshofer.fastdoubleparser.bte.ByteSet;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Interface for sets of characters.
 */
public interface CharSet {
    /**
     * Returns true if the set contains the specified character.
     *
     * @param ch a character
     * @return true if the byte is in the set
     */
    boolean containsKey(char ch);


    /**
     * Creates a new {@link CharSet} from the provided set.
     *
     * @param set        a set of characters
     * @param ignoreCase whether the {@link CharSet} shall ignore the
     *                   case of the characters
     * @return a new {@link ByteSet} instance
     */
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

    /**
     * Creates a copy of the provided set, or returns the same set.
     * <p>
     * If {@code ignoreCase} is set to true, the copy will contain
     * an upper and lower case character for each character in the provided
     * set.
     *
     * @param set a set of characters
     * @param ignoreCase whether the copy of the set shall contain
     *                   upper and lower case characters from the
     *                   provided set
     * @return a new set if {@code ignoreCase} is false, otherwise a copy of the set
     */
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
