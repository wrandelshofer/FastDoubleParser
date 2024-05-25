[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ch.randelshofer/fastdoubleparser/badge.svg)](https://maven-badges.herokuapp.com/maven-central/ch.randelshofer/fastdoubleparser)

# FastDoubleParser

This is a Java port of Daniel Lemire's [fast_float](https://github.com/fastfloat/fast_float) project.

This project provides parsers for `double`, `float`, `BigDecimal` and `BigInteger` values.
The `double` and `float` parsers are optimised for speed for the most common inputs.
The `BigDecimal` and `BigInteger` parsers are optimised for speed on all inputs.

The code in this project contains optimised versions for Java SE 1.8, 11, 17, and 21.
The code is released in a single multi-release jar, which contains the code for all these versions
except 20.

## License

### Project License

This project can be licensed under the
[MIT License](https://github.com/wrandelshofer/FastDoubleParser/blob/645dcc236687d22897406ddfeac45fa52d292580/LICENSE).

### Code License

Some code *in* this project is derived from the following projects:

- [fast_float](https://github.com/fastfloat/fast_float), licensed
  under [MIT License](https://github.com/fastfloat/fast_float/blob/35d523195bf7d57aba0e735ad6eba1e6f71ba8d6/LICENSE-MIT)
- [bigint](https://github.com/tbuktu/bigint/tree/floatfft), licensed
  under [BSD 2-clause License](https://github.com/tbuktu/bigint/blob/617c8cd8a7c5e4fb4d919c6a4d11e2586107f029/LICENSE)
-

The code is marked as such.

If you redistribute code, you must follow the terms of all involved licenses (MIT License, BSD 2-clause License).

The build scripts in this project do include the license files into the jar files.
So that the released jar files automatically comply with the licenses, when you use them.

## Usage

```java
module MyModule {
    requires ch.randelshofer.fastdoubleparser;
}
```

```java
import ch.randelshofer.fastdoubleparser.JavaDoubleParser;
import ch.randelshofer.fastdoubleparser.JavaFloatParser;
import ch.randelshofer.fastdoubleparser.JavaBigDecimalParser;
import ch.randelshofer.fastdoubleparser.JavaBigIntegerParser;
import ch.randelshofer.fastdoubleparser.JsonDoubleParser;

class MyMain {
    public static void main(String... args) {
        double d = JavaDoubleParser.parseDouble("1.2345e135");
        float f = JavaFloatParser.parseFloat("1.2345f");
        BigDecimal bd = JavaBigDecimalParser.parseBigDecimal("1.2345");
        BigInteger bi = JavaBigIntegerParser.parseBigInteger("12345");
        double jsonD = JsonDoubleParser.parseDouble("1.2345e85");
    }
}
```

The `parse...()`-methods take a `CharacterSequence`. a `char`-array or a `byte`-array as argument. This way, you can
parse from a `StringBuffer` or an array without having to convert your input to a `String`. Parsing from an array is
faster, because the parser can process multiple characters at once using SIMD instructions.

## Performance Tuning

The JVM does not reliably inline `String.charAt(int)`. This may negativily impact the
`parse...()`-methods that take a `CharacterSequence` as an argument.

To ensure optimal performance, you can use the following java command line option:

    -XX:CompileCommand=inline,java/lang/String.charAt

## Performance Characteristics

### `float` and `double` parsers

On common input data, the fast `double` and `float` parsers are about 4 times faster than
`java.lang.Double.valueOf(String)` and `java.lang.Float.valueOf(String)`.

For less common inputs, the fast parsers can be slower than their `java.lang` counterparts.

A `double` value can always be specified exactly with up to 17 digits in the significand.
A `float` only needs up to 8 digits.
Therefore, inputs with more than 19 digits in the significand are considered less common.
Such inputs are expected to occur if the input data was created with more precision, and needs to be narrowed down
to the precision of a `double` or a `float`.

### `BigDecimal` and `BigInteger` parsers

On common input data, the fast `BigDecimal` and `BigInteger` parsers are slightly faster than
`java.math.BigDecimal(String)` and `java.math.BigInteger(String)`.

For less common inputs with many digits, the fast parsers can be a lot faster than their `java.math` counterparts.
The fast parsers can convert even the longest supported inputs in less than 6 minutes, whereas
their `java.math` counterparts need months!

The fast parsers convert digit characters from base 10 to a bit sequence in base 2
using a divide-and-conquer algorithm. Small sequences of digits are converted
individually to bit sequences and then gradually combined to the final bit sequence.
This algorithm needs to perform multiplications of very long bit sequences.
The multiplications are performed in the frequency domain using a discrete fourier transform.
The multiplications in the frequency domain can be performed in `O(N log N (log log N))` time,
where `N` is the number of digits.
In contrast, conventional multiplication algorithms in the time domain need `O(N²)` time.


### Memory usage and computation time

The memory usage depends on the result type and the maximal supported input character length.

The computation times are given for a Mac mini 2018 with Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz.

| Parser               | Result Type          | Maximal<br/>input length | Memory usage<br/>JVM -Xmx | Computation<br/>Time |
|----------------------|----------------------|-------------------------:|--------------------------:|---------------------:|
| JavaDoubleParser     | java.lang.Double     |                 2^31 - 5 |              10 gigabytes |              < 5 sec |
| JavaFloatParser      | java.lang.Float      |                 2^31 - 5 |              10 gigabytes |              < 5 sec |
| JavaBigIntegerParser | java.math.BigInteger |            1,292,782,622 |              16 gigabytes |              < 6 min |
| JavaBigDecimalParser | java.math.BigDecimal |            1,292,782,635 |              16 gigabytes |              < 6 min |

## Performance measurements

### Random double numbers in the range from 0 to 1

Most input lines look like this: `0.4011441469603171`.

Mac Mini (2018)<br>
CPU: Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz<br>
OS: Mac OS X, 14.5, 12 processors available<br>
VM: Java 22, OpenJDK 64-Bit Server VM, Oracle Corporation, 22.0.1+8-16<br>
-XX:CompileCommand=inline,java/lang/String.charAt<br>

| Method                      |   MB/s |  stdev | Mfloats/s |   ns/f | speedup | JDK    |
|-----------------------------|-------:|-------:|----------:|-------:|--------:|--------|
| java.lang.Double            |  94.14 |  2.7 % |      5.40 | 185.11 |    1.00 | 22.0.1 |
| java.lang.Float             | 102.59 |  2.2 % |      5.89 | 169.86 |    1.00 | 22.0.1 |
| java.math.BigDecimal        | 176.01 |  5.0 % |     10.10 |  99.01 |    1.00 | 22.0.1 |
| JavaDoubleParser String     | 628.48 |  4.4 % |     36.07 |  27.73 |    6.68 | 22.0.1 |
| JavaDoubleParser char[]     | 625.05 |  9.1 % |     35.87 |  27.88 |    6.64 | 22.0.1 |
| JavaDoubleParser byte[]     | 702.05 |  7.1 % |     40.29 |  24.82 |    7.46 | 22.0.1 |
| JsonDoubleParser String     | 611.18 | 10.7 % |     35.07 |  28.51 |    6.49 | 22.0.1 |
| JsonDoubleParser char[]     | 705.56 |  3.9 % |     40.49 |  24.70 |    7.49 | 22.0.1 |
| JsonDoubleParser byte[]     | 684.10 |  5.6 % |     39.26 |  25.47 |    7.27 | 22.0.1 |
| JavaFloatParser  String     | 541.12 |  9.5 % |     31.05 |  32.20 |    5.27 | 22.0.1 |
| JavaFloatParser  char[]     | 734.66 |  4.5 % |     42.16 |  23.72 |    7.16 | 22.0.1 |
| JavaFloatParser  byte[]     | 710.40 |  7.1 % |     40.77 |  24.53 |    6.92 | 22.0.1 |
| JavaBigDecimalParser String | 556.84 | 11.5 % |     31.95 |  31.29 |    3.16 | 22.0.1 |
| JavaBigDecimalParser char[] | 626.44 |  9.7 % |     35.95 |  27.82 |    3.56 | 22.0.1 |
| JavaBigDecimalParser byte[] | 649.03 |  4.1 % |     37.24 |  26.85 |    3.69 | 22.0.1 |

MacBook Pro (2023)<br>
CPU: Apple M2 Max<br>
OS: Mac OS X, 14.4.1, 12 processors available<br>
VM: Java 22, OpenJDK 64-Bit Server VM, Oracle Corporation, 22.0.1+8-16<br>

| Method                      |   MB/s | stdev | Mfloats/s |   ns/f | speedup | JDK    |
|-----------------------------|-------:|------:|----------:|-------:|--------:|--------|
| java.lang.Double            |  73.56 | 7.2 % |      4.22 | 236.86 |    1.00 | 22.0.1 |
| java.lang.Float             |  67.21 | 4.1 % |      3.86 | 259.24 |    1.00 | 22.0.1 |
| java.math.BigDecimal        | 190.32 | 5.9 % |     10.92 |  91.55 |    1.00 | 22.0.1 |
| JavaDoubleParser String     | 535.88 | 2.4 % |     30.75 |  32.52 |    7.28 | 22.0.1 |
| JavaDoubleParser char[]     | 777.25 | 4.2 % |     44.61 |  22.42 |   10.57 | 22.0.1 |
| JavaDoubleParser byte[]     | 793.95 | 5.4 % |     45.57 |  21.95 |   10.79 | 22.0.1 |
| JsonDoubleParser String     | 540.59 | 5.1 % |     31.03 |  32.23 |    7.35 | 22.0.1 |
| JsonDoubleParser char[]     | 832.95 | 3.7 % |     47.80 |  20.92 |   11.32 | 22.0.1 |
| JsonDoubleParser byte[]     | 816.40 | 3.7 % |     46.85 |  21.34 |   11.10 | 22.0.1 |
| JavaFloatParser  String     | 570.60 | 4.2 % |     32.75 |  30.54 |    8.49 | 22.0.1 |
| JavaFloatParser  char[]     | 799.58 | 3.9 % |     45.89 |  21.79 |   11.90 | 22.0.1 |
| JavaFloatParser  byte[]     | 747.36 | 3.5 % |     42.89 |  23.31 |   11.12 | 22.0.1 |
| JavaBigDecimalParser String | 693.00 | 5.2 % |     39.77 |  25.14 |    3.64 | 22.0.1 |
| JavaBigDecimalParser char[] | 804.60 | 3.8 % |     46.18 |  21.66 |    4.23 | 22.0.1 |
| JavaBigDecimalParser byte[] | 811.60 | 4.7 % |     46.58 |  21.47 |    4.26 | 22.0.1 |


### The data file `canada.txt`

This file contains numbers in the range from -128 to +128.
Most input lines look like this: `52.038048000000117`.

Mac Mini (2018)<br>
CPU: Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz<br>
OS: Mac OS X, 14.5, 12 processors available<br>
VM: Java 22, OpenJDK 64-Bit Server VM, Oracle Corporation, 22.0.1+8-16<br>
-XX:CompileCommand=inline,java/lang/String.charAt<br>

| Method                      |   MB/s |  stdev | Mfloats/s |   ns/f | speedup | JDK    |
|-----------------------------|-------:|-------:|----------:|-------:|--------:|--------|
| java.lang.Double            |  85.80 |  2.7 % |      4.93 | 202.82 |    1.00 | 22.0.1 |
| java.lang.Float             |  98.33 |  3.1 % |      5.65 | 176.97 |    1.00 | 22.0.1 |
| java.math.BigDecimal        | 316.93 |  8.8 % |     18.21 |  54.91 |    1.00 | 22.0.1 |
| JavaDoubleParser String     | 406.12 |  2.8 % |     23.34 |  42.85 |    4.73 | 22.0.1 |
| JavaDoubleParser char[]     | 621.42 | 12.9 % |     35.71 |  28.00 |    7.24 | 22.0.1 |
| JavaDoubleParser byte[]     | 637.52 |  7.6 % |     36.64 |  27.30 |    7.43 | 22.0.1 |
| JsonDoubleParser String     | 395.60 |  9.1 % |     22.73 |  43.99 |    4.61 | 22.0.1 |
| JsonDoubleParser char[]     | 527.31 |  7.3 % |     30.30 |  33.00 |    6.15 | 22.0.1 |
| JsonDoubleParser byte[]     | 678.84 |  8.2 % |     39.01 |  25.63 |    7.91 | 22.0.1 |
| JavaFloatParser  String     | 402.45 |  9.6 % |     23.13 |  43.24 |    4.09 | 22.0.1 |
| JavaFloatParser  char[]     | 663.05 |  8.6 % |     38.10 |  26.24 |    6.74 | 22.0.1 |
| JavaFloatParser  byte[]     | 710.55 |  3.2 % |     40.83 |  24.49 |    7.23 | 22.0.1 |
| JavaBigDecimalParser String | 447.42 | 11.5 % |     25.71 |  38.89 |    1.41 | 22.0.1 |
| JavaBigDecimalParser char[] | 654.04 |  5.8 % |     37.59 |  26.61 |    2.06 | 22.0.1 |
| JavaBigDecimalParser byte[] | 661.62 |  6.9 % |     38.02 |  26.30 |    2.09 | 22.0.1 |

MacBook Pro (2023)<br>
CPU: Apple M2 Max<br>
OS: Mac OS X, 14.4.1, 12 processors available<br>
VM: Java 22, OpenJDK 64-Bit Server VM, Oracle Corporation, 22.0.1+8-16<br>

| Method                      |   MB/s | stdev | Mfloats/s |   ns/f | speedup | JDK    |
|-----------------------------|-------:|------:|----------:|-------:|--------:|--------|
| java.lang.Double            |  63.55 | 7.5 % |      3.65 | 273.82 |    1.00 | 22.0.1 |
| java.lang.Float             |  67.66 | 7.8 % |      3.89 | 257.21 |    1.00 | 22.0.1 |
| java.math.BigDecimal        | 307.19 | 5.7 % |     17.65 |  56.65 |    1.00 | 22.0.1 |
| JavaDoubleParser String     | 470.83 | 2.1 % |     27.06 |  36.96 |    7.41 | 22.0.1 |
| JavaDoubleParser char[]     | 782.42 | 3.6 % |     44.96 |  22.24 |   12.31 | 22.0.1 |
| JavaDoubleParser byte[]     | 762.71 | 2.8 % |     43.83 |  22.82 |   12.00 | 22.0.1 |
| JsonDoubleParser String     | 476.51 | 4.3 % |     27.38 |  36.52 |    7.50 | 22.0.1 |
| JsonDoubleParser char[]     | 687.24 | 4.4 % |     39.49 |  25.32 |   10.81 | 22.0.1 |
| JsonDoubleParser byte[]     | 776.33 | 3.8 % |     44.61 |  22.41 |   12.22 | 22.0.1 |
| JavaFloatParser  String     | 435.91 | 3.7 % |     25.05 |  39.92 |    6.44 | 22.0.1 |
| JavaFloatParser  char[]     | 756.53 | 4.6 % |     43.48 |  23.00 |   11.18 | 22.0.1 |
| JavaFloatParser  byte[]     | 788.67 | 2.9 % |     45.32 |  22.06 |   11.66 | 22.0.1 |
| JavaBigDecimalParser String | 622.51 | 5.7 % |     35.77 |  27.95 |    2.03 | 22.0.1 |
| JavaBigDecimalParser char[] | 914.94 | 4.8 % |     52.58 |  19.02 |    2.98 | 22.0.1 |
| JavaBigDecimalParser byte[] | 916.15 | 2.6 % |     52.65 |  18.99 |    2.98 | 22.0.1 |


### The data file `mesh.txt`

This file contains input lines like `1749`, and `0.539081215858`.

Mac Mini (2018)<br>
CPU: Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz<br>
OS: Mac OS X, 14.5, 12 processors available<br>
VM: Java 22, OpenJDK 64-Bit Server VM, Oracle Corporation, 22.0.1+8-16<br>
-XX:CompileCommand=inline,java/lang/String.charAt<br>

| Method                      |   MB/s |  stdev | Mfloats/s |  ns/f | speedup | JDK    |
|-----------------------------|-------:|-------:|----------:|------:|--------:|--------|
| java.lang.Double            | 227.90 |  9.7 % |     31.05 | 32.21 |    1.00 | 22.0.1 |
| java.lang.Float             | 109.91 |  6.6 % |     14.97 | 66.79 |    1.00 | 22.0.1 |
| java.math.BigDecimal        | 248.86 |  4.9 % |     33.90 | 29.50 |    1.00 | 22.0.1 |
| JavaDoubleParser String     | 346.80 |  3.3 % |     47.24 | 21.17 |    1.52 | 22.0.1 |
| JavaDoubleParser char[]     | 468.55 | 11.2 % |     63.83 | 15.67 |    2.06 | 22.0.1 |
| JavaDoubleParser byte[]     | 508.89 |  2.5 % |     69.32 | 14.43 |    2.23 | 22.0.1 |
| JsonDoubleParser String     | 343.58 |  4.4 % |     46.80 | 21.37 |    1.51 | 22.0.1 |
| JsonDoubleParser char[]     | 463.02 |  2.3 % |     63.08 | 15.85 |    2.03 | 22.0.1 |
| JsonDoubleParser byte[]     | 437.25 |  9.1 % |     59.57 | 16.79 |    1.92 | 22.0.1 |
| JavaFloatParser  String     | 316.58 |  4.3 % |     43.13 | 23.19 |    2.88 | 22.0.1 |
| JavaFloatParser  char[]     | 446.73 |  7.5 % |     60.86 | 16.43 |    4.06 | 22.0.1 |
| JavaFloatParser  byte[]     | 381.73 |  7.8 % |     52.00 | 19.23 |    3.47 | 22.0.1 |
| JavaBigDecimalParser String | 347.91 | 10.1 % |     47.39 | 21.10 |    1.40 | 22.0.1 |
| JavaBigDecimalParser char[] | 439.71 |  7.3 % |     59.90 | 16.69 |    1.77 | 22.0.1 |
| JavaBigDecimalParser byte[] | 432.23 | 13.1 % |     58.88 | 16.98 |    1.74 | 22.0.1 |

MacBook Pro (2023)<br>
CPU: Apple M2 Max<br>
OS: Mac OS X, 14.4.1, 12 processors available<br>
VM: Java 22, OpenJDK 64-Bit Server VM, Oracle Corporation, 22.0.1+8-16<br>

| Method                      |   MB/s |  stdev | Mfloats/s |  ns/f | speedup | JDK    |
|-----------------------------|-------:|-------:|----------:|------:|--------:|--------|
| java.lang.Double            | 232.97 |  5.8 % |     31.74 | 31.51 |    1.00 | 22.0.1 |
| java.lang.Float             |  77.76 | 12.4 % |     10.59 | 94.41 |    1.00 | 22.0.1 |
| java.math.BigDecimal        | 222.91 |  8.0 % |     30.37 | 32.93 |    1.00 | 22.0.1 |
| JavaDoubleParser String     | 374.46 |  2.6 % |     51.01 | 19.60 |    1.61 | 22.0.1 |
| JavaDoubleParser char[]     | 564.49 |  3.8 % |     76.90 | 13.00 |    2.42 | 22.0.1 |
| JavaDoubleParser byte[]     | 568.67 |  4.0 % |     77.47 | 12.91 |    2.44 | 22.0.1 |
| JsonDoubleParser String     | 406.26 |  2.7 % |     55.34 | 18.07 |    1.74 | 22.0.1 |
| JsonDoubleParser char[]     | 551.59 |  3.7 % |     75.14 | 13.31 |    2.37 | 22.0.1 |
| JsonDoubleParser byte[]     | 574.77 |  5.0 % |     78.30 | 12.77 |    2.47 | 22.0.1 |
| JavaFloatParser  String     | 354.15 |  3.8 % |     48.24 | 20.73 |    4.55 | 22.0.1 |
| JavaFloatParser  char[]     | 521.56 |  3.6 % |     71.05 | 14.07 |    6.71 | 22.0.1 |
| JavaFloatParser  byte[]     | 445.93 |  2.7 % |     60.75 | 16.46 |    5.73 | 22.0.1 |
| JavaBigDecimalParser String | 439.91 |  6.6 % |     59.93 | 16.69 |    1.97 | 22.0.1 |
| JavaBigDecimalParser char[] | 603.77 |  7.5 % |     82.25 | 12.16 |    2.71 | 22.0.1 |
| JavaBigDecimalParser byte[] | 581.81 |  8.1 % |     79.26 | 12.62 |    2.61 | 22.0.1 |


### The data file `canada_hex.txt`

This file contains numbers in the range from -128 to +128 in hexadecimal notation.
Most input lines look like this: `-0x1.09219008205fcp6`.

Mac Mini (2018)<br>
CPU: Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz<br>
OS: Mac OS X, 14.5, 12 processors available<br>
VM: Java 22, OpenJDK 64-Bit Server VM, Oracle Corporation, 22.0.1+8-16<br>
-XX:CompileCommand=inline,java/lang/String.charAt<br>

| Method                  |   MB/s |  stdev | Mfloats/s |   ns/f | speedup | JDK    |
|-------------------------|-------:|-------:|----------:|-------:|--------:|--------|
| java.lang.Double        |  37.78 |  3.6 % |      2.07 | 482.77 |    1.00 | 22.0.1 |
| java.lang.Float         |  38.11 |  3.6 % |      2.09 | 478.55 |    1.00 | 22.0.1 |
| JavaDoubleParser String | 386.27 | 10.6 % |     21.18 |  47.22 |   10.22 | 22.0.1 |
| JavaDoubleParser char[] | 582.56 |  3.4 % |     31.94 |  31.31 |   15.42 | 22.0.1 |
| JavaDoubleParser byte[] | 761.83 |  8.3 % |     41.77 |  23.94 |   20.17 | 22.0.1 |
| JavaFloatParser  String | 415.73 |  9.7 % |     22.79 |  43.87 |   10.91 | 22.0.1 |
| JavaFloatParser  char[] | 539.76 |  7.4 % |     29.60 |  33.79 |   14.16 | 22.0.1 |
| JavaFloatParser  byte[] | 679.67 |  3.2 % |     37.27 |  26.83 |   17.83 | 22.0.1 |

MacBook Pro (2023)<br>
CPU: Apple M2 Max<br>
OS: Mac OS X, 14.4.1, 12 processors available<br>
VM: Java 22, OpenJDK 64-Bit Server VM, Oracle Corporation, 22.0.1+8-16<br>

| Method                  |   MB/s | stdev | Mfloats/s |   ns/f | speedup | JDK    |
|-------------------------|-------:|------:|----------:|-------:|--------:|--------|
| java.lang.Double        |  50.90 | 1.7 % |      2.79 | 358.30 |    1.00 | 22.0.1 |
| java.lang.Float         |  51.09 | 1.5 % |      2.80 | 356.97 |    1.00 | 22.0.1 |
| JavaDoubleParser String | 514.81 | 2.4 % |     28.23 |  35.43 |   10.11 | 22.0.1 |
| JavaDoubleParser char[] | 623.69 | 3.8 % |     34.20 |  29.24 |   12.25 | 22.0.1 |
| JavaDoubleParser byte[] | 821.31 | 2.2 % |     45.03 |  22.21 |   16.14 | 22.0.1 |
| JavaFloatParser  String | 475.82 | 2.6 % |     26.09 |  38.33 |    9.31 | 22.0.1 |
| JavaFloatParser  char[] | 584.62 | 2.8 % |     32.06 |  31.20 |   11.44 | 22.0.1 |
| JavaFloatParser  byte[] | 791.85 | 3.8 % |     43.42 |  23.03 |   15.50 | 22.0.1 |

### Comparison with C version

For comparison. here are the test results
of [simple_fastfloat_benchmark](https://github.com/lemire/simple_fastfloat_benchmark)  
on the same computer:

    version: Thu Mar 31 10:18:12 2022 -0400 f2082bf747eabc0873f2fdceb05f9451931b96dc

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256

    $ ./build/benchmarks/benchmark
    # parsing random numbers
    available models (-m): uniform one_over_rand32 simple_uniform32 simple_int32 int_e_int simple_int64 bigint_int_dot_int big_ints 
    model: generate random numbers uniformly in the interval [0.0.1.0]
    volume: 100000 floats
    volume = 2.09808 MB 
    netlib                      :   317.31 MB/s (+/- 6.0 %)    15.12 Mfloat/s      66.12 ns/f 
    doubleconversion            :   263.89 MB/s (+/- 4.2 %)    12.58 Mfloat/s      79.51 ns/f 
    strtod                      :    86.13 MB/s (+/- 3.7 %)     4.10 Mfloat/s     243.61 ns/f 
    abseil                      :   467.27 MB/s (+/- 9.0 %)    22.27 Mfloat/s      44.90 ns/f 
    fastfloat                   :   880.79 MB/s (+/- 6.6 %)    41.98 Mfloat/s      23.82 ns/f 

    Java 22, OpenJDK 64-Bit Server VM, Oracle Corporation, 22.0.1+8-16
    java.lang.Double            :    94.32 MB/s (+/- 3.1 %)     5.41 Mfloat/s     184.73 ns/f     1.00 speedup
    JavaDoubleParser String     :   585.84 MB/s (+/-12.4 %)    33.62 Mfloat/s      29.74 ns/f     6.21 speedup
    JavaDoubleParser char[]     :   659.27 MB/s (+/- 9.6 %)    37.84 Mfloat/s      26.43 ns/f     6.99 speedup
    JavaDoubleParser byte[]     :   729.46 MB/s (+/- 9.7 %)    41.86 Mfloat/s      23.89 ns/f     7.73 speedup

'

    $ ./build/benchmarks/benchmark -f data/canada.txt
    # read 111126 lines 
    volume = 1.93374 MB 
    netlib                      :   337.79 MB/s (+/- 5.8 %)    19.41 Mfloat/s      51.52 ns/f 
    doubleconversion            :   254.22 MB/s (+/- 6.0 %)    14.61 Mfloat/s      68.45 ns/f 
    strtod                      :    73.33 MB/s (+/- 7.1 %)     4.21 Mfloat/s     237.31 ns/f 
    abseil                      :   411.11 MB/s (+/- 7.3 %)    23.63 Mfloat/s      42.33 ns/f 
    fastfloat                   :   741.32 MB/s (+/- 5.3 %)    42.60 Mfloat/s      23.47 ns/f 

     Java 22, OpenJDK 64-Bit Server VM, Oracle Corporation, 22.0.1+8-16
    java.lang.Double            :    87.48 MB/s (+/- 3.2 %)     5.03 Mfloat/s     198.93 ns/f     1.00 speedup
    JavaDoubleParser String     :   386.93 MB/s (+/- 8.8 %)    22.24 Mfloat/s      44.97 ns/f     4.42 speedup
    JavaDoubleParser char[]     :   637.55 MB/s (+/- 9.0 %)    36.64 Mfloat/s      27.29 ns/f     7.29 speedup
    JavaDoubleParser byte[]     :   694.16 MB/s (+/- 7.9 %)    39.89 Mfloat/s      25.07 ns/f     7.94 speedup

# Building and running the code

This project requires **at least** the items below to build it from source:

- Maven 3.8.6
- OpenJDK SE 22

This project contains optimised code for various JDK versions.
If you intend to assess the fitness and/or performance of this project for all supported
JDKs, you **also need to** install the following items:

- OpenJDK SE 8
- OpenJDK SE 11
- OpenJDK SE 17
- OpenJDK SE 21
- OpenJDK SE 22

When you clone the code repository from github. you can choose from the following branches:

- `main` Aims to contain only working code.
- `dev` This code may or may not work. This code uses the experimental Vector API, and the Foreign Memory Access API,
  that are included in Java 22.

## Command sequence with Java SE 20 on macOS:

```shell
git clone https://github.com/wrandelshofer/FastDoubleParser.git
cd FastDoubleParser 
javac --enable-preview -source 22 -d out -encoding utf8 --module-source-path fastdoubleparser-dev/src/main/java --module ch.randelshofer.fastdoubleparser    
javac --enable-preview -source 22 -d out -encoding utf8 -p out --module-source-path fastdoubleparserdemo-dev/src/main/java --module ch.randelshofer.fastdoubleparserdemo
java -XX:CompileCommand=inline,java/lang/String.charAt --enable-preview -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main  
java -XX:CompileCommand=inline,java/lang/String.charAt --enable-preview -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main FastDoubleParserDemo/data/canada.txt   
```

## Command sequence with Java SE 8, 11, 17, 21, and 22, and Maven 3.8.6 on macOS:

```shell
git clone https://github.com/wrandelshofer/FastDoubleParser.git
cd FastDoubleParser
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-22.jdk/Contents/Home 
mvn clean
mvn package
export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-22.jdk/Contents/Home 
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/mesh.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada_hex.txt
export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home 
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/mesh.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada_hex.txt
export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home 
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/mesh.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada_hex.txt
export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-11.jdk/Contents/Home
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/mesh.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada_hex.txt
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-8.jdk/Contents/Home
java -XX:CompileCommand=inline,java/lang/String.charAt -cp "fastdoubleparser/target/*:fastdoubleparserdemo/target/*" ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -XX:CompileCommand=inline,java/lang/String.charAt -cp "fastdoubleparser/target/*:fastdoubleparserdemo/target/*" ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -cp "fastdoubleparser/target/*:fastdoubleparserdemo/target/*" ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/mesh.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -cp "fastdoubleparser/target/*:fastdoubleparserdemo/target/*" ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada_hex.txt
```

## IntelliJ IDEA with Java SE 8, 11, 17, 21, 22 on macOS

Prerequisites:

1. Install the following Java SDKs: 8, 11, 17, 21, 22.
   _If you do not need to edit the code, you only need to install the Java 22 SDK._
2. Install IntelliJ IDEA

Steps:

1. Start IntelliJ IDEA
2. From the main menu, choose **Git > Clone...**
3. In the dialog that opens, enter the URL https://github.com/wrandelshofer/FastDoubleParser.git,
   specify the directory in which you want to save the project and click **Clone**.
4. Intellij IDEA will now clone the repository and open a new project window.
   However, the project modules are not yet configured correctly.
5. From the main menu of the new project window, choose **View > Tool Windows > Maven**
6. In the Maven tool window, run the Maven build **Parent project for FastDoubleParser > Lifecycle > compile**
7. In the toolbar of the Maven tool window, click **Reload All Maven Projects**
8. Intellij IDEA knows now for each module, where the **source**,
   **generated source**,  **test source**, and **generated test source** folders are.
   However, the project modules have still incorrect JDK dependencies.
9. _You can skip this step, if you do not want to edit the code._
   From the main menu, choose **File > Project Structure...**
10. _You can skip this step, if you do not want to edit the code._
    In the dialog that opens, select in the navigation bar **Project Settings > Modules**
11. _You can skip this step, if you do not want to edit the code._
    For each module in the right pane of the dialog, select the **Dependencies** tab.
    Specify the corresponding **Module SDK** for modules which have a name that ends in
    **-Java8**, **-Java11**, **-Java17**, **-Java21**.
    Do not change modules with other name endings - they must stay on the Java 20 SDK.

12. From the main menu, choose **Build > Build Project**
    Intellij IDEA will now properly build the project.

## Editing the code

The majority of the code is located in the module named **fastdoubleparser-dev**,
and **fastdoubleparserdemo-dev**.
The code in these modules uses early access features of the Java 20 SDK.

Modules which have a name that ends in **-Java8**, **-Java11**, **-Java17**, **-Java21**, **-Java22**
contain deltas of the **-dev** modules.

The delta code is located in the **source** and **test** folders of the module.
Code from the **-dev** module is located in the **generated source** and
**generated test source** folders.

The Maven POM of a module contains **maven-resources-plugin** elements that copy code
from the **-dev** module to the delta modules.

## Testing the code

Unfortunately it is not possible to test floating parsers exhaustively, because the input
and output spaces are far too big.

| Parser               | Input Space                                                                                     | Output Space                                                                            |
|----------------------|-------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|
| JavaDoubleParser     | 1 to 2<sup>31</sup>-1 chars<br>= 65536<sup>2<sup>31</sup></sup><br>= 2<sup>34,359,738,368</sup> | 64 bits<br>= 2<sup>64</sup>                                                             |
| JavaFloatParser      | 1 to 2<sup>31</sup>-1 chars<br>= 2<sup>34,359,738,368</sup>                                     | 32 bits<br>= 2<sup>32</sup>                                                             |
| JsonDoubleParser     | 1 to 2<sup>31</sup>-1 chars<br>= 2<sup>34,359,738,368</sup>                                     | 64 bits<br>= 2<sup>64</sup>                                                             |
| JavaBigIntegerParser | 1 to 1,292,782,622 chars<br>= 65536<sup>1292782623</sup><br>= 2<sup>20,684,521,968</sup>        | 0 to 2<sup>31</sup> bits<br>= 2<sup>2<sup>31</sup></sup><br>= 2<sup>2,147,483,648</sup> |
| JavaBigDecimalParser | 1 to 1,292,782,635 chars<br>= 65536<sup>1292782636</sup><br>= 2<sup>20,684,522,176</sup>        | 0 to 2<sup>31</sup> bit mantissa * 64 bit exponent<br>= 2<sup>12,884,901,888</sup>      |

You can quickly run a number of hand-picked tests that aim for 100 % line coverage:

```
mvn -DenableLongRunningTests=true test
```

You can run additional tests with the following command. The purpose of these tests is to explore additional
regions of the input and output spaces.

```
mvn -DenableLongRunningTests=true test
```