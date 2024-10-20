/*
 * @(#)MyMain.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparserdemo;

import ch.randelshofer.fastdoubleparser.ConfigurableDoubleParser;
import ch.randelshofer.fastdoubleparser.JavaBigDecimalParser;
import ch.randelshofer.fastdoubleparser.JavaBigIntegerParser;
import ch.randelshofer.fastdoubleparser.JavaDoubleParser;
import ch.randelshofer.fastdoubleparser.JavaFloatParser;
import ch.randelshofer.fastdoubleparser.JsonDoubleParser;
import ch.randelshofer.fastdoubleparser.NumberFormatSymbols;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

class MyMain {
    public static void main(String... args) {
        double d = JavaDoubleParser.parseDouble("1.2345e135");
        float f = JavaFloatParser.parseFloat("1.2345f");
        BigDecimal bd = JavaBigDecimalParser.parseBigDecimal("1.2345");
        BigInteger bi = JavaBigIntegerParser.parseBigInteger("12345");
        double jsonD = JsonDoubleParser.parseDouble("1.2345e85");

        var symbols = NumberFormatSymbols.fromDecimalFormatSymbols(new DecimalFormatSymbols(Locale.GERMAN));
        var confdParser = new ConfigurableDoubleParser(symbols);
        double confD = confdParser.parseDouble("123.456,89");
    }
}