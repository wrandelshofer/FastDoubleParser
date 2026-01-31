/*
 * @(#)ByteSet.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Interface for sets of bytes.
 */
public interface ByteSet {
    /**
     * Returns true if the set contains the specified byte.
     *
     * @param b a byte
     * @return true if the byte is in the set
     */
    boolean containsKey(byte b);

    /**
     * Creates a new {@link ByteSet} from the provided set.
     *
     * @param set        a set of characters
     * @param ignoreCase whether the {@link ByteSet} shall ignore the
     *                   case of the characters
     * @return a new {@link ByteSet} instance
     */
    static ByteSet copyOf(Set<Character> set, boolean ignoreCase) {
        set = applyIgnoreCase(set, ignoreCase);
        switch (set.size()) {
            case 0:
                return new ByteSetOfNone();
            case 1:
                return new ByteSetOfOne(set);
            default:
                return set.size() < 5 ? new ByteSetOfFew(set) : new ByteToIntMap(set);
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
