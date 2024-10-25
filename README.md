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

## Dependency

You can download released Jar files from [github](https://github.com/wrandelshofer/FastDoubleParser/releases),
or from a public Maven using the following dependency descriptor:

```xml

<dependency>
  <groupId>ch.randelshofer</groupId>
  <artifactId>fastdoubleparser</artifactId>
  <version>…version…</version>
</dependency>
```

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
import ch.randelshofer.fastdoubleparser.NumberFormatSymbols;
import ch.randelshofer.fastdoubleparser.ConfigurableDoubleParser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormatSymbols;

import java.util.List;
import java.util.Locale;
import java.util.Set;

class MyMain {
    public static void main(String... args) {
        double d = JavaDoubleParser.parseDouble("1.2345e135");
        System.out.println("Java double value: " + d);

        float f = JavaFloatParser.parseFloat("1.2345f");
        System.out.println("Java float value: " + f);

        BigDecimal bd = JavaBigDecimalParser.parseBigDecimal("1.2345");
        System.out.println("Java big decimal value: " + bd);

        BigInteger bi = JavaBigIntegerParser.parseBigInteger("12345");
        System.out.println("Java big integer value: " + bi);

        double jsonD = JsonDoubleParser.parseDouble("1.2345e85");
        System.out.println("JSON double value: " + jsonD);

        var symbols = NumberFormatSymbols.fromDecimalFormatSymbols(new DecimalFormatSymbols(Locale.GERMAN));
        boolean ignoreCase = true;
        var confdParser = new ConfigurableDoubleParser(symbols, ignoreCase);
        double confD1 = confdParser.parseDouble("123.456,89e5");
        double confD2 = confdParser.parseDouble("-0.15425,89E-5");
        System.out.println("Double value in German Locale: " + confD1);
        System.out.println("Another double value in German Locale: " + confD2);

        symbols = NumberFormatSymbols.fromDecimalFormatSymbols(new DecimalFormatSymbols(Locale.forLanguageTag("zh-CN")));
        symbols = symbols
                .withDigits(List.of('〇', '一', '二', '三', '四', '五', '六', '七', '八', '九'))
                .withExponentSeparator((Set.of("*一〇^")));

        confdParser = new ConfigurableDoubleParser(symbols, ignoreCase);
        double confZh = confdParser.parseDouble("四一.五七五三七一六六二一四五九八*一〇^七");
        System.out.println("Double value in Chinese Locale: " + confZh);
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

### The data file `canada.txt`

This file contains numbers in the range from -128 to +128.
Most input lines look like this: `52.038048000000117`.

CPU: Apple M2 Max<br>
OS: Mac OS X, 14.7, 12 processors available<br>
VM: Java 23, OpenJDK 64-Bit Server VM, Azul Systems, Inc., 23.0.1+11<br>
-XX:CompileCommand=inline,java/lang/String.charAt

| Method                                  |    MB/s |  stdev | Mfloats/s |   ns/f | speedup | JDK    |
|-----------------------------------------|--------:|-------:|----------:|-------:|--------:|--------|
| java.lang.Double                        |  107.96 |  2.0 % |      6.20 | 161.19 |  1.00=a | 23.0.1 |
| java.lang.Float                         |  118.12 |  3.1 % |      6.79 | 147.32 |  1.00=b | 23.0.1 |
| java.math.BigDecimal                    |  400.25 |  4.8 % |     23.00 |  43.48 |  1.00=c | 23.0.1 |
| java.text.NumberFormat                  |   72.06 |  1.6 % |      4.14 | 241.49 |  1.00=d | 23.0.1 |
| com.ibm.icu.text.NumberFormat           |   24.32 |  2.7 % |      1.40 | 715.62 |  1.00=e | 23.0.1 |
| JavaDoubleParser CharSequence           |  532.05 |  4.3 % |     30.58 |  32.71 |  4.93*a | 23.0.1 |
| JavaDoubleParser char[]                 |  973.38 |  7.1 % |     55.94 |  17.88 |  9.02*a | 23.0.1 |
| JavaDoubleParser byte[]                 |  962.18 |  8.0 % |     55.29 |  18.09 |  8.91*a | 23.0.1 |
| JsonDoubleParser CharSequence           |  575.45 |  5.8 % |     33.07 |  30.24 |  5.33*a | 23.0.1 |
| JsonDoubleParser char[]                 |  991.20 |  6.2 % |     56.96 |  17.56 |  9.18*a | 23.0.1 |
| JsonDoubleParser byte[]                 |  990.74 |  5.5 % |     56.93 |  17.56 |  9.18*a | 23.0.1 |
| JavaFloatParser  CharSequence           |  572.02 |  6.0 % |     32.87 |  30.42 |  4.84*b | 23.0.1 |
| JavaFloatParser  char[]                 | 1007.96 | 21.5 % |     57.92 |  17.26 |  8.53*b | 23.0.1 |
| JavaFloatParser  byte[]                 | 1011.75 |  6.3 % |     58.14 |  17.20 |  8.57*b | 23.0.1 |
| JavaBigDecimalParser CharSequence       |  773.44 |  5.9 % |     44.45 |  22.50 |  1.93*c | 23.0.1 |
| JavaBigDecimalParser char[]             | 1140.28 |  6.3 % |     65.53 |  15.26 |  2.85*c | 23.0.1 |
| JavaBigDecimalParser byte[]             | 1097.82 | 15.1 % |     63.09 |  15.85 |  2.74*c | 23.0.1 |
| ConfigurableDoubleParser CharSequence   |  483.99 |  5.6 % |     27.81 |  35.95 |  6.72*d | 23.0.1 |
| ConfigurableDoubleParser char[]         |  689.58 |  7.1 % |     39.63 |  25.23 |  9.57*d | 23.0.1 |
| ConfigurableDoubleParser byte[]         |  625.56 |  4.4 % |     35.95 |  27.82 |  8.68*d | 23.0.1 |
| ConfigurableDoubleParserCI CharSequence |  493.74 |  5.4 % |     28.37 |  35.24 |  6.85*d | 23.0.1 |
| ConfigurableDoubleParserCI char[]       |  701.50 |  4.6 % |     40.31 |  24.81 |  9.73*d | 23.0.1 |
| ConfigurableDoubleParserCI byte[]       |  531.04 |  6.5 % |     30.52 |  32.77 |  7.37*d | 23.0.1 |

Mac Mini (2018)<br>
CPU: Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz<br>
OS: Mac OS X, 15.0.1, 12 processors available<br>
VM: Java 24, OpenJDK 64-Bit Server VM, Oracle Corporation, 24-ea+20-2362<br>

| Method                                  |   MB/s |  stdev | Mfloats/s |    ns/f | speedup | JDK   |
|-----------------------------------------|-------:|-------:|----------:|--------:|--------:|-------|
| java.lang.Double                        |  88.84 |  3.8 % |      5.11 |  195.87 |  1.00=a | 24-ea |
| java.lang.Float                         | 101.30 |  3.7 % |      5.82 |  171.78 |  1.00=b | 24-ea |
| java.math.BigDecimal                    | 321.36 |  8.6 % |     18.47 |   54.15 |  1.00=c | 24-ea |
| java.text.NumberFormat                  |  45.79 |  3.0 % |      2.63 |  379.99 |  1.00=d | 24-ea |
| com.ibm.icu.text.NumberFormat           |  15.98 |  3.0 % |      0.92 | 1088.62 |  1.00=e | 24-ea |
| JavaDoubleParser CharSequence           | 393.16 |  9.2 % |     22.59 |   44.26 |  4.43*a | 24-ea |
| JavaDoubleParser char[]                 | 595.25 | 12.8 % |     34.21 |   29.23 |  6.70*a | 24-ea |
| JavaDoubleParser byte[]                 | 685.38 | 10.1 % |     39.39 |   25.39 |  7.71*a | 24-ea |
| JsonDoubleParser CharSequence           | 391.58 |  9.8 % |     22.50 |   44.44 |  4.41*a | 24-ea |
| JsonDoubleParser char[]                 | 609.59 | 10.4 % |     35.03 |   28.55 |  6.86*a | 24-ea |
| JsonDoubleParser byte[]                 | 675.52 |  9.2 % |     38.82 |   25.76 |  7.60*a | 24-ea |
| JavaFloatParser  CharSequence           | 402.35 |  9.1 % |     23.12 |   43.25 |  3.97*b | 24-ea |
| JavaFloatParser  char[]                 | 712.35 | 10.5 % |     40.94 |   24.43 |  7.03*b | 24-ea |
| JavaFloatParser  byte[]                 | 626.70 |  9.4 % |     36.01 |   27.77 |  6.19*b | 24-ea |
| JavaBigDecimalParser CharSequence       | 428.92 | 17.7 % |     24.65 |   40.57 |  1.33*c | 24-ea |
| JavaBigDecimalParser char[]             | 660.18 | 12.2 % |     37.94 |   26.36 |  2.05*c | 24-ea |
| JavaBigDecimalParser byte[]             | 669.97 | 11.5 % |     38.50 |   25.97 |  2.08*c | 24-ea |
| ConfigurableDoubleParser CharSequence   | 335.02 |  9.2 % |     19.25 |   51.94 |  7.32*d | 24-ea |
| ConfigurableDoubleParser char[]         | 527.14 | 12.1 % |     30.29 |   33.01 | 11.51*d | 24-ea |
| ConfigurableDoubleParser byte[]         | 474.35 |  7.6 % |     27.26 |   36.69 | 10.36*d | 24-ea |
| ConfigurableDoubleParserCI CharSequence | 330.35 |  9.9 % |     18.98 |   52.67 |  7.21*d | 24-ea |
| ConfigurableDoubleParserCI char[]       | 519.32 |  9.8 % |     29.84 |   33.51 | 11.34*d | 24-ea |
| ConfigurableDoubleParserCI byte[]       | 406.10 |  7.4 % |     23.34 |   42.85 |  8.87*d | 24-ea |



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
    netlib                       :   317.31 MB/s (+/- 6.0 %)    15.12 Mfloat/s      66.12 ns/f 
    doubleconversion             :   263.89 MB/s (+/- 4.2 %)    12.58 Mfloat/s      79.51 ns/f 
    strtod                       :    86.13 MB/s (+/- 3.7 %)     4.10 Mfloat/s     243.61 ns/f 
    abseil                       :   467.27 MB/s (+/- 9.0 %)    22.27 Mfloat/s      44.90 ns/f 
    fastfloat                    :   880.79 MB/s (+/- 6.6 %)    41.98 Mfloat/s      23.82 ns/f 

    Java 22, OpenJDK 64-Bit Server VM, Oracle Corporation, 22.0.1+8-16
    java.lang.Double             :    94.32 MB/s (+/- 3.1 %)     5.41 Mfloat/s     184.73 ns/f     1.00 speedup
    JavaDoubleParser CharSequence:   585.84 MB/s (+/-12.4 %)    33.62 Mfloat/s      29.74 ns/f     6.21 speedup
    JavaDoubleParser char[]      :   659.27 MB/s (+/- 9.6 %)    37.84 Mfloat/s      26.43 ns/f     6.99 speedup
    JavaDoubleParser byte[]      :   729.46 MB/s (+/- 9.7 %)    41.86 Mfloat/s      23.89 ns/f     7.73 speedup

'

    $ ./build/benchmarks/benchmark -f data/canada.txt
    # read 111126 lines 
    volume = 1.93374 MB 
    netlib                       :   337.79 MB/s (+/- 5.8 %)    19.41 Mfloat/s      51.52 ns/f 
    doubleconversion             :   254.22 MB/s (+/- 6.0 %)    14.61 Mfloat/s      68.45 ns/f 
    strtod                       :    73.33 MB/s (+/- 7.1 %)     4.21 Mfloat/s     237.31 ns/f 
    abseil                       :   411.11 MB/s (+/- 7.3 %)    23.63 Mfloat/s      42.33 ns/f 
    fastfloat                    :   741.32 MB/s (+/- 5.3 %)    42.60 Mfloat/s      23.47 ns/f 

     Java 22, OpenJDK 64-Bit Server VM, Oracle Corporation, 22.0.1+8-16
    java.lang.Double             :    87.48 MB/s (+/- 3.2 %)     5.03 Mfloat/s     198.93 ns/f     1.00 speedup
    JavaDoubleParser CharSequence:   386.93 MB/s (+/- 8.8 %)    22.24 Mfloat/s      44.97 ns/f     4.42 speedup
    JavaDoubleParser char[]      :   637.55 MB/s (+/- 9.0 %)    36.64 Mfloat/s      27.29 ns/f     7.29 speedup
    JavaDoubleParser byte[]      :   694.16 MB/s (+/- 7.9 %)    39.89 Mfloat/s      25.07 ns/f     7.94 speedup

# Building and running the code

This project requires **at least** the items below to build it from source:

- Maven 3.9.9
- OpenJDK SE 23

This project contains optimised code for various JDK versions.
If you intend to assess the fitness and/or performance of this project for all supported
JDKs, you **also need to** install the following items:

- OpenJDK SE 8
- OpenJDK SE 11
- OpenJDK SE 17
- OpenJDK SE 21
- OpenJDK SE 23

When you clone the code repository from github. you can choose from the following branches:

- `main` Aims to contain only working code.
- `dev` This code may or may not work. This code uses the experimental Vector API, and the Foreign Memory Access API,
  that are included in Java 23.

## Command sequence with Java SE 23 on macOS:

```shell
git clone https://github.com/wrandelshofer/FastDoubleParser.git
cd FastDoubleParser 
export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home 
javac --enable-preview -source 23 -d out -encoding utf8 --module-source-path fastdoubleparser-dev/src/main/java --module ch.randelshofer.fastdoubleparser    
javac --enable-preview -source 23 -d out -encoding utf8 -p out --module-source-path fastdoubleparserdemo-dev/src/main/java --module ch.randelshofer.fastdoubleparserdemo
java -XX:CompileCommand=inline,java/lang/String.charAt --enable-preview -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -XX:CompileCommand=inline,java/lang/String.charAt --enable-preview -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt   
java -XX:CompileCommand=inline,java/lang/String.charAt --enable-preview -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/mesh.txt   
java -XX:CompileCommand=inline,java/lang/String.charAt --enable-preview -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada_hex.txt   
```

## Command sequence with Azul Zulu Java SE 8, 11, 17, 21, and 23, and Maven 3.9.8 on macOS:

```shell
git clone https://github.com/wrandelshofer/FastDoubleParser.git
cd FastDoubleParser
export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home 
mvn clean
mvn package
export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home 
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

## IntelliJ IDEA with Java SE 8, 11, 17, 21, 23 on macOS

Prerequisites:

1. Install the following Java SDKs: 8, 11, 17, 21, 23.
   _If you do not need to edit the code, you only need to install the Java 23 SDK._
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

Modules which have a name that ends in **-Java8**, **-Java11**, **-Java17**, **-Java21**, **-Java23**
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