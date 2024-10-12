/*
 * @(#)new LenientDoubleParser()Test.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import static ch.randelshofer.fastdoubleparser.FloatValueTestDataFactory.createDataForDoubleDecimalClingerInputClasses;
import static ch.randelshofer.fastdoubleparser.FloatValueTestDataFactory.createDataForDoubleDecimalLimits;
import static ch.randelshofer.fastdoubleparser.FloatValueTestDataFactory.createDataForSignificandDigitsInputClasses;
import static ch.randelshofer.fastdoubleparser.JavaDoubleTestDataFactory.createDataForBadStrings;
import static ch.randelshofer.fastdoubleparser.JavaDoubleTestDataFactory.createDataForLegalCroppedStrings;
import static ch.randelshofer.fastdoubleparser.JavaDoubleTestDataFactory.createDataForLegalDecStrings;
import static ch.randelshofer.fastdoubleparser.JavaDoubleTestDataFactory.createFloatTestDataForInputClassesInMethodParseFloatValue;
import static ch.randelshofer.fastdoubleparser.JavaDoubleTestDataFactory.createLongRunningDoubleTestData;
import static ch.randelshofer.fastdoubleparser.JavaDoubleTestDataFactory.createTestDataForInfinity;
import static ch.randelshofer.fastdoubleparser.JavaDoubleTestDataFactory.createTestDataForNaN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests class {@link new LenientDoubleParser()}
 */
public class LenientDoubleParserTest {
    public static final int EXPECTED_MAX_INPUT_LENGTH = Integer.MAX_VALUE - 4;

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_CharSequence() {
        return createRegularDoubleTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> new LenientDoubleParser().parseDouble(u.input()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_CharSequence_int_int() {
        return createRegularDoubleTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> new LenientDoubleParser().parseDouble(u.input(), u.charOffset(), u.charLength()))));
    }

    @TestFactory
    @Disabled("long running test")
    public Stream<DynamicNode> dynamicTests_parseDouble_CharSequence_int_int_longRunningTests() {
        ToDoubleFunction<NumberTestData> lambda = u -> new LenientDoubleParser().parseDouble(u.input(), u.charOffset(), u.charLength());
        return createLongRunningDoubleTestData()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, lambda)));
    }

    public static List<NumberTestData> createRegularDoubleTestData() {
        List<NumberTestData> list = new ArrayList<>();
        list.addAll(createTestDataForInfinity());
        list.addAll(createTestDataForNaN());
        list.addAll(createDataForDoubleDecimalLimits());
        list.addAll(createDataForBadStrings());
        list.addAll(createDataForLegalDecStrings());
        list.addAll(createDataForDoubleDecimalClingerInputClasses());
        list.addAll(createDataForLegalCroppedStrings());
        list.addAll(createFloatTestDataForInputClassesInMethodParseFloatValue());
        list.addAll(createDataForSignificandDigitsInputClasses());
        return list;
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_Localized() {
        String languageTag = "de-CH";
        Locale locale = Locale.forLanguageTag(languageTag);
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) NumberFormat.getNumberInstance(locale)).getDecimalFormatSymbols();
        return createLocalizedTestData(locale).stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> new LenientDoubleParser(decimalFormatSymbols).parseDouble(u.input()))));
    }

    public static List<NumberTestData> createLocalizedTestData(Locale locale) {
        String languageTag = locale.toLanguageTag();
        NumberFormat f = NumberFormat.getNumberInstance(locale);
        List<NumberTestData> list = new ArrayList<>();
        for (double v : new double[]{1_000_000.05, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NaN}) {
            list.add(new NumberTestData(languageTag + " " + f.format(v), f.format(v), v));
        }
        return list;
    }

    private void test(NumberTestData d, ToDoubleFunction<NumberTestData> f) {
        if (d.input() instanceof String) {
            if (d.expectedErrorMessage() != null) {
                try {
                    double actual = f.applyAsDouble(d);
                    fail("should throw an exception but returned " + actual);
                } catch (IllegalArgumentException e) {
                    if (!Objects.equals(d.expectedErrorMessage(), e.getMessage())) {
                        e.printStackTrace();
                        assertEquals(d.expectedErrorMessage(), e.getMessage());
                    }
                    assertEquals(d.expectedThrowableClass(), e.getClass());
                }
            } else {
                double actual = f.applyAsDouble(d);
                assertEquals(d.expectedValue().doubleValue(), actual);
            }
        }
    }
}
