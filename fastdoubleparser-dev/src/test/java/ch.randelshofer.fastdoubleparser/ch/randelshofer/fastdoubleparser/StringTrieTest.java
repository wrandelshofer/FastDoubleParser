/*
 * @(#)StringTrieTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringTrieTest {
    @Test
    public void shouldAddAndRetrieve() {
        StringTrie trie = new StringTrie(Set.of("e", "E", "Exp"));
        assertEquals(0, trie.match("a"));
        assertEquals(1, trie.match("e"));
        assertEquals(1, trie.match("E"));
        assertEquals(3, trie.match("Exp"));
        assertEquals(3, trie.match("Exponent"));
    }
}
