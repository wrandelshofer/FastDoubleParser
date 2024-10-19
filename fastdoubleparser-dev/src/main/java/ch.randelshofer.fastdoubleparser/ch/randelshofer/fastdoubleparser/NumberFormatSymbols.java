/*
 * @(#)NumberFormatSymbols.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;


import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Defines a set of symbols for {@link ConfigurableDoubleParser}.
 *
 * @param decimalSeparator  a set of decimal separator characters
 * @param groupingSeparator a set of grouping characters
 * @param exponentSeparator a set of exponent separator strings
 * @param minusSign         a set of minus sign characters
 * @param plusSign          a set of plus sign characters
 * @param infinity          a set of infinity strings
 * @param nan               a set of NaN strings
 * @param digits            the digit characters from 0 to 9
 */
public record NumberFormatSymbols(Set<Character> decimalSeparator, Set<Character> groupingSeparator,
                                  Set<String> exponentSeparator, Set<Character> minusSign, Set<Character> plusSign,
                                  Set<String> infinity, Set<String> nan, List<Character> digits) {
    public NumberFormatSymbols(Set<Character> decimalSeparator, Set<Character> groupingSeparator, Set<String> exponentSeparator, Set<Character> minusSign, Set<Character> plusSign, Set<String> infinity, Set<String> nan, List<Character> digits) {
        if (digits.size() != 10) throw new IllegalArgumentException("digits list must have size 10");
        this.decimalSeparator = decimalSeparator;
        this.groupingSeparator = groupingSeparator;
        this.exponentSeparator = exponentSeparator;
        this.minusSign = minusSign;
        this.plusSign = plusSign;
        this.infinity = infinity;
        this.nan = nan;
        this.digits = digits;
    }

    /**
     * Convenience constructor.
     *
     * @param decimalSeparators  each character in this string defines a decimal separator
     * @param groupingSeparators each character in this string defines a decimal separator
     * @param exponentSeparators each string in this collection defines an exponent separator
     * @param minusSigns         each character in this string defines a minus sign
     * @param plusSigns          each character in this string defines a plus sign
     * @param infinity           each string in this collection defines an infinity string
     * @param nan                each string in this collection defines a NaN string
     * @param digits             the first 10 characters in this string define the digit characters from 0 to 9
     */
    public NumberFormatSymbols(String decimalSeparators, String groupingSeparators,
                               Collection<String> exponentSeparators, String minusSigns, String plusSigns,
                               Collection<String> infinity, Collection<String> nan, String digits) {
        this(toSet(decimalSeparators),
                toSet(groupingSeparators),
                new LinkedHashSet<>(exponentSeparators),
                toSet(minusSigns),
                toSet(plusSigns),
                new LinkedHashSet<>(infinity),
                new LinkedHashSet<>(nan),
                toList(expandDigits(digits)));
    }

    private static String expandDigits(String digits) {
        if (digits.length() == 10) return digits;
        if (digits.length() != 1)
            throw new IllegalArgumentException("digits must have length 1 or 10, digits=\"" + digits + "\"");
        var buf = new StringBuilder(10);
        char zeroChar = digits.charAt(0);
        for (int i = 0; i < 10; i++) {
            buf.append((char) (zeroChar + i));
        }
        return buf.toString();
    }

    private static Set<Character> toSet(String chars) {
        Set<Character> set = new LinkedHashSet<>(chars.length() * 2);
        for (char ch : chars.toCharArray()) {
            set.add(ch);
        }
        return set;
    }

    private static List<Character> toList(String chars) {
        List<Character> list = new ArrayList<>(10);
        for (char ch : chars.toCharArray()) {
            list.add(ch);
        }
        return list;
    }

    public static NumberFormatSymbols fromDecimalFormatSymbols(DecimalFormatSymbols symbols) {
        List<Character> digits = new ArrayList<>(10);
        char zeroDigit = symbols.getZeroDigit();
        for (int i = 0; i < 10; i++) {
            digits.add((char) (zeroDigit + i));
        }
        return new NumberFormatSymbols(
                Collections.singleton(symbols.getDecimalSeparator()),
                Collections.singleton(symbols.getGroupingSeparator()),
                Collections.singleton(symbols.getExponentSeparator()),
                Collections.singleton(symbols.getMinusSign()),
                Collections.emptySet(),
                Collections.singleton(symbols.getInfinity()),
                Collections.singleton(symbols.getNaN()),
                digits
        );
    }

    public static NumberFormatSymbols fromDefault() {
        return new NumberFormatSymbols(
                Collections.singleton('.'),
                Collections.emptySet(),
                new HashSet<>(Arrays.asList("e", "E")),
                Collections.singleton('-'),
                Collections.singleton('+'),
                Collections.singleton("Infinity"),
                Collections.singleton("NaN"),
                Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        );
    }


    @Override
    public String toString() {
        return "NumberFormatSymbols[" +
                "decimalSeparator=" + decimalSeparator + ", " +
                "groupingSeparator=" + groupingSeparator + ", " +
                "exponentSeparator=" + exponentSeparator + ", " +
                "minusSign=" + minusSign + ", " +
                "plusSign=" + plusSign + ", " +
                "infinity=" + infinity + ", " +
                "nan=" + nan + ", " +
                "digits=" + digits + ']';
    }

}