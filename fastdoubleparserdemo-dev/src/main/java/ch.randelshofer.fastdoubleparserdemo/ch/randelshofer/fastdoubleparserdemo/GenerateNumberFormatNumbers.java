/*
 * @(#)GenerateNumberFormatNumbers.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparserdemo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Random;

/**
 * Generate random numbers in a gamma corrected random number distribution function.
 */
public final class GenerateNumberFormatNumbers {
    private double gammaCorrection(double value, double invGamma) {
        return Math.pow(value, invGamma);
    }

    public void generate(Path path, NumberFormat f, DecimalFormat fsc, double range, int size, double gamma, String digits) throws IOException {
        Random rng = new Random();
        double invGamma = 1 / gamma;
        try (BufferedWriter w = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
            rng.doubles(size)
                    .map(v -> gammaCorrection(v, invGamma))
                    .map(v -> v * range)
                    .map(v -> (Double.doubleToRawLongBits(v) & 1) == 1 ? -v : v)
                    .forEach(v -> {
                                try {
                                    w.write(replaceDigits(gammaCorrection(rng.nextFloat(), invGamma) < 0.5 ? f.format(v) : fsc.format(v), digits));
                                    w.newLine();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );

        }
    }


    private String replaceDigits(String str, String digits) {
        if (digits == null) return str;
        StringBuilder buf = new StringBuilder(str);
        for (int i = 0; i < buf.length(); i++) {
            char ch = buf.charAt(i);
            int digit = (char) (ch - '0');
            if (digit < 10) {
                buf.setCharAt(i, digits.charAt(digit));
            }
        }
        return buf.toString();
    }

    public static void main(String... args) throws IOException, ParseException {
        Locale locale = Locale.forLanguageTag("ar");
        DecimalFormat f = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        DecimalFormat fsc = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        fsc.applyPattern("####,##0.0######E0##");
        String digits = null;
        DecimalFormatSymbols symbols = f.getDecimalFormatSymbols();
        /*
        symbols.setExponentSeparator("*10^");
        f.setDecimalFormatSymbols(symbols);
        fsc.setDecimalFormatSymbols(symbols);
        digits = "〇一二三四五六七八九";
        */

        double range = 1e9;
        int size = 50_000;
        double gamma = 0.2;
        Path path = Paths.get("fastdoubleparserdemo/data/formatted_"
                + locale.getLanguage()
                + (locale.getCountry() == null || locale.getCountry().isEmpty() ? "" : "-" + locale.getCountry())
                + ".txt").toAbsolutePath();
        new GenerateNumberFormatNumbers().generate(path, f, fsc, range, size, gamma, digits);
    }

}
