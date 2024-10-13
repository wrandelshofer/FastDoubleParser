/*
 * @(#)NumberFormatSymbols.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;


import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Defines a set of symbols for {@link ConfigurableDoubleParser}.
 */
public final class NumberFormatSymbols {
    private final Set<Character> decimalSeparator;
    private final Set<Character> groupingSeparator;
    private final Set<String> exponentSeparator;
    private final Set<Character> minusSign;
    private final Set<Character> plusSign;
    private final Set<String> infinity;
    private final Set<String> nan;
    private final char zeroDigit;

    /**
     * Creates a new instance.
     *
     * @param decimalSeparator  a set of decimal separator characters
     * @param groupingSeparator a set of grouping characters
     * @param exponentSeparator a set of exponent separator strings
     * @param minusSign         a set of minus sign characters
     * @param plusSign          a set of plus sign characters
     * @param infinity          a set of infinity strings
     * @param nan               a set of NaN strings
     * @param zeroDigit         a zero digit character
     */
    public NumberFormatSymbols(Set<Character> decimalSeparator, Set<Character> groupingSeparator,
                               Set<String> exponentSeparator, Set<Character> minusSign, Set<Character> plusSign,
                               Set<String> infinity, Set<String> nan, char zeroDigit) {
        this.decimalSeparator = decimalSeparator;
        this.groupingSeparator = groupingSeparator;
        this.exponentSeparator = exponentSeparator;
        this.minusSign = minusSign;
        this.plusSign = plusSign;
        this.infinity = infinity;
        this.nan = nan;
        this.zeroDigit = zeroDigit;
    }

    public static NumberFormatSymbols fromDecimalFormatSymbols(DecimalFormatSymbols symbols) {
        return new NumberFormatSymbols(
                Collections.singleton(symbols.getDecimalSeparator()),
                Collections.singleton(symbols.getGroupingSeparator()),
                Collections.singleton(symbols.getExponentSeparator()),
                Collections.singleton(symbols.getMinusSign()),
                Collections.emptySet(),
                Collections.singleton(symbols.getInfinity()),
                Collections.singleton(symbols.getNaN()),
                symbols.getZeroDigit()
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
                '0'
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
                "zeroDigit=" + zeroDigit + ']';
    }

    public Set<Character> decimalSeparator() {
        return decimalSeparator;
    }

    public Set<Character> groupingSeparator() {
        return groupingSeparator;
    }

    public Set<String> exponentSeparator() {
        return exponentSeparator;
    }

    public Set<Character> minusSign() {
        return minusSign;
    }

    public Set<Character> plusSign() {
        return plusSign;
    }

    public Set<String> infinity() {
        return infinity;
    }

    public Set<String> nan() {
        return nan;
    }

    public char zeroDigit() {
        return zeroDigit;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        NumberFormatSymbols that = (NumberFormatSymbols) obj;
        return Objects.equals(this.decimalSeparator, that.decimalSeparator) &&
                Objects.equals(this.groupingSeparator, that.groupingSeparator) &&
                Objects.equals(this.exponentSeparator, that.exponentSeparator) &&
                Objects.equals(this.minusSign, that.minusSign) &&
                Objects.equals(this.plusSign, that.plusSign) &&
                Objects.equals(this.infinity, that.infinity) &&
                Objects.equals(this.nan, that.nan) &&
                this.zeroDigit == that.zeroDigit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(decimalSeparator, groupingSeparator, exponentSeparator, minusSign, plusSign, infinity, nan, zeroDigit);
    }


}