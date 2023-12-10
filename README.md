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
OS: Mac OS X, 14.1.2, 12 processors available<br>
VM: Java 22, OpenJDK 64-Bit Server VM, Oracle Corporation, 22-ea+27-2262<br>

| Method                      |   MB/s |  stdev | Mfloats/s |   ns/f | speedup | JDK   |
|-----------------------------|-------:|-------:|----------:|-------:|--------:|-------|
| java.lang.Double            |  81.46 | 13.5 % |      4.68 | 213.85 |    1.00 | 22-ea |
| java.lang.Float             |  87.58 | 12.2 % |      5.03 | 198.93 |    1.00 | 22-ea |
| java.math.BigDecimal        | 162.20 | 15.5 % |      9.31 | 107.41 |    1.00 | 22-ea |
| JavaDoubleParser String     | 435.82 | 26.2 % |     25.02 |  39.97 |    5.35 | 22-ea |
| JavaDoubleParser char[]     | 575.63 | 27.2 % |     33.04 |  30.26 |    7.07 | 22-ea |
| JavaDoubleParser byte[]     | 646.53 | 22.8 % |     37.11 |  26.95 |    7.94 | 22-ea |
| JsonDoubleParser String     | 477.30 | 29.7 % |     27.40 |  36.50 |    5.86 | 22-ea |
| JsonDoubleParser char[]     | 562.24 | 27.3 % |     32.27 |  30.99 |    6.90 | 22-ea |
| JsonDoubleParser byte[]     | 591.25 | 26.3 % |     33.94 |  29.47 |    7.26 | 22-ea |
| JavaFloatParser  String     | 447.03 | 28.0 % |     25.66 |  38.97 |    5.10 | 22-ea |
| JavaFloatParser  char[]     | 654.79 | 26.1 % |     37.59 |  26.61 |    7.48 | 22-ea |
| JavaFloatParser  byte[]     | 651.75 | 28.0 % |     37.41 |  26.73 |    7.44 | 22-ea |
| JavaBigDecimalParser String | 471.37 | 30.4 % |     27.06 |  36.96 |    2.91 | 22-ea |
| JavaBigDecimalParser char[] | 596.18 | 28.5 % |     34.22 |  29.22 |    3.68 | 22-ea |
| JavaBigDecimalParser byte[] | 559.08 | 26.3 % |     32.09 |  31.16 |    3.45 | 22-ea |


### The data file `canada.txt`

This file contains numbers in the range from -128 to +128.
Most input lines look like this: `52.038048000000117`.

Mac Mini (2018)<br>
CPU: Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz<br>
OS: Mac OS X, 14.1.2, 12 processors available<br>
VM: Java 22, OpenJDK 64-Bit Server VM, Oracle Corporation, 22-ea+27-2262<br>

| Method                      |   MB/s |  stdev | Mfloats/s |   ns/f | speedup | JDK   |
|-----------------------------|-------:|-------:|----------:|-------:|--------:|-------|
| java.lang.Double            |  75.94 | 11.6 % |      4.36 | 229.15 |    1.00 | 22-ea |
| java.lang.Float             |  88.58 | 12.1 % |      5.09 | 196.46 |    1.00 | 22-ea |
| java.math.BigDecimal        | 282.91 | 21.8 % |     16.26 |  61.51 |    1.00 | 22-ea |
| JavaDoubleParser String     | 339.05 | 21.9 % |     19.48 |  51.32 |    4.46 | 22-ea |
| JavaDoubleParser char[]     | 530.43 | 23.2 % |     30.48 |  32.81 |    6.98 | 22-ea |
| JavaDoubleParser byte[]     | 582.90 | 22.0 % |     33.50 |  29.85 |    7.68 | 22-ea |
| JsonDoubleParser String     | 331.53 | 22.0 % |     19.05 |  52.49 |    4.37 | 22-ea |
| JsonDoubleParser char[]     | 485.24 | 23.9 % |     27.89 |  35.86 |    6.39 | 22-ea |
| JsonDoubleParser byte[]     | 594.52 | 28.2 % |     34.17 |  29.27 |    7.83 | 22-ea |
| JavaFloatParser  String     | 318.42 | 24.7 % |     18.30 |  54.65 |    3.59 | 22-ea |
| JavaFloatParser  char[]     | 498.41 | 34.0 % |     28.64 |  34.91 |    5.63 | 22-ea |
| JavaFloatParser  byte[]     | 544.61 | 31.2 % |     31.30 |  31.95 |    6.15 | 22-ea |
| JavaBigDecimalParser String | 353.76 | 29.3 % |     20.33 |  49.19 |    1.25 | 22-ea |
| JavaBigDecimalParser char[] | 512.00 | 35.9 % |     29.42 |  33.99 |    1.81 | 22-ea |
| JavaBigDecimalParser byte[] | 524.35 | 32.1 % |     30.13 |  33.19 |    1.85 | 22-ea |


