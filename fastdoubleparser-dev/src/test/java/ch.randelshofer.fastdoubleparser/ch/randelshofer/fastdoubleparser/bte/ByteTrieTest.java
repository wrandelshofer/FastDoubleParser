/*
 * @(#)CharTrieTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ByteTrieTest {
    @Test
    public void shouldAddAndRetrieve() {
        ByteTrieOfFew trie = new ByteTrieOfFew(new HashSet<>(Arrays.asList("e", "E", "Exp")));
        assertEquals(0, trie.match(new byte[]{'a'}));
        assertEquals(1, trie.match(new byte[]{'e'}));
        assertEquals(1, trie.match(new byte[]{'E'}));
        assertEquals(3, trie.match(new byte[]{'E', 'x', 'p'}));
        assertEquals(3, trie.match(new byte[]{'E', 'x', 'p', 'o', 'n', 'e', 'n', 't'}));
    }
}
