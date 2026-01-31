/*
 * @(#)JavaBigIntegerParserTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests class {@link JavaBigIntegerParser}
 * <p>
 * The goal of this test is to achieve 100% code coverage (in the class, method, line and branch metric)
 * in the following source files:
 * <ul>
 *     <li>{@link JavaBigIntegerFromByteArray}</li>
 *     <li>{@link JavaBigIntegerFromCharArray}</li>
 *     <li>{@link JavaBigIntegerFromCharSequence}</li>
 *     <li>{@link JavaBigIntegerParser}</li>
 * </ul>
 */
public final class JavaBigIntegerParserTest {


    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaBigIntegerParserDataSource#parseBigInteger_CharSequence")
    public void parseBigInteger_CharSequence(NumberTestDataSupplier ds) {
        NumberTestData d = ds.supplier().get();
        if (d.radix() != 10 || d.charOffset() != 0 || d.charLength() != d.input().length()) return;
        if (d.expectedThrowableClass() == null) {
            BigInteger actual = JavaBigIntegerParser.parseBigInteger(d.input());
            assertEquals(d.expectedValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaBigIntegerParser.parseBigInteger(d.input()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaBigIntegerParserDataSource#parseBigInteger_CharSequence_int_int")
    public void parseBigInteger_CharSequence_int_int(NumberTestDataSupplier ds) {
        NumberTestData d = ds.supplier().get();
        if (d.radix() != 10) return;
        if (d.expectedThrowableClass() == null) {
            BigInteger actual = JavaBigIntegerParser.parseBigInteger(d.input(), d.charOffset(), d.charLength());
            assertEquals(d.expectedValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaBigIntegerParser.parseBigInteger(d.input(), d.charOffset(), d.charLength()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaBigIntegerParserDataSource#parseBigInteger_byteArray")
    public void parseBigInteger_byteArray(NumberTestDataSupplier ds) {
        NumberTestData d = ds.supplier().get();
        if (d.radix() != 10 || d.charOffset() != 0 || d.charLength() != d.input().length()) return;
        if (d.expectedThrowableClass() == null) {
            BigInteger actual = JavaBigIntegerParser.parseBigInteger(d.input().toString().getBytes(StandardCharsets.UTF_8));
            assertEquals(d.expectedValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaBigIntegerParser.parseBigInteger(d.input().toString().getBytes(StandardCharsets.UTF_8)));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaBigIntegerParserDataSource#parseBigInteger_byteArray_int_int")
    public void parseBigInteger_byteArray_int_int(NumberTestDataSupplier ds) {
        NumberTestData d = ds.supplier().get();
        if (d.radix() != 10) return;
        if (d.expectedThrowableClass() == null) {
            BigInteger actual = JavaBigIntegerParser.parseBigInteger(d.input().toString().getBytes(StandardCharsets.UTF_8), d.byteOffset(), d.byteLength());
            assertEquals(d.expectedValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaBigIntegerParser.parseBigInteger(d.input().toString().getBytes(StandardCharsets.UTF_8), d.byteOffset(), d.byteLength()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaBigIntegerParserDataSource#parseBigInteger_charArray")
    public void parseBigInteger_charArray(NumberTestDataSupplier ds) {
        NumberTestData d = ds.supplier().get();
        if (d.radix() != 10 || d.charOffset() != 0 || d.charLength() != d.input().length()) return;
        if (d.expectedThrowableClass() == null) {
            BigInteger actual = JavaBigIntegerParser.parseBigInteger(d.input().toString().toCharArray());
            assertEquals(d.expectedValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaBigIntegerParser.parseBigInteger(d.input().toString().toCharArray()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaBigIntegerParserDataSource#parseBigInteger_charArray_int_int")
    public void parseBigInteger_charArray_int_int(NumberTestDataSupplier ds) {
        NumberTestData d = ds.supplier().get();
        if (d.radix() != 10) return;
        if (d.expectedThrowableClass() == null) {
            BigInteger actual = JavaBigIntegerParser.parseBigInteger(d.input().toString().toCharArray(), d.charOffset(), d.charLength());
            assertEquals(d.expectedValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaBigIntegerParser.parseBigInteger(d.input().toString().toCharArray(), d.charOffset(), d.charLength()));
        }
    }


    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaBigIntegerParserDataSource#parseBigInteger_CharSequence_int")
    public void parseBigInteger_CharSequence_int(NumberTestDataSupplier ds) {
        NumberTestData d = ds.supplier().get();
        if (d.charOffset() != 0 || d.charLength() != d.input().length()) return;
        if (d.expectedThrowableClass() == null) {
            BigInteger actual = JavaBigIntegerParser.parseBigInteger(d.input(), d.radix());
            assertEquals(d.expectedValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaBigIntegerParser.parseBigInteger(d.input(), d.radix()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaBigIntegerParserDataSource#parseBigInteger_CharSequence_int_int_int")
    public void parseBigInteger_CharSequence_int_int_int(NumberTestDataSupplier ds) {
        NumberTestData d = ds.supplier().get();
        if (d.expectedThrowableClass() == null) {
            BigInteger actual = JavaBigIntegerParser.parseBigInteger(d.input(), d.charOffset(), d.charLength(), d.radix());
            assertEquals(d.expectedValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaBigIntegerParser.parseBigInteger(d.input(), d.charOffset(), d.charLength(), d.radix()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaBigIntegerParserDataSource#parseBigInteger_byteArray_int")
    public void parseBigInteger_byteArray_int(NumberTestDataSupplier ds) {
        NumberTestData d = ds.supplier().get();
        if (d.charOffset() != 0 || d.charLength() != d.input().length()) return;
        if (d.expectedThrowableClass() == null) {
            BigInteger actual = JavaBigIntegerParser.parseBigInteger(d.input().toString().getBytes(StandardCharsets.UTF_8), d.radix());
            assertEquals(d.expectedValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaBigIntegerParser.parseBigInteger(d.input().toString().getBytes(StandardCharsets.UTF_8), d.radix()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaBigIntegerParserDataSource#parseBigInteger_byteArray_int_int_int")
    public void parseBigInteger_byteArray_int_int_int(NumberTestDataSupplier ds) {
        NumberTestData d = ds.supplier().get();
        if (d.expectedThrowableClass() == null) {
            BigInteger actual = JavaBigIntegerParser.parseBigInteger(d.input().toString().getBytes(StandardCharsets.UTF_8), d.byteOffset(), d.byteLength(), d.radix());
            assertEquals(d.expectedValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaBigIntegerParser.parseBigInteger(d.input().toString().getBytes(StandardCharsets.UTF_8), d.byteOffset(), d.byteLength(), d.radix()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaBigIntegerParserDataSource#parseBigInteger_charArray_int")
    public void parseBigInteger_charArray_int(NumberTestDataSupplier ds) {
        NumberTestData d = ds.supplier().get();
        if (d.charOffset() != 0 || d.charLength() != d.input().length()) return;
        if (d.expectedThrowableClass() == null) {
            BigInteger actual = JavaBigIntegerParser.parseBigInteger(d.input().toString().toCharArray(), d.radix());
            assertEquals(d.expectedValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaBigIntegerParser.parseBigInteger(d.input().toString().toCharArray(), d.radix()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaBigIntegerParserDataSource#parseBigInteger_charArray_int_int_int")
    public void parseBigInteger_charArray_int_int_int(NumberTestDataSupplier ds) {
        NumberTestData d = ds.supplier().get();
        if (d.expectedThrowableClass() == null) {
            BigInteger actual = JavaBigIntegerParser.parseBigInteger(d.input().toString().toCharArray(), d.charOffset(), d.charLength(), d.radix());
            assertEquals(d.expectedValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaBigIntegerParser.parseBigInteger(d.input().toString().toCharArray(), d.charOffset(), d.charLength(), d.radix()));
        }
    }


}
