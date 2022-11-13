package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class LargeJavaBigDecimalFromByteArrayTest extends AbstractBigDecimalParserTest {


    private void test(BigDecimalTestData d, Function<BigDecimalTestData, BigDecimal> f) {
        CompletableFuture<BigDecimal> expectedValueFuture = new CompletableFuture<>();
        ForkJoinPool.commonPool().execute(() -> {
            expectedValueFuture.complete(d.expectedValue().get());
        });
        long start = System.nanoTime();
        BigDecimal actual = f.apply(d);
        long end = System.nanoTime();
        System.out.println("elapsed: " + Duration.ofNanos((end - start)));
        BigDecimal expectedValue = null;
        start = System.nanoTime();
        try {
            expectedValue = expectedValueFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        end = System.nanoTime();
        System.out.println("wait time for expectedValue: " + Duration.ofNanos((end - start)));
        if (expectedValue != null) {
            assertEquals(0, expectedValue.compareTo(actual));
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
                        () -> test(t, u -> new JavaBigDecimalFromByteArray().parseBigDecimalString(
                                u.input().getBytes(StandardCharsets.ISO_8859_1),
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
                                u.input().getBytes(StandardCharsets.ISO_8859_1),
                                u.byteOffset(), u.byteLength()))));

    }
}
