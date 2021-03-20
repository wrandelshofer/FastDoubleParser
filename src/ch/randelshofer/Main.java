/*
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer;

import ch.randelshofer.math.FastDoubleParser;

public class Main {

    public static void main(String[] args) {
        double v = FastDoubleParser.parseDouble(args[0]);
        System.out.println(v);
    }
}
