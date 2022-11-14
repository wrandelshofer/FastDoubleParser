package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class JavaBigDecimalFromByteArrayTest extends AbstractBigDecimalParserTest {


    private void test(BigDecimalTestData d, Function<BigDecimalTestData, BigDecimal> f) {
        BigDecimal expectedValue = d.expectedValue().get();
        BigDecimal actual = f.apply(d);
        if (expectedValue != null) {
            assertEquals(0, expectedValue.compareTo(actual),
                    "expected:" + expectedValue + " <> actual:" + actual);
            assertEquals(expectedValue, actual);
        } else {
            assertNull(actual);
        }
    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsParseBigDecimalFromByteArray() {
        return createRegularTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigDecimalParser.parseBigDecimalOrNull(u.input().toString().getBytes(StandardCharsets.ISO_8859_1),
                                u.byteOffset(), u.byteLength()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicInputClassesTest() {
        return createTestDataForInputClassesInMethodParseBigDecimalString().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigDecimalParser.parseBigDecimalOrNull(u.input().toString().getBytes(StandardCharsets.ISO_8859_1),
                                u.byteOffset(), u.byteLength()))));

    }

    @Disabled
    @TestFactory
    public Stream<DynamicTest> dynamicTestsVeryLongStrings() {
        return createDataForVeryLongStrings().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> new JavaBigDecimalFromByteArray().parseBigDecimalString(
                                u.input().toString().getBytes(StandardCharsets.ISO_8859_1),
                                u.byteOffset(), u.byteLength()))));

    }
}
