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
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    @ParameterizedTest
    @ValueSource(strings = {
            "80",
            "c0",
            "c0 80",
            "e0 80",
            "e0 80 80",
            "e0 80 e0",
            "f0 80 80",
            "f0 80 80 80",
            "f0 80 80 80",
            "f0 80 a0 a0",
    })
    public void shouldNotDecode(String str) {
        String[] hexes = str.split(" ");
        byte[] bytes = new byte[hexes.length];
        for (int i = 0; i < hexes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hexes[i], 16);

        }
        assertThrows(NumberFormatException.class, () -> Utf8Decoder.decode(bytes, 0, bytes.length));
    }
}