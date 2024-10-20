/*
 * @(#)ConfigurableDoubleParserTestDataFactory.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import static ch.randelshofer.fastdoubleparser.AbstractNumberParser.SYNTAX_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class ConfigurableDoubleParserTestDataFactory {

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
        list.addAll(createSwissIgnoreCaseNumberFormatSymbolsTestData());
        list.addAll(createChineseNumberFormatSymbolsTestData());
        list.addAll(createEnglishIgnoreCaseNumberFormatSymbolsTestData());
        list.addAll(createArabianNumberFormatSymbolsTestData());
        list.addAll(createEstonianNumberFormatSymbolsTestData());
        return list;
    }

    public static List<NumberTestData> createEstonianNumberFormatSymbolsTestData() {
        List<NumberTestData> list = new ArrayList<>();
        Locale estonianLocale = new Locale("et", "EE");
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(
                estonianLocale
        );
        assertEquals(dfs.getMinusSign(), '\u2212', "Expected estonian minus sign U+2212");
        NumberFormatSymbols symbols = new NumberFormatSymbols(
                "" + dfs.getDecimalSeparator(),
                "" + dfs.getGroupingSeparator(),
                Collections.singleton(dfs.getExponentSeparator()),
                "" + dfs.getMinusSign() + '-', // adding estonian minus sign and normal one
                "" + '\uff0b' + '+', // adding full-width plus sign and normal one
                Collections.singleton(dfs.getInfinity()),
                Collections.singleton(dfs.getNaN()),
                "" + dfs.getZeroDigit()
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

    public static List<NumberTestData> createChineseNumberFormatSymbolsTestData() {
        List<NumberTestData> list = new ArrayList<>();
        Locale chineseLocale = new Locale("zh", "CN");
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(
                chineseLocale
        );
        NumberFormatSymbols symbols = new NumberFormatSymbols(
                "" + dfs.getDecimalSeparator(),
                "" + dfs.getGroupingSeparator(),
                Collections.singleton(dfs.getExponentSeparator()),
                "" + dfs.getMinusSign(),
                "+",
                Collections.singleton(dfs.getInfinity()),
                Collections.singleton(dfs.getNaN()),
                "〇一二三四五六七八九"
        );
        list.addAll(List.of(
                new NumberTestData("Chinese locale 一,二三四,五六七.〇八九", dfs.getLocale(), symbols, "一,二三四,五六七.〇八九", 1234567.089),
                new NumberTestData("Chinese locale 〇.五六四", dfs.getLocale(), symbols, "〇.五六四", 0.564)
        ));
        return list;
    }

    public static List<NumberTestData> createEnglishIgnoreCaseNumberFormatSymbolsTestData() {
        List<NumberTestData> list = new ArrayList<>();
        Locale englishLocale = Locale.ENGLISH;
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(englishLocale);
        dfs.setInfinity("Infinity");
        dfs.setExponentSeparator("Exp");
        dfs.setNaN("NaN");
        NumberFormatSymbols symbols = new NumberFormatSymbols(
                "" + dfs.getDecimalSeparator(),
                "" + dfs.getGroupingSeparator(),
                Collections.singleton(dfs.getExponentSeparator()),
                "" + dfs.getMinusSign(),
                "+",
                Collections.singleton(dfs.getInfinity()),
                Collections.singleton(dfs.getNaN()),
                "" + dfs.getZeroDigit()
        );
        DecimalFormat fmt = new DecimalFormat("#00.0####E0", dfs);
        for (double n : new double[]{3e-9, -7e8, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            list.add(new NumberTestData("ignoreCase: " + fmt.format(n), englishLocale, symbols, true, fmt.format(n), n));
            list.add(new NumberTestData("ignoreCase: lower-case " + fmt.format(n).toLowerCase(englishLocale), englishLocale, symbols, true, fmt.format(n).toLowerCase(englishLocale), n));
            list.add(new NumberTestData("ignoreCase: upper-case " + fmt.format(n).toUpperCase(englishLocale), englishLocale, symbols, true, fmt.format(n).toUpperCase(englishLocale), n));
        }
        list.add(new NumberTestData("ignoreCase: " + dfs.getNaN().toLowerCase(), englishLocale, symbols, true, dfs.getNaN().toLowerCase(), Double.NaN));
        list.add(new NumberTestData("ignoreCase: " + dfs.getNaN().toUpperCase(), englishLocale, symbols, true, dfs.getNaN().toUpperCase(), Double.NaN));
        list.add(new NumberTestData("ignoreCase: 12" + dfs.getExponentSeparator().toLowerCase() + "5", englishLocale, symbols, true, "12" + dfs.getExponentSeparator().toLowerCase() + "5", 12e5));
        list.add(new NumberTestData("ignoreCase: 12" + dfs.getExponentSeparator().toUpperCase() + "5", englishLocale, symbols, true, "12" + dfs.getExponentSeparator().toUpperCase() + "5", 12e5));

        return list;
    }

    public static List<NumberTestData> createSwissIgnoreCaseNumberFormatSymbolsTestData() {
        List<NumberTestData> list = new ArrayList<>();
        Locale swissLocale = new Locale("de", "CH");
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(swissLocale);
        NumberFormatSymbols symbols = new NumberFormatSymbols(
                "" + dfs.getDecimalSeparator(),
                "" + dfs.getGroupingSeparator() + "'",
                new LinkedHashSet<>(Arrays.asList(dfs.getExponentSeparator(), "Exp")),
                "" + dfs.getMinusSign(),
                "+",
                new LinkedHashSet<>(Arrays.asList(dfs.getInfinity(), "Inf", "Infinity")),
                Collections.singleton(dfs.getNaN()),
                "" + dfs.getZeroDigit()
        );
        list.add(new NumberTestData("ignoreCase: 12’961’872.332", swissLocale, symbols, true, "12’961’872.332", 12961872.332));
        list.add(new NumberTestData("ignoreCase: " + dfs.getNaN().toLowerCase(), swissLocale, symbols, true, dfs.getNaN().toLowerCase(), Double.NaN));
        list.add(new NumberTestData("ignoreCase: " + dfs.getNaN().toUpperCase(), swissLocale, symbols, true, dfs.getNaN().toUpperCase(), Double.NaN));
        list.add(new NumberTestData("ignoreCase: 12" + dfs.getExponentSeparator().toLowerCase() + "5", swissLocale, symbols, true, "12" + dfs.getExponentSeparator().toLowerCase() + "5", 12e5));
        list.add(new NumberTestData("ignoreCase: 12" + dfs.getExponentSeparator().toUpperCase() + "5", swissLocale, symbols, true, "12" + dfs.getExponentSeparator().toUpperCase() + "5", 12e5));
        list.add(new NumberTestData("case-sensitive, invalid: 12.3458’67", "12.3458’67", 0, 9, 0, 9, 10, null, SYNTAX_ERROR, NumberFormatException.class, swissLocale, symbols));
        return list;
    }

    public static List<NumberTestData> createArabianNumberFormatSymbolsTestData() {
        List<NumberTestData> list = new ArrayList<>();
        Locale arabianLocale = new Locale("ar");
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(
                arabianLocale
        );
        NumberFormatSymbols symbols = NumberFormatSymbols.fromDecimalFormatSymbols(dfs);
        DecimalFormat fmt = new DecimalFormat("#00.0####E0", dfs);
        for (double n : new double[]{3e-9, -7e8, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
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
