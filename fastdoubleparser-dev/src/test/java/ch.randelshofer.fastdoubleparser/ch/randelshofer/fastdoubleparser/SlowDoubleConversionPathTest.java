/*
 * @(#)SlowDoubleConversionPathTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.chr.CharDigitSet;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SlowDoubleConversionPathTest {
    @Test
    public void shouldComputeBigDecimal() {
        String str = "12345678901234.9876543210987";
        CharDigitSet digitSet = CharDigitSet.copyOf(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
        BigDecimal bigDecimal = SlowDoubleConversionPath.toBigDecimal(str, digitSet, 0, str.indexOf('.'), str.indexOf('.') + 1, str.length(), 700, 7);
        assertEquals(12345678901234.9876543210987e7, bigDecimal.doubleValue());
    }

    @Test
    public void shouldComputeBigDecimalTruncatedInFractionPart() {
        String str = "12345678901234.9876543210987";
        CharDigitSet digitSet = CharDigitSet.copyOf(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
        BigDecimal bigDecimal = SlowDoubleConversionPath.toBigDecimal(str, digitSet, 0, str.indexOf('.'), str.indexOf('.') + 1, str.length(), 15, 7);
        assertEquals(12345678901234.9e7, bigDecimal.doubleValue());
    }

    @Test
    public void shouldComputeBigDecimalTruncated11InIntegerPart() {
        String str = "12345678901234.9876543210987";
        CharDigitSet digitSet = CharDigitSet.copyOf(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
        BigDecimal bigDecimal = SlowDoubleConversionPath.toBigDecimal(str, digitSet, 0, str.indexOf('.'), str.indexOf('.') + 1, str.length(), 11, 7);
        assertEquals(12345678901000.0e7, bigDecimal.doubleValue());
    }

    @Test
    public void shouldComputeBigDecimalTruncated3InIntegerPart() {
        String str = "12345678901234.9876543210987";
        CharDigitSet digitSet = CharDigitSet.copyOf(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
        BigDecimal bigDecimal = SlowDoubleConversionPath.toBigDecimal(str, digitSet, 0, str.indexOf('.'), str.indexOf('.') + 1, str.length(), 3, 7);
        assertEquals(12300000000000.0e7, bigDecimal.doubleValue());
    }

    @Test
    public void shouldComputeBigDecimalWithGrouping() {
        String str = "12,345,678,901,234.9876543210987";
        CharDigitSet digitSet = CharDigitSet.copyOf(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
        BigDecimal bigDecimal = SlowDoubleConversionPath.toBigDecimal(str, digitSet, 0, str.indexOf('.'), str.indexOf('.') + 1, str.length(), 700, 7);
        assertEquals(12345678901234.9876543210987e7, bigDecimal.doubleValue());
    }

    @Test
    public void shouldComputeBigDecimalTruncatedInFractionPartWithGrouping() {
        String str = "12,345,678,901,234.9876543210987";
        CharDigitSet digitSet = CharDigitSet.copyOf(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
        BigDecimal bigDecimal = SlowDoubleConversionPath.toBigDecimal(str, digitSet, 0, str.indexOf('.'), str.indexOf('.') + 1, str.length(), 15, 7);
        assertEquals(12345678901234.9e7, bigDecimal.doubleValue());
    }

    @Test
    public void shouldComputeBigDecimalTruncated11InIntegerPartWithGrouping() {
        String str = "12,345,678,901,234.9876543210987";
        CharDigitSet digitSet = CharDigitSet.copyOf(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
        BigDecimal bigDecimal = SlowDoubleConversionPath.toBigDecimal(str, digitSet, 0, str.indexOf('.'), str.indexOf('.') + 1, str.length(), 11, 7);
        assertEquals(12345678901000.0e7, bigDecimal.doubleValue());
    }

    @Test
    public void shouldComputeBigDecimalTruncated3InIntegerPartWithGrouping() {
        String str = "12,345,678,901,234.9876543210987";
        CharDigitSet digitSet = CharDigitSet.copyOf(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
        BigDecimal bigDecimal = SlowDoubleConversionPath.toBigDecimal(str, digitSet, 0, str.indexOf('.'), str.indexOf('.') + 1, str.length(), 3, 7);
        assertEquals(12300000000000.0e7, bigDecimal.doubleValue());
    }

    @Test
    public void shouldComputeBigDecimalTruncated3InIntegerPartWithGroupingAndLeadingZeroes() {
        String str = "00,012,345,678,901,234.9876543210987";
        CharDigitSet digitSet = CharDigitSet.copyOf(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
        BigDecimal bigDecimal = SlowDoubleConversionPath.toBigDecimal(str, digitSet, 0, str.indexOf('.'), str.indexOf('.') + 1, str.length(), 3, 7);
        assertEquals(12300000000000.0e7, bigDecimal.doubleValue());
    }

}