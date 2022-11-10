package ch.randelshofer.fastdoubleparser;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaBigDecimalFromCharSequenceTest extends AbstractBigDecimalParserTest {

    protected void testParse(String s) {
        BigDecimal expected = new BigDecimal(s);
        BigDecimal actual = JavaBigDecimalParser.parseBigDecimal(s);
        assertEquals(expected, actual);
    }
}
