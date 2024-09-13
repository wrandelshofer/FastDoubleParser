[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ch.randelshofer/fastdoubleparser/badge.svg)](https://maven-badges.herokuapp.com/maven-central/ch.randelshofer/fastdoubleparser)

# FastDoubleParser

This is a Java port of Daniel Lemire's [fast_float](https://github.com/fastfloat/fast_float) project.

This project provides parsers for `double`, `float`, `BigDecimal` and `BigInteger` values.
The `double` and `float` parsers are optimised for speed for the most common inputs.
The `BigDecimal` and `BigInteger` parsers are optimised for speed on all inputs.

The code in this project contains optimised versions for Java SE 1.8, 11, 17, 21 and 22.
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

The JVM does not reliably inline `String.charAt(int)`. This may negatively impact the
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
OS: Mac OS X, 14.6.1, 12 processors available<br>
VM: Java 24, OpenJDK 64-Bit Server VM, Azul Systems, Inc., 24-beta+13<br>
-XX:CompileCommand=inline,java/lang/String.charAt<br>

| Method                      |   MB/s |  stdev | Mfloats/s |   ns/f | speedup | JDK     |
|-----------------------------|-------:|-------:|----------:|-------:|--------:|---------|
| java.lang.Double            |  90.34 |  3.9 % |      5.19 | 192.84 |    1.00 | 24-beta |
| java.lang.Float             | 100.09 |  3.7 % |      5.75 | 174.05 |    1.00 | 24-beta |
| java.math.BigDecimal        | 185.08 |  5.5 % |     10.62 |  94.13 |    1.00 | 24-beta |
| JavaDoubleParser String     | 544.88 | 13.0 % |     31.28 |  31.97 |    6.03 | 24-beta |
| JavaDoubleParser char[]     | 617.45 | 11.2 % |     35.44 |  28.21 |    6.83 | 24-beta |
| JavaDoubleParser byte[]     | 736.46 |  3.1 % |     42.27 |  23.66 |    8.15 | 24-beta |
| JsonDoubleParser String     | 591.50 |  8.3 % |     33.95 |  29.45 |    6.55 | 24-beta |
| JsonDoubleParser char[]     | 696.54 |  6.1 % |     39.98 |  25.01 |    7.71 | 24-beta |
| JsonDoubleParser byte[]     | 655.92 |  3.1 % |     37.65 |  26.56 |    7.26 | 24-beta |
| JavaFloatParser  String     | 551.75 |  6.3 % |     31.67 |  31.57 |    5.51 | 24-beta |
| JavaFloatParser  char[]     | 708.39 |  6.2 % |     40.66 |  24.59 |    7.08 | 24-beta |
| JavaFloatParser  byte[]     | 736.70 |  5.9 % |     42.29 |  23.65 |    7.36 | 24-beta |
| JavaBigDecimalParser String | 530.96 |  8.2 % |     30.48 |  32.81 |    2.87 | 24-beta |
| JavaBigDecimalParser char[] | 637.52 |  6.0 % |     36.59 |  27.33 |    3.44 | 24-beta |
| JavaBigDecimalParser byte[] | 659.48 |  2.9 % |     37.85 |  26.42 |    3.56 | 24-beta |

MacBook Pro (2023)<br>
CPU: Apple M2 Max<br>
OS: Mac OS X, 14.6.1, 12 processors available<br>
VM: Java 24, OpenJDK 64-Bit Server VM, Azul Systems, Inc., 24-beta+13<br>
-XX:CompileCommand=inline,java/lang/String.charAt<br>

| Method                      |   MB/s | stdev | Mfloats/s |   ns/f | speedup | JDK     |
|-----------------------------|-------:|------:|----------:|-------:|--------:|---------|
| java.lang.Double            | 103.13 | 5.8 % |      5.92 | 168.97 |    1.00 | 24-beta |
| java.lang.Float             | 121.32 | 2.4 % |      6.96 | 143.64 |    1.00 | 24-beta |
| java.math.BigDecimal        | 189.35 | 2.9 % |     10.87 |  92.03 |    1.00 | 24-beta |
| JavaDoubleParser String     | 616.08 | 7.2 % |     35.35 |  28.28 |    5.97 | 24-beta |
| JavaDoubleParser char[]     | 779.92 | 4.9 % |     44.76 |  22.34 |    7.56 | 24-beta |
| JavaDoubleParser byte[]     | 815.18 | 3.9 % |     46.78 |  21.38 |    7.90 | 24-beta |
| JsonDoubleParser String     | 544.99 | 6.3 % |     31.28 |  31.97 |    5.28 | 24-beta |
| JsonDoubleParser char[]     | 796.67 | 4.4 % |     45.72 |  21.87 |    7.72 | 24-beta |
| JsonDoubleParser byte[]     | 804.16 | 2.8 % |     46.15 |  21.67 |    7.80 | 24-beta |
| JavaFloatParser  String     | 633.53 | 3.6 % |     36.36 |  27.51 |    5.22 | 24-beta |
| JavaFloatParser  char[]     | 827.80 | 3.7 % |     47.50 |  21.05 |    6.82 | 24-beta |
| JavaFloatParser  byte[]     | 690.68 | 3.7 % |     39.64 |  25.23 |    5.69 | 24-beta |
| JavaBigDecimalParser String | 634.82 | 6.8 % |     36.43 |  27.45 |    3.35 | 24-beta |
| JavaBigDecimalParser char[] | 736.93 | 1.9 % |     42.29 |  23.65 |    3.89 | 24-beta |
| JavaBigDecimalParser byte[] | 676.75 | 4.7 % |     38.84 |  25.75 |    3.57 | 24-beta |


### The data file `canada.txt`

This file contains numbers in the range from -128 to +128.
Most input lines look like this: `52.038048000000117`.

Mac Mini (2018)<br>
CPU: Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz<br>
OS: Mac OS X, 14.6.1, 12 processors available<br>
VM: Java 24, OpenJDK 64-Bit Server VM, Azul Systems, Inc., 24-beta+13<br>
-XX:CompileCommand=inline,java/lang/String.charAt<br>

| Method                      |   MB/s |  stdev | Mfloats/s |   ns/f | speedup | JDK     |
|-----------------------------|-------:|-------:|----------:|-------:|--------:|---------|
| java.lang.Double            |  84.62 |  3.2 % |      4.86 | 205.64 |    1.00 | 24-beta |
| java.lang.Float             | 100.81 |  4.4 % |      5.79 | 172.62 |    1.00 | 24-beta |
| java.math.BigDecimal        | 322.73 |  8.6 % |     18.55 |  53.92 |    1.00 | 24-beta |
| JavaDoubleParser String     | 406.95 | 10.3 % |     23.39 |  42.76 |    4.81 | 24-beta |
| JavaDoubleParser char[]     | 627.35 |  8.4 % |     36.05 |  27.74 |    7.41 | 24-beta |
| JavaDoubleParser byte[]     | 685.50 |  6.6 % |     39.39 |  25.38 |    8.10 | 24-beta |
| JsonDoubleParser String     | 400.24 |  5.9 % |     23.00 |  43.48 |    4.73 | 24-beta |
| JsonDoubleParser char[]     | 573.10 |  3.8 % |     32.93 |  30.36 |    6.77 | 24-beta |
| JsonDoubleParser byte[]     | 693.39 |  3.1 % |     39.85 |  25.10 |    8.19 | 24-beta |
| JavaFloatParser  String     | 390.37 |  4.4 % |     22.43 |  44.58 |    3.87 | 24-beta |
| JavaFloatParser  char[]     | 637.53 |  4.1 % |     36.64 |  27.29 |    6.32 | 24-beta |
| JavaFloatParser  byte[]     | 634.38 |  3.4 % |     36.46 |  27.43 |    6.29 | 24-beta |
| JavaBigDecimalParser String | 465.26 |  5.7 % |     26.74 |  37.40 |    1.44 | 24-beta |
| JavaBigDecimalParser char[] | 652.82 |  8.6 % |     37.52 |  26.66 |    2.02 | 24-beta |
| JavaBigDecimalParser byte[] | 633.94 |  4.0 % |     36.43 |  27.45 |    1.96 | 24-beta |

MacBook Pro (2023)<br>
CPU: Apple M2 Max<br>
OS: Mac OS X, 14.6.1, 12 processors available<br>
VM: Java 24, OpenJDK 64-Bit Server VM, Azul Systems, Inc., 24-beta+13<br>
-XX:CompileCommand=inline,java/lang/String.charAt<br>

| Method                      |   MB/s | stdev | Mfloats/s |   ns/f | speedup | JDK     |
|-----------------------------|-------:|------:|----------:|-------:|--------:|---------|
| java.lang.Double            | 100.17 | 4.5 % |      5.76 | 173.72 |    1.00 | 24-beta |
| java.lang.Float             | 119.69 | 5.8 % |      6.88 | 145.39 |    1.00 | 24-beta |
| java.math.BigDecimal        | 383.19 | 3.6 % |     22.02 |  45.41 |    1.00 | 24-beta |
| JavaDoubleParser String     | 518.31 | 1.3 % |     29.79 |  33.57 |    5.17 | 24-beta |
| JavaDoubleParser char[]     | 699.50 | 3.8 % |     40.20 |  24.88 |    6.98 | 24-beta |
| JavaDoubleParser byte[]     | 739.89 | 2.7 % |     42.52 |  23.52 |    7.39 | 24-beta |
| JsonDoubleParser String     | 488.27 | 3.4 % |     28.06 |  35.64 |    4.87 | 24-beta |
| JsonDoubleParser char[]     | 601.89 | 3.1 % |     34.59 |  28.91 |    6.01 | 24-beta |
| JsonDoubleParser byte[]     | 680.65 | 2.8 % |     39.11 |  25.57 |    6.79 | 24-beta |
| JavaFloatParser  String     | 490.24 | 2.7 % |     28.17 |  35.50 |    4.10 | 24-beta |
| JavaFloatParser  char[]     | 681.75 | 2.1 % |     39.18 |  25.52 |    5.70 | 24-beta |
| JavaFloatParser  byte[]     | 697.36 | 2.4 % |     40.07 |  24.95 |    5.83 | 24-beta |
| JavaBigDecimalParser String | 475.46 | 1.7 % |     27.32 |  36.60 |    1.24 | 24-beta |
| JavaBigDecimalParser char[] | 794.76 | 2.7 % |     45.67 |  21.90 |    2.07 | 24-beta |
| JavaBigDecimalParser byte[] | 747.19 | 4.8 % |     42.94 |  23.29 |    1.95 | 24-beta |


### The data file `mesh.txt`

This file contains input lines like `1749`, and `0.539081215858`.

Mac Mini (2018)<br>
CPU: Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz<br>
OS: Mac OS X, 14.6.1, 12 processors available<br>
VM: Java 24, OpenJDK 64-Bit Server VM, Azul Systems, Inc., 24-beta+13<br>
-XX:CompileCommand=inline,java/lang/String.charAt<br>

| Method                      |   MB/s |  stdev | Mfloats/s |  ns/f | speedup | JDK     |
|-----------------------------|-------:|-------:|----------:|------:|--------:|---------|
| java.lang.Double            | 233.08 | 11.3 % |     31.75 | 31.49 |    1.00 | 24-beta |
| java.lang.Float             | 115.75 |  5.9 % |     15.77 | 63.42 |    1.00 | 24-beta |
| java.math.BigDecimal        | 245.43 | 12.4 % |     33.43 | 29.91 |    1.00 | 24-beta |
| JavaDoubleParser String     | 328.64 | 13.6 % |     44.77 | 22.34 |    1.41 | 24-beta |
| JavaDoubleParser char[]     | 472.90 |  4.0 % |     64.42 | 15.52 |    2.03 | 24-beta |
| JavaDoubleParser byte[]     | 481.44 |  9.1 % |     65.59 | 15.25 |    2.07 | 24-beta |
| JsonDoubleParser String     | 340.62 |  5.7 % |     46.40 | 21.55 |    1.46 | 24-beta |
| JsonDoubleParser char[]     | 419.50 |  6.8 % |     57.15 | 17.50 |    1.80 | 24-beta |
| JsonDoubleParser byte[]     | 436.21 |  7.2 % |     59.42 | 16.83 |    1.87 | 24-beta |
| JavaFloatParser  String     | 321.18 |  7.1 % |     43.75 | 22.86 |    2.77 | 24-beta |
| JavaFloatParser  char[]     | 428.64 |  5.4 % |     58.39 | 17.13 |    3.70 | 24-beta |
| JavaFloatParser  byte[]     | 438.92 |  4.3 % |     59.79 | 16.72 |    3.79 | 24-beta |
| JavaBigDecimalParser String | 344.87 |  6.2 % |     46.98 | 21.29 |    1.41 | 24-beta |
| JavaBigDecimalParser char[] | 452.22 |  7.0 % |     61.60 | 16.23 |    1.84 | 24-beta |
| JavaBigDecimalParser byte[] | 469.67 |  8.3 % |     63.98 | 15.63 |    1.91 | 24-beta |

MacBook Pro (2023)<br>
CPU: Apple M2 Max<br>
OS: Mac OS X, 14.6.1, 12 processors available<br>
VM: Java 24, OpenJDK 64-Bit Server VM, Azul Systems, Inc., 24-beta+13<br>
-XX:CompileCommand=inline,java/lang/String.charAt<br>

| Method                      |   MB/s | stdev | Mfloats/s |  ns/f | speedup | JDK     |
|-----------------------------|-------:|------:|----------:|------:|--------:|---------|
| java.lang.Double            | 235.18 | 6.5 % |     32.04 | 31.21 |    1.00 | 24-beta |
| java.lang.Float             | 124.91 | 5.5 % |     17.02 | 58.77 |    1.00 | 24-beta |
| java.math.BigDecimal        | 279.83 | 4.1 % |     38.12 | 26.23 |    1.00 | 24-beta |
| JavaDoubleParser String     | 405.69 | 2.6 % |     55.27 | 18.09 |    1.73 | 24-beta |
| JavaDoubleParser char[]     | 460.44 | 3.6 % |     62.73 | 15.94 |    1.96 | 24-beta |
| JavaDoubleParser byte[]     | 482.24 | 3.5 % |     65.69 | 15.22 |    2.05 | 24-beta |
| JsonDoubleParser String     | 422.72 | 3.6 % |     57.59 | 17.37 |    1.80 | 24-beta |
| JsonDoubleParser char[]     | 454.11 | 3.0 % |     61.86 | 16.16 |    1.93 | 24-beta |
| JsonDoubleParser byte[]     | 501.59 | 3.4 % |     68.33 | 14.63 |    2.13 | 24-beta |
| JavaFloatParser  String     | 321.48 | 3.8 % |     43.79 | 22.83 |    2.57 | 24-beta |
| JavaFloatParser  char[]     | 414.89 | 2.3 % |     56.52 | 17.69 |    3.32 | 24-beta |
| JavaFloatParser  byte[]     | 456.44 | 3.4 % |     62.18 | 16.08 |    3.65 | 24-beta |
| JavaBigDecimalParser String | 354.47 | 4.1 % |     48.29 | 20.71 |    1.27 | 24-beta |
| JavaBigDecimalParser char[] | 414.05 | 7.7 % |     56.41 | 17.73 |    1.48 | 24-beta |
| JavaBigDecimalParser byte[] | 536.53 | 7.4 % |     73.09 | 13.68 |    1.92 | 24-beta |


### The data file `canada_hex.txt`

This file contains numbers in the range from -128 to +128 in hexadecimal notation.
Most input lines look like this: `-0x1.09219008205fcp6`.

Mac Mini (2018)<br>
CPU: Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz<br>
OS: Mac OS X, 14.6.1, 12 processors available<br>
VM: Java 24, OpenJDK 64-Bit Server VM, Azul Systems, Inc., 24-beta+13<br>
-XX:CompileCommand=inline,java/lang/String.charAt<br>

| Method                  |   MB/s | stdev | Mfloats/s |   ns/f | speedup | JDK     |
|-------------------------|-------:|------:|----------:|-------:|--------:|---------|
| java.lang.Double        |  41.65 | 3.2 % |      2.28 | 437.87 |    1.00 | 24-beta |
| java.lang.Float         |  41.70 | 3.0 % |      2.29 | 437.39 |    1.00 | 24-beta |
| JavaDoubleParser String | 413.23 | 9.7 % |     22.66 |  44.14 |    9.92 | 24-beta |
| JavaDoubleParser char[] | 558.89 | 7.8 % |     30.64 |  32.63 |   13.42 | 24-beta |
| JavaDoubleParser byte[] | 787.78 | 7.1 % |     43.19 |  23.15 |   18.91 | 24-beta |
| JavaFloatParser  String | 411.22 | 7.4 % |     22.55 |  44.35 |    9.86 | 24-beta |
| JavaFloatParser  char[] | 468.57 | 6.7 % |     25.69 |  38.92 |   11.24 | 24-beta |
| JavaFloatParser  byte[] | 788.61 | 2.9 % |     43.24 |  23.13 |   18.91 | 24-beta |

MacBook Pro (2023)<br>
CPU: Apple M2 Max<br>
OS: Mac OS X, 14.6.1, 12 processors available<br>
VM: Java 24, OpenJDK 64-Bit Server VM, Azul Systems, Inc., 24-beta+13<br>
-XX:CompileCommand=inline,java/lang/String.charAt<br>

| Method                  |   MB/s | stdev | Mfloats/s |   ns/f | speedup | JDK     |
|-------------------------|-------:|------:|----------:|-------:|--------:|---------|
| java.lang.Double        |  41.95 | 1.4 % |      2.30 | 434.75 |    1.00 | 24-beta |
| java.lang.Float         |  42.07 | 1.5 % |      2.31 | 433.52 |    1.00 | 24-beta |
| JavaDoubleParser String | 430.37 | 2.1 % |     23.60 |  42.38 |   10.26 | 24-beta |
| JavaDoubleParser char[] | 623.75 | 2.8 % |     34.20 |  29.24 |   14.87 | 24-beta |
| JavaDoubleParser byte[] | 840.88 | 2.8 % |     46.11 |  21.69 |   20.04 | 24-beta |
| JavaFloatParser  String | 404.41 | 2.0 % |     22.17 |  45.10 |    9.61 | 24-beta |
| JavaFloatParser  char[] | 560.33 | 2.2 % |     30.72 |  32.55 |   13.32 | 24-beta |
| JavaFloatParser  byte[] | 828.25 | 2.9 % |     45.41 |  22.02 |   19.69 | 24-beta |

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

## Command sequence with Java SE 22 on macOS:

```shell
git clone https://github.com/wrandelshofer/FastDoubleParser.git
cd FastDoubleParser 
export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-22.jdk/Contents/Home 
javac --enable-preview -source 22 -d out -encoding utf8 --module-source-path fastdoubleparser-dev/src/main/java --module ch.randelshofer.fastdoubleparser    
javac --enable-preview -source 22 -d out -encoding utf8 -p out --module-source-path fastdoubleparserdemo-dev/src/main/java --module ch.randelshofer.fastdoubleparserdemo
java -XX:CompileCommand=inline,java/lang/String.charAt --enable-preview -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -XX:CompileCommand=inline,java/lang/String.charAt --enable-preview -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt   
java -XX:CompileCommand=inline,java/lang/String.charAt --enable-preview -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/mesh.txt   
java -XX:CompileCommand=inline,java/lang/String.charAt --enable-preview -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada_hex.txt   
```

## Command sequence with Azul Zulu Java SE 8, 11, 17, 21, and 22, and Maven 3.9.8 on macOS:

```shell
git clone https://github.com/wrandelshofer/FastDoubleParser.git
cd FastDoubleParser
export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-22.jdk/Contents/Home 
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