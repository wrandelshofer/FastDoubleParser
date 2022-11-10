package ch.randelshofer.fastdoubleparser;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaBigDecimalFromByteArrayTest extends AbstractBigDecimalParserTest {


    protected void testParse(String s) {
        BigDecimal expected = new BigDecimal(s);
        BigDecimal actual = JavaBigDecimalParser.parseBigDecimal(s.getBytes(StandardCharsets.ISO_8859_1));
        assertEquals(expected, actual);
    }
}
