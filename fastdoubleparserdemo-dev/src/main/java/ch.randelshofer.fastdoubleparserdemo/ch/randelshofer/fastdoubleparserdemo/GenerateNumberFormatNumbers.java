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
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Random;

/**
 * Generate random numbers in a gamma corrected random number distribution function.
 */
public class GenerateNumberFormatNumbers {
    private double gammaCorrection(double value, double invGamma) {
        return Math.pow(value, invGamma);
    }

    public void generate(Path path, NumberFormat f, double range, int size, double gamma) throws IOException {
        Random rng = new Random();
        double invGamma = 1 / gamma;
        try (BufferedWriter w = Files.newBufferedWriter(path)) {
            rng.doubles(size)
                    .map(v -> gammaCorrection(v, invGamma))
                    .map(v -> v * range)
                    .map(v -> (Double.doubleToRawLongBits(v) & 1) == 1 ? -v : v)
                    .forEach(v -> {
                                try {
                                    w.write(f.format(v));
                                    w.newLine();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );

        }
    }

    public static void main(String... args) throws IOException, ParseException {
        Locale locale = new Locale("ar");
        NumberFormat f = NumberFormat.getNumberInstance(locale);
        double range = 1e9;
        int size = 100_000;
        double gamma = 0.2;
        Path path = Paths.get("fastdoubleparserdemo/data/formatted_"
                + locale.getLanguage()
                + (locale.getCountry() == null ? "" : "-" + locale.getCountry())
                + ".txt").toAbsolutePath();
        new GenerateNumberFormatNumbers().generate(path, f, range, size, gamma);
    }

}
