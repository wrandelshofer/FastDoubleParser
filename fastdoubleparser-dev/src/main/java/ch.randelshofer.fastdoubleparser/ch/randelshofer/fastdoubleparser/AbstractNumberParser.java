/*
 * @(#)AbstractNumberParser.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

abstract class AbstractNumberParser {
    /**
     * Message text for the {@link IllegalArgumentException} that is thrown
     * when offset or length are illegal
     */
    public static final String ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH = "offset < 0 or length > str.length";
    /**
     * Message text for the {@link NumberFormatException} that is thrown
     * when the syntax is illegal.
     */
    public static final String SYNTAX_ERROR = "illegal syntax";
    /**
     * Message text for the {@link NumberFormatException} that is thrown
     * when there are too many input digits.
     */
    public static final String VALUE_EXCEEDS_LIMITS = "value exceeds limits";


}
