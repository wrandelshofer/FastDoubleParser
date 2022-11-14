/*
 * @(#)TestData.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

public record FloatTestData(String title,
                            CharSequence input,
                            int charOffset, int charLength,
                            int byteOffset, int byteLength,
                            double expectedDoubleValue,
                            float expectedFloatValue,
                            boolean valid) {
    public FloatTestData(CharSequence input, double expectedDoubleValue, float expectedFloatValue) {
        this(input.toString(), input, 0, input.length(), 0, input.length(),
                expectedDoubleValue,
                expectedFloatValue, true);
    }

    public FloatTestData(CharSequence input, double expectedDoubleValue, float expectedFloatValue, int offset, int length) {
        this(input.toString(), input, offset, length, offset, length,
                expectedDoubleValue,
                expectedFloatValue, true);
    }

    public FloatTestData(String title, CharSequence input, double expectedDoubleValue, float expectedFloatValue) {
        this(title.length() + (long) input.length() > 100 || title.contains(input)
                        ? title
                        : title + " " + input,
                input, 0, input.length(), 0, input.length(),
                expectedDoubleValue,
                expectedFloatValue, true);
    }

    public FloatTestData(CharSequence input) {
        this(input.toString(), input);
    }

    public FloatTestData(String title, CharSequence input) {
        this(title.contains(input) ? title : title + " " + input, input, 0, input.length(), 0, input.length(),
                Double.NaN,
                Float.NaN, false);
    }
}
