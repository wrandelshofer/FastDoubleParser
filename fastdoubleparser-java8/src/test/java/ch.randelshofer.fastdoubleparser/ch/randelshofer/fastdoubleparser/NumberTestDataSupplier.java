/*
 * @(#)NumberTestDataSupplier.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.Objects;
import java.util.function.Supplier;

public final class NumberTestDataSupplier {
    private final String title;
    private final Supplier<NumberTestData> supplier;

    public NumberTestDataSupplier(String title,
                                  Supplier<NumberTestData> supplier) {
        this.title = title;
        this.supplier = supplier;
    }

    public NumberTestDataSupplier(String inputValue) {
        this(inputValue, () -> new NumberTestData(inputValue));
    }

    public String title() {
        return title;
    }

    public Supplier<NumberTestData> supplier() {
        return supplier;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        NumberTestDataSupplier that = (NumberTestDataSupplier) obj;
        return Objects.equals(this.title, that.title) &&
                Objects.equals(this.supplier, that.supplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, supplier);
    }

    @Override
    public String toString() {
        return "NumberTestDataSupplier[" +
                "title=" + title + ", " +
                "supplier=" + supplier + ']';
    }

}
