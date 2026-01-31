/*
 * @(#)MoreAssertions.java
 * Copyright © 2026 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoreAssertions {
    public static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable) {
        return Assertions.assertThrows(expectedType, executable, (String) null);
    }

    public static <T extends Throwable> void assertThrows(String exceptionMessage, Class<T> expectedType, Executable executable) {
        try {
            executable.execute();
            Assertions.fail("Expected %s to be thrown, but nothing was thrown.".formatted(expectedType));
        } catch (Throwable e) {
            assertEquals(expectedType, e.getClass(), "Unexpected exception type thrown");
            assertEquals(exceptionMessage, e.getMessage(), "Unexpected exception message");
        }
    }
}
