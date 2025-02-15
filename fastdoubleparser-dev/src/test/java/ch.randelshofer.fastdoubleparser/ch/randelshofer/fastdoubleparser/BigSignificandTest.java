/*
 * @(#)BigSignificandTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link BigSignificand}.
 */
public final class BigSignificandTest {
    @Test
    public void shouldAddValuesThatExceedIntValueRange() {
        int value = 2_000_000_000;
        int addend = 1_000_000_000;

        BigInteger expected = BigInteger.valueOf(value);
        expected = expected.add(BigInteger.valueOf(addend));

        BigSignificand instance = new BigSignificand(64);
        instance.add(value);
        instance.add(addend);
        BigInteger actual = instance.toBigInteger();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldMultiplyValuesThatExceedIntValueRange() {
        int value = 2_000_000_000;
        int factor = 1_000_000_000;

        BigInteger expected = BigInteger.valueOf(value);
        expected = expected.multiply(BigInteger.valueOf(factor));

        BigSignificand instance = new BigSignificand(64);
        instance.add(value);
        instance.fma(factor, 0);
        BigInteger actual = instance.toBigInteger();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldFmaValuesThatExceedIntValueRange() {
        int value = 2_000_000_000;
        int addend = 1_900_000_000;
        int factor = 1_000_000_000;

        BigInteger expected = BigInteger.valueOf(value);
        expected = expected.multiply(BigInteger.valueOf(factor));
        expected = expected.add(BigInteger.valueOf(addend));

        BigSignificand instance = new BigSignificand(64);
        instance.fma(1, value);
        instance.fma(factor, addend);
        BigInteger actual = instance.toBigInteger();

        assertEquals(expected, actual);
    }
}
