/*
 * @(#)JavaBigDecimalParserTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests class {@link JavaBigDecimalParser}
 * <p>
 * The goal of this test is to achieve 100% code coverage (in the class, method, line and branch metric)
 * in the following source files:
 * <ul>
 *     <li>{@link JavaBigDecimalFromByteArray}</li>
 *     <li>{@link JavaBigDecimalFromCharArray}</li>
 *     <li>{@link JavaBigDecimalFromCharSequence}</li>
 *     <li>{@link JavaBigDecimalParser}</li>
 * </ul>
 */
public final class JavaBigDecimalParserTest {


    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaBigDecimalParserDataSource#parseBigDecimal_CharSequence")
    public void parseBigDecimal_CharSequence(NumberTestDataSupplier ds) {
        NumberTestData d = ds.supplier().get();
        if (d.charOffset() != 0 || d.charLength() != d.input().length()) return;
        if (d.expectedThrowableClass() == null) {
            BigDecimal actual = JavaBigDecimalParser.parseBigDecimal(d.input());
            assertEquals(d.expectedValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaBigDecimalParser.parseBigDecimal(d.input()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaBigDecimalParserDataSource#parseBigDecimal_CharSequence_int_int")
    public void parseBigDecimal_CharSequence_int_int(NumberTestDataSupplier ds) {
        NumberTestData d = ds.supplier().get();
        if (d.expectedThrowableClass() == null) {
            BigDecimal actual = JavaBigDecimalParser.parseBigDecimal(d.input(), d.charOffset(), d.charLength());
            assertEquals(d.expectedValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaBigDecimalParser.parseBigDecimal(d.input(), d.charOffset(), d.charLength()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaBigDecimalParserDataSource#parseBigDecimal_byteArray")
    public void parseBigDecimal_byteArray(NumberTestDataSupplier ds) {
        NumberTestData d = ds.supplier().get();
        if (d.charOffset() != 0 || d.charLength() != d.input().length()) return;
        if (d.expectedThrowableClass() == null) {
            BigDecimal actual = JavaBigDecimalParser.parseBigDecimal(d.input().toString().getBytes(StandardCharsets.UTF_8));
            assertEquals(d.expectedValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaBigDecimalParser.parseBigDecimal(d.input().toString().getBytes(StandardCharsets.UTF_8)));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaBigDecimalParserDataSource#parseBigDecimal_byteArray_int_int")
    public void parseBigDecimal_byteArray_int_int(NumberTestDataSupplier ds) {
        NumberTestData d = ds.supplier().get();
        if (d.expectedThrowableClass() == null) {
            BigDecimal actual = JavaBigDecimalParser.parseBigDecimal(d.input().toString().getBytes(StandardCharsets.UTF_8), d.byteOffset(), d.byteLength());
            assertEquals(d.expectedValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaBigDecimalParser.parseBigDecimal(d.input().toString().getBytes(StandardCharsets.UTF_8), d.byteOffset(), d.byteLength()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaBigDecimalParserDataSource#parseBigDecimal_charArray")
    public void parseBigDecimal_charArray(NumberTestDataSupplier ds) {
        NumberTestData d = ds.supplier().get();
        if (d.charOffset() != 0 || d.charLength() != d.input().length()) return;
        if (d.expectedThrowableClass() == null) {
            BigDecimal actual = JavaBigDecimalParser.parseBigDecimal(d.input().toString().toCharArray());
            assertEquals(d.expectedValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaBigDecimalParser.parseBigDecimal(d.input().toString().toCharArray()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaBigDecimalParserDataSource#parseBigDecimal_charArray_int_int")
    public void parseBigDecimal_charArray_int_int(NumberTestDataSupplier ds) {
        NumberTestData d = ds.supplier().get();
        if (d.expectedThrowableClass() == null) {
            BigDecimal actual = JavaBigDecimalParser.parseBigDecimal(d.input().toString().toCharArray(), d.charOffset(), d.charLength());
            assertEquals(d.expectedValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaBigDecimalParser.parseBigDecimal(d.input().toString().toCharArray(), d.charOffset(), d.charLength()));
        }
    }


}
