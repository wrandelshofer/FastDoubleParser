/*
 * @(#)JmhUtf8Decoder.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.*;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * # JMH version: 1.37
 * # VM version: JDK 23, OpenJDK 64-Bit Server VM, 23+37
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 *                                       (str)  Mode  Cnt    Score   Error  Units
 * mOne   grouped:abcdÀÁÂÃՀՁՂՃठडढण𐀀𐀁𐀂𐀃  avgt    2   80.014          ns/op
 * mTwo  grouped:abcdÀÁÂÃՀՁՂՃठडढण𐀀𐀁𐀂𐀃   avgt    2  114.896          ns/op
 * mSwar  grouped:abcdÀÁÂÃՀՁՂՃठडढण𐀀𐀁𐀂𐀃  avgt    2  109.290          ns/op
 *
 *
 * Apple M2 Max, 12 (8 performance and 4 efficiency)
 *
 *                                           (str)  Mode  Cnt    Score   Error  Units
 * mOne           grouped:abcdÀÁÂÃՀՁՂՃठडढण𐀀𐀁𐀂𐀃  avgt    2   51.716          ns/op
 * mSwar          grouped:abcdÀÁÂÃՀՁՂՃठडढण𐀀𐀁𐀂𐀃  avgt    2  262.281          ns/op
 * mSwarUnrolled  grouped:abcdÀÁÂÃՀՁՂՃठडढण𐀀𐀁𐀂𐀃  avgt    2  270.079          ns/op
 * </pre>
 */
@Fork(value = 1)
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhUtf8Decoder {

    @Param({
            "grouped:abcd" + "ÀÁÂÃ" + "ՀՁՂՃ" + "ठडढण" + "𐀀" + "𐀁𐀂𐀃"
    })
    public String str;
    public byte[] utf8;
    public char[] utf16;

    @Setup()
    public void setUp() {
        utf8 = str.getBytes(StandardCharsets.UTF_8);
        utf16 = new char[utf8.length];
    }


    @Benchmark
    public int m() {
        return Utf8Decoder.decode(utf8, 0, utf8.length, utf16, 0);
    }

}
