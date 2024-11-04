/*
 * @(#)DecimalFormatMain.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public final class DecimalFormatMain {

    public static void main(String... args) {
        Locale locale = Locale.forLanguageTag("ar");
        DecimalFormat f = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        String formatted = f.format(-123_456.789);
        System.out.print('`' + formatted + '`' + '\n');
        for (char ch : formatted.toCharArray()) {
            System.out.print("U+" + Integer.toHexString(ch) + " ");
        }
        System.out.println();
    }
}
