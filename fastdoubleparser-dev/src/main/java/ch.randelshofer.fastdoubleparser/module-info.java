/*
 * @(#)module-info.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */

/**
 * Provides fast parsers for Java {@code FloatingPointLiteral}s,
 * and JSON {@code number}s.
 */
module ch.randelshofer.fastdoubleparser {
    requires jdk.incubator.vector;
    exports ch.randelshofer.fastdoubleparser;
}