# FastDoubleParser

This is a Java port of Daniel Lemire's fast_float.

https://github.com/fastfloat/fast_float

Usage:

    import ch.randelshofer.fastdoubleparser.FastDoubleParser;
    import ch.randelshofer.fastdoubleparser.FastFloatParser;

    double d = FastDoubleParser.parseDouble("1.2345");
    float f = FastFloatParser.parseFloat("1.2345");

Method `parseDouble()` takes a `CharacterSequence`. a `char`-array or a `byte`-array as argument. This way. you can
parse from a `StringBuffer` or an array without having to convert your input to a `String`. Parsing from an array is
faster. because the parser can process multiple characters at once using SIMD instructions.

When you clone the code repository from github. you can choose from the following branches:

- `main` The code in this branch requires Java 17.
- `java8` The code in this branch requires Java 8.
- `dev` This code may or may not work. This code uses the experimental Vector API, and the Foreign Memory Access API,
  that are included in Java 19.

How to run the performance tests on a Mac:

1. Install Java JDK 8 or higher. for example [OpenJDK Java 18](https://jdk.java.net/18/)
2. Install the XCode command line tools from Apple.
3. Open the Terminal and execute the following commands:

Command sequence for Java 18 or higher:

     git clone https://github.com/wrandelshofer/FastDoubleParser.git
     cd FastDoubleParser 
     javac --enable-preview -source 18 -d out -encoding utf8 --module-source-path src/main/java --module ch.randelshofer.fastdoubleparser    
     javac --enable-preview -source 18 -d out -encoding utf8 -p out --module-source-path FastDoubleParserDemo/src/main/java --module ch.randelshofer.fastdoubleparserdemo
     java --enable-preview -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main  
     java --enable-preview -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main data/canada.txt   

Command sequence for Java 8 or higher:

     git clone https://github.com/wrandelshofer/FastDoubleParser.git
     cd FastDoubleParser 
     git checkout java8
     mkdir out
     javac -d out -encoding utf8 -sourcepath src/main/java/ch.randelshofer.fastdoubleparser src/main/java/ch.randelshofer.fastdoubleparser/**/*.java    
     javac -d out -encoding utf8 -cp out -sourcepath FastDoubleParserDemo/src/main/java/ch.randelshofer.fastdoubleparserdemo FastDoubleParserDemo/src/main/java/ch.randelshofer.fastdoubleparserdemo/**/*.java
     java -cp out ch.randelshofer.fastdoubleparserdemo.Main  
     java -cp out ch.randelshofer.fastdoubleparserdemo.Main data/canada.txt   

On my Mac mini (2018) I get the results shown below. The speedup factor with respect to `Double.parseDouble` ranges from
0.5 to 6 depending on the shape of the input data. You can expect a speedup factor of 4 for common input data shapes.

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    x86_64, Mac OS X, 12.5.1, 12
    OpenJDK 64-Bit Server VM, Oracle Corporation, 19+36-2238
    -XX:+UnlockExperimentalVMOptions
    Parsing random doubles in the range [0,1).
    [...]
    Measuring: Trying to reach a confidence level of 99.8 % which only deviates by 1 % from the average measured duration.
    [...]
    Measurement results:
    java.lang.Double            :    82.54 MB/s (+/- 4.0 %)     4.74 Mfloat/s     211.07 ns/f
    java.lang.Float             :    87.81 MB/s (+/- 3.5 %)     5.04 Mfloat/s     198.39 ns/f
    java.math.BigDecimal        :   162.87 MB/s (+/- 8.3 %)     9.35 Mfloat/s     106.96 ns/f
    JavaDoubleParser String     :   478.07 MB/s (+/-17.6 %)    27.44 Mfloat/s      36.44 ns/f
    JavaDoubleParser char[]     :   514.80 MB/s (+/-12.8 %)    29.55 Mfloat/s      33.84 ns/f
    JavaDoubleParser byte[]     :   592.20 MB/s (+/-10.3 %)    33.99 Mfloat/s      29.42 ns/f
    JsonDoubleParser String     :   453.11 MB/s (+/-17.4 %)    26.01 Mfloat/s      38.45 ns/f
    JsonDoubleParser char[]     :   552.69 MB/s (+/-14.5 %)    31.73 Mfloat/s      31.52 ns/f
    JsonDoubleParser byte[]     :   627.73 MB/s (+/-11.2 %)    36.03 Mfloat/s      27.75 ns/f
    JavaFloatParser  String     :   462.63 MB/s (+/-17.8 %)    26.56 Mfloat/s      37.66 ns/f
    JavaFloatParser  char[]     :   475.10 MB/s (+/-11.3 %)    27.27 Mfloat/s      36.67 ns/f
    JavaFloatParser  byte[]     :   612.48 MB/s (+/-12.1 %)    35.16 Mfloat/s      28.44 ns/f
    JavaBigDecimalParser String :   516.93 MB/s (+/-20.0 %)    29.67 Mfloat/s      33.70 ns/f
    JavaBigDecimalParser char[] :   563.40 MB/s (+/-15.1 %)    32.34 Mfloat/s      30.92 ns/f
    JavaBigDecimalParser byte[] :   655.11 MB/s (+/-15.1 %)    37.60 Mfloat/s      26.59 ns/f
    
    Speedup JavaDoubleParser String     vs java.lang.Double    : 5.79
    Speedup JavaDoubleParser char[]     vs java.lang.Double    : 6.24
    Speedup JavaDoubleParser byte[]     vs java.lang.Double    : 7.18
    Speedup JsonDoubleParser String     vs java.lang.Double    : 5.49
    Speedup JsonDoubleParser char[]     vs java.lang.Double    : 6.70
    Speedup JsonDoubleParser byte[]     vs java.lang.Double    : 7.61
    Speedup JavaFloatParser  String     vs java.lang.Float     : 5.27
    Speedup JavaFloatParser  char[]     vs java.lang.Float     : 5.41
    Speedup JavaFloatParser  byte[]     vs java.lang.Float     : 6.98
    Speedup JavaBigDecimalParser String vs java.math.BigDecimal: 3.17
    Speedup JavaBigDecimalParser char[] vs java.math.BigDecimal: 3.46
    Speedup JavaBigDecimalParser byte[] vs java.math.BigDecimal: 4.02
    You can also provide a filename: it should contain one string per line corresponding to a number.
    
    Process finished with exit code 0


'

    parsing numbers in file /Users/Shared/Developer/Java/FastDoubleParser/github/FastDoubleParser/data/canada.txt
    read 111126 lines
    [...]
    Measurement results:
    java.lang.Double            :    74.87 MB/s (+/- 5.3 %)     4.30 Mfloat/s     232.42 ns/f
    java.lang.Float             :    88.38 MB/s (+/- 4.2 %)     5.08 Mfloat/s     196.89 ns/f
    java.math.BigDecimal        :   238.67 MB/s (+/- 8.6 %)    13.72 Mfloat/s      72.91 ns/f
    JavaDoubleParser String     :   310.43 MB/s (+/-11.7 %)    17.84 Mfloat/s      56.06 ns/f
    JavaDoubleParser char[]     :   395.27 MB/s (+/-15.7 %)    22.71 Mfloat/s      44.02 ns/f
    JavaDoubleParser byte[]     :   469.50 MB/s (+/-15.7 %)    26.98 Mfloat/s      37.06 ns/f
    JsonDoubleParser String     :   383.39 MB/s (+/-11.2 %)    22.03 Mfloat/s      45.39 ns/f
    JsonDoubleParser char[]     :   473.21 MB/s (+/-10.7 %)    27.19 Mfloat/s      36.77 ns/f
    JsonDoubleParser byte[]     :   571.90 MB/s (+/- 7.7 %)    32.87 Mfloat/s      30.43 ns/f
    JavaFloatParser  String     :   407.16 MB/s (+/- 9.5 %)    23.40 Mfloat/s      42.74 ns/f
    JavaFloatParser  char[]     :   543.53 MB/s (+/- 9.7 %)    31.24 Mfloat/s      32.02 ns/f
    JavaFloatParser  byte[]     :   603.61 MB/s (+/- 8.5 %)    34.69 Mfloat/s      28.83 ns/f
    JavaBigDecimalParser String :   435.04 MB/s (+/- 9.9 %)    25.00 Mfloat/s      40.00 ns/f
    JavaBigDecimalParser char[] :   561.30 MB/s (+/-10.2 %)    32.26 Mfloat/s      31.00 ns/f
    JavaBigDecimalParser byte[] :   641.92 MB/s (+/- 8.5 %)    36.89 Mfloat/s      27.11 ns/f
    
    Speedup JavaDoubleParser String     vs java.lang.Double    : 4.15
    Speedup JavaDoubleParser char[]     vs java.lang.Double    : 5.28
    Speedup JavaDoubleParser byte[]     vs java.lang.Double    : 6.27
    Speedup JsonDoubleParser String     vs java.lang.Double    : 5.12
    Speedup JsonDoubleParser char[]     vs java.lang.Double    : 6.32
    Speedup JsonDoubleParser byte[]     vs java.lang.Double    : 7.64
    Speedup JavaFloatParser  String     vs java.lang.Float     : 4.61
    Speedup JavaFloatParser  char[]     vs java.lang.Float     : 6.15
    Speedup JavaFloatParser  byte[]     vs java.lang.Float     : 6.83
    Speedup JavaBigDecimalParser String vs java.math.BigDecimal: 1.82
    Speedup JavaBigDecimalParser char[] vs java.math.BigDecimal: 2.35
    Speedup JavaBigDecimalParser byte[] vs java.math.BigDecimal: 2.69

FastDoubleParser also speeds up parsing of hexadecimal float literals:

    parsing numbers in file /Users/Shared/Developer/Java/FastDoubleParser/github/FastDoubleParser/data/canada_hex.txt
    read 111126 lines
    [...]
    Measurement results:
    java.lang.Double            :    38.45 MB/s (+/- 5.1 %)     2.11 Mfloat/s     474.29 ns/f
    java.lang.Float             :    39.15 MB/s (+/- 4.2 %)     2.15 Mfloat/s     465.82 ns/f
    JavaDoubleParser String     :   407.92 MB/s (+/- 8.1 %)    22.37 Mfloat/s      44.71 ns/f
    JavaDoubleParser char[]     :   605.77 MB/s (+/- 9.0 %)    33.21 Mfloat/s      30.11 ns/f
    JavaDoubleParser byte[]     :   640.55 MB/s (+/- 7.7 %)    35.12 Mfloat/s      28.47 ns/f
    JavaFloatParser  String     :   389.53 MB/s (+/- 7.0 %)    21.36 Mfloat/s      46.82 ns/f
    JavaFloatParser  char[]     :   606.53 MB/s (+/- 7.5 %)    33.26 Mfloat/s      30.07 ns/f
    JavaFloatParser  byte[]     :   636.46 MB/s (+/- 7.1 %)    34.90 Mfloat/s      28.66 ns/f
    
    Speedup JavaDoubleParser String     vs java.lang.Double    : 10.61
    Speedup JavaDoubleParser char[]     vs java.lang.Double    : 15.75
    Speedup JavaDoubleParser byte[]     vs java.lang.Double    : 16.66
    Speedup JavaFloatParser  String     vs java.lang.Float     : 9.95
    Speedup JavaFloatParser  char[]     vs java.lang.Float     : 15.49
    Speedup JavaFloatParser  byte[]     vs java.lang.Float     : 16.26

## Comparison of JDK versions

The Y-axis shows Mfloat/s.

![ComparisonOfJvmVersions.png](ComparisonOfJvmVersions.png)

| Method              | 1.8.0_281  | 11.0.8 | 18.0.1.1 | 19-ea | 20-ea | 17.0.3graalvm |
|---------------------|------------|--------|----------|-------|-------|---------------|
| Double              | 4.86       | 5.34   | 5.09     | 5.28  | 4.81  | 6.96          |
| FastDouble String   | 20.30      | 27.83  | 31.18    | 32.59 | 26.15 | 32.74         |
| FastDouble char[]   | 30.60      | 30.68  | 33.58    | 37.20 | 29.95 | 34.32         |
| FastDouble byte[]   | 31.29      | 35.61  | 38.24    | 39.67 | 34.23 | 40.21         |

## Comparison with C version

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

    OpenJDK 19+36-2238
    java.lang.Double                        :    88.51 MB/s (+/- 5.5 %)     5.08 Mfloat/s     196.86 ns/f
    JavaDoubleParser String                 :   508.13 MB/s (+/-19.0 %)    29.16 Mfloat/s      34.29 ns/f
    JavaDoubleParser char[]                 :   582.40 MB/s (+/-15.0 %)    33.43 Mfloat/s      29.92 ns/f
    JavaDoubleParser byte[]                 :   657.50 MB/s (+/-13.4 %)    37.74 Mfloat/s      26.50 ns/f

'

    $ ./build/benchmarks/benchmark -f data/canada.txt
    # read 111126 lines 
    volume = 1.93374 MB 
    netlib                                  :   337.79 MB/s (+/- 5.8 %)    19.41 Mfloat/s      51.52 ns/f 
    doubleconversion                        :   254.22 MB/s (+/- 6.0 %)    14.61 Mfloat/s      68.45 ns/f 
    strtod                                  :    73.33 MB/s (+/- 7.1 %)     4.21 Mfloat/s     237.31 ns/f 
    abseil                                  :   411.11 MB/s (+/- 7.3 %)    23.63 Mfloat/s      42.33 ns/f 
    fastfloat                               :   741.32 MB/s (+/- 5.3 %)    42.60 Mfloat/s      23.47 ns/f 

    OpenJDK 19+36-2238
    java.lang.Double                        :    87.50 MB/s (+/- 4.0 %)     5.03 Mfloat/s     198.88 ns/f
    JavaDoubleParser String                 :   446.05 MB/s (+/- 9.5 %)    25.63 Mfloat/s      39.01 ns/f
    JavaDoubleParser char[]                 :   537.01 MB/s (+/- 9.1 %)    30.86 Mfloat/s      32.40 ns/f
    JavaDoubleParser byte[]                 :   582.76 MB/s (+/- 2.3 %)    33.49 Mfloat/s      29.86 ns/f
