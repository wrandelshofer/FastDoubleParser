# FastDoubleParser

This is a Java port of Daniel Lemire's fast_double_parser.

https://github.com/lemire/fast_double_parser

Usage:

    import ch.randelshofer.fastdoubleparser.FastDoubleParser;
    import ch.randelshofer.fastdoubleparser.FastFloatParser;

    double d = FastDoubleParser.parseDouble("1.2345");
    float f = FastFloatParser.parseFloat("1.2345");

Method `parseDouble()` takes a `CharacterSequence`, a `char`-array or a `byte`-array as argument. This way, you can
parse from a `StringBuffer` or an array without having to convert your input to a `String`. Parsing from an array is
faster, because the parser can process multiple characters at once using SIMD instructions.

When you clone the code repository from github, you can choose from the following branches:

- `main` The code in this branch requires Java 17.
- `java8` The code in this branch requires Java 8.
- `dev` This code may or may not work. This code uses the experimental Vector API that is included in Java 17 and 18.

How to run the performance tests on a Mac:

1. Install Java JDK 8 or higher, for example [OpenJDK Java 18](https://jdk.java.net/18/)
2. Install the XCode command line tools from Apple.
3. Open the Terminal and execute the following commands:

Command sequence for Java 17 or higher:

     git clone https://github.com/wrandelshofer/FastDoubleParser.git
     cd FastDoubleParser 
     javac -d out -encoding utf8 --module-source-path src/main/java --module ch.randelshofer.fastdoubleparser    
     javac -d out -encoding utf8 -p out --module-source-path FastDoubleParserDemo/src/main/java --module ch.randelshofer.fastdoubleparserdemo
     java -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main  
     java -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main data/canada.txt   

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

    WARNING: Using incubator modules: jdk.incubator.vector
    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
    x86_64, Mac OS X, 12.3.1, 12
    OpenJDK 64-Bit Server VM, Oracle Corporation, 18.0.1+10-24
    
    
    parsing random doubles in the range [0,1)
    Trying to reach a confidence level of 99.8 % which only deviates by 1 % from the average measured duration.
    FastDouble String :   512.84 MB/s    29.43 Mfloat/s    33.97 ns/f
    FastDouble char[] :   652.80 MB/s    37.47 Mfloat/s    26.69 ns/f
    FastDouble byte[] :   628.88 MB/s    36.09 Mfloat/s    27.70 ns/f
    Double            :    90.28 MB/s     5.18 Mfloat/s   192.99 ns/f
    FastFloat  String :   378.64 MB/s    21.73 Mfloat/s    46.02 ns/f
    FastFloat  char[] :   618.63 MB/s    35.51 Mfloat/s    28.16 ns/f
    FastFloat  byte[] :   578.82 MB/s    33.22 Mfloat/s    30.10 ns/f
    Float             :    95.56 MB/s     5.48 Mfloat/s   182.33 ns/f
    
    Speedup FastDouble String vs Double: 5.68
    Speedup FastDouble char[] vs Double: 7.23
    Speedup FastDouble byte[] vs Double: 6.97
    Speedup FastFloat  String vs Double: 4.19
    Speedup FastFloat  char[] vs Double: 6.85
    Speedup FastFloat  byte[] vs Double: 6.41
    Speedup Float             vs Double: 1.06

'

    parsing numbers in file /Users/Shared/Developer/Java/FastDoubleParser/github/FastDoubleParser/data/canada.txt
    read 111126 lines
    Trying to reach a confidence level of 99.8 % which only deviates by 1 % from the average measured duration.
    FastDouble String :   379.52 MB/s    21.81 Mfloat/s    45.85 ns/f
    FastDouble char[] :   482.58 MB/s    27.73 Mfloat/s    36.06 ns/f
    FastDouble byte[] :   471.96 MB/s    27.12 Mfloat/s    36.87 ns/f
    Double            :    82.34 MB/s     4.73 Mfloat/s   211.34 ns/f
    FastFloat  String :   381.75 MB/s    21.94 Mfloat/s    45.58 ns/f
    FastFloat  char[] :   521.50 MB/s    29.97 Mfloat/s    33.37 ns/f
    FastFloat  byte[] :   546.26 MB/s    31.39 Mfloat/s    31.86 ns/f
    Float             :   101.79 MB/s     5.85 Mfloat/s   170.95 ns/f
    
    Speedup FastDouble String vs Double: 4.61
    Speedup FastDouble char[] vs Double: 5.86
    Speedup FastDouble byte[] vs Double: 5.73
    Speedup FastFloat  String vs Double: 4.64
    Speedup FastFloat  char[] vs Double: 6.33
    Speedup FastFloat  byte[] vs Double: 6.63
    Speedup Float             vs Double: 1.24

FastDoubleParser also speeds up parsing of hexadecimal float literals:

    parsing numbers in file /Users/Shared/Developer/Java/FastDoubleParser/github/FastDoubleParser/data/canada_hex.txt
    read 111126 lines
    Trying to reach a confidence level of 99.8 % which only deviates by 1 % from the average measured duration.
    FastDouble String :   378.40 MB/s    20.75 Mfloat/s    48.20 ns/f
    FastDouble char[] :   558.92 MB/s    30.65 Mfloat/s    32.63 ns/f
    FastDouble byte[] :   691.41 MB/s    37.91 Mfloat/s    26.38 ns/f
    Double            :    51.98 MB/s     2.85 Mfloat/s   350.84 ns/f
    FastFloat  String :   392.88 MB/s    21.54 Mfloat/s    46.42 ns/f
    FastFloat  char[] :   617.52 MB/s    33.86 Mfloat/s    29.53 ns/f
    FastFloat  byte[] :   657.71 MB/s    36.06 Mfloat/s    27.73 ns/f
    Float             :    53.23 MB/s     2.92 Mfloat/s   342.64 ns/f
    
    Speedup FastDouble String vs Double: 7.28
    Speedup FastDouble char[] vs Double: 10.75
    Speedup FastDouble byte[] vs Double: 13.30
    Speedup FastFloat  String vs Double: 7.56
    Speedup FastFloat  char[] vs Double: 11.88
    Speedup FastFloat  byte[] vs Double: 12.65
    Speedup Float             vs Double: 1.02

## Comparison with C version

For comparison, here are the test results
of [simple_fastfloat_benchmark](https://github.com/lemire/simple_fastfloat_benchmark)  
on the same computer:

    version: Thu Mar 31 10:18:12 2022 -0400 f2082bf747eabc0873f2fdceb05f9451931b96dc

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256

    $ ./build/benchmarks/benchmark
    # parsing random numbers
    available models (-m): uniform one_over_rand32 simple_uniform32 simple_int32 int_e_int simple_int64 bigint_int_dot_int big_ints 
    model: generate random numbers uniformly in the interval [0.0,1.0]
    volume: 100000 floats
    volume = 2.09808 MB 
    netlib                                  :   317.31 MB/s (+/- 6.0 %)    15.12 Mfloat/s      66.12 ns/f 
    doubleconversion                        :   263.89 MB/s (+/- 4.2 %)    12.58 Mfloat/s      79.51 ns/f 
    strtod                                  :    86.13 MB/s (+/- 3.7 %)     4.10 Mfloat/s     243.61 ns/f 
    abseil                                  :   467.27 MB/s (+/- 9.0 %)    22.27 Mfloat/s      44.90 ns/f 
    fastfloat                               :   880.79 MB/s (+/- 6.6 %)    41.98 Mfloat/s      23.82 ns/f 

    OpenJDK 18.0.1+10-24
    FastDouble String                       :   512.84 MB/s                29.43 Mfloat/s      33.97 ns/f
    FastDouble char[]                       :   652.80 MB/s                37.47 Mfloat/s      26.69 ns/f
    FastDouble byte[]                       :   628.88 MB/s                36.09 Mfloat/s      27.70 ns/f
    Double                                  :    90.28 MB/s                 5.18 Mfloat/s     192.99 ns/f

'

    $ ./build/benchmarks/benchmark -f data/canada.txt
    # read 111126 lines 
    volume = 1.93374 MB 
    netlib                                  :   337.79 MB/s (+/- 5.8 %)    19.41 Mfloat/s      51.52 ns/f 
    doubleconversion                        :   254.22 MB/s (+/- 6.0 %)    14.61 Mfloat/s      68.45 ns/f 
    strtod                                  :    73.33 MB/s (+/- 7.1 %)     4.21 Mfloat/s     237.31 ns/f 
    abseil                                  :   411.11 MB/s (+/- 7.3 %)    23.63 Mfloat/s      42.33 ns/f 
    fastfloat                               :   741.32 MB/s (+/- 5.3 %)    42.60 Mfloat/s      23.47 ns/f 

    OpenJDK 18.0.1+10-24
    FastDouble String                       :   379.52 MB/s                21.81 Mfloat/s      45.85 ns/f
    FastDouble char[]                       :   482.58 MB/s                27.73 Mfloat/s      36.06 ns/f
    FastDouble byte[]                       :   471.96 MB/s                27.12 Mfloat/s      36.87 ns/f
    Double                                  :    82.34 MB/s                 4.73 Mfloat/s     211.34 ns/f