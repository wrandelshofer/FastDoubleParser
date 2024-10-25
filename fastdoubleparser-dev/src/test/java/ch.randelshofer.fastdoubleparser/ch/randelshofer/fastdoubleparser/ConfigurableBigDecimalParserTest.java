/*
 * @(#)ConfigurableBigDecimalParserTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static ch.randelshofer.fastdoubleparser.ConfigurableBigDecimalParserTestDataFactory.createLocalizedTestData;
import static ch.randelshofer.fastdoubleparser.ConfigurableBigDecimalParserTestDataFactory.createNumberFormatSymbolsTestData;
import static ch.randelshofer.fastdoubleparser.JavaDoubleTestDataFactory.createLongRunningDoubleTestData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests class {@link new ConfigurableBigDecimalParser()}
 */
@Disabled("ConfigurableBigDecimalParser is not fully implemented yet")
public final class ConfigurableBigDecimalParserTest {

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseBigDecimal_CharSequence() {
        return createRegularDoubleTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> testSkipOffsetLength(t, u -> new ConfigurableBigDecimalParser(u.symbols() == null ? NumberFormatSymbols.fromDefault() : u.symbols(), u.ignoreCase()).parseBigDecimal(u.input(), u.charOffset(), u.charLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseBigDecimal_CharSequence_int_int() {
        return createRegularDoubleTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> new ConfigurableBigDecimalParser(u.symbols() == null ? NumberFormatSymbols.fromDefault() : u.symbols(), u.ignoreCase()).parseBigDecimal(u.input(), u.charOffset(), u.charLength()))));
    }

    @TestFactory
    @Disabled("long running test")
    public Stream<DynamicNode> dynamicTests_parseBigDecimal_CharSequence_int_int_longRunningTests() {
        Function<NumberTestData, BigDecimal> lambda = u -> new ConfigurableBigDecimalParser(u.symbols() == null ? NumberFormatSymbols.fromDefault() : u.symbols(), u.ignoreCase()).parseBigDecimal(u.input(), u.charOffset(), u.charLength());
        return createLongRunningDoubleTestData()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, lambda)));
    }

    public static List<NumberTestDataSupplier> createRegularDoubleTestData() {
        List<NumberTestDataSupplier> list = new ArrayList<>();
        list.addAll(BigDecimalTestDataFactory.createRegularTestData());
        return list;
    }

    @TestFactory
    public List<DynamicNode> dynamicTests_parseBigDecimal_Localized() {
        List<DynamicNode> list = new ArrayList<>();
        for (Locale locale : new Locale[]{new Locale("de", "CH"), new Locale("fr", "FR"), new Locale("ar")}) {

            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) NumberFormat.getNumberInstance(locale)).getDecimalFormatSymbols();
            List<NumberTestData> dataList = new ArrayList<>();
            dataList.addAll(createLocalizedTestData(locale));
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
        test(u, d -> new ConfigurableBigDecimalParser(decimalFormatSymbols, u.ignoreCase()).parseBigDecimal(d.input()));
        test(u, d -> new ConfigurableBigDecimalParser(decimalFormatSymbols, u.ignoreCase()).parseBigDecimal(d.input().toString().toCharArray()));
        test(u, d -> new ConfigurableBigDecimalParser(decimalFormatSymbols, u.ignoreCase()).parseBigDecimal(d.input().toString().getBytes(StandardCharsets.UTF_8)));
    }

    @TestFactory
    public List<DynamicNode> dynamicTests_parseBigDecimal_NumberFormatSymbols() {
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
        test(u, d -> new ConfigurableBigDecimalParser(u.symbols(), u.ignoreCase()).parseBigDecimal(d.input(), d.charOffset(), d.charLength()));
        test(u, d -> new ConfigurableBigDecimalParser(u.symbols(), u.ignoreCase()).parseBigDecimal(d.input().toString().toCharArray(), d.charOffset(), d.charLength()));
        test(u, d -> new ConfigurableBigDecimalParser(u.symbols(), u.ignoreCase()).parseBigDecimal(d.input().toString().getBytes(StandardCharsets.UTF_8), d.byteOffset(), d.byteLength()));
    }


    private void test(NumberTestData d, Function<NumberTestData, BigDecimal> f) {
        if (d.input() instanceof String) {
            if (d.expectedErrorMessage() != null) {
                try {
                    BigDecimal actual = f.apply(d);
                    fail("should throw an exception but returned " + actual);
                } catch (IllegalArgumentException e) {
                    if (!Objects.equals(d.expectedErrorMessage(), e.getMessage())) {
                        e.printStackTrace();
                        assertEquals(d.expectedErrorMessage(), e.getMessage());
                    }
                    assertEquals(d.expectedThrowableClass(), e.getClass());
                }
            } else {
                BigDecimal actual = f.apply(d);
                assertEquals(d.expectedValue(), actual);
            }
        }
    }

    private void test(NumberTestDataSupplier d, Function<NumberTestData, BigDecimal> f) {
        test(d.supplier().get(), f);
    }

    private void testSkipOffsetLength(NumberTestDataSupplier d, Function<NumberTestData, BigDecimal> f) {
        NumberTestData d1 = d.supplier().get();
        if (d1.charOffset() == 0 && d1.charLength() == d1.input().length()) {
            test(d1, f);
        }
    }
}
