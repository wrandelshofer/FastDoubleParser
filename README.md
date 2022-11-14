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
    -XX:+UnlockDiagnosticVMOptions, -XX:CompileCommand=inline,java/lang/String.charAt
    Parsing random doubles in the range [0,1).
    [...]
    Measuring: Trying to reach a confidence level of 99.8 % which only deviates by 1 % from the average measured duration.
    [...]
    Measurement results:
    java.lang.Double            :    80.98 MB/s (+/- 3.6 % stdv) (+/- 1.0 % conf,    128 trials)     4.65 Mfloat/s   215.10 ns/f
    java.lang.Float             :    86.28 MB/s (+/- 4.2 % stdv) (+/- 0.9 % conf,    192 trials)     4.95 Mfloat/s   201.91 ns/f
    java.math.BigDecimal        :   143.22 MB/s (+/- 7.1 % stdv) (+/- 1.0 % conf,    512 trials)     8.22 Mfloat/s   121.63 ns/f
    JavaDoubleParser String     :   310.97 MB/s (+/-16.8 % stdv) (+/- 1.0 % conf,   2720 trials)    17.85 Mfloat/s    56.02 ns/f
    JavaDoubleParser char[]     :   381.00 MB/s (+/-19.5 % stdv) (+/- 1.0 % conf,   3648 trials)    21.87 Mfloat/s    45.72 ns/f
    JavaDoubleParser byte[]     :   484.09 MB/s (+/-19.0 % stdv) (+/- 1.0 % conf,   3456 trials)    27.79 Mfloat/s    35.99 ns/f
    JsonDoubleParser String     :   313.54 MB/s (+/-12.5 % stdv) (+/- 1.0 % conf,   1504 trials)    18.00 Mfloat/s    55.56 ns/f
    JsonDoubleParser char[]     :   398.57 MB/s (+/-17.4 % stdv) (+/- 1.0 % conf,   2912 trials)    22.88 Mfloat/s    43.71 ns/f
    JsonDoubleParser byte[]     :   483.45 MB/s (+/-20.1 % stdv) (+/- 1.0 % conf,   3872 trials)    27.75 Mfloat/s    36.03 ns/f
    JavaFloatParser  String     :   311.75 MB/s (+/-19.5 % stdv) (+/- 1.0 % conf,   3648 trials)    17.90 Mfloat/s    55.88 ns/f
    JavaFloatParser  char[]     :   384.39 MB/s (+/-24.1 % stdv) (+/- 1.0 % conf,   5568 trials)    22.07 Mfloat/s    45.32 ns/f
    JavaFloatParser  byte[]     :   561.39 MB/s (+/- 8.2 % stdv) (+/- 1.0 % conf,    672 trials)    32.23 Mfloat/s    31.03 ns/f
    JavaBigDecimalParser String :   530.23 MB/s (+/-14.8 % stdv) (+/- 1.0 % conf,   2112 trials)    30.44 Mfloat/s    32.85 ns/f
    JavaBigDecimalParser char[] :   591.02 MB/s (+/-10.2 % stdv) (+/- 1.0 % conf,   1024 trials)    33.93 Mfloat/s    29.47 ns/f
    JavaBigDecimalParser byte[] :   686.39 MB/s (+/- 9.2 % stdv) (+/- 1.0 % conf,    832 trials)    39.40 Mfloat/s    25.38 ns/f
    
    Speedup JavaDoubleParser String     vs java.lang.Double    : 3.84
    Speedup JavaDoubleParser char[]     vs java.lang.Double    : 4.70
    Speedup JavaDoubleParser byte[]     vs java.lang.Double    : 5.98
    Speedup JsonDoubleParser String     vs java.lang.Double    : 3.87
    Speedup JsonDoubleParser char[]     vs java.lang.Double    : 4.92
    Speedup JsonDoubleParser byte[]     vs java.lang.Double    : 5.97
    Speedup JavaFloatParser  String     vs java.lang.Float     : 3.61
    Speedup JavaFloatParser  char[]     vs java.lang.Float     : 4.46
    Speedup JavaFloatParser  byte[]     vs java.lang.Float     : 6.51
    Speedup JavaBigDecimalParser String vs java.math.BigDecimal: 3.70
    Speedup JavaBigDecimalParser char[] vs java.math.BigDecimal: 4.13
    Speedup JavaBigDecimalParser byte[] vs java.math.BigDecimal: 4.79

