/*
 * @(#)WriteRandomFileTest.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class WriteRandomFileTest {
    @Test
    public void test() throws IOException {
        Random rng = new Random();
        try (BufferedWriter w = Files.newBufferedWriter(Paths.get("data/minusMilToMil_float.txt"))) {
            for (int i = 0; i < 1000; i++) {
                float v = rng.nextFloat(-1_000_000.0f, 1_000_000.0f);
                w.write(Float.toString(v));
                w.write('\n');
            }
        }
    }
}
