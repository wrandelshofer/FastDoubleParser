/*
 * @(#)ParseFloatWithVectorTest.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class VectorizedFloatFromByteArrayTest {

    @TestFactory
    List<DynamicNode> dynamicTestsLegalInput() {
        return List.of(
                dynamicTest("1", () -> doLegalTest("1")),
                dynamicTest("-1", () -> doLegalTest("-1")),
                dynamicTest("1e2", () -> doLegalTest("1e2")),
                dynamicTest("1e-2", () -> doLegalTest("1e-2")),
                dynamicTest("-1e-2", () -> doLegalTest("-1e-2")),
                dynamicTest("1234567890123456", () -> doLegalTest("1234567890123456")),
                dynamicTest("-123456789012345", () -> doLegalTest("-123456789012345")),
                dynamicTest("1e12345678901234", () -> doLegalTest("1e12345678901234")),
                dynamicTest("12345678901234e1", () -> doLegalTest("12345678901234e1")),
                dynamicTest("1234567890123e-1", () -> doLegalTest("1234567890123e-1")),
                dynamicTest("1e-1234567890123", () -> doLegalTest("1e-1234567890123")),
                dynamicTest("-123456789012e-1", () -> doLegalTest("-123456789012e-1")),
                dynamicTest("-1e-123456789012", () -> doLegalTest("-1e-123456789012")),
                dynamicTest(".1", () -> doLegalTest(".1")),
                dynamicTest("1.", () -> doLegalTest("1.")),
                dynamicTest("1.2", () -> doLegalTest("1.2")),
                dynamicTest("-1.2", () -> doLegalTest("-1.2")),
                dynamicTest("1.2e3", () -> doLegalTest("1.2e3")),
                dynamicTest("1.2e-3", () -> doLegalTest("1.2e-3")),
                dynamicTest("-1.2e-3", () -> doLegalTest("-1.2e-3")),
                dynamicTest("1.23456789012345", () -> doLegalTest("1.23456789012345")),
                dynamicTest("-1.2345678901234", () -> doLegalTest("-1.2345678901234")),
                dynamicTest("1.2e123456789012", () -> doLegalTest("1.2e123456789012")),
                dynamicTest("1.123456789012e1", () -> doLegalTest("1.123456789012e1")),
                dynamicTest("12345678901.2e-3", () -> doLegalTest("12345678901.2e-3")),
                dynamicTest("1.e-123456789012", () -> doLegalTest("1.e-123456789012")),
                dynamicTest("-12345.789012e-1", () -> doLegalTest("-12345.789012e-1")),
                dynamicTest("-.1e-12356789012", () -> doLegalTest("-.1e-12356789012")),
                dynamicTest("0", () -> doLegalTest("0")),
                dynamicTest("365", () -> doLegalTest("365")),
                dynamicTest("10.1", () -> doLegalTest("10.1")),
                dynamicTest("3.1415927", () -> doLegalTest("3.1415927")),
                dynamicTest("1.6162552e-35", () -> doLegalTest("1.6162552e-35"))
        );
    }

    private void doLegalTest(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.ISO_8859_1);
        float actual = new VectorizedFloatFromByteArray().parseFloat(bytes, 0, bytes.length);
        float expected = Float.parseFloat(s);
        assertEquals(expected, actual);
    }


}
