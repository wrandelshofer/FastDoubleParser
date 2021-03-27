/*
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package org.fastdoubleparser.parser;

import org.openjdk.jmh.annotations.Benchmark;

/**
 * Benchmarks for selected floating point strings.
 * <p>
 * FIXME Add benchmarks for floating point strings that trigger
 *       slow paths in {@link FastDoubleMath} once we have implement them.
 * <pre>
 * # JMH version: 1.28
 * # VM version: JDK 16, OpenJDK 64-Bit Server VM, 16+36-2231
 * Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark                                  Mode  Cnt         Score        Error  Units
 * Double14HexDigitsWith3DigitExp            thrpt   25   2822766.656 ±  25505.007  ops/s
 * Double17DigitsWith3DigitExp               thrpt   25   4250630.743 ±  52250.815  ops/s
 * Double19DigitsWith3DigitExp               thrpt   25   4302500.167 ±  21428.307  ops/s
 * Double19DigitsWithoutExp                  thrpt   25   2340559.716 ±  21398.357  ops/s
 * FastDoubleParser14HexDigitsWith3DigitExp  thrpt   25  25424684.215 ±  86479.866  ops/s
 * FastDoubleParser17DigitsWith3DigitExp     thrpt   25  30957687.582 ± 327486.865  ops/s
 * FastDoubleParser19DigitsWith3DigitExp     thrpt   25  29489000.245 ± 165496.465  ops/s
 * FastDoubleParser19DigitsWithoutExp        thrpt   25  33537655.214 ± 233446.518  ops/s
 * </pre>
 */
public class DoubleParserJmhBenchmark {
    @Benchmark
    public void benchmarkDouble14HexDigitsWith3DigitExp() {
        String str = "0x123.456789abcdep123";
        Double.parseDouble(str);
    }


    @Benchmark
    public void benchmarkDouble17DigitsWith3DigitExp() {
        String str = "123.45678901234567e123";
        Double.parseDouble(str);
    }


    @Benchmark
    public void benchmarkDouble19DigitsWithoutExp() {
        String str = "123.4567890123456789";
        Double.parseDouble(str);
    }

    //  @Benchmark
    public void benchmarkDouble19DigitsWith3DigitExp() {
        String str = "123.4567890123456789e123";
        Double.parseDouble(str);
    }

    @Benchmark
    public void benchmarkFastDoubleParser19DigitsWith3DigitExp() {
        String str = "123.4567890123456789e123";
        FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    public void benchmarkFastDoubleParser14HexDigitsWith3DigitExp() {
        String str = "0x123.456789abcdep123";
        FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    public void benchmarkFastDoubleParser17DigitsWith3DigitExp() {
        String str = "123.45678901234567e123";
        FastDoubleParser.parseDouble(str);
    }

    @Benchmark
    public void benchmarkFastDoubleParser19DigitsWithoutExp() {
        String str = "123.4567890123456789";
        FastDoubleParser.parseDouble(str);
    }
}
