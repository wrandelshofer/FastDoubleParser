/*
 * @(#)ConfigurableDoubleParserTestDataFactory.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

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

        //--
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
                new NumberTestData("Estonian locale with Estonian minus", dfs.getLocale(), symbols, dfs.getMinusSign() + "13,35", -13.35),
                new NumberTestData("Estonian locale with ordinary minus", dfs.getLocale(), symbols, "-13,35", -13.35),
                new NumberTestData("Estonian locale with full-width plus", dfs.getLocale(), symbols, "\uff0b13,35", 13.35),
                new NumberTestData("Estonian locale with ordinary plus", dfs.getLocale(), symbols, "+13,35", 13.35)
        ));
        //--

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
