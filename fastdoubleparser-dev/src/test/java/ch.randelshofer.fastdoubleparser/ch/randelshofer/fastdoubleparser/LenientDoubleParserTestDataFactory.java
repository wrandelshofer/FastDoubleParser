/*
 * @(#)LenientDoubleParserTestDataFactory.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LenientDoubleParserTestDataFactory {

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
            list.add(new NumberTestData(locale, languageTag + " " + f.format(v), f.format(v), v));
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
            list.add(new NumberTestData(locale, languageTag + " " + f.format(v), f.format(v), v));
        }

        return list;
    }
}