### The data file `mesh.txt`

This file contains input lines like `1749`, and `0.539081215858`.

Mac Mini (2018)<br>
CPU: Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz<br>
OS: Mac OS X, 14.1.2, 12 processors available<br>
VM: Java 22, OpenJDK 64-Bit Server VM, Oracle Corporation, 22-ea+27-2262<br>

| Method                      |   MB/s |  stdev | Mfloats/s |  ns/f | speedup | JDK   |
|-----------------------------|-------:|-------:|----------:|------:|--------:|-------|
| java.lang.Double            | 201.28 | 27.3 % |     27.42 | 36.47 |    1.00 | 22-ea |
| java.lang.Float             | 100.54 | 22.1 % |     13.70 | 73.01 |    1.00 | 22-ea |
| java.math.BigDecimal        | 225.28 | 27.4 % |     30.69 | 32.58 |    1.00 | 22-ea |
| JavaDoubleParser String     | 293.61 | 36.1 % |     40.00 | 25.00 |    1.46 | 22-ea |
| JavaDoubleParser char[]     | 411.18 | 33.1 % |     56.01 | 17.85 |    2.04 | 22-ea |
| JavaDoubleParser byte[]     | 446.62 | 29.7 % |     60.84 | 16.44 |    2.22 | 22-ea |
| JsonDoubleParser String     | 299.41 | 30.3 % |     40.79 | 24.52 |    1.49 | 22-ea |
| JsonDoubleParser char[]     | 417.52 | 26.2 % |     56.88 | 17.58 |    2.07 | 22-ea |
| JsonDoubleParser byte[]     | 433.78 | 25.4 % |     59.09 | 16.92 |    2.16 | 22-ea |
| JavaFloatParser  String     | 256.30 | 27.4 % |     34.92 | 28.64 |    2.55 | 22-ea |
| JavaFloatParser  char[]     | 378.07 | 30.4 % |     51.50 | 19.42 |    3.76 | 22-ea |
| JavaFloatParser  byte[]     | 348.58 | 25.6 % |     47.49 | 21.06 |    3.47 | 22-ea |
| JavaBigDecimalParser String | 296.15 | 28.1 % |     40.34 | 24.79 |    1.31 | 22-ea |
| JavaBigDecimalParser char[] | 407.31 | 29.6 % |     55.49 | 18.02 |    1.81 | 22-ea |
| JavaBigDecimalParser byte[] | 405.13 | 31.1 % |     55.19 | 18.12 |    1.80 | 22-ea |


### The data file `canada_hex.txt`

This file contains numbers in the range from -128 to +128 in hexadecimal notation.
Most input lines look like this: `-0x1.09219008205fcp6`.

Mac Mini (2018)<br>
CPU: Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz<br>
OS: Mac OS X, 14.1.2, 12 processors available<br>
VM: Java 22, OpenJDK 64-Bit Server VM, Oracle Corporation, 22-ea+27-2262<br>

