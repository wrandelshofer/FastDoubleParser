/*
 * @(#)CharTrieOfFewIgnoreCaseTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;


import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CharTrieOfFewIgnoreCaseTest {
    @Test
    public void shouldMatchString() {
        Set<String> strings = new LinkedHashSet<>(Arrays.asList("NaN", "інф"));
        CharTrieOfFewIgnoreCase trie = new CharTrieOfFewIgnoreCase(strings);

        assertEquals(0, trie.match(new char[]{'a'}));
        assertEquals(0, trie.match(new char[]{'A'}));

        // NAN, nan, nAn
        assertEquals(3, trie.match(new char[]{'N', 'A', 'N'}));
        assertEquals(3, trie.match(new char[]{'n', 'a', 'n'}));
        assertEquals(3, trie.match(new char[]{'n', 'A', 'n'}));

        // "ІНФ", "інф", "іНф"
        assertEquals(3, trie.match(new char[]{'І', 'Н', 'Ф'}));
        assertEquals(3, trie.match(new char[]{'і', 'н', 'ф'}));
        assertEquals(3, trie.match(new char[]{'і', 'Н', 'ф'}));
    }
}
