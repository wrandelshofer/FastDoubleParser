/*
 * @(#)ByteTrie.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;

import java.nio.charset.StandardCharsets;
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

    static ByteTrie copyOf(Set<String> set, boolean ignoreCase) {
        switch (set.size()) {
            case 0:
                return new ByteTrieOfNone();
            case 1:
                String str = set.iterator().next();
                if (ignoreCase) {
                    switch (str.length()) {
                        case 0:
                            return new ByteTrieOfNone();
                        case 1:
                            LinkedHashSet<String> newSet = new LinkedHashSet<>();
                            newSet.add(str.toLowerCase());
                            newSet.add(str.toUpperCase());
                            if (newSet.size() == 1) {
                                if (newSet.iterator().next().getBytes(StandardCharsets.UTF_8).length == 1) {
                                    return new ByteTrieOfOneSingleByte(newSet);
                                }
                                return new ByteTrieOfOne(newSet);
                            }
                            return new ByteTrieOfFew(newSet);
                        default:
                            return new ByteTrieOfOneIgnoreCase(set);
                    }
                }
                if (set.iterator().next().getBytes(StandardCharsets.UTF_8).length == 1) {
                    return new ByteTrieOfOneSingleByte(set);
                }
                return new ByteTrieOfOne(set);
            default:
                if (ignoreCase) {
                    if (isAscii(set)) {
                        return new ByteTrieOfFewIgnoreCaseAscii(set);
                    }
                    return new ByteTrieOfFewIgnoreCaseUtf8(set);
                }
                return new ByteTrieOfFew(set);
        }
    }

    static boolean isAscii(Set<String> set) {
        for (String str : set) {
            for (int i = 0, n = str.length(); i < n; i++) {
                if (str.charAt(i) > 127) {
                    return false;
                }
            }
        }
        return true;
    }

    static ByteTrie copyOfChars(Set<Character> set, boolean ignoreCase) {
        Set<String> strSet = new HashSet<>(set.size() * 2);
        if (ignoreCase) {
            for (char ch : set) {
                String string = new String(new char[]{ch});
                strSet.add(string.toLowerCase());
                strSet.add(string.toUpperCase());

            }
            return copyOf(strSet, false);
        }

        for (char ch : set) {
            strSet.add(new String(new char[]{ch}));

        }
        return copyOf(strSet, ignoreCase);
    }
}
