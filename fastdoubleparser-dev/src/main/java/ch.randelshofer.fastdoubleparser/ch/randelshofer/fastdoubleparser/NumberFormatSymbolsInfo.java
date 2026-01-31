/*
 * @(#)NumberFormatSymbolsInfo.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.chr.CharSet;
import ch.randelshofer.fastdoubleparser.chr.FormatCharSet;

import java.util.Collection;
import java.util.Set;

/**
 * Provides information about {@link NumberFormatSymbols}.
 */
class NumberFormatSymbolsInfo {
    /**
     * Returns true if all symbols are ASCII code points in the range U+0000 to U+007f.
     */
    static boolean isAscii(NumberFormatSymbols symbols) {
        return isAsciiCharCollection(symbols.decimalSeparator())
                && isAsciiCharCollection(symbols.groupingSeparator())
                && isAsciiStringCollection(symbols.exponentSeparator())
                && isAsciiCharCollection(symbols.minusSign())
                && isAsciiCharCollection(symbols.plusSign())
                && isAsciiStringCollection(symbols.infinity())
                && isAsciiStringCollection(symbols.nan())
                && isAsciiCharCollection(symbols.digits())
                ;
    }

    /**
     * Returns true if all single character symbols are ASCII code points in the range U+0000 to U+007f.
     */
    static boolean isMostlyAscii(NumberFormatSymbols symbols) {
        return isAsciiCharCollection(symbols.decimalSeparator())
                && isAsciiCharCollection(symbols.groupingSeparator())
                //        && isAsciiStringCollection(symbols.exponentSeparator())
                && isAsciiCharCollection(symbols.minusSign())
                && isAsciiCharCollection(symbols.plusSign())
                //      && isAsciiStringCollection(symbols.infinity())
                //        && isAsciiStringCollection(symbols.nan())
                && isAsciiCharCollection(symbols.digits())
                ;
    }

    static boolean isDigitsTokensAscii(NumberFormatSymbols symbols) {
        return //isAsciiCharCollection(symbols.decimalSeparator())
                //  && isAsciiCharCollection(symbols.groupingSeparator())
                //        && isAsciiStringCollection(symbols.exponentSeparator())
                //  && isAsciiCharCollection(symbols.minusSign())
                // && isAsciiCharCollection(symbols.plusSign())
                //      && isAsciiStringCollection(symbols.infinity())
                //        && isAsciiStringCollection(symbols.nan())
                isAsciiCharCollection(symbols.digits())
                ;
    }

    static boolean isLeftToRight(NumberFormatSymbols symbols) {
        return isLeftToRightCharCollection(symbols.decimalSeparator())
                && isLeftToRightCharCollection(symbols.groupingSeparator())
                && isLeftToRightCollection(symbols.exponentSeparator())
                && isLeftToRightCharCollection(symbols.minusSign())
                && isLeftToRightCharCollection(symbols.plusSign())
                && isLeftToRightCollection(symbols.infinity())
                && isLeftToRightCollection(symbols.nan())
                && isLeftToRightCharCollection(symbols.digits())
                ;
    }

    static boolean isAsciiStringCollection(Collection<String> collection) {
        for (String str : collection) {
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (ch > 0x7f) return false;
            }
        }
        return true;
    }

    static boolean isAsciiCharCollection(Collection<Character> collection) {
        for (char ch : collection) {
            if (ch > 0x7f) return false;
        }
        return true;
    }

    static boolean containsFormatChars(NumberFormatSymbols symbols) {
        FormatCharSet formatCharSet = new FormatCharSet();
        return containsChars(symbols.decimalSeparator(), formatCharSet)
                || containsChars(symbols.groupingSeparator(), formatCharSet)
                || containsChars(symbols.exponentSeparator(), formatCharSet)
                || containsChars(symbols.minusSign(), formatCharSet)
                || containsChars(symbols.plusSign(), formatCharSet)
                || containsChars(symbols.infinity(), formatCharSet)
                || containsChars(symbols.nan(), formatCharSet)
                || containsChars(symbols.digits(), formatCharSet)
                ;
    }

    private static boolean containsChars(Set<String> strings, FormatCharSet set) {
        for (String str : strings) {
            for (int i = 0, n = str.length(); i < n; i++) {
                if (set.containsKey(str.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean containsChars(Collection<Character> characters, CharSet set) {
        for (char ch : characters) {
            if (set.containsKey(ch)) {
                return true;
            }
        }
        return false;
    }

    static boolean isLeftToRightCollection(Collection<String> collection) {
        for (String str : collection) {
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (!isLeftToRight(ch)) return false;
            }
        }
        return true;
    }

    static boolean isLeftToRightCharCollection(Collection<Character> collection) {
        for (char ch : collection) {
            if (!isLeftToRight(ch)) return false;
        }
        return true;
    }

    static boolean isLeftToRight(char ch) {
        byte directionality = Character.getDirectionality(ch);
        if (directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT) return false;
        if (directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC) return false;
        if (directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE) return false;
        return true;
    }
}
