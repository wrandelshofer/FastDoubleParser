/*
 * @(#)JmhFloat.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.36
 * # JDK 20.0.1, OpenJDK 64-Bit Server VM, 20.0.1+9-29
 * # Intel(R) Core(TM) i5-6200U CPU @ 2.30GHz
 *
 * Benchmark    (str)  Mode  Cnt    Score   Error  Units
 * lookupHex2Byte      avgt    5  2.020 ± 0.009  ns/op
 * lookupHex2Char      avgt    5  2.019 ± 0.010  ns/op
 * lookupHexTwiceByte  avgt    5  2.336 ± 0.057  ns/op
 * lookupHexTwiceChar  avgt    5  2.321 ± 0.075  ns/op
 *
 * Process finished with exit code 0
 * </pre>
 */

@Fork(value = 1)
@Measurement(iterations = 5, time = 1)
@Warmup(iterations = 2, time = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhLookupHex2 {

    public char highChar = 'A';
    public char lowChar = '1';
    public char highByte = 'A';
    public char lowByte = '1';
    public boolean illegalDigits;

    @Setup(Level.Iteration)
    public void prepare(){
        illegalDigits = false;// otherwise it seems that evaluation of expression for "illegalDigits" is skipped
    }

    // several lines of code added to imitate the context, which optimized method is situated in
    @Benchmark
    public void lookupHex2Char(Blackhole blackhole) {
        int result = AbstractNumberParser.lookupHex2(highChar, lowChar);
        byte b = (byte) result;
        blackhole.consume(b);
        illegalDigits |= result < 0;
        blackhole.consume(illegalDigits);
    }

    @Benchmark
    public void lookupHexTwiceChar(Blackhole blackhole) {
        int high = AbstractNumberParser.lookupHex(highChar);
        int low =  AbstractNumberParser.lookupHex(lowChar);
        byte b = (byte) (high << 4 | low);
        blackhole.consume(b);
        illegalDigits |= low < 0 | high < 0;
        blackhole.consume(illegalDigits);
    }

    @Benchmark
    public void lookupHex2Byte(Blackhole blackhole) {
        int result = AbstractNumberParser.lookupHex2(highByte, lowByte);
        byte b = (byte) result;
        blackhole.consume(b);
        illegalDigits |= result < 0;
        blackhole.consume(illegalDigits);
    }

    @Benchmark
    public void lookupHexTwiceByte(Blackhole blackhole) {
        int high = AbstractNumberParser.lookupHex(highByte);
        int low =  AbstractNumberParser.lookupHex(lowByte);
        byte b = (byte) (high << 4 | low);
        blackhole.consume(b);
        illegalDigits |= low < 0 | high < 0;
        blackhole.consume(illegalDigits);
    }
}





