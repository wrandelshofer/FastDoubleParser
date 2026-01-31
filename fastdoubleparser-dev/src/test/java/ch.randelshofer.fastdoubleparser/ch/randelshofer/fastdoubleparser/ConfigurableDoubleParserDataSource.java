/*
 * @(#)JavaDoubleParserDataSource.java
 * Copyright © 2025 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.stream.Stream;

public class ConfigurableDoubleParserDataSource {
    private static final boolean longRunningTests = !"false".equals(System.getProperty("enableLongRunningTests"));
    private static final int MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 4;

    public static Stream<NumberTestData> parseDouble_CharSequence() {
        return createRegularDoubleTestData().stream().filter(d -> d.charOffset() == 0 && d.charLength() == d.input().length());
    }

    public static Stream<NumberTestData> parseDouble_CharSequence_int_int() {
        return createRegularDoubleTestData().stream().filter(d -> d.byteLength() < MAX_ARRAY_LENGTH);
    }

    public static Stream<NumberTestData> parseDouble_byteArray() {
        return createRegularDoubleTestData().stream().filter(d -> d.charOffset() == 0 && d.byteLength() == d.input().length() && d.byteLength() < MAX_ARRAY_LENGTH);
    }

    public static Stream<NumberTestData> parseDouble_byteArray_int_int() {
        return createRegularDoubleTestData().stream().filter(d -> d.byteLength() < MAX_ARRAY_LENGTH);
    }

    public static Stream<NumberTestData> parseDouble_charArray() {
        return createRegularDoubleTestData().stream().filter(d -> d.charOffset() == 0 && d.charLength() == d.input().length() && d.charLength() < MAX_ARRAY_LENGTH);
    }

    public static Stream<NumberTestData> parseDouble_charArray_int_int() {
        return createRegularDoubleTestData().stream().filter(d -> d.charLength() < MAX_ARRAY_LENGTH);
    }

    public static List<NumberTestData> createLocalizedTestData(Locale locale, DecimalFormatSymbols decSymbols) {
        NumberFormatSymbols symbols = NumberFormatSymbols.fromDecimalFormatSymbols(decSymbols);
        String languageTag = locale.toLanguageTag();
        List<NumberTestData> list = new ArrayList<>();
        // non-scientific pattern
        DecimalFormat f = new DecimalFormat("#,##0.###", decSymbols);
        for (double v : new double[]{
                1_234_567.89,
                -1_234_567.89}) {
            String formatted = f.format(v);
            list.add(new NumberTestData(languageTag + " " + formatted, locale, formatted, v).withSymbols(symbols));
            list.add(new NumberTestData(languageTag + " IC " + formatted, locale, symbols, true, formatted, v));
            list.add(new NumberTestData(languageTag + " IC-uppercase " + formatted.toUpperCase(), locale, symbols, true, formatted.toUpperCase(), v));
            list.add(new NumberTestData(languageTag + " IC-lowercase " + formatted.toUpperCase().toLowerCase(), locale, symbols, true, formatted.toUpperCase().toLowerCase(), v));
        }
        // scientific pattern
        f = new DecimalFormat("#,###,##0.0########E0", decSymbols);
        for (double v : new double[]{
                3.4e25,
                -3.4e-25,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY,
                Double.NaN}) {
            String formatted = f.format(v);
            list.add(new NumberTestData(languageTag + " " + formatted, locale, formatted, v).withSymbols(symbols));
            list.add(new NumberTestData(languageTag + " IC " + formatted, locale, symbols, true, formatted, v));
            list.add(new NumberTestData(languageTag + " IC-uppercase " + formatted.toUpperCase(), locale, symbols, true, formatted.toUpperCase(), v));
            list.add(new NumberTestData(languageTag + " IC-lowercase " + formatted.toUpperCase().toLowerCase(), locale, symbols, true, formatted.toUpperCase().toLowerCase(), v));
        }
        return list;
    }


    public static List<NumberTestData> createLocalizedTestData(Locale locale, DecimalFormatSymbols decSymbols, List<NumberTestData> inputList) {
        String languageTag = locale.toLanguageTag();
        DecimalFormat f = new DecimalFormat("#,###,##0.0########E0", decSymbols);
        f.setMaximumFractionDigits(768);
        List<NumberTestData> list = new ArrayList<>();
        NumberFormatSymbols symbols = NumberFormatSymbols.fromDecimalFormatSymbols(decSymbols);
        for (NumberTestData t : inputList) {
            Number v = t.expectedValue();
            if (v == null) continue;
            list.add(new NumberTestData(languageTag + " " + f.format(v), locale, f.format(v), v).withSymbols(symbols));
        }
        return list;
    }

    public static List<NumberTestData> createLocalizedTestData() {
        List<NumberTestData> baseList = JavaDoubleDataSource.createFloatTestDataForInputClassesInMethodParseDecLiteral();
        List<NumberTestData> list = new ArrayList<>();
        List<Locale> locales;
        if (longRunningTests) {
            locales = new ArrayList<>(Arrays.asList(Locale.getAvailableLocales()));
        } else {
            locales = new ArrayList<>(List.of(
                    Locale.of("en", "US"),
                    Locale.of("ar"))
            );
        }

        for (Locale locale : locales) {
            list.addAll(createLocalizedTestData(locale, DecimalFormatSymbols.getInstance(locale)));
            if (longRunningTests) {
                list.addAll(createLocalizedTestData(locale, DecimalFormatSymbols.getInstance(locale), baseList));
            }
        }
        DecimalFormatSymbols decSymbols = DecimalFormatSymbols.getInstance(Locale.of("de", "CH"));
        decSymbols.setGroupingSeparator('’');//U+2019 RIGHT SINGLE QUOTATION MARK
        list.addAll(createLocalizedTestData(Locale.of("de", "CH"), decSymbols));
        String languageTag = Locale.of("de", "CH").toLanguageTag() + "_CUSTOM";
        DecimalFormat f = new DecimalFormat("#,##0.###", decSymbols);
        NumberFormatSymbols symbols = NumberFormatSymbols.fromDecimalFormatSymbols(decSymbols);
        Set<String> newValue = new java.util.HashSet<>();
        newValue.add(decSymbols.getInfinity());
        newValue.add("Inf");
        newValue.add("Infinity");
        symbols = symbols.withInfinity(newValue);
        for (double v : new double[]{
                1_234_567.89,
                -1_234_567.89}) {
            String formatted = f.format(v);
            list.add(new NumberTestData(languageTag + " " + formatted, Locale.of("de", "CH"), formatted, v).withSymbols(symbols));
            list.add(new NumberTestData(languageTag + " IC " + formatted, Locale.of("de", "CH"), symbols, true, formatted, v));
            list.add(new NumberTestData(languageTag + " IC-uppercase " + formatted.toUpperCase(), Locale.of("de", "CH"), symbols, true, formatted.toUpperCase(), v));
            list.add(new NumberTestData(languageTag + " IC-lowercase " + formatted.toUpperCase().toLowerCase(), Locale.of("de", "CH"), symbols, true, formatted.toUpperCase().toLowerCase(), v));
        }
        // scientific pattern
        f = new DecimalFormat("#,###,##0.0########E0", decSymbols);
        for (double v : new double[]{
                3.4e25,
                -3.4e-25,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY,
                Double.NaN}) {
            String formatted = f.format(v);
            list.add(new NumberTestData(languageTag + " " + formatted, Locale.of("de", "CH"), formatted, v).withSymbols(symbols));
            list.add(new NumberTestData(languageTag + " IC " + formatted, Locale.of("de", "CH"), symbols, true, formatted, v));
            list.add(new NumberTestData(languageTag + " IC-uppercase " + formatted.toUpperCase(), Locale.of("de", "CH"), symbols, true, formatted.toUpperCase(), v));
            list.add(new NumberTestData(languageTag + " IC-lowercase " + formatted.toUpperCase().toLowerCase(), Locale.of("de", "CH"), symbols, true, formatted.toUpperCase().toLowerCase(), v));
        }
        list.add(new NumberTestData(languageTag + " Inf", Locale.of("de", "CH"), symbols, true, "Inf", Double.POSITIVE_INFINITY));
        list.add(new NumberTestData(languageTag + " Infi", Locale.of("de", "CH"), symbols, true, "Infi", null).withException(AbstractFloatValueParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class));


        return list;
    }

    public static List<NumberTestData> createCustomTestData() {
        List<NumberTestData> baseList = JavaDoubleDataSource.createFloatTestDataForInputClassesInMethodParseDecLiteral();
        List<NumberTestData> list = new ArrayList<>();
        List<Locale> locales;
        for (Locale locale : new Locale[]{Locale.of("de", "CH")}) {
            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
            symbols.setGroupingSeparator('’');//U+2019 RIGHT SINGLE QUOTATION MARK
            list.addAll(createLocalizedTestData(locale, symbols));
        }
        return list;
    }

    public static List<NumberTestData> createDataForIllegalDecStringsWithWhitespace() {
        return Arrays.asList(
                new NumberTestData("AbstractConfigurableFloatingPointBitsFromXXX.parseFloatValue(): Blank String", "   ", 0, 3, 0, 3, AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class)
        );
    }


    public static List<NumberTestData> createRegularDoubleTestData() {
        List<NumberTestData> list = new ArrayList<>();
        list.addAll(JavaFloatingPointDataSource.createTestDataForInfinity());
        list.addAll(JavaFloatingPointDataSource.createTestDataForNaN());
        list.addAll(GenericDoubleDataSource.createDataForDoubleDecimalLimits());
        list.addAll(GenericFloatingPointDataSource.createDataForIllegalStrings());
        list.addAll(GenericDoubleDataSource.createDataForLegalDoubleDecStrings());
        list.addAll(GenericDoubleDataSource.createDataForDoubleDecimalClingerInputClasses());
        list.addAll(JavaDoubleDataSource.createDataForLegalCroppedStrings());
        list.addAll(JavaDoubleDataSource.createFloatTestDataForInputClassesInMethodParseFloatValue());
        list.addAll(JavaDoubleDataSource.createFloatTestDataForInputClassesInMethodParseDecLiteral());
        list.addAll(GenericDoubleDataSource.createDataForDoubleSignificandDigitsInputClasses());
        list.addAll(createLocalizedTestData());
        list.addAll(createCustomTestData());
        list.addAll(createDataForIllegalDecStringsWithWhitespace());
        if (longRunningTests) {
            list.addAll(GenericFloatDataSource.createDataWithVeryLongInputStrings());
        }
        return list;
    }
}
