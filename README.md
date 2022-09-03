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
    Warming JVM up (code must be compiled by C2 compiler for optimal performance).
    Trying to reach a confidence level of 99.8 % which only deviates by 1 % from the average measured duration.
    Measurement results:
    java.lang.Double        :    94.49 MB/s (+/- 3.3 %)     5.42 Mfloat/s     184.42 ns/f
    java.lang.Float         :   105.38 MB/s (+/- 4.1 %)     6.05 Mfloat/s     165.36 ns/f
    JavaDoubleParser String :   608.51 MB/s (+/- 3.5 %)    34.92 Mfloat/s      28.64 ns/f
    JavaDoubleParser char[] :   638.72 MB/s (+/- 4.3 %)    36.65 Mfloat/s      27.28 ns/f
    JavaDoubleParser byte[] :   679.01 MB/s (+/- 4.1 %)    38.97 Mfloat/s      25.66 ns/f
    JsonDoubleParser String :   574.58 MB/s (+/-12.6 %)    32.97 Mfloat/s      30.33 ns/f
    JsonDoubleParser char[] :   607.42 MB/s (+/- 4.0 %)    34.86 Mfloat/s      28.69 ns/f
    JsonDoubleParser byte[] :   699.42 MB/s (+/- 8.9 %)    40.14 Mfloat/s      24.91 ns/f
    JavaFloatParser  String :   581.56 MB/s (+/-10.7 %)    33.37 Mfloat/s      29.96 ns/f
    JavaFloatParser  char[] :   626.41 MB/s (+/- 2.8 %)    35.95 Mfloat/s      27.82 ns/f
    JavaFloatParser  byte[] :   684.83 MB/s (+/- 3.1 %)    39.30 Mfloat/s      25.44 ns/f
    
    Speedup JavaDoubleParser String vs java.lang.Double : 6.44
    Speedup JavaDoubleParser char[] vs java.lang.Double : 6.76
    Speedup JavaDoubleParser byte[] vs java.lang.Double : 7.19
    Speedup JsonDoubleParser String vs java.lang.Double : 6.08
    Speedup JsonDoubleParser char[] vs java.lang.Double : 6.43
    Speedup JsonDoubleParser byte[] vs java.lang.Double : 7.40
    Speedup JavaFloatParser  String vs java.lang.Float  : 5.52
    Speedup JavaFloatParser  char[] vs java.lang.Float  : 5.94
    Speedup JavaFloatParser  byte[] vs java.lang.Float  : 6.50
    You can also provide a filename: it should contain one string per line corresponding to a number.
'

    parsing numbers in file /Users/Shared/Developer/Java/FastDoubleParser/github/FastDoubleParser/data/canada.txt
    read 111126 lines
    Trying to reach a confidence level of 99.8 % which only deviates by 1 % from the average measured duration.
    Measurement results:
    java.lang.Double        :    85.15 MB/s (+/- 4.2 %)     4.89 Mfloat/s     204.36 ns/f
    java.lang.Float         :   104.43 MB/s (+/- 3.6 %)     6.00 Mfloat/s     166.63 ns/f
    JavaDoubleParser String :   399.69 MB/s (+/- 1.6 %)    22.97 Mfloat/s      43.54 ns/f
    JavaDoubleParser char[] :   566.07 MB/s (+/- 6.3 %)    32.53 Mfloat/s      30.74 ns/f
    JavaDoubleParser byte[] :   561.07 MB/s (+/- 1.9 %)    32.24 Mfloat/s      31.01 ns/f
    JsonDoubleParser String :   393.65 MB/s (+/- 2.4 %)    22.62 Mfloat/s      44.21 ns/f
    JsonDoubleParser char[] :   549.63 MB/s (+/- 4.2 %)    31.59 Mfloat/s      31.66 ns/f
    JsonDoubleParser byte[] :   549.57 MB/s (+/- 1.4 %)    31.58 Mfloat/s      31.66 ns/f
    JavaFloatParser  String :   440.78 MB/s (+/- 6.7 %)    25.33 Mfloat/s      39.48 ns/f
    JavaFloatParser  char[] :   609.24 MB/s (+/- 3.1 %)    35.01 Mfloat/s      28.56 ns/f
    JavaFloatParser  byte[] :   621.52 MB/s (+/- 4.1 %)    35.72 Mfloat/s      28.00 ns/f
    
    Speedup JavaDoubleParser String vs java.lang.Double : 4.69
    Speedup JavaDoubleParser char[] vs java.lang.Double : 6.65
    Speedup JavaDoubleParser byte[] vs java.lang.Double : 6.59
    Speedup JsonDoubleParser String vs java.lang.Double : 4.62
    Speedup JsonDoubleParser char[] vs java.lang.Double : 6.45
    Speedup JsonDoubleParser byte[] vs java.lang.Double : 6.45
    Speedup JavaFloatParser  String vs java.lang.Float  : 4.22
    Speedup JavaFloatParser  char[] vs java.lang.Float  : 5.83
    Speedup JavaFloatParser  byte[] vs java.lang.Float  : 5.95

