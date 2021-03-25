/*
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package org.fastdoubleparser.parser;

public class JavaDoubleParserJmhBenchmark {
    /**
     * <pre>
     * # JMH version: 1.28
     * # VM version: JDK 16, OpenJDK 64-Bit Server VM, 16+36-2231
     * Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
     *
     * Benchmark   Mode  Cnt         Score        Error  Units
     * JavaDoubl  thrpt   25  32510483.485 ± 165661.331  ops/s
     * </pre>
     */
    @org.openjdk.jmh.annotations.Benchmark
    public void benchmarkFastDoubleParser19DigitsWithoutExp() {
        String str = "123.4567890123456789";
        FastDoubleParser.parseDouble(str);
    }

    /**
     * <pre>
     * JMH version: 1.28
     * VM version: JDK 16, OpenJDK 64-Bit Server VM, 16+36-2231
     * Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
     *
     * Benchmark     Mode  Cnt        Score       Error  Units
     * JavaDoubleP  thrpt   25  2333618.738 ± 25695.231  ops/s
     * </pre>
     */
    @org.openjdk.jmh.annotations.Benchmark
    public void benchmarkDouble() {
        String str = "123.4567890123456789";
        Double.parseDouble(str);

    }

}
