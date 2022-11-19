package ch.randelshofer.fastdoubleparser;

abstract class AbstractNumberParser {
    public static final String ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH = "offset < 0 or length > str.length";
    public static final String SYNTAX_ERROR = "illegal syntax";
    public static final String VALUE_EXCEEDS_LIMITS = "value exceeds limits";
    /**
     * See {@link JavaBigDecimalParser}.
     */
    public final static int MAX_INPUT_LENGTH = Integer.MAX_VALUE - 4;
}
