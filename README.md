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
    OpenJDK 64-Bit Server VM, Oracle Corporation, 17+35-2724
    -XX:+UnlockDiagnosticVMOptions, -XX:CompileCommand=inline,java/lang/String.charAt
    Parsing random doubles in the range [0,1).
    [...]
    Measuring: Trying to reach a confidence level of 99.8 % which only deviates by 1 % from the average measured duration.
    [...]
    Measurement results:
    java.lang.Double            :    91.91 MB/s (+/- 4.5 %)     5.28 Mfloat/s     189.54 ns/f
    java.lang.Float             :    92.23 MB/s (+/- 5.3 %)     5.29 Mfloat/s     188.88 ns/f
    java.math.BigDecimal        :   173.80 MB/s (+/- 7.0 %)     9.98 Mfloat/s     100.23 ns/f
    JavaDoubleParser String     :   492.49 MB/s (+/-12.2 %)    28.27 Mfloat/s      35.37 ns/f
    JavaDoubleParser char[]     :   560.07 MB/s (+/- 8.4 %)    32.15 Mfloat/s      31.10 ns/f
    JavaDoubleParser byte[]     :   580.96 MB/s (+/- 7.3 %)    33.35 Mfloat/s      29.99 ns/f
    JsonDoubleParser String     :   488.57 MB/s (+/-13.6 %)    28.05 Mfloat/s      35.66 ns/f
    JsonDoubleParser char[]     :   528.92 MB/s (+/-15.2 %)    30.36 Mfloat/s      32.94 ns/f
    JsonDoubleParser byte[]     :   598.88 MB/s (+/-10.4 %)    34.38 Mfloat/s      29.09 ns/f
    JavaFloatParser  String     :   429.24 MB/s (+/-15.1 %)    24.64 Mfloat/s      40.58 ns/f
    JavaFloatParser  char[]     :   513.65 MB/s (+/- 9.3 %)    29.49 Mfloat/s      33.91 ns/f
    JavaFloatParser  byte[]     :   538.75 MB/s (+/-14.6 %)    30.93 Mfloat/s      32.33 ns/f
    JavaBigDecimalParser String :   352.79 MB/s (+/-31.0 %)    20.25 Mfloat/s      49.38 ns/f
    JavaBigDecimalParser char[] :   541.51 MB/s (+/-17.0 %)    31.09 Mfloat/s      32.17 ns/f
    JavaBigDecimalParser byte[] :   654.44 MB/s (+/-13.8 %)    37.57 Mfloat/s      26.62 ns/f
    
    Speedup JavaDoubleParser String     vs java.lang.Double    : 5.36
    Speedup JavaDoubleParser char[]     vs java.lang.Double    : 6.09
    Speedup JavaDoubleParser byte[]     vs java.lang.Double    : 6.32
    Speedup JsonDoubleParser String     vs java.lang.Double    : 5.32
    Speedup JsonDoubleParser char[]     vs java.lang.Double    : 5.75
    Speedup JsonDoubleParser byte[]     vs java.lang.Double    : 6.52
    Speedup JavaFloatParser  String     vs java.lang.Float     : 4.65
    Speedup JavaFloatParser  char[]     vs java.lang.Float     : 5.57
    Speedup JavaFloatParser  byte[]     vs java.lang.Float     : 5.84
    Speedup JavaBigDecimalParser String vs java.math.BigDecimal: 2.03
    Speedup JavaBigDecimalParser char[] vs java.math.BigDecimal: 3.12
    Speedup JavaBigDecimalParser byte[] vs java.math.BigDecimal: 3.77

'

    parsing numbers in file /Users/Shared/Developer/Java/FastDoubleParser/github/FastDoubleParser/data/canada.txt
    read 111126 lines
    [...]
    Measurement results:
    java.lang.Double            :    72.20 MB/s (+/- 3.8 %)     4.15 Mfloat/s     241.02 ns/f
    java.lang.Float             :    86.45 MB/s (+/- 4.6 %)     4.97 Mfloat/s     201.28 ns/f
    java.math.BigDecimal        :   257.95 MB/s (+/-16.2 %)    14.82 Mfloat/s      67.46 ns/f
    JavaDoubleParser String     :   357.44 MB/s (+/- 8.3 %)    20.54 Mfloat/s      48.68 ns/f
    JavaDoubleParser char[]     :   476.53 MB/s (+/- 8.7 %)    27.38 Mfloat/s      36.52 ns/f
    JavaDoubleParser byte[]     :   515.54 MB/s (+/- 7.5 %)    29.63 Mfloat/s      33.75 ns/f
    JsonDoubleParser String     :   394.89 MB/s (+/- 9.4 %)    22.69 Mfloat/s      44.07 ns/f
    JsonDoubleParser char[]     :   454.46 MB/s (+/- 8.8 %)    26.12 Mfloat/s      38.29 ns/f
    JsonDoubleParser byte[]     :   532.41 MB/s (+/- 7.5 %)    30.60 Mfloat/s      32.68 ns/f
    JavaFloatParser  String     :   386.31 MB/s (+/- 9.8 %)    22.20 Mfloat/s      45.04 ns/f
    JavaFloatParser  char[]     :   493.23 MB/s (+/- 8.2 %)    28.34 Mfloat/s      35.28 ns/f
    JavaFloatParser  byte[]     :   570.59 MB/s (+/- 7.0 %)    32.79 Mfloat/s      30.50 ns/f
    JavaBigDecimalParser String :   429.92 MB/s (+/-10.9 %)    24.71 Mfloat/s      40.48 ns/f
    JavaBigDecimalParser char[] :   586.05 MB/s (+/-11.5 %)    33.68 Mfloat/s      29.69 ns/f
    JavaBigDecimalParser byte[] :   662.35 MB/s (+/- 9.1 %)    38.06 Mfloat/s      26.27 ns/f
    
    Speedup JavaDoubleParser String     vs java.lang.Double    : 4.95
    Speedup JavaDoubleParser char[]     vs java.lang.Double    : 6.60
    Speedup JavaDoubleParser byte[]     vs java.lang.Double    : 7.14
    Speedup JsonDoubleParser String     vs java.lang.Double    : 5.47
    Speedup JsonDoubleParser char[]     vs java.lang.Double    : 6.29
    Speedup JsonDoubleParser byte[]     vs java.lang.Double    : 7.37
    Speedup JavaFloatParser  String     vs java.lang.Float     : 4.47
    Speedup JavaFloatParser  char[]     vs java.lang.Float     : 5.71
    Speedup JavaFloatParser  byte[]     vs java.lang.Float     : 6.60
    Speedup JavaBigDecimalParser String vs java.math.BigDecimal: 1.67
    Speedup JavaBigDecimalParser char[] vs java.math.BigDecimal: 2.27
    Speedup JavaBigDecimalParser byte[] vs java.math.BigDecimal: 2.57

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

    OpenJDK 20-ea+22-1594
    java.lang.Double                        :    80.96 MB/s (+/- 5.4 %)     4.65 Mfloat/s     214.95 ns/f
    JavaDoubleParser String                 :   415.63 MB/s (+/-10.1 %)    23.88 Mfloat/s      41.87 ns/f
    JavaDoubleParser char[]                 :   584.31 MB/s (+/- 9.7 %)    33.58 Mfloat/s      29.78 ns/f
    JavaDoubleParser byte[]                 :   609.54 MB/s (+/- 7.4 %)    35.03 Mfloat/s      28.55 ns/f

