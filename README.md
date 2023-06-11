[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ch.randelshofer/fastdoubleparser/badge.svg)](https://maven-badges.herokuapp.com/maven-central/ch.randelshofer/fastdoubleparser)

# FastDoubleParser

This is a Java port of Daniel Lemire's [fast_float](https://github.com/fastfloat/fast_float) project.

This project provides parsers for `double`, `float`, `BigDecimal` and `BigInteger` values.
The `double` and `float` parsers are optimised for speed for the most common inputs.
The `BigDecimal` and `BigInteger` parsers are optimised for speed on all inputs.

The code in this project contains optimised versions for Java SE 1.8, 11, 17, 19 and 20.
The code is released in a single multi-release jar, which contains the code for all these versions
except 20.

## License

Everything except the content of the folder `supplemental_test_files` is MIT License.

The content of the folder `supplemental_test_files` is Apache 2.0 License.

Alternatively, you can license this project under the Apache 2.0 License. In this case, you do not need to
include the MIT License in your project.
If you copy source files, make sure that you change the copyright notice in the copied files accordingly.
So that it is immediately clear under which license you use the code. For example:

- Replace the file headers with the file headers of your project.
- Insert a comment in the file, that states that the file originates from
  FastDoubleParser, Copyright © Werner Randelshofer, Switzerland, Apache 2.0 License.

### Code License

Some code *in* this project is derived from the following projects:

- [fast_float](https://github.com/fastfloat/fast_float), licensed
  under [Apache 2.0 License](https://github.com/fastfloat/fast_float/blob/dc88f6f882ac7eb8ec3765f633835cb76afa0ac2/LICENSE-APACHE)
- [bigint](https://github.com/tbuktu/bigint/tree/floatfft), licensed
  under [BSD 2-clause License](https://github.com/tbuktu/bigint/blob/617c8cd8a7c5e4fb4d919c6a4d11e2586107f029/LICENSE)

The code is marked as such.

If you redistribute code, you must follow the terms of all involved licenses.

The build scripts in this project do include the data that are required by these licenses in deployed source- and
classes-Jar files.

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

On my Mac mini (2018) I get the results shown below.

    CPU: Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    VM : Java 20, OpenJDK 64-Bit Server VM, Oracle Corporation, 20.0.1+9-29

### Random double numbers in the range from 0 to 1

Most input lines look like this: `0.4011441469603171`.

| Method                      |   MB/s |  stdev | Mfloats/s |   ns/f | speedup | JDK    |
|-----------------------------|-------:|-------:|----------:|-------:|--------:|--------|
| java.lang.Double            |  90.27 |  2.6 % |      5.18 | 192.99 |    1.00 | 20.0.1 |
| java.lang.Float             |  94.81 |  3.1 % |      5.44 | 183.75 |    1.00 | 20.0.1 |
| java.math.BigDecimal        | 175.94 |  5.1 % |     10.10 |  99.02 |    1.00 | 20.0.1 |
| JavaDoubleParser String     | 529.71 | 15.5 % |     30.41 |  32.89 |    5.87 | 20.0.1 |
| JavaDoubleParser char[]     | 692.80 |  4.1 % |     39.77 |  25.15 |    7.67 | 20.0.1 |
| JavaDoubleParser byte[]     | 719.80 |  9.4 % |     41.32 |  24.20 |    7.97 | 20.0.1 |
| JsonDoubleParser String     | 397.93 | 12.0 % |     22.84 |  43.78 |    4.41 | 20.0.1 |
| JsonDoubleParser char[]     | 634.79 | 12.2 % |     36.44 |  27.44 |    7.03 | 20.0.1 |
| JsonDoubleParser byte[]     | 678.56 |  3.4 % |     38.95 |  25.67 |    7.52 | 20.0.1 |
| JavaFloatParser  String     | 462.01 | 15.6 % |     26.52 |  37.71 |    4.87 | 20.0.1 |
| JavaFloatParser  char[]     | 715.02 | 13.5 % |     41.04 |  24.37 |    7.54 | 20.0.1 |
| JavaFloatParser  byte[]     | 730.54 | 10.6 % |     41.93 |  23.85 |    7.71 | 20.0.1 |
| JavaBigDecimalParser String | 518.90 | 17.4 % |     29.78 |  33.57 |    2.95 | 20.0.1 |
| JavaBigDecimalParser char[] | 668.01 |  5.5 % |     38.34 |  26.08 |    3.80 | 20.0.1 |
| JavaBigDecimalParser byte[] | 636.84 | 12.3 % |     36.55 |  27.36 |    3.62 | 20.0.1 |

### The data file `canada.txt`

This file contains numbers in the range from -128 to +128.
Most input lines look like this: `52.038048000000117`.

| Method                      |   MB/s |  stdev | Mfloats/s |   ns/f | speedup | JDK    |
|-----------------------------|-------:|-------:|----------:|-------:|--------:|--------|
| java.lang.Double            |  78.95 |  4.3 % |      4.54 | 220.41 |    1.00 | 20.0.1 |
| java.lang.Float             |  93.71 |  3.2 % |      5.39 | 185.70 |    1.00 | 20.0.1 |
| java.math.BigDecimal        | 319.11 |  2.7 % |     18.34 |  54.53 |    1.00 | 20.0.1 |
| JavaDoubleParser String     | 365.79 | 10.0 % |     21.02 |  47.57 |    4.63 | 20.0.1 |
| JavaDoubleParser char[]     | 597.98 | 14.8 % |     34.36 |  29.10 |    7.57 | 20.0.1 |
| JavaDoubleParser byte[]     | 659.34 |  2.7 % |     37.89 |  26.39 |    8.35 | 20.0.1 |
| JsonDoubleParser String     | 369.28 | 10.4 % |     21.22 |  47.12 |    4.68 | 20.0.1 |
| JsonDoubleParser char[]     | 567.69 | 10.8 % |     32.62 |  30.65 |    7.19 | 20.0.1 |
| JsonDoubleParser byte[]     | 665.41 |  4.3 % |     38.24 |  26.15 |    8.43 | 20.0.1 |
| JavaFloatParser  String     | 360.22 | 10.0 % |     20.70 |  48.31 |    3.84 | 20.0.1 |
| JavaFloatParser  char[]     | 646.58 | 12.4 % |     37.16 |  26.91 |    6.90 | 20.0.1 |
| JavaFloatParser  byte[]     | 683.14 |  2.9 % |     39.26 |  25.47 |    7.29 | 20.0.1 |
| JavaBigDecimalParser String | 426.62 | 11.0 % |     24.52 |  40.79 |    1.34 | 20.0.1 |
| JavaBigDecimalParser char[] | 671.93 | 15.8 % |     38.61 |  25.90 |    2.11 | 20.0.1 |
| JavaBigDecimalParser byte[] | 732.53 |  4.9 % |     42.10 |  23.76 |    2.30 | 20.0.1 |

### The data file `mesh.txt`

This file contains input lines like `1749`, and `0.539081215858`.

| Method                      |   MB/s |  stdev | Mfloats/s |  ns/f | speedup | JDK    |
|-----------------------------|-------:|-------:|----------:|------:|--------:|--------|
| java.lang.Double            | 219.85 | 10.8 % |     29.95 | 33.39 |    1.00 | 20.0.1 |
| java.lang.Float             | 103.24 |  8.3 % |     14.06 | 71.10 |    1.00 | 20.0.1 |
| java.math.BigDecimal        | 246.50 |  6.8 % |     33.58 | 29.78 |    1.00 | 20.0.1 |
| JavaDoubleParser String     | 319.75 | 17.8 % |     43.56 | 22.96 |    1.45 | 20.0.1 |
| JavaDoubleParser char[]     | 479.18 |  3.7 % |     65.28 | 15.32 |    2.18 | 20.0.1 |
| JavaDoubleParser byte[]     | 514.17 |  3.0 % |     70.04 | 14.28 |    2.34 | 20.0.1 |
| JsonDoubleParser String     | 320.47 | 14.6 % |     43.66 | 22.91 |    1.46 | 20.0.1 |
| JsonDoubleParser char[]     | 464.53 | 13.3 % |     63.28 | 15.80 |    2.11 | 20.0.1 |
| JsonDoubleParser byte[]     | 489.19 |  3.9 % |     66.64 | 15.01 |    2.23 | 20.0.1 |
| JavaFloatParser  String     | 266.68 | 14.2 % |     36.33 | 27.53 |    2.58 | 20.0.1 |
| JavaFloatParser  char[]     | 443.07 |  5.8 % |     60.36 | 16.57 |    4.29 | 20.0.1 |
| JavaFloatParser  byte[]     | 478.89 |  2.4 % |     65.24 | 15.33 |    4.64 | 20.0.1 |
| JavaBigDecimalParser String | 343.73 | 14.5 % |     46.82 | 21.36 |    1.39 | 20.0.1 |
| JavaBigDecimalParser char[] | 448.99 | 16.4 % |     61.16 | 16.35 |    1.82 | 20.0.1 |
| JavaBigDecimalParser byte[] | 444.25 |  3.2 % |     60.52 | 16.52 |    1.80 | 20.0.1 |

### The data file `canada_hex.txt`

This file contains numbers in the range from -128 to +128 in hexadecimal notation.
Most input lines look like this: `-0x1.09219008205fcp6`.

| Method                  |   MB/s |  stdev | Mfloats/s |   ns/f | speedup | JDK    |
|-------------------------|-------:|-------:|----------:|-------:|--------:|--------|
| java.lang.Double        |  38.52 |  2.5 % |      2.11 | 473.44 |    1.00 | 20.0.1 |
| java.lang.Float         |  37.81 |  3.2 % |      2.07 | 482.31 |    1.00 | 20.0.1 |
| JavaDoubleParser String | 395.68 | 11.1 % |     21.70 |  46.09 |   10.27 | 20.0.1 |
| JavaDoubleParser char[] | 572.07 | 10.4 % |     31.37 |  31.88 |   14.85 | 20.0.1 |
| JavaDoubleParser byte[] | 682.38 |  9.1 % |     37.41 |  26.73 |   17.71 | 20.0.1 |
| JavaFloatParser  String | 402.73 |  9.9 % |     22.08 |  45.29 |   10.65 | 20.0.1 |
| JavaFloatParser  char[] | 521.99 |  8.4 % |     28.62 |  34.94 |   13.80 | 20.0.1 |
| JavaFloatParser  byte[] | 692.74 |  9.0 % |     37.98 |  26.33 |   18.32 | 20.0.1 |

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

    OpenJDK 20+36-2344
    java.lang.Double            :    93.97 MB/s (+/- 5.0 %)     5.39 Mfloat/s     185.43 ns/f     1.00 speedup
    JavaDoubleParser String     :   534.52 MB/s (+/-11.2 %)    30.67 Mfloat/s      32.60 ns/f     5.69 speedup
    JavaDoubleParser char[]     :   620.86 MB/s (+/- 9.9 %)    35.63 Mfloat/s      28.07 ns/f     6.61 speedup
    JavaDoubleParser byte[]     :   724.91 MB/s (+/- 5.7 %)    41.60 Mfloat/s      24.04 ns/f     7.71 speedup

'

    $ ./build/benchmarks/benchmark -f data/canada.txt
    # read 111126 lines 
    volume = 1.93374 MB 
    netlib                      :   337.79 MB/s (+/- 5.8 %)    19.41 Mfloat/s      51.52 ns/f 
    doubleconversion            :   254.22 MB/s (+/- 6.0 %)    14.61 Mfloat/s      68.45 ns/f 
    strtod                      :    73.33 MB/s (+/- 7.1 %)     4.21 Mfloat/s     237.31 ns/f 
    abseil                      :   411.11 MB/s (+/- 7.3 %)    23.63 Mfloat/s      42.33 ns/f 
    fastfloat                   :   741.32 MB/s (+/- 5.3 %)    42.60 Mfloat/s      23.47 ns/f 

    OpenJDK 20+36-2344
    java.lang.Double            :    82.56 MB/s (+/- 4.4 %)     4.74 Mfloat/s     210.76 ns/f     1.00 speedup
    JavaDoubleParser String     :   366.27 MB/s (+/- 9.7 %)    21.05 Mfloat/s      47.51 ns/f     4.44 speedup
    JavaDoubleParser char[]     :   571.76 MB/s (+/-11.4 %)    32.86 Mfloat/s      30.43 ns/f     6.93 speedup
    JavaDoubleParser byte[]     :   622.03 MB/s (+/- 7.5 %)    35.75 Mfloat/s      27.98 ns/f     7.53 speedup

# Building and running the code

This project requires **at least** the items below to build it from source:

- Maven 3.8.6
- OpenJDK SE 20

This project contains optimised code for various JDK versions.
If you intend to assess the fitness and/or performance of this project for all supported
JDKs, you **also need** to install the following items:

- OpenJDK SE 8
- OpenJDK SE 11
- OpenJDK SE 17
- OpenJDK SE 19

When you clone the code repository from github. you can choose from the following branches:

- `main` Aims to contain only working code.
- `dev` This code may or may not work. This code uses the experimental Vector API, and the Foreign Memory Access API,
  that are included in Java 20.

## Command sequence with Java SE 20 on macOS:

```shell
git clone https://github.com/wrandelshofer/FastDoubleParser.git
cd FastDoubleParser 
javac --enable-preview -source 20 -d out -encoding utf8 --module-source-path fastdoubleparser-dev/src/main/java --module ch.randelshofer.fastdoubleparser    
javac --enable-preview -source 20 -d out -encoding utf8 -p out --module-source-path fastdoubleparserdemo-dev/src/main/java --module ch.randelshofer.fastdoubleparserdemo
java -XX:CompileCommand=inline,java/lang/String.charAt --enable-preview -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main  
java -XX:CompileCommand=inline,java/lang/String.charAt --enable-preview -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main FastDoubleParserDemo/data/canada.txt   
```

## Command sequence with Java SE 8, 11, 17, 19 and 20 and Maven 3.8.6 on macOS:

```shell
git clone https://github.com/wrandelshofer/FastDoubleParser.git
cd FastDoubleParser
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-20.jdk/Contents/Home 
mvn clean
mvn package
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-20.jdk/Contents/Home 
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/mesh.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada_hex.txt
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-19.jdk/Contents/Home 
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/mesh.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada_hex.txt
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home 
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/mesh.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada_hex.txt
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.8.jdk/Contents/Home
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/mesh.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada_hex.txt
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_281.jdk/Contents/Home
java -XX:CompileCommand=inline,java/lang/String.charAt -cp "fastdoubleparser/target/*:fastdoubleparserdemo/target/*" ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -XX:CompileCommand=inline,java/lang/String.charAt -cp "fastdoubleparser/target/*:fastdoubleparserdemo/target/*" ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -cp "fastdoubleparser/target/*:fastdoubleparserdemo/target/*" ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/mesh.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -cp "fastdoubleparser/target/*:fastdoubleparserdemo/target/*" ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada_hex.txt
```

## IntelliJ IDEA with Java SE 8, 11, 17, 19 and 20 on macOS

Prerequisites:

1. Install the following Java SDKs: 8, 11, 17, 19 and 20.
   _If you do not need to edit the code, you only need to install the Java 20 SDK._
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
    **-Java8**, **-Java11**, **-Java17**, **-Java19**.
    Do not change modules with other name endings - they must stay on the Java 20 SDK.

12. From the main menu, choose **Build > Build Project**
    Intellij IDEA will now properly build the project.

## Editing the code

The majority of the code is located in the module named **fastdoubleparser-dev**,
and **fastdoubleparserdemo-dev**.
The code in these modules uses early access features of the Java 20 SDK.

Modules which have a name that ends in **-Java8**, **-Java11**, **-Java17**, **-Java19**
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