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
        DecimalFormat f = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        f.applyPattern("####,##0.0######E0##");
        NumberFormatSymbols symbols = NumberFormatSymbols.fromDecimalFormatSymbols(f.getDecimalFormatSymbols());
        List<NumberTestData> list = new ArrayList<>();
        for (double v : new double[]{
                1_234_567.89,
                -1_234_567.89,
                3e25,
                -3e25,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY,
                Double.NaN}) {
            String formatted = f.format(v);
            list.add(new NumberTestData(languageTag + " " + formatted, locale, formatted, v));
            list.add(new NumberTestData(languageTag + " IC " + formatted, locale, symbols, true, formatted, v));
            list.add(new NumberTestData(languageTag + " IC-uppercase " + formatted.toUpperCase(), locale, symbols, true, formatted.toUpperCase(), v));
            list.add(new NumberTestData(languageTag + " IC-lowercase " + formatted.toUpperCase().toLowerCase(), locale, symbols, true, formatted.toUpperCase().toLowerCase(), v));
        }

        return list;
    }

    public static List<NumberTestData> createNumberFormatSymbolsTestData() {
        List<NumberTestData> list = new ArrayList<>();
        list.addAll(createSwissIgnoreCaseNumberFormatSymbolsTestData());
        list.addAll(createChineseNumberFormatSymbolsTestData());
        list.addAll(createEnglishIgnoreCaseNumberFormatSymbolsTestData());
        list.addAll(createArabianNumberFormatSymbolsTestData());
        list.addAll(createCustomArabianNumberFormatSymbolsTestData());
        list.addAll(createEstonianNumberFormatSymbolsTestData());
        return list;
    }

    public static List<NumberTestData> createEstonianNumberFormatSymbolsTestData() {
        List<NumberTestData> list = new ArrayList<>();
        Locale locale = new Locale("et", "EE");
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(
                locale
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
        list.addAll(Arrays.asList(
                new NumberTestData(locale + " with Estonian minus", locale, symbols, "\u221213,35", -13.35),
                new NumberTestData(locale + " with ordinary minus", locale, symbols, "-13,35", -13.35),
                new NumberTestData(locale + " with full-width plus", locale, symbols, "\uff0b13,35", 13.35),
                new NumberTestData(locale + " with ordinary plus", locale, symbols, "+13,35", 13.35),
                new NumberTestData(locale + " with Estonian minus exponent", locale, symbols, "13,35×10^\u22124", 13.35e-4),
                new NumberTestData(locale + " with ordinary minus exponent", locale, symbols, "13,35×10^-4", 13.35e-4),
                new NumberTestData(locale + " with full-width plus exponent", locale, symbols, "13,35×10^\uff0b4", 13.35e4),
                new NumberTestData(locale + " with ordinary plus exponent", locale, symbols, "13,35×10^+4", 13.35e4),
                new NumberTestData(locale + ", Outside Clinger fast path, mantissa overflows in semi-fast path, 7.2057594037927933e+16",
                        locale, symbols, "7,2057594037927933×10^16", 7.2057594037927933e+16d)
        ));
        return list;
    }

    public static List<NumberTestData> createChineseNumberFormatSymbolsTestData() {
        List<NumberTestData> list = new ArrayList<>();
        Locale locale = new Locale("zh", "CN");
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(
                locale
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
        list.addAll(Arrays.asList(
                new NumberTestData(locale + " locale 一,二三四,五六七.〇八九", locale, symbols, "一,二三四,五六七.〇八九", 1234567.089),
                new NumberTestData(locale + " locale 〇.五六四", locale, symbols, "〇.五六四", 0.564)
        ));
        return list;
    }

    public static List<NumberTestData> createEnglishIgnoreCaseNumberFormatSymbolsTestData() {
        List<NumberTestData> list = new ArrayList<>();
        Locale locale = Locale.ENGLISH;
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(locale);
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
            list.add(new NumberTestData(locale + " ignoreCase: " + fmt.format(n), locale, symbols, true, fmt.format(n), n));
            list.add(new NumberTestData(locale + " ignoreCase: lower-case " + fmt.format(n).toLowerCase(locale), locale, symbols, true, fmt.format(n).toLowerCase(locale), n));
            list.add(new NumberTestData(locale + " ignoreCase: upper-case " + fmt.format(n).toUpperCase(locale), locale, symbols, true, fmt.format(n).toUpperCase(locale), n));
        }
        list.add(new NumberTestData(locale + " ignoreCase: " + dfs.getNaN().toLowerCase(), locale, symbols, true, dfs.getNaN().toLowerCase(), Double.NaN));
        list.add(new NumberTestData(locale + " ignoreCase: " + dfs.getNaN().toUpperCase(), locale, symbols, true, dfs.getNaN().toUpperCase(), Double.NaN));
        list.add(new NumberTestData(locale + " ignoreCase: 12" + dfs.getExponentSeparator().toLowerCase() + "5", locale, symbols, true, "12" + dfs.getExponentSeparator().toLowerCase() + "5", 12e5));
        list.add(new NumberTestData(locale + " ignoreCase: 12" + dfs.getExponentSeparator().toUpperCase() + "5", locale, symbols, true, "12" + dfs.getExponentSeparator().toUpperCase() + "5", 12e5));

        return list;
    }

    public static List<NumberTestData> createSwissIgnoreCaseNumberFormatSymbolsTestData() {
        List<NumberTestData> list = new ArrayList<>();
        Locale locale = new Locale("de", "CH");
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(locale);
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
        list.add(new NumberTestData(locale + " ignoreCase: 12’961’872.332", locale, symbols, true, "12’961’872.332", 12961872.332));
        list.add(new NumberTestData(locale + " ignoreCase: " + dfs.getNaN().toLowerCase(), locale, symbols, true, dfs.getNaN().toLowerCase(), Double.NaN));
        list.add(new NumberTestData(locale + " ignoreCase: " + dfs.getNaN().toUpperCase(), locale, symbols, true, dfs.getNaN().toUpperCase(), Double.NaN));
        list.add(new NumberTestData(locale + " ignoreCase: 12" + dfs.getExponentSeparator().toLowerCase() + "5", locale, symbols, true, "12" + dfs.getExponentSeparator().toLowerCase() + "5", 12e5));
        list.add(new NumberTestData(locale + " ignoreCase: 12" + dfs.getExponentSeparator().toUpperCase() + "5", locale, symbols, true, "12" + dfs.getExponentSeparator().toUpperCase() + "5", 12e5));
        list.add(new NumberTestData(locale + " case-sensitive, invalid: 12.3458’67", "12.3458’67", 0, 9, 0, 9, 10, null, SYNTAX_ERROR, NumberFormatException.class, locale, symbols));
        return list;
    }

    public static List<NumberTestData> createArabianNumberFormatSymbolsTestData() {
        List<NumberTestData> list = new ArrayList<>();
        Locale locale = new Locale("ar");
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(
                locale
        );
        NumberFormatSymbols symbols = NumberFormatSymbols.fromDecimalFormatSymbols(dfs);
        DecimalFormat fmt = new DecimalFormat("#00.0####E0", dfs);
        for (double n : new double[]{3e-9, -7e8, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            list.add(new NumberTestData(locale + " " + fmt.format(n), locale, symbols, fmt.format(n), n));
        }
        list.add(new NumberTestData(locale + " U+061C followed by - sign", locale, symbols, true, "\u061C-١٢٣٬٤٥٦٫٧٨٩", -123_456.789));
        list.add(new NumberTestData(locale + " U+061C not followed by - sign", locale, symbols, true, "\u061C١٢٣٬٤٥٦٫٧٨٩", 123_456.789));

        return list;
    }

    public static List<NumberTestData> createCustomArabianNumberFormatSymbolsTestData() {
        List<NumberTestData> list = new ArrayList<>();
        Locale locale = new Locale("ar");
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(
                locale
        );
        NumberFormatSymbols symbols = NumberFormatSymbols.fromDecimalFormatSymbols(dfs);
        symbols.withMinusSign(new LinkedHashSet<>(Arrays.asList('-', '\u061C')));

        DecimalFormat fmt = new DecimalFormat("#00.0####E0", dfs);
        for (double n : new double[]{3e-9, -7e8, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            list.add(new NumberTestData(locale + " " + fmt.format(n), locale, symbols, fmt.format(n), n));
        }
        list.add(new NumberTestData(locale + " customized locale, U+061C followed by - sign", locale, symbols, true, "\u061C-١٢٣٬٤٥٦٫٧٨٩", -123_456.789));
        // list.add(new NumberTestData(locale+" customized locale, U+061C not followed by - sign", locale, symbols, true, "\u061C١٢٣٬٤٥٦٫٧٨٩", -123_456.789));

        return list;
    }

    public static List<NumberTestData> createDataForLegalConfiguredStrings() {
        return Arrays.asList(
                new NumberTestData("NaN+", Double.NaN),
                new NumberTestData("Infinity+", Double.POSITIVE_INFINITY),
                new NumberTestData("NaN-", Double.NaN),
                new NumberTestData("Infinity-", Double.NEGATIVE_INFINITY),
                new NumberTestData("1+", 1),
                new NumberTestData("1.2+", 1.2),
                new NumberTestData("1.2+e3", 1.2e3),
                new NumberTestData("1.2+E3", 1.2e3),
                new NumberTestData("1.2+e3", 1.2e3),
                new NumberTestData("1-", -1),
                new NumberTestData("1.2-", -1.2),
                new NumberTestData("1.2-e3", -1.2e3),
                new NumberTestData("1.2-E3", -1.2e3),
                new NumberTestData("1.2-e3", -1.2e3),
                new NumberTestData("1.2e-3", 1.2e-3),
                new NumberTestData("1.2E-3", 1.2e-3),
                new NumberTestData("1.2e-3", 1.2e-3),
                new NumberTestData("1.2e3-", 1.2e-3),
                new NumberTestData("1.2E3-", 1.2e-3),
                new NumberTestData("1.2e+3", 1.2e3),
                new NumberTestData("1.2e+3", 1.2e3),
                new NumberTestData("1.2E+3", 1.2e3),
                new NumberTestData("1.2e3+", 1.2e3),
                new NumberTestData("1.2e3+", 1.2e3),
                new NumberTestData("1.2E3+", 1.2e3)
        );
    }

    public static List<NumberTestData> createDataForBadConfiguredStrings() {
        return Arrays.asList(
                new NumberTestData("+NaN+"),
                new NumberTestData("-NaN-"),
                new NumberTestData("+NaN-"),
                new NumberTestData("-NaN+"),
                new NumberTestData("+Infinity+"),
                new NumberTestData("+Infinity-"),
                new NumberTestData("-Infinity+"),
                new NumberTestData("-Infinity-"),
                new NumberTestData("+1+"),
                new NumberTestData("-1.2+"),
                new NumberTestData("+1.2+e3"),
                new NumberTestData("-1.2+E3"),
                new NumberTestData("+1.2+e3"),
                new NumberTestData("-1-"),
                new NumberTestData("-1.2-"),
                new NumberTestData("-1.2-e3"),
                new NumberTestData("-1.2-E3"),
                new NumberTestData("-1.2-e3"),
                new NumberTestData("-1.2-e3"),
                new NumberTestData("-1.2-E-3"),
                new NumberTestData("-1.2e-3-"),
                new NumberTestData("1.2e-3-"),
                new NumberTestData("1.2E-3-"),
                new NumberTestData("1.2e+3+"),
                new NumberTestData("1.2e+3+"),
                new NumberTestData("1.2E+3+"),
                new NumberTestData("1.2e+3+"),
                new NumberTestData("1.2e+3+"),
                new NumberTestData("1.2E+3+")
        );
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
