/*
 * @(#)CharTrieTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.chr;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CharTrieTest {
    @Test
    public void shouldAddAndRetrieve() {
        CharTrieOfFew trie = new CharTrieOfFew(new HashSet<>(Arrays.asList("e", "E", "Exp")));
        assertEquals(0, trie.match("a"));
        assertEquals(1, trie.match("e"));
        assertEquals(1, trie.match("E"));
        assertEquals(3, trie.match("Exp"));
        assertEquals(3, trie.match("Exponent"));
    }
}