| Method                  |   MB/s |  stdev | Mfloats/s |   ns/f | speedup | JDK   |
|-------------------------|-------:|-------:|----------:|-------:|--------:|-------|
| java.lang.Double        |  37.49 |  8.7 % |      2.06 | 486.53 |    1.00 | 22-ea |
| java.lang.Float         |  37.82 |  6.8 % |      2.07 | 482.29 |    1.00 | 22-ea |
| JavaDoubleParser String | 364.94 | 19.6 % |     20.01 |  49.98 |    9.74 | 22-ea |
| JavaDoubleParser char[] | 545.30 | 21.7 % |     29.90 |  33.45 |   14.55 | 22-ea |
| JavaDoubleParser byte[] | 666.52 | 19.6 % |     36.55 |  27.36 |   17.78 | 22-ea |
| JavaFloatParser  String | 375.92 | 18.9 % |     20.61 |  48.52 |    9.94 | 22-ea |
| JavaFloatParser  char[] | 542.13 | 21.5 % |     29.72 |  33.64 |   14.34 | 22-ea |
| JavaFloatParser  byte[] | 660.81 | 19.6 % |     36.23 |  27.60 |   17.47 | 22-ea |

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

    Java 22, OpenJDK 64-Bit Server VM, Oracle Corporation, 22-ea+27-2262
    java.lang.Double            :    88.13 MB/s (+/- 5.2 %)     5.06 Mfloat/s     197.70 ns/f     1.00 speedup
    JavaDoubleParser String     :   557.46 MB/s (+/- 4.1 %)    31.99 Mfloat/s      31.26 ns/f     6.33 speedup
    JavaDoubleParser char[]     :   609.25 MB/s (+/-16.9 %)    34.97 Mfloat/s      28.60 ns/f     6.91 speedup
    JavaDoubleParser byte[]     :   716.19 MB/s (+/- 3.9 %)    41.10 Mfloat/s      24.33 ns/f     8.13 speedup

'

    $ ./build/benchmarks/benchmark -f data/canada.txt
    # read 111126 lines 
    volume = 1.93374 MB 
    netlib                      :   337.79 MB/s (+/- 5.8 %)    19.41 Mfloat/s      51.52 ns/f 
    doubleconversion            :   254.22 MB/s (+/- 6.0 %)    14.61 Mfloat/s      68.45 ns/f 
    strtod                      :    73.33 MB/s (+/- 7.1 %)     4.21 Mfloat/s     237.31 ns/f 
    abseil                      :   411.11 MB/s (+/- 7.3 %)    23.63 Mfloat/s      42.33 ns/f 
    fastfloat                   :   741.32 MB/s (+/- 5.3 %)    42.60 Mfloat/s      23.47 ns/f 

    Java 22, OpenJDK 64-Bit Server VM, Oracle Corporation, 22-ea+27-2262
    java.lang.Double            :    76.39 MB/s (+/- 8.0 %)     4.39 Mfloat/s     227.79 ns/f     1.00 speedup
    JavaDoubleParser String     :   369.85 MB/s (+/-13.2 %)    21.25 Mfloat/s      47.05 ns/f     4.84 speedup
    JavaDoubleParser char[]     :   549.88 MB/s (+/-15.3 %)    31.60 Mfloat/s      31.65 ns/f     7.20 speedup
    JavaDoubleParser byte[]     :   645.33 MB/s (+/-13.9 %)    37.08 Mfloat/s      26.97 ns/f     8.45 speedup

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
javac --enable-preview -source 20 -d out -encoding utf8 --module-source-path fastdoubleparser-dev/src/main/java --module ch.randelshofer.fastdoubleparser    
javac --enable-preview -source 20 -d out -encoding utf8 -p out --module-source-path fastdoubleparserdemo-dev/src/main/java --module ch.randelshofer.fastdoubleparserdemo
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