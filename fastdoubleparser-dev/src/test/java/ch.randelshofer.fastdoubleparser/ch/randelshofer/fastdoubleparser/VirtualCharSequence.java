/*
 * @(#)VirtualCharSequence.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class VirtualCharSequence implements CharSequence {
    private final int length;
    private final char fill;
    private final char[] prefix;
    private final char[] suffix;
    private final char[] middle;
    private final int prefixTo;
    private final int middleFrom;
    private final int middleTo;
    private final int suffixFrom;

    private boolean isIso;

    public VirtualCharSequence(String prefix, int middleFrom, String middle, String suffix, char fill, int length) {
        this.length = length;
        this.prefix = prefix.toCharArray();
        this.middle = middle.toCharArray();
        this.suffix = suffix.toCharArray();
        this.prefixTo = this.prefix.length;
        this.middleFrom = middleFrom;
        this.middleTo = middleFrom + this.middle.length;
        this.suffixFrom = length - this.suffix.length;
        this.fill = fill;

        isIso = isIso8859_1(this.prefix) && isIso8859_1(this.middle)
                && isIso8859_1(this.suffix) && isIso8859_1(new char[]{this.fill});
    }

    private static boolean isIso8859_1(char[] ch) {
        int fours = ch.length / 4;
        int i = 0;
        while (i < fours) {
            if ((ch[i++] | ch[i++] | ch[i++] | ch[i++]) > 0xff) {
                return false;
            }
        }
        while (i < ch.length) {
            if (ch[i++] > 0xff) {
                return false;
            }
        }
        return true;
    }

    public VirtualCharSequence(char fill, int length) {
        this("", 0, "", "", fill, length);
    }

    public VirtualCharSequence(String prefix, char fill, int length) {
        this(prefix, 0, "", "", fill, length);
    }

    public byte[] getBytes() {
        byte[] chars = new byte[length];
        Arrays.fill(chars, (byte) fill);
        arraycopy(prefix, 0, chars, 0, prefix.length);
        arraycopy(middle, 0, chars, middleFrom, middle.length);
        arraycopy(suffix, 0, chars, suffixFrom, suffix.length);
        return chars;
    }

    private void arraycopy(char[] src, int srcPos, byte[] dest, int destPos, int length) {
        for (int i = 0; i < length; i++) {
            dest[destPos + i] = (byte) src[srcPos + i];
        }
    }

    public char[] toCharArray() {
        char[] chars = new char[length];
        Arrays.fill(chars, fill);
        System.arraycopy(prefix, 0, chars, 0, prefix.length);
        System.arraycopy(middle, 0, chars, middleFrom, middle.length);
        System.arraycopy(suffix, 0, chars, suffixFrom, suffix.length);
        return chars;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public char charAt(int index) {
        if (index < prefixTo) {
            return prefix[index];
        } else if (index < middleFrom) {
            return fill;
        } else if (index < middleTo) {
            return middle[index - middleFrom];
        } else if (index < suffixFrom) {
            return fill;
        } else {
            return suffix[index - suffixFrom];
        }
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new VirtualCharSequence(
                start < prefixTo ? new String(prefix, start, Math.min(end, prefix.length)) : "",
                Math.min(end, middleFrom),
                end < middleFrom ? new String(middle, Math.max(0, start - middleFrom), Math.min(end, middleTo) - middleFrom) : "",
                end <= suffixFrom ? "" : new String(suffix, 0, suffixFrom - end),
                fill,
                end - start
        );
    }

    @Override
    public String toString() {
        if (length <= Integer.MAX_VALUE - 4) {
            if (isIso) {
                return new String(toByteArray(this), StandardCharsets.ISO_8859_1);
            } else {
                return new String(toCharArray(this));
            }
        }
        return "VirtualCharSequence{" +
                "length=" + length +
                ", fill=" + fill +
                ", prefix=" + new String(prefix) +
                ", suffix=" + new String(suffix) +
                ", middle=" + new String(middle) +
                ", prefixTo=" + prefixTo +
                ", middleFrom=" + middleFrom +
                ", middleTo=" + middleTo +
                ", suffixFrom=" + suffixFrom +
                '}';
    }

    public static char[] toCharArray(CharSequence u) {
        return u instanceof VirtualCharSequence
                ? ((VirtualCharSequence) u).toCharArray()
                : u.toString().toCharArray();
    }

    public static byte[] toByteArray(CharSequence u) {
        return u instanceof VirtualCharSequence
                ? ((VirtualCharSequence) u).getBytes()
                : u.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

}
