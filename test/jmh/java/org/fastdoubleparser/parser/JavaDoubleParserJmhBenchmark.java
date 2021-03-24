/*
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package org.fastdoubleparser.parser;public class JavaDoubleParserJmhBenchmark {
    /**
     * # JMH version: 1.28
     * # VM version: JDK 16, OpenJDK 64-Bit Server VM, 16+36-2231
     *
     * Benchmark   Mode  Cnt         Score       Error  Units
     * JavaDouble thrpt   25  28876011.268 ± 88964.737  ops/s
     */
    @org.openjdk.jmh.annotations.Benchmark
    public void bechmarkFastDoubleParser() {
        String str="58.264442000000031";
        FastDoubleParser.parseDouble(str);

    }
    @org.openjdk.jmh.annotations.Benchmark
    public void benchmarkDouble() {
        String str="58.264442000000031";
        Double.parseDouble(str);

    }

}
