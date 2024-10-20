/*
 * @(#)CharToIntMapTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser.bte;


import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class ByteTrieOfFewIgnoreCaseTest {
    @Test
    public void shouldMatchString() {
        Set<String> strings = new LinkedHashSet<>(Arrays.asList("NaN", "інф"));
        ByteTrieOfFewIgnoreCase trie = new ByteTrieOfFewIgnoreCase(strings);

        assertEquals(0, trie.match(new byte[]{'a'}));
        assertEquals(0, trie.match(new byte[]{'A'}));

        // NAN, nan, nAn
        assertEquals(3, trie.match(new byte[]{0x4e, 0x41, 0x4e}));
        assertEquals(3, trie.match(new byte[]{0x6e, 0x61, 0x6e}));
        assertEquals(3, trie.match(new byte[]{0x6e, 0x41, 0x6e}));

        // "ІНФ", "інф", "іНф"
        assertEquals(6, trie.match(new byte[]{(byte) 0xd0, (byte) 0x86, (byte) 0xd0, (byte) 0x9d, (byte) 0xd0, (byte) 0xa4}));
        assertEquals(6, trie.match(new byte[]{(byte) 0xd1, (byte) 0x96, (byte) 0xd0, (byte) 0xbd, (byte) 0xd1, (byte) 0x84}));
        assertEquals(6, trie.match(new byte[]{(byte) 0xd1, (byte) 0x96, (byte) 0xd0, (byte) 0x9d, (byte) 0xd1, (byte) 0x84}));
    }
}
