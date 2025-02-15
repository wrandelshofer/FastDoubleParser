/*
 * @(#)NumberFormatSymbols.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;


import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
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
    private final List<Character> digits;

    /**
     * Canonical constructor.
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
    public NumberFormatSymbols(Set<Character> decimalSeparator, Set<Character> groupingSeparator,
                               Set<String> exponentSeparator, Set<Character> minusSign, Set<Character> plusSign,
                               Set<String> infinity, Set<String> nan, List<Character> digits) {
        if (Objects.requireNonNull(digits, "digits").size() != 10)
            throw new IllegalArgumentException("digits list must have size 10");
        this.decimalSeparator = Set.copyOf(Objects.requireNonNull(decimalSeparator, "decimalSeparator"));
        this.groupingSeparator = Set.copyOf(Objects.requireNonNull(groupingSeparator, "groupingSeparator"));
        this.exponentSeparator = Set.copyOf(Objects.requireNonNull(exponentSeparator, "exponentSeparator"));
        this.minusSign = Set.copyOf(Objects.requireNonNull(minusSign, "minusSign"));
        this.plusSign = Set.copyOf(Objects.requireNonNull(plusSign, "plusSign"));
        this.infinity = Set.copyOf(Objects.requireNonNull(infinity, "infinity"));
        this.nan = Set.copyOf(Objects.requireNonNull(nan, "nan"));
        this.digits = List.copyOf(digits);
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

    /**
     * Creates a new instance from the provided {@link DecimalFormatSymbols}.
     *
     * @param symbols the decimal format symbols
     * @return a new instance
     */
    public static NumberFormatSymbols fromDecimalFormatSymbols(DecimalFormatSymbols symbols) {
        List<Character> digits = new ArrayList<>(10);
        char zeroDigit = symbols.getZeroDigit();
        for (int i = 0; i < 10; i++) {
            digits.add((char) (zeroDigit + i));
        }
        return new NumberFormatSymbols(
                Set.of(symbols.getDecimalSeparator()),
                Set.of(symbols.getGroupingSeparator()),
                Set.of(symbols.getExponentSeparator()),
                Set.of(symbols.getMinusSign()),
                Set.of(),
                Set.of(symbols.getInfinity()),
                Set.of(symbols.getNaN()),
                digits
        );
    }

    /**
     * Creates a new instance with the following default symbols.
     * <dl>
     *     <dt>decimalSeparator </dt><dd>{@code .}</dd>
     *     <dt>groupingSeparator</dt><dd>none</dd>
     *     <dt>exponentSeparator</dt><dd>{@code e}, {@code E}</dd>
     *     <dt>minusSign        </dt><dd>{@code -}</dd>
     *     <dt>plusSign         </dt><dd>{@code +}</dd>
     *     <dt>infinity         </dt><dd>{@code Infinity}</dd>
     *     <dt>nan              </dt><dd>{@code NaN}</dd>
     *     <dt>digits           </dt><dd>{@code 0} ... {@code 9}</dd>
     * </dl>
     *
     * @return a new instance
     */
    public static NumberFormatSymbols fromDefault() {
        return new NumberFormatSymbols(
                Set.of('.'),
                Set.of(),
                Set.of("e", "E"),
                Set.of('-'),
                Set.of('+'),
                Set.of("Infinity"),
                Set.of("NaN"),
                Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        );
    }

    private static List<Character> toList(String chars) {
        List<Character> set = new ArrayList<>(10);
        for (char ch : chars.toCharArray()) {
            set.add(ch);
        }
        return set;
    }

    private static Set<Character> toSet(String chars) {
        Set<Character> set = new LinkedHashSet<>(chars.length() * 2);
        for (char ch : chars.toCharArray()) {
            set.add(ch);
        }
        return set;
    }

    public Set<Character> decimalSeparator() {
        return decimalSeparator;
    }

    public List<Character> digits() {
        return digits;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (NumberFormatSymbols) obj;
        return Objects.equals(this.decimalSeparator, that.decimalSeparator) &&
                Objects.equals(this.groupingSeparator, that.groupingSeparator) &&
                Objects.equals(this.exponentSeparator, that.exponentSeparator) &&
                Objects.equals(this.minusSign, that.minusSign) &&
                Objects.equals(this.plusSign, that.plusSign) &&
                Objects.equals(this.infinity, that.infinity) &&
                Objects.equals(this.nan, that.nan) &&
                Objects.equals(this.digits, that.digits);
    }

    public Set<String> exponentSeparator() {
        return exponentSeparator;
    }

    public Set<Character> groupingSeparator() {
        return groupingSeparator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(decimalSeparator, groupingSeparator, exponentSeparator, minusSign, plusSign, infinity, nan, digits);
    }

    public Set<String> infinity() {
        return infinity;
    }

    public Set<Character> minusSign() {
        return minusSign;
    }

    public Set<String> nan() {
        return nan;
    }

    public Set<Character> plusSign() {
        return plusSign;
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

    /**
     * Creates a new instance with the specified decimal separator symbols.
     *
     * @param newValue the decimal separator symbols
     * @return a new instance
     */
    public NumberFormatSymbols withDecimalSeparator(Set<Character> newValue) {
        return new NumberFormatSymbols(newValue,
                groupingSeparator,
                exponentSeparator,
                minusSign,
                plusSign,
                infinity,
                nan,
                digits);
    }

    /**
     * Creates a new instance with the specified digits.
     *
     * @param newValue the digits
     * @return a new instance
     */
    public NumberFormatSymbols withDigits(List<Character> newValue) {
        return new NumberFormatSymbols(decimalSeparator,
                groupingSeparator,
                exponentSeparator,
                minusSign,
                plusSign,
                infinity,
                nan,
                newValue);
    }

    /**
     * Creates a new instance with the specified exponent separator symbols.
     *
     * @param newValue the exponent separator symbols
     * @return a new instance
     */
    public NumberFormatSymbols withExponentSeparator(Set<String> newValue) {
        return new NumberFormatSymbols(decimalSeparator,
                groupingSeparator,
                newValue,
                minusSign,
                plusSign,
                infinity,
                nan,
                digits);
    }

    /**
     * Creates a new instance with the specified grouping separator symbols.
     *
     * @param newValue the grouping separator symbols
     * @return a new instance
     */
    public NumberFormatSymbols withGroupingSeparator(Set<Character> newValue) {
        return new NumberFormatSymbols(decimalSeparator,
                newValue,
                exponentSeparator,
                minusSign,
                plusSign,
                infinity,
                nan,
                digits);
    }

    /**
     * Creates a new instance with the specified infinity symbols.
     *
     * @param newValue the infinity symbols
     * @return a new instance
     */
    public NumberFormatSymbols withInfinity(Set<String> newValue) {
        return new NumberFormatSymbols(decimalSeparator,
                groupingSeparator,
                exponentSeparator,
                minusSign,
                plusSign,
                newValue,
                nan,
                digits);
    }

    /**
     * Creates a new instance with the specified minus sign symbols.
     *
     * @param newValue the minus sign symbols
     * @return a new instance
     */
    public NumberFormatSymbols withMinusSign(Set<Character> newValue) {
        return new NumberFormatSymbols(decimalSeparator,
                groupingSeparator,
                exponentSeparator,
                newValue,
                plusSign,
                infinity,
                nan,
                digits);
    }

    /**
     * Creates a new instance with the specified NaN symbols.
     *
     * @param newValue the NaN symbols
     * @return a new instance
     */
    public NumberFormatSymbols withNaN(Set<String> newValue) {
        return new NumberFormatSymbols(decimalSeparator,
                groupingSeparator,
                exponentSeparator,
                minusSign,
                plusSign,
                infinity,
                newValue,
                digits);
    }

    /**
     * Creates a new instance with the specified plus sign symbols.
     *
     * @param newValue the plus sign symbols
     * @return a new instance
     */
    public NumberFormatSymbols withPlusSign(Set<Character> newValue) {
        return new NumberFormatSymbols(decimalSeparator,
                groupingSeparator,
                exponentSeparator,
                minusSign,
                newValue,
                infinity,
                nan,
                digits);
    }

}