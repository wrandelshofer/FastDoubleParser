/*
 * @(#)Utf8DecoderTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Utf8DecoderTest {

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    @ParameterizedTest
    @ValueSource(strings = {
            "lowerbounds\u0000\u0080\u0800\ud800\udc00",
            "upperbounds\u007f\u07ff\uffff\udbff\udfff"})
    public void shouldDecode(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        Utf8Decoder.Result actual = Utf8Decoder.decode(bytes, 0, bytes.length);
        char[] expected = str.toCharArray();

        String actualStr = new String(actual.chars(), 0, actual.length());
        assertEquals(str, actualStr);

        assertEquals(expected.length, actual.length());
        assertArrayEquals(expected, Arrays.copyOf(actual.chars(), actual.length()));
    }
}