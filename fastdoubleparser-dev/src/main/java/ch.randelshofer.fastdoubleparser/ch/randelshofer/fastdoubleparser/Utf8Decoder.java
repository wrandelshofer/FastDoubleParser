/*
 * @(#)Utf8Decoder.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

/**
 * Decodes UTF-8 encoded bytes to a character array.
 */
class Utf8Decoder {
    private Utf8Decoder() {
    }

    static final class Result {
        private final char[] chars;
        private final int length;

        Result(char[] chars, int length) {
            this.chars = chars;
            this.length = length;
        }

        public char[] chars() {
            return chars;
        }

        public int length() {
            return length;
        }
    }


    /**
     * Table for minimal legal UTF-16 values depending on the op-code.
     */
    private static int[] minLegalValueTable = {0, 0, 0x0080, 0x0800, 0x010000, Integer.MAX_VALUE};

    /**
     * Squashed code. This code may be interesting for future Swar stuff.
     */
    static Result decodeSquashed(byte[] bytes, int offset, int length) {
        char[] chars = new char[length];
        boolean invalid = false;
        int charIndex = 0;
        int limit = offset + length;
        int remainingContinuations = 0;
        int acc = 0;
        int minLegalValue = 0;
        for (int i = offset; i < limit; i++) {
            byte b = bytes[i];
            int opCode = Math.min(5, Integer.numberOfLeadingZeros(~(byte) b << 24));
            if (opCode == 1) {// process the continuation of a code point
                acc = (acc << 6) | b & 0b111111;
                remainingContinuations--;
                invalid |= remainingContinuations < 0;// continuation at start of character is illegal
                if (remainingContinuations == 0) {
                    if (acc >= 0x010000) {
                        chars[charIndex - 1] = (char) (0xd800 | ((acc - 0x10000) >>> 10) & 0b1111111111);
                        chars[charIndex++] = (char) (0xdc00 | (acc - 0x10000) & 0b1111111111);
                    } else {
                        chars[charIndex - 1] = (char) acc;
                    }
                    // the UTF-16 surrogates (U+D800 through U+DFFF) are not legal Unicode
                    invalid |= acc < minLegalValue | 0xd800 <= acc && acc <= 0xdfff;
                }
            } else {// process a new code point
                invalid |= remainingContinuations > 0;
                acc = b & (0xff >>> opCode);
                chars[charIndex++] = (char) acc;
                minLegalValue = minLegalValueTable[opCode];
                remainingContinuations = opCode - 1;
            }
        }

        if (invalid) {
            throw new NumberFormatException("invalid UTF-8 encoding");
        }
        return new Result(chars, charIndex);
    }

    static Result decode(byte[] bytes, int offset, int length) {
        char[] chars = new char[length];
        boolean invalid = false;
        int charIndex = 0;
        int limit = offset + length;
        int remainingContinuations = 0;
        int acc = 0;
        int minLegalValue = 0;
        for (int i = offset; i < limit; i++) {
            byte b = bytes[i];
            switch (Integer.numberOfLeadingZeros(~(byte) b << 24)) {
                case 0:
                    // process code points U+0000 to U+007f
                    // decode 0b0aaa_aaaa to 0b0000_0000_0aaa_aaaa
                    chars[charIndex++] = (char) b;
                    break;
                case 1:
                    // process the continuation of a code point
                    acc = (acc << 6) | b & 0b111111;
                    remainingContinuations--;
                    invalid |= remainingContinuations < 0;// continuation at start of character is illegal
                    if (remainingContinuations == 0) {
                        if (acc >= 0x010000) {
                            chars[charIndex++] = (char) (0xd800 | ((acc - 0x10000) >>> 10) & 0b1111111111);
                            chars[charIndex++] = (char) (0xdc00 | (acc - 0x10000) & 0b1111111111);
                        } else {
                            chars[charIndex++] = (char) acc;
                        }
                        // the UTF-16 surrogates (U+D800 through U+DFFF) are not legal Unicode
                        invalid |= acc < minLegalValue | 0xd800 <= acc && acc <= 0xdfff;
                    }
                    break;
                case 2:
                    // process code points U+0080 to U+07ff
                    // decode 0b110a_aaaa 0b10bb_bbbb to 0b0000_aaaa_abb_bbbb
                    invalid |= remainingContinuations > 0;
                    acc = b & 0b11111;
                    remainingContinuations = 1;
                    minLegalValue = 0x0080;
                    break;
                case 3:
                    // process code points U+0800 to U+ffff
                    // decode 0b1110_aaaa 0b10bb_bbbb 0b10cc_cccc to 0baaaa_bbbb_bbcc_cccc
                    invalid |= remainingContinuations > 0;
                    acc = b & 0b1111;
                    remainingContinuations = 2;
                    minLegalValue = 0x0800;
                    break;
                case 4:
                    // process code points U+010000 to U+10ffff
                    // decode 0b1111_0aaa 0b10bb_bbbb 0b10cc_cccc 0b10dd_dddd to 0ba_aabb_bbbb_cccc_ccdd_dddd
                    invalid |= remainingContinuations > 0;
                    acc = b & 0b111;
                    minLegalValue = 0x010000;
                    remainingContinuations = 3;
                    break;
                default:
                    invalid = true;
                    break;
            }
        }

        if (invalid) {
            throw new NumberFormatException("invalid UTF-8 encoding");
        }
        return new Result(chars, charIndex);
    }
}
