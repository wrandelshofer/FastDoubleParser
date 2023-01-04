/*
 * @(#)ReadFileTest.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Iterator;

public class ManualTest {
    public static void main(String... args) throws IOException, InterruptedException {
        Thread.sleep(10_000);
        Path path = Paths.get(args.length > 0 ? args[0] : "fastdoubleparserdemo/data/BigIntegerMaxValue.txt");
        System.out.println(path.toAbsolutePath());
        for (Iterator<String> i = Files.lines(path).iterator(); i.hasNext(); ) {
            String line = i.next();
            byte[] bytes = line.getBytes(StandardCharsets.ISO_8859_1);
            line = null;
            byte[] negativeBytes = new byte[bytes.length + 1];
            System.arraycopy(bytes, 0, negativeBytes, 1, bytes.length);
            negativeBytes[0] = '-';

            System.out.println("parsing line");
            parseLine(bytes);
            /*
            System.out.println("parsing negative line");
            parseLine(negativeBytes);

            bytes[bytes.length-1]+=1;
            negativeBytes[negativeBytes.length-1]+=1;
            System.out.println("parsing line with last digit incremented by 1");
            parseLine(bytes);
            System.out.println("parsing negative line with last digit incremented by 1");
            parseLine(negativeBytes);
             */
        }
    }

    private static void parseLine(byte[] bytes) {
        try {
            long start = System.nanoTime();
            BigInteger bigInteger = JavaBigIntegerParser.parseBigInteger(bytes);
            System.out.println(Duration.ofNanos(System.nanoTime() - start));
            System.out.println("BigInteger bitLength=" + bigInteger.bitLength());
        } catch (NumberFormatException e) {
            System.out.println("BigInteger failed " + e);
        }
        try {
            long start = System.nanoTime();
            BigDecimal bigDecimal = JavaBigDecimalParser.parseBigDecimal(bytes);
            System.out.println(Duration.ofNanos(System.nanoTime() - start));
            System.out.println("BigDecimal signum=" + bigDecimal.signum() + " scale=" + bigDecimal.scale());
        } catch (NumberFormatException e) {
            System.out.println("BigDecimal failed " + e);
        }
    }
}