'

    parsing numbers in file /Users/Shared/Developer/Java/FastDoubleParser/github/FastDoubleParser/data/canada.txt
    read 111126 lines
    [...]
    Measurement results:
    java.lang.Double            :    75.65 MB/s (+/- 7.1 % stdv) (+/- 1.0 % conf,    512 trials)     4.35 Mfloat/s   230.01 ns/f
    java.lang.Float             :    93.38 MB/s (+/- 4.4 % stdv) (+/- 1.0 % conf,    192 trials)     5.37 Mfloat/s   186.36 ns/f
    java.math.BigDecimal        :   287.23 MB/s (+/- 7.2 % stdv) (+/- 1.0 % conf,    512 trials)    16.51 Mfloat/s    60.58 ns/f
    JavaDoubleParser String     :   379.67 MB/s (+/- 8.6 % stdv) (+/- 1.0 % conf,    736 trials)    21.82 Mfloat/s    45.83 ns/f
    JavaDoubleParser char[]     :   518.82 MB/s (+/- 8.7 % stdv) (+/- 1.0 % conf,    736 trials)    29.81 Mfloat/s    33.54 ns/f
    JavaDoubleParser byte[]     :   555.75 MB/s (+/- 9.1 % stdv) (+/- 1.0 % conf,    832 trials)    31.94 Mfloat/s    31.31 ns/f
    JsonDoubleParser String     :   397.14 MB/s (+/-10.4 % stdv) (+/- 1.0 % conf,   1056 trials)    22.82 Mfloat/s    43.82 ns/f
    JsonDoubleParser char[]     :   495.36 MB/s (+/- 8.8 % stdv) (+/- 1.0 % conf,    736 trials)    28.47 Mfloat/s    35.13 ns/f
    JsonDoubleParser byte[]     :   578.79 MB/s (+/- 8.0 % stdv) (+/- 1.0 % conf,    640 trials)    33.26 Mfloat/s    30.07 ns/f
    JavaFloatParser  String     :   392.00 MB/s (+/- 9.5 % stdv) (+/- 1.0 % conf,    896 trials)    22.53 Mfloat/s    44.39 ns/f
    JavaFloatParser  char[]     :   453.96 MB/s (+/- 1.7 % stdv) (+/- 0.9 % conf,     32 trials)    26.09 Mfloat/s    38.33 ns/f
    JavaFloatParser  byte[]     :   550.15 MB/s (+/- 7.2 % stdv) (+/- 1.0 % conf,    512 trials)    31.62 Mfloat/s    31.63 ns/f
    JavaBigDecimalParser String :   436.34 MB/s (+/-10.4 % stdv) (+/- 1.0 % conf,   1056 trials)    25.07 Mfloat/s    39.88 ns/f
    JavaBigDecimalParser char[] :   598.30 MB/s (+/- 9.6 % stdv) (+/- 1.0 % conf,    896 trials)    34.38 Mfloat/s    29.08 ns/f
    JavaBigDecimalParser byte[] :   674.83 MB/s (+/- 9.4 % stdv) (+/- 1.0 % conf,    864 trials)    38.78 Mfloat/s    25.79 ns/f
    
    Speedup JavaDoubleParser String     vs java.lang.Double    : 5.02
    Speedup JavaDoubleParser char[]     vs java.lang.Double    : 6.86
    Speedup JavaDoubleParser byte[]     vs java.lang.Double    : 7.35
    Speedup JsonDoubleParser String     vs java.lang.Double    : 5.25
    Speedup JsonDoubleParser char[]     vs java.lang.Double    : 6.55
    Speedup JsonDoubleParser byte[]     vs java.lang.Double    : 7.65
    Speedup JavaFloatParser  String     vs java.lang.Float     : 4.20
    Speedup JavaFloatParser  char[]     vs java.lang.Float     : 4.86
    Speedup JavaFloatParser  byte[]     vs java.lang.Float     : 5.89
    Speedup JavaBigDecimalParser String vs java.math.BigDecimal: 1.52
    Speedup JavaBigDecimalParser char[] vs java.math.BigDecimal: 2.08
    Speedup JavaBigDecimalParser byte[] vs java.math.BigDecimal: 2.35

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

