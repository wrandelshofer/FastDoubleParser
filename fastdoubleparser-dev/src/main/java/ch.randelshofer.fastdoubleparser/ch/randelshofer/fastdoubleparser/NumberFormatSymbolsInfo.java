/*
 * @(#)NumberFormatSymbolsInfo.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.Collection;

/**
 * Provides information about {@link ch.randelshofer.fastdoubleparser.NumberFormatSymbols}.
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
}
