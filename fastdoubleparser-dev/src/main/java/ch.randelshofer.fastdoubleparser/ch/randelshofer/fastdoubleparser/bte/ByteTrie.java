/*
 * @(#)ByteTrie.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public interface ByteTrie {


    /**
     * Searches for the longest matching string in the trie
     * that matches the provided string.
     *
     * @param str a string
     * @return the length of the longest matching string, or 0 if no string matches
     */
    default int match(byte[] str) {
        return match(str, 0, str.length);
    }


    /**
     * Searches for the longest matching string in the trie
     * that matches the provided string.
     *
     * @param str        a string
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     * @return the length of the longest matching string, or 0 if no string matches
     */
    int match(byte[] str, int startIndex, int endIndex);

    public static ByteTrie copyOf(Set<String> set, boolean ignoreCase) {
        switch (set.size()) {
            case 0:
                return new ByteTrieOfNone();
            case 1:
                if (ignoreCase) {
                    String str = set.iterator().next();
                    switch (str.length()) {
                        case 0:
                            return new ByteTrieOfNone();
                        case 1:
                            LinkedHashSet<String> newSet = new LinkedHashSet<>();
                            newSet.add(str.toLowerCase());
                            newSet.add(str.toUpperCase());
                            return newSet.size() == 1 ? new ByteTrieOfOne(newSet) : new ByteTrieOfFew(newSet);
                        default:
                            return new ByteTrieOfFewIgnoreCase(set);
                    }
                }
                return new ByteTrieOfOne(set);
            default:
                return ignoreCase ? new ByteTrieOfFewIgnoreCase(set) : new ByteTrieOfFew(set);
        }
    }

    public static ByteTrie copyOfChars(Set<Character> set, boolean ignoreCase) {
        Set<String> strSet = new HashSet<>(set.size() * 2);
        for (char ch : set) {
            strSet.add(new String(new char[]{ch}));
        }
        return copyOf(strSet, ignoreCase);
    }
}
