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
    java.lang.Double            :    87.56 MB/s (+/- 6.7 %)     5.03 Mfloat/s     198.97 ns/f
    java.lang.Float             :    92.64 MB/s (+/- 5.7 %)     5.32 Mfloat/s     188.07 ns/f
    java.math.BigDecimal        :   168.11 MB/s (+/- 6.9 %)     9.65 Mfloat/s     103.64 ns/f
    JavaDoubleParser String     :   388.09 MB/s (+/-11.3 %)    22.28 Mfloat/s      44.89 ns/f
    JavaDoubleParser char[]     :   454.38 MB/s (+/-11.1 %)    26.08 Mfloat/s      38.34 ns/f
    JavaDoubleParser byte[]     :   515.40 MB/s (+/-10.9 %)    29.58 Mfloat/s      33.80 ns/f
    JsonDoubleParser String     :   449.16 MB/s (+/-18.2 %)    25.78 Mfloat/s      38.79 ns/f
    JsonDoubleParser char[]     :   393.95 MB/s (+/-22.0 %)    22.61 Mfloat/s      44.22 ns/f
    JsonDoubleParser byte[]     :   525.83 MB/s (+/-18.6 %)    30.18 Mfloat/s      33.13 ns/f
    JavaFloatParser  String     :   437.85 MB/s (+/-16.8 %)    25.13 Mfloat/s      39.79 ns/f
    JavaFloatParser  char[]     :   469.22 MB/s (+/-11.8 %)    26.93 Mfloat/s      37.13 ns/f
    JavaFloatParser  byte[]     :   535.85 MB/s (+/-10.0 %)    30.76 Mfloat/s      32.51 ns/f
    JavaBigDecimalParser String :   481.87 MB/s (+/-19.9 %)    27.66 Mfloat/s      36.16 ns/f
    JavaBigDecimalParser char[] :   573.29 MB/s (+/-18.0 %)    32.91 Mfloat/s      30.39 ns/f
    JavaBigDecimalParser byte[] :   628.67 MB/s (+/-16.1 %)    36.08 Mfloat/s      27.71 ns/f
    
    Speedup JavaDoubleParser String     vs java.lang.Double    : 4.43
    Speedup JavaDoubleParser char[]     vs java.lang.Double    : 5.19
    Speedup JavaDoubleParser byte[]     vs java.lang.Double    : 5.89
    Speedup JsonDoubleParser String     vs java.lang.Double    : 5.13
    Speedup JsonDoubleParser char[]     vs java.lang.Double    : 4.50
    Speedup JsonDoubleParser byte[]     vs java.lang.Double    : 6.01
    Speedup JavaFloatParser  String     vs java.lang.Float     : 4.73
    Speedup JavaFloatParser  char[]     vs java.lang.Float     : 5.07
    Speedup JavaFloatParser  byte[]     vs java.lang.Float     : 5.78
    Speedup JavaBigDecimalParser String vs java.math.BigDecimal: 2.87
    Speedup JavaBigDecimalParser char[] vs java.math.BigDecimal: 3.41
    Speedup JavaBigDecimalParser byte[] vs java.math.BigDecimal: 3.74

'

    parsing numbers in file /Users/Shared/Developer/Java/FastDoubleParser/github/FastDoubleParser/data/canada.txt
    read 111126 lines
    [...]
    Measurement results:
    java.lang.Double            :    80.77 MB/s (+/- 5.2 %)     4.64 Mfloat/s     215.44 ns/f
    java.lang.Float             :    96.83 MB/s (+/- 4.3 %)     5.56 Mfloat/s     179.70 ns/f
    java.math.BigDecimal        :   276.40 MB/s (+/- 9.5 %)    15.88 Mfloat/s      62.96 ns/f
    JavaDoubleParser String     :   343.75 MB/s (+/-12.1 %)    19.75 Mfloat/s      50.62 ns/f
    JavaDoubleParser char[]     :   440.38 MB/s (+/-11.7 %)    25.31 Mfloat/s      39.51 ns/f
    JavaDoubleParser byte[]     :   489.21 MB/s (+/-10.2 %)    28.11 Mfloat/s      35.57 ns/f
    JsonDoubleParser String     :   366.76 MB/s (+/-12.2 %)    21.08 Mfloat/s      47.45 ns/f
    JsonDoubleParser char[]     :   436.74 MB/s (+/-11.2 %)    25.10 Mfloat/s      39.84 ns/f
    JsonDoubleParser byte[]     :   512.64 MB/s (+/-10.0 %)    29.46 Mfloat/s      33.94 ns/f
    JavaFloatParser  String     :   350.00 MB/s (+/-11.3 %)    20.11 Mfloat/s      49.72 ns/f
    JavaFloatParser  char[]     :   476.71 MB/s (+/-13.4 %)    27.40 Mfloat/s      36.50 ns/f
    JavaFloatParser  byte[]     :   518.69 MB/s (+/-11.3 %)    29.81 Mfloat/s      33.55 ns/f
    JavaBigDecimalParser String :   386.73 MB/s (+/-14.3 %)    22.22 Mfloat/s      45.00 ns/f
    JavaBigDecimalParser char[] :   569.71 MB/s (+/-16.3 %)    32.74 Mfloat/s      30.54 ns/f
    JavaBigDecimalParser byte[] :   648.12 MB/s (+/-14.2 %)    37.25 Mfloat/s      26.85 ns/f
    
    Speedup JavaDoubleParser String     vs java.lang.Double    : 4.26
    Speedup JavaDoubleParser char[]     vs java.lang.Double    : 5.45
    Speedup JavaDoubleParser byte[]     vs java.lang.Double    : 6.06
    Speedup JsonDoubleParser String     vs java.lang.Double    : 4.54
    Speedup JsonDoubleParser char[]     vs java.lang.Double    : 5.41
    Speedup JsonDoubleParser byte[]     vs java.lang.Double    : 6.35
    Speedup JavaFloatParser  String     vs java.lang.Float     : 3.61
    Speedup JavaFloatParser  char[]     vs java.lang.Float     : 4.92
    Speedup JavaFloatParser  byte[]     vs java.lang.Float     : 5.36
    Speedup JavaBigDecimalParser String vs java.math.BigDecimal: 1.40
    Speedup JavaBigDecimalParser char[] vs java.math.BigDecimal: 2.06
    Speedup JavaBigDecimalParser byte[] vs java.math.BigDecimal: 2.34

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

