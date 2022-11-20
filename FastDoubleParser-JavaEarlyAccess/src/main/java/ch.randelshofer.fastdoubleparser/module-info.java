/**
 * Provides fast parsers for Java {@code FloatingPointLiteral}s,
 * and JSON {@code number}s.
 */
module ch.randelshofer.fastdoubleparser {
    requires jdk.incubator.vector;
    exports ch.randelshofer.fastdoubleparser;
}