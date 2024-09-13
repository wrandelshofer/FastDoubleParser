/*
 * @(#)NumberTestDataSupplier.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.function.Supplier;

public record NumberTestDataSupplier(String title,
                                     Supplier<NumberTestData> supplier) {
    public NumberTestDataSupplier(String inputValue) {
        this(inputValue, () -> new NumberTestData(inputValue));
    }
}
