/*
 * @(#)ConfigurableDoubleParserTestDataFactory.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigurableDoubleParserTestDataFactory {

    public static List<NumberTestData> createLocalizedTestData(Locale locale) {
        String languageTag = locale.toLanguageTag();
        NumberFormat f = NumberFormat.getNumberInstance(locale);
        List<NumberTestData> list = new ArrayList<>();
        for (double v : new double[]{
                1_234_567.89,
                -1_234_567.89,
                3e25,
                -3e25,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY,
                Double.NaN}) {
            list.add(new NumberTestData(languageTag + " " + f.format(v), locale, f.format(v), v));
        }

        return list;
    }

    public static List<NumberTestData> createNumberFormatSymbolsTestData() {
        List<NumberTestData> list = new ArrayList<>();
        list.addAll(createIgnoreCaseNumberFormatSymbolsTestData());
        list.addAll(createArabianNumberFormatSymbolsTestData());
        list.addAll(createEstonianNumberFormatSymbolsTestData());
        return list;
    }

    public static List<NumberTestData> createEstonianNumberFormatSymbolsTestData() {
        List<NumberTestData> list = new ArrayList<>();
        Locale estonianLocale = new Locale("et", "EE");
        var dfs = DecimalFormatSymbols.getInstance(
                estonianLocale
        );
        assertEquals(dfs.getMinusSign(), '\u2212', "Expected estonian minus sign U+2212");
        var symbols = new NumberFormatSymbols(
                Set.of(dfs.getDecimalSeparator()),
                Set.of(dfs.getGroupingSeparator()),
                Set.of(dfs.getExponentSeparator()),
                Set.of(dfs.getMinusSign(), '-'), // adding estonian minus sign and normal one
                Set.of('\uff0b', '+'), // adding full-width plus sign and normal one
                Set.of(dfs.getInfinity()),
                Set.of(dfs.getNaN()),
                dfs.getZeroDigit()
        );
        list.addAll(List.of(
                new NumberTestData("Estonian locale with Estonian minus", dfs.getLocale(), symbols, "\u221213,35", -13.35),
                new NumberTestData("Estonian locale with ordinary minus", dfs.getLocale(), symbols, "-13,35", -13.35),
                new NumberTestData("Estonian locale with full-width plus", dfs.getLocale(), symbols, "\uff0b13,35", 13.35),
                new NumberTestData("Estonian locale with ordinary plus", dfs.getLocale(), symbols, "+13,35", 13.35),
                new NumberTestData("Estonian locale with Estonian minus exponent", dfs.getLocale(), symbols, "13,35×10^\u22124", 13.35e-4),
                new NumberTestData("Estonian locale with ordinary minus exponent", dfs.getLocale(), symbols, "13,35×10^-4", 13.35e-4),
                new NumberTestData("Estonian locale with full-width plus exponent", dfs.getLocale(), symbols, "13,35×10^\uff0b4", 13.35e4),
                new NumberTestData("Estonian locale with ordinary plus exponent", dfs.getLocale(), symbols, "13,35×10^+4", 13.35e4),
                new NumberTestData("Estonian locale, Outside Clinger fast path, mantissa overflows in semi-fast path, 7.2057594037927933e+16",
                        dfs.getLocale(), symbols, "7,2057594037927933×10^16", 7.2057594037927933e+16d)
        ));
        return list;
    }

    public static List<NumberTestData> createIgnoreCaseNumberFormatSymbolsTestData() {
        List<NumberTestData> list = new ArrayList<>();
        Locale englishLocale = Locale.ENGLISH;
        var dfs = DecimalFormatSymbols.getInstance(englishLocale);
        dfs.setInfinity("Infinity");
        dfs.setExponentSeparator("Exp");
        dfs.setNaN("NaN");
        var symbols = new NumberFormatSymbols(
                Set.of(dfs.getDecimalSeparator()),
                Set.of(dfs.getGroupingSeparator()),
                Set.of(dfs.getExponentSeparator()),
                Set.of(dfs.getMinusSign()),
                Set.of('+'),
                Set.of(dfs.getInfinity()),
                Set.of(dfs.getNaN()),
                dfs.getZeroDigit()
        );
        DecimalFormat fmt = new DecimalFormat("#00.0####E0", dfs);
        for (var n : new double[]{3e-9, -7e8, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            list.add(new NumberTestData("ignoreCase: " + fmt.format(n), englishLocale, symbols, true, fmt.format(n), n));
            list.add(new NumberTestData("ignoreCase: lower-case " + fmt.format(n).toLowerCase(englishLocale), englishLocale, symbols, true, fmt.format(n).toLowerCase(englishLocale), n));
            list.add(new NumberTestData("ignoreCase: upper-case " + fmt.format(n).toUpperCase(englishLocale), englishLocale, symbols, true, fmt.format(n).toUpperCase(englishLocale), n));
        }

        return list;
    }

    public static List<NumberTestData> createArabianNumberFormatSymbolsTestData() {
        List<NumberTestData> list = new ArrayList<>();
        Locale arabianLocale = new Locale("ar");
        var dfs = DecimalFormatSymbols.getInstance(
                arabianLocale
        );
        var symbols = NumberFormatSymbols.fromDecimalFormatSymbols(dfs);
        DecimalFormat fmt = new DecimalFormat("#00.0####E0", dfs);
        for (var n : new double[]{3e-9, -7e8, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            list.add(new NumberTestData(fmt.format(n), arabianLocale, symbols, fmt.format(n), n));
        }

        return list;
    }

    public static List<NumberTestData> createLocalizedTestData(Locale locale, List<NumberTestData> inputList) {
        String languageTag = locale.toLanguageTag();
        NumberFormat f = NumberFormat.getNumberInstance(locale);
        f.setMaximumFractionDigits(768);
        List<NumberTestData> list = new ArrayList<>();
        for (NumberTestData t : inputList) {
            Number v = t.expectedValue();
            if (v == null) continue;
            ;
            list.add(new NumberTestData(languageTag + " " + f.format(v), locale, f.format(v), v));
        }

        return list;
    }
}
