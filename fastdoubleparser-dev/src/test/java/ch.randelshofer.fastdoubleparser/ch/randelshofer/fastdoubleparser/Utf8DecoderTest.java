/*
 * @(#)Utf8DecoderTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public final class Utf8DecoderTest {

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    @ParameterizedTest
    @ValueSource(strings = {
            "lowerbounds:\u0000\u0080\u0800\ud800\udc00",
            "upperbounds:\u007f\u07ff\uffff\udbff\udfff",
            "twobytes1char:Распу́тин",
            "threebytes1char:孔夫子",
            "fourbytes2char:𐀀𐀁𐀂𐀃",
            "grouped:abcd" + "ÀÁÂÃ" + "ՀՁՂՃ" + "ठडढण" + "𐀀" + "𐀁𐀂𐀃",
            "mixed:aÀՀठ" + "𐀀" + "𐀃" + "णՃÃd",
    })
    public void shouldDecode(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        char[] chars = new char[bytes.length];
        int actualLength = Utf8Decoder.decode(bytes, 0, bytes.length, chars, 0);
        char[] expected = str.toCharArray();

        /*
        for (int i = 0; i < actualLength; i++) {
            System.out.print(" '" + chars[i] + "'" + Integer.toHexString(chars[i]));
        }
        System.out.println();

         */
        String actualStr = new String(chars, 0, actualLength);

        assertEquals(str, actualStr);


        assertEquals(expected.length, actualLength);
        assertArrayEquals(expected, Arrays.copyOf(chars, actualLength));
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
        assertThrows(NumberFormatException.class, () -> Utf8Decoder.decode(bytes, 0, bytes.length, new char[bytes.length], 0));
    }
}