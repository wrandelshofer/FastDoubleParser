/*
 * @(#)TestData.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

public record TestData(String title,
                       String input,
                       int charOffset, int charLength,
                       int byteOffset, int byteLength,
                       double expectedDoubleValue,
                       float expectedFloatValue,
                       boolean valid) {
    public TestData(String input, double expectedDoubleValue, float expectedFloatValue) {
        this(input, input, 0, input.length(), 0, input.length(),
                expectedDoubleValue,
                expectedFloatValue, true);
    }

    public TestData(String input, double expectedDoubleValue, float expectedFloatValue, int offset, int length) {
        this(input, input, offset, length, offset, length,
                expectedDoubleValue,
                expectedFloatValue, true);
    }

    public TestData(String title, String input, double expectedDoubleValue, float expectedFloatValue) {
        this(title.length() + (long) input.length() > 100 || title.contains(input)
                        ? title
                        : title + " " + input,
                input, 0, input.length(), 0, input.length(),
                expectedDoubleValue,
                expectedFloatValue, true);
    }

    public TestData(String input) {
        this(input, input);
    }

    public TestData(String title, String input) {
        this(title.contains(input) ? title : title + " " + input, input, 0, input.length(), 0, input.length(),
                Double.NaN,
                Float.NaN, false);
    }
}
