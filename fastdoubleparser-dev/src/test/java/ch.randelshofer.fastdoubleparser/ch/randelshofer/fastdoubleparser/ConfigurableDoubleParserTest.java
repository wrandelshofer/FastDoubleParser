/*
 * @(#)ConfigurableDoubleParserTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import static ch.randelshofer.fastdoubleparser.ConfigurableDoubleParserTestDataFactory.createLocalizedTestData;
import static ch.randelshofer.fastdoubleparser.ConfigurableDoubleParserTestDataFactory.createNumberFormatSymbolsTestData;
import static ch.randelshofer.fastdoubleparser.FloatValueTestDataFactory.createDataForDoubleDecimalClingerInputClasses;
import static ch.randelshofer.fastdoubleparser.FloatValueTestDataFactory.createDataForDoubleDecimalLimits;
import static ch.randelshofer.fastdoubleparser.FloatValueTestDataFactory.createDataForSignificandDigitsInputClasses;
import static ch.randelshofer.fastdoubleparser.JavaDoubleTestDataFactory.createDataForBadStrings;
import static ch.randelshofer.fastdoubleparser.JavaDoubleTestDataFactory.createDataForLegalCroppedStrings;
import static ch.randelshofer.fastdoubleparser.JavaDoubleTestDataFactory.createDataForLegalDecStrings;
import static ch.randelshofer.fastdoubleparser.JavaDoubleTestDataFactory.createFloatTestDataForInputClassesInMethodParseFloatValue;
import static ch.randelshofer.fastdoubleparser.JavaDoubleTestDataFactory.createLongRunningDoubleTestData;
import static ch.randelshofer.fastdoubleparser.JavaFloatTestDataFactory.createTestDataForInfinity;
import static ch.randelshofer.fastdoubleparser.JavaFloatTestDataFactory.createTestDataForNaN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests class {@link new ConfigurableDoubleParser()}
 */
public class ConfigurableDoubleParserTest {

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_CharSequence() {
        return createRegularDoubleTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> new ConfigurableDoubleParser(u.symbols() == null ? NumberFormatSymbols.fromDefault() : u.symbols(), u.ignoreCase()).parseDouble(u.input()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_CharSequence_int_int() {
        return createRegularDoubleTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> new ConfigurableDoubleParser(u.symbols() == null ? NumberFormatSymbols.fromDefault() : u.symbols(), u.ignoreCase()).parseDouble(u.input(), u.charOffset(), u.charLength()))));
    }

    @TestFactory
    @Disabled("long running test")
    public Stream<DynamicNode> dynamicTests_parseDouble_CharSequence_int_int_longRunningTests() {
        ToDoubleFunction<NumberTestData> lambda = u -> new ConfigurableDoubleParser(u.symbols() == null ? NumberFormatSymbols.fromDefault() : u.symbols(), u.ignoreCase()).parseDouble(u.input(), u.charOffset(), u.charLength());
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
        list.addAll(createNumberFormatSymbolsTestData());
        return list;
    }

    @TestFactory
    public List<DynamicNode> dynamicTests_parseDouble_Localized() {
        List<DynamicNode> list = new ArrayList<>();
        for (Locale locale : new Locale[]{new Locale("de,CH"), new Locale("fr", "FR"), new Locale("ar")}) {

            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) NumberFormat.getNumberInstance(locale)).getDecimalFormatSymbols();
            List<NumberTestData> dataList = new ArrayList<>();
            dataList.addAll(createLocalizedTestData(locale));
            dataList.addAll(createLocalizedTestData(locale, createFloatTestDataForInputClassesInMethodParseFloatValue()));
            dataList.addAll(createLocalizedTestData(locale, createDataForSignificandDigitsInputClasses()));
            dataList.stream()
                    .filter(t -> t.charLength() == t.input().length()
                            && t.charOffset() == 0)
                    .map(t -> dynamicTest(t.title(),
                            () -> performTestDecimalFormatSymbols(t, decimalFormatSymbols)))
                    .forEach(list::add);
        }

        return list;
    }


    public void performTestDecimalFormatSymbols(NumberTestData u, DecimalFormatSymbols decimalFormatSymbols) {
        test(u, d -> new ConfigurableDoubleParser(decimalFormatSymbols, u.ignoreCase()).parseDouble(d.input()));
        test(u, d -> new ConfigurableDoubleParser(decimalFormatSymbols, u.ignoreCase()).parseDouble(d.input().toString().toCharArray()));
        test(u, d -> new ConfigurableDoubleParser(decimalFormatSymbols, u.ignoreCase()).parseDouble(d.input().toString().getBytes(StandardCharsets.UTF_8)));
    }

    @TestFactory
    public List<DynamicNode> dynamicTests_parseDouble_NumberFormatSymbols() {
        List<DynamicNode> list = new ArrayList<>();
        List<NumberTestData> dataList = new ArrayList<>();
        dataList.addAll(createNumberFormatSymbolsTestData());
            dataList.stream()
                    .filter(t -> t.charLength() == t.input().length()
                            && t.charOffset() == 0)
                    .map(t -> dynamicTest(t.title(),
                            () -> performTestNumberFormatSymbols(t)))
                    .forEach(list::add);


        return list;
    }


    public void performTestNumberFormatSymbols(NumberTestData u) {
        test(u, d -> new ConfigurableDoubleParser(u.symbols(), u.ignoreCase()).parseDouble(d.input()));
        test(u, d -> new ConfigurableDoubleParser(u.symbols(), u.ignoreCase()).parseDouble(d.input().toString().toCharArray()));
        test(u, d -> new ConfigurableDoubleParser(u.symbols(), u.ignoreCase()).parseDouble(d.input().toString().getBytes(StandardCharsets.UTF_8)));
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
