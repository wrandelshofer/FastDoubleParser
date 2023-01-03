[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ch.randelshofer/fastdoubleparser/badge.svg)](https://maven-badges.herokuapp.com/maven-central/ch.randelshofer/fastdoubleparser)

# FastDoubleParser

This is a Java port of Daniel Lemire's [fast_float](https://github.com/fastfloat/fast_float) project.

This project provides parsers for `double`, `float`, `BigDecimal` and `BigInteger` values.
The parsers are optimised for speed for the most common inputs.

The code in this project contains optimised versions for Java SE 1.8, 11, 17, 19 and 20-ea.
The code is released in a single multi-release jar, which contains the code for all these versions
except 20-ea.

Usage:

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

The `parse...()`-methods take a `CharacterSequence`. a `char`-array or a `byte`-array as argument. This way. you can
parse from a `StringBuffer` or an array without having to convert your input to a `String`. Parsing from an array is
faster. because the parser can process multiple characters at once using SIMD instructions.

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

On common input data, the fast `BigDecimal` and `BigInteger` parsers are about as fast as
`java.math.BigDecimal(String)` and `java.math.BigInteger(String)`.

For less common inputs with many digits, the fast parsers can be a lot faster than their `java.math` counterparts.
The fast parsers can convert even the longest supported inputs in less than 10 minutes, whereas
their `java.math` counterparts need months (!).

The fast parsers convert digit characters from base 10 to a bit sequence in base 2
using a divide-and-conquer algorithm. Small sequences of digits are converted
individually to bit sequences and then gradually combined to the final bit sequence.
This algorithm needs to perform multiplications of very long bit sequences.
The multiplications are performed in the frequency domain using a discrete fourier transform.
The multiplications in the frequency domain can be performed in `O(Nlog N (log log N))` time,
where `N` is the number of digits.
In contrast, conventional multiplication algorithms in the time domain need `O(N²)` time.

If your input data contains inputs with many thousands of digits, consider using the `parallelParse` methods
of the fast algorithms. They have even lower constant time factors than the regular fast `parse` methods - they use
more CPU and memory resources though.

### Memory usage and computation time

The memory usage depends on the result type and the maximal supported input character length.

The computation times are given for a Mac mini 2018 with Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz.

| Parser               |Result Type          | Maximal<br/>input length | Memory usage<br/>JVM -Xmx | Computation<br/>Time |
|----------------------|---------------------|---------------------:|----------------------:|---------------------:|
| JavaDoubleParser     |java.lang.Double     |             2^31 - 5 |          10 gigabytes |              < 5 sec |
| JavaFloatParser      |java.lang.Float      |             2^31 - 5 |          10 gigabytes |              < 5 sec |
| JavaBigIntegerParser |java.math.BigInteger |        1,292,782,622 |          14 gigabytes |              < 7 min |
| JavaBigDecimalParser |java.math.BigDecimal |        1,292,782,635 |          14 gigabytes |              < 7 min |

## Performance measurements

On my Mac mini (2018) I get the results shown below.

### Random double numbers in the range from 0 to 1

Most numbers look like this: `0.4011441469603171`.

|Method                     | MB/s  |stdev|Mfloats/s| ns/f   | speedup | JDK    |
|---------------------------|------:|-----:|------:|--------:|--------:|--------|
|java.lang.Double           |  91.07| 3.8 %|   5.23|   191.31|     1.00|20-ea   |
|java.lang.Float            |  96.15| 7.3 %|   5.52|   181.20|     1.00|20-ea   |
|java.math.BigDecimal       | 192.26| 8.0 %|  11.03|    90.62|     1.00|20-ea   |
|JavaDoubleParser String    | 400.80|14.9 %|  23.00|    43.47|     4.40|20-ea   |
|JavaDoubleParser char[]    | 520.21|14.2 %|  29.86|    33.49|     5.71|20-ea   |
|JavaDoubleParser byte[]    | 593.21|21.2 %|  34.05|    29.37|     6.51|20-ea   |
|JsonDoubleParser String    | 411.79|13.6 %|  23.64|    42.31|     4.52|20-ea   |
|JsonDoubleParser char[]    | 562.95|14.9 %|  32.31|    30.95|     6.18|20-ea   |
|JsonDoubleParser byte[]    | 613.03|14.0 %|  35.19|    28.42|     6.73|20-ea   |
|JavaFloatParser  String    | 367.07|12.2 %|  21.07|    47.46|     3.82|20-ea   |
|JavaFloatParser  char[]    | 518.64|11.4 %|  29.77|    33.59|     5.39|20-ea   |
|JavaFloatParser  byte[]    | 613.75| 9.3 %|  35.23|    28.39|     6.38|20-ea   |
|JavaBigDecimalParser String| 398.83|14.1 %|  22.89|    43.68|     2.07|20-ea   |
|JavaBigDecimalParser char[]| 557.90|14.3 %|  32.02|    31.23|     2.90|20-ea   |
|JavaBigDecimalParser byte[]| 652.20|16.6 %|  37.43|    26.71|     3.39|20-ea   |

### The data file `canada.txt`

This file contains numbers in the range from -128 to +128.
Most numbers look like this: `52.038048000000117`.

|Method                     | MB/s  |stdev|Mfloats/s| ns/f   | speedup | JDK    |
|---------------------------|------:|-----:|------:|--------:|--------:|--------|
|java.lang.Double           |  71.28| 9.7 %|   4.10|   244.12|     1.00|20-ea   |
|java.lang.Float            |  87.45| 7.1 %|   5.03|   198.98|     1.00|20-ea   |
|java.math.BigDecimal       | 244.03|10.9 %|  14.02|    71.31|     1.00|20-ea   |
|JavaDoubleParser String    | 294.45|13.1 %|  16.92|    59.10|     4.13|20-ea   |
|JavaDoubleParser char[]    | 419.26|13.0 %|  24.09|    41.51|     5.88|20-ea   |
|JavaDoubleParser byte[]    | 461.00|20.0 %|  26.49|    37.75|     6.47|20-ea   |
|JsonDoubleParser String    | 312.11|16.1 %|  17.94|    55.75|     4.38|20-ea   |
|JsonDoubleParser char[]    | 381.20|22.6 %|  21.91|    45.65|     5.35|20-ea   |
|JsonDoubleParser byte[]    | 463.28|20.2 %|  26.62|    37.56|     6.50|20-ea   |
|JavaFloatParser  String    | 285.14|13.0 %|  16.39|    61.03|     3.26|20-ea   |
|JavaFloatParser  char[]    | 411.34|20.3 %|  23.64|    42.30|     4.70|20-ea   |
|JavaFloatParser  byte[]    | 526.98|16.9 %|  30.28|    33.02|     6.03|20-ea   |
|JavaBigDecimalParser String| 288.74|23.5 %|  16.59|    60.27|     1.18|20-ea   |
|JavaBigDecimalParser char[]| 442.03|14.5 %|  25.40|    39.37|     1.81|20-ea   |
|JavaBigDecimalParser byte[]| 457.09|21.1 %|  26.27|    38.07|     1.87|20-ea   |

### The data file `mesh.txt`

This file contains integer numbers like `1749`, and floating point numbers like
`0.539081215858`.

|Method                     | MB/s  |stdev|Mfloats/s| ns/f   | speedup | JDK    |
|---------------------------|------:|-----:|------:|--------:|--------:|--------|
|java.lang.Double           | 167.28|22.7 %|  22.79|    43.88|     1.00|20-ea   |
|java.lang.Float            |  94.41|11.0 %|  12.86|    77.75|     1.00|20-ea   |
|java.math.BigDecimal       | 181.11|24.2 %|  24.67|    40.53|     1.00|20-ea   |
|JavaDoubleParser String    | 230.94|22.0 %|  31.46|    31.79|     1.38|20-ea   |
|JavaDoubleParser char[]    | 330.35|23.2 %|  45.00|    22.22|     1.97|20-ea   |
|JavaDoubleParser byte[]    | 353.26|20.2 %|  48.12|    20.78|     2.11|20-ea   |
|JsonDoubleParser String    | 230.26|19.0 %|  31.37|    31.88|     1.38|20-ea   |
|JsonDoubleParser char[]    | 321.63|23.1 %|  43.81|    22.82|     1.92|20-ea   |
|JsonDoubleParser byte[]    | 374.07|24.2 %|  50.96|    19.62|     2.24|20-ea   |
|JavaFloatParser  String    | 199.30|22.5 %|  27.15|    36.83|     2.11|20-ea   |
|JavaFloatParser  char[]    | 245.89|33.6 %|  33.50|    29.85|     2.60|20-ea   |
|JavaFloatParser  byte[]    | 287.94|33.6 %|  39.22|    25.49|     3.05|20-ea   |
|JavaBigDecimalParser String| 211.58|36.2 %|  28.82|    34.70|     1.17|20-ea   |
|JavaBigDecimalParser char[]| 319.68|39.6 %|  43.55|    22.96|     1.77|20-ea   |
|JavaBigDecimalParser byte[]| 337.29|36.4 %|  45.95|    21.76|     1.86|20-ea   |

### The data file `canada_hex.txt`

This file contains numbers in the range from -128 to +128 in hexadecimal notation.
Most numbers look like this: `-0x1.09219008205fcp6`.

|Method                     | MB/s  |stdev|Mfloats/s| ns/f   | speedup | JDK    |
|---------------------------|------:|-----:|------:|--------:|--------:|--------|
|java.lang.Double           |  37.32| 4.5 %|   2.05|   488.70|     1.00|20-ea   |
|java.lang.Float            |  37.71| 2.9 %|   2.07|   483.68|     1.00|20-ea   |
|JavaDoubleParser String    | 329.93|10.5 %|  18.09|    55.28|     8.84|20-ea   |
|JavaDoubleParser char[]    | 473.75|14.9 %|  25.98|    38.50|    12.69|20-ea   |
|JavaDoubleParser byte[]    | 525.24|13.3 %|  28.80|    34.72|    14.07|20-ea   |
|JavaFloatParser  String    | 311.42| 9.9 %|  17.08|    58.56|     8.26|20-ea   |
|JavaFloatParser  char[]    | 440.50|13.0 %|  24.15|    41.40|    11.68|20-ea   |
|JavaFloatParser  byte[]    | 529.19|12.0 %|  29.02|    34.46|    14.03|20-ea   |

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
    netlib                                  :   317.31 MB/s (+/- 6.0 %)    15.12 Mfloat/s      66.12 ns/f 
    doubleconversion                        :   263.89 MB/s (+/- 4.2 %)    12.58 Mfloat/s      79.51 ns/f 
    strtod                                  :    86.13 MB/s (+/- 3.7 %)     4.10 Mfloat/s     243.61 ns/f 
    abseil                                  :   467.27 MB/s (+/- 9.0 %)    22.27 Mfloat/s      44.90 ns/f 
    fastfloat                               :   880.79 MB/s (+/- 6.6 %)    41.98 Mfloat/s      23.82 ns/f 

    OpenJDK 20-ea+22-1594
    java.lang.Double                        :    89.59 MB/s (+/- 6.0 %)     5.14 Mfloat/s     194.44 ns/f
    JavaDoubleParser String                 :   485.97 MB/s (+/-13.8 %)    27.90 Mfloat/s      35.85 ns/f
    JavaDoubleParser char[]                 :   562.55 MB/s (+/-10.0 %)    32.29 Mfloat/s      30.97 ns/f
    JavaDoubleParser byte[]                 :   644.65 MB/s (+/- 8.7 %)    37.01 Mfloat/s      27.02 ns/f

'

    $ ./build/benchmarks/benchmark -f data/canada.txt
    # read 111126 lines 
    volume = 1.93374 MB 
    netlib                                  :   337.79 MB/s (+/- 5.8 %)    19.41 Mfloat/s      51.52 ns/f 
    doubleconversion                        :   254.22 MB/s (+/- 6.0 %)    14.61 Mfloat/s      68.45 ns/f 
    strtod                                  :    73.33 MB/s (+/- 7.1 %)     4.21 Mfloat/s     237.31 ns/f 
    abseil                                  :   411.11 MB/s (+/- 7.3 %)    23.63 Mfloat/s      42.33 ns/f 
    fastfloat                               :   741.32 MB/s (+/- 5.3 %)    42.60 Mfloat/s      23.47 ns/f 

    OpenJDK 20-ea+29-2280
    java.lang.Double            :    77.84 MB/s (+/- 4.1 %)     4.47 Mfloat/s     223.54 ns/f     1.00 speedup
    JavaDoubleParser String     :   329.79 MB/s (+/-13.4 %)    18.95 Mfloat/s      52.77 ns/f     4.24 speedup
    JavaDoubleParser char[]     :   521.30 MB/s (+/-15.2 %)    29.96 Mfloat/s      33.38 ns/f     6.70 speedup
    JavaDoubleParser byte[]     :   560.48 MB/s (+/-12.7 %)    32.21 Mfloat/s      31.05 ns/f     7.20 speedup

# Building and running the code

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
java --enable-preview -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main  
java --enable-preview -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main FastDoubleParserDemo/data/canada.txt   
```

## Command sequence with Java SE 8, 11, 17, 19 and 20 and Maven 3.8.6 on macOS:

```shell
git clone https://github.com/wrandelshofer/FastDoubleParser.git
cd FastDoubleParser
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-20.jdk/Contents/Home 
mvn clean
mvn package
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-20.jdk/Contents/Home 
java -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt
java -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/mesh.txt
java -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada_hex.txt
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-19.jdk/Contents/Home 
java -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt
java -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/mesh.txt
java -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada_hex.txt
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home 
java -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt
java -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/mesh.txt
java -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada_hex.txt
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.8.jdk/Contents/Home
java -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt
java -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/mesh.txt
java -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada_hex.txt
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_281.jdk/Contents/Home
java -cp "fastdoubleparser/target/*:fastdoubleparserdemo/target/*" ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -cp "fastdoubleparser/target/*:fastdoubleparserdemo/target/*" ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt
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