FastDoubleParser also speeds up parsing of hexadecimal float literals:

    parsing numbers in file /Users/Shared/Developer/Java/FastDoubleParser/github/FastDoubleParser/data/canada_hex.txt
    read 111126 lines
    Trying to reach a confidence level of 99.8 % which only deviates by 1 % from the average measured duration.
    Measurement results:
    java.lang.Double        :    38.88 MB/s (+/- 4.0 %)     2.13 Mfloat/s     469.04 ns/f
    java.lang.Float         :    39.20 MB/s (+/- 3.7 %)     2.15 Mfloat/s     465.31 ns/f
    JavaDoubleParser String :   461.18 MB/s (+/- 7.5 %)    25.29 Mfloat/s      39.55 ns/f
    JavaDoubleParser char[] :   599.14 MB/s (+/- 8.1 %)    32.85 Mfloat/s      30.44 ns/f
    JavaDoubleParser byte[] :   592.10 MB/s (+/- 6.8 %)    32.47 Mfloat/s      30.80 ns/f
    JavaFloatParser  String :   461.38 MB/s (+/- 7.4 %)    25.30 Mfloat/s      39.53 ns/f
    JavaFloatParser  char[] :   536.92 MB/s (+/- 2.3 %)    29.44 Mfloat/s      33.97 ns/f
    JavaFloatParser  byte[] :   607.47 MB/s (+/- 6.2 %)    33.31 Mfloat/s      30.02 ns/f
    
    Speedup JavaDoubleParser String vs java.lang.Double : 11.86
    Speedup JavaDoubleParser char[] vs java.lang.Double : 15.41
    Speedup JavaDoubleParser byte[] vs java.lang.Double : 15.23
    Speedup JavaFloatParser  String vs java.lang.Float  : 11.77
    Speedup JavaFloatParser  char[] vs java.lang.Float  : 13.70
    Speedup JavaFloatParser  byte[] vs java.lang.Float  : 15.50

## Comparison of JDK versions

The Y-axis shows Mfloat/s.

![ComparisonOfJvmVersions.png](ComparisonOfJvmVersions.png)

|Method            |1.8.0_281|11.0.8|18.0.1.1|19-ea|17.0.3graalvm|
|-------------------|---|---|---|---|---|
|Double            |4.86|5.34|5.09|5.28|6.96|
|FastDouble String |20.30|27.83|31.18|32.59|32.74|
|FastDouble char[] |30.60|30.68|33.58|37.20|34.32|
|FastDouble byte[] |31.29|35.61|38.24|39.67|40.21|

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
    java.lang.Double                        :    94.69 MB/s (+/- 4.1 %)     5.44 Mfloat/s     183.98 ns/f
    JavaDoubleParser String                 :   601.64 MB/s (+/- 3.6 %)    34.54 Mfloat/s      28.95 ns/f
    JavaDoubleParser char[]                 :   593.65 MB/s (+/- 4.8 %)    34.08 Mfloat/s      29.34 ns/f
    JavaDoubleParser byte[]                 :   686.86 MB/s (+/- 4.4 %)    39.43 Mfloat/s      25.36 ns/f

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
    java.lang.Double                        :    81.94 MB/s (+/- 3.2 %)     4.71 Mfloat/s     212.36 ns/f
    JavaDoubleParser String                 :   382.54 MB/s (+/- 8.9 %)    21.98 Mfloat/s      45.49 ns/f
    JavaDoubleParser char[]                 :   519.47 MB/s (+/- 7.9 %)    29.85 Mfloat/s      33.50 ns/f
    JavaDoubleParser byte[]                 :   550.68 MB/s (+/- 5.6 %)    31.65 Mfloat/s      31.60 ns/f
