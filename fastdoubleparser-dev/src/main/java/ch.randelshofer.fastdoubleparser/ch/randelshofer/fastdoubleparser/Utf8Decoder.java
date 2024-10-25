/*
 * @(#)Utf8DecoderSeq.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

/**
 * Decodes UTF-8 encoded bytes to a character array.
 */
final class Utf8Decoder {
    private Utf8Decoder() {
    }

    /**
     * <pre>
     *  code points U+0000 to U+007f
     *  decode 0b0aaa_aaaa to 0b0000_0000_0aaa_aaaa
     *
     *  code points U+0080 to U+07ff
     *  decode decode 110xxxyy_10yyzzzz to xxx_yyyyzzzz
     *
     *  code points U+0800 to U+ffff
     *  decode 1110wwww_10xxxxyy_10yyzzzz to wwwxxxx_yyyyzzzz
     *
     *  process code points U+010000 to U+10ffff
     *  decode 11110uvv_10vvwwww_10xxxxyy_10yyzzzz to uvvvv_wwwwxxxx_yyyyzzzz
     *  subtract 0x10000 from the value
     *  then encode as two utf-16 code points:
     *  U' = yyyyyyyyyyxxxxxxxxxx  // U - 0x10000
     *  W1 = 110110yyyyyyyyyy      // 0xD800 + yyyyyyyyyy
     *  W2 = 110111xxxxxxxxxx      // 0xDC00 + xxxxxxxxxx
     * </pre>
     *
     * @param in        utf-8 encoded bytes
     * @param offset    start of utf-8 encoded bytes
     * @param length    length of utf-8 encoded bytes
     * @param out       output chars must have a size of at least 2
     * @param offsetOut start of chars
     * @return index out
     */

    static int decode(byte[] in, int offset, int length, char[] out, int offsetOut) {
        boolean invalid = false;
        int limit = offset + length;
        int value;
        int c1, c2, c3;
        int j = offsetOut;
        int i = offset;
        while (i < limit) {
            byte b = in[i];
            int opcode = Integer.numberOfLeadingZeros(~(byte) b << 24);
            if (i + opcode > limit) throw new NumberFormatException("UTF-8 code point is incomplete");
            switch (opcode) {
                case 0:
                    // process code points U+0000 to U+007f
                    // decode 0yyyzzzz
                    out[j++] = (char) b;
                    i++;
                    break;
                case 1:
                    invalid = true;
                    i = limit;
                    break;
                case 2:
                    // process code points U+0080 to U+07ff
                    // decode 110xxxyy_10yyzzzz to xxx_yyyyzzzz
                    c1 = in[i + 1];
                    value = (b & 0b11111) << 6 | c1 & 0b111111;
                    invalid |= value < 0x0080 | (c1 & 0xc0) != 0x80;
                    out[j++] = (char) value;
                    i += 2;
                    break;
                case 3:
                    // process code points U+0800 to U+ffff
                    // decode 1110wwww_10xxxxyy_10yyzzzz to wwwxxxx_yyyyzzzz
                    c1 = in[i + 1];
                    c2 = in[i + 2];
                    value = (b & 0b1111) << 12 | (c1 & 0b111111) << 6 | c2 & 0b111111;
                    invalid |= value < 0x0800 | (c1 & c2 & 0xc0) != 0x80;
                    out[j++] = (char) value;
                    i += 3;
                    break;
                case 4:
                    // process code points U+010000 to U+10ffff
                    // decode 11110uvv_10vvwwww_10xxxxyy_10yyzzzz to uvvvv_wwwwxxxx_yyyyzzzz
                    // then subtract - 0x10000 from the value
                    // then encode as two utf-16 code points
                    // U' = yyyyyyyyyyxxxxxxxxxx  // U - 0x10000
                    // W1 = 110110yyyyyyyyyy      // 0xD800 + yyyyyyyyyy
                    // W2 = 110111xxxxxxxxxx      // 0xDC00 + xxxxxxxxxx
                    c1 = in[i + 1];
                    c2 = in[i + 2];
                    c3 = in[i + 3];
                    value = (b & 0b111) << 18 | (c1 & 0b111111) << 12 | (c2 & 0b111111) << 6 | c3 & 0b111111;
                    invalid |= value < 0x010000 | (c1 & c2 & c3 & 0xc0) != 0x80;
                    value -= 0x10000;
                    out[j++] = (char) (0xd800 | (value >>> 10) & 0b1111111111);
                    out[j++] = (char) (0xdc00 | value & 0b1111111111);
                    i += 4;
                    break;
                default:
                    invalid = true;
                    i = limit;
                    break;
            }
        }

        if (invalid) {
            throw new NumberFormatException("invalid UTF-8 encoding");
        }
        return j;
    }
}
