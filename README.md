# FastDoubleParser

This is a straight-forward C++ to Java port of Daniel Lemire's fast_double_parser.

https://github.com/lemire/fast_double_parser

Usage:

    import FastDoubleParser;

    double d = FastDoubleParser.parseDouble("1.2345");

Note: Method parseDouble takes a `CharacterSequence` as its argument. So, if you have a text in a `StringBuffer`, you do
not need to convert it to a `String`, because `StringBuffer` extends from `CharacterSequence`. If you have a char-array
or a byte-array as input, you can use `FastDoubleParserFromCharArray` or
`FastDoubleParserFromByteArray` respectively.

The test directory contains some functional tests, and some performance tests.

**Please note that the main branch contains bleeding edge code for Java 18.**
**For production use, you can download one of the releases for Java 11 and Java 8.**

How to run the performance test on a Mac:

1. Install Java JDK 18 or higher, for example [OpenJDK.](https://jdk.java.net/18/)
2. Install the XCode command line tools from Apple.
3. Open the Terminal and execute the following commands:

Command:

     git clone https://github.com/wrandelshofer/FastDoubleParser.git
     cd FastDoubleParser 
     javac -d out -encoding utf8 --module-source-path src/main/java --module ch.randelshofer.fastdoubleparser    
     javac -Xlint:deprecation -d out -encoding utf8 -p out --module-source-path FastDoubleParserDemo/src/main/java --module ch.randelshofer.fastdoubleparserdemo
     java -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main  
     java -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main data/canada.txt   

On my Mac mini (2018) I get the results shown below. The results vary on the JVM and platform being used.
FastDoubleParser.parseDouble() is at least 4 times faster than Double.parseDouble().

If your input is a char array or a byte array you can use FastDoubleParserFromCharArray.parseDouble() or
FastDoubleParserFromByteArray.parseDouble() which are even slightly faster.

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
    x86_64, Mac OS X, 12.1, 12
    OpenJDK 64-Bit Server VM, Oracle Corporation, 18-ea+30-2029
    -XX:+UnlockExperimentalVMOptions
    
    parsing random numbers in the range [0,1)
    [...]
    FastDoubleParser               :    499.62 MB/s (+/- 6.3 %)    28.53 Mfloat/s      35.05 ns/f
    FastDoubleParserFromCharArray  :    648.27 MB/s (+/- 2.0 %)    37.19 Mfloat/s      26.89 ns/f
    FastDoubleParserFromByteArray  :    700.86 MB/s (+/- 3.7 %)    40.17 Mfloat/s      24.89 ns/f
    Double                         :     97.28 MB/s (+/- 2.9 %)     5.58 Mfloat/s     179.25 ns/f
    
    Speedup FastDoubleParser              vs Double: 5.14
    Speedup FastDoubleParserFromCharArray vs Double: 6.66
    Speedup FastDoubleParserFromByteArray vs Double: 7.20

'

    parsing numbers in file data/canada.txt
    read 111126 lines
    [...]
    FastDoubleParser               :    375.24 MB/s (+/- 2.9 %)    21.55 Mfloat/s      46.41 ns/f
    FastDoubleParserFromCharArray  :    501.84 MB/s (+/- 1.8 %)    28.83 Mfloat/s      34.69 ns/f
    FastDoubleParserFromByteArray  :    505.38 MB/s (+/- 4.5 %)    28.96 Mfloat/s      34.53 ns/f
    Double                         :     84.54 MB/s (+/- 3.9 %)     4.85 Mfloat/s     206.18 ns/f
    
    Speedup FastDoubleParser              vs Double: 4.44
    Speedup FastDoubleParserFromCharArray vs Double: 5.94
    Speedup FastDoubleParserFromByteArray vs Double: 5.98

FastDoubleParser also speeds up parsing of hexadecimal float literals:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
    x86_64, Mac OS X, 12.1, 12
    OpenJDK 64-Bit Server VM, Oracle Corporation, 18-ea+30-2029
    -XX:+UnlockExperimentalVMOptions

    parsing numbers in file data/minusOneToOne_hexfloats.txt
    read 100000 lines
    [...]
    FastDoubleParser               :    414.67 MB/s (+/- 5.1 %)    21.36 Mfloat/s      46.82 ns/f
    FastDoubleParserFromCharArray  :    582.23 MB/s (+/- 2.1 %)    30.07 Mfloat/s      33.25 ns/f
    FastDoubleParserFromByteArray  :    594.85 MB/s (+/- 1.0 %)    30.74 Mfloat/s      32.53 ns/f
    Double                         :     52.02 MB/s (+/- 3.5 %)     2.68 Mfloat/s     372.46 ns/f
    
    Speedup FastDoubleParser              vs Double: 7.97
    Speedup FastDoubleParserFromCharArray vs Double: 11.19
    Speedup FastDoubleParserFromByteArray vs Double: 11.44

'

    parsing numbers in file data/canada_hexfloats.txt
    read 111126 lines
    [...]
    FastDoubleParser               :    467.98 MB/s (+/- 3.7 %)    25.62 Mfloat/s      39.03 ns/f
    FastDoubleParserFromCharArray  :    631.67 MB/s (+/- 4.1 %)    34.57 Mfloat/s      28.92 ns/f
    FastDoubleParserFromByteArray  :    651.60 MB/s (+/- 1.2 %)    35.72 Mfloat/s      27.99 ns/f
    Double                         :     50.22 MB/s (+/- 3.6 %)     2.75 Mfloat/s     363.68 ns/f
    
    Speedup FastDoubleParser              vs Double: 9.32
    Speedup FastDoubleParserFromCharArray vs Double: 12.58
    Speedup FastDoubleParserFromByteArray vs Double: 12.98

Please note that the performance gains depend a lot on the shape of the input data. Below are two test sets that are
less favorable for the current implementation of the code:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
    x86_64, Mac OS X, 12.1, 12
    OpenJDK 64-Bit Server VM, Oracle Corporation, 18-ea+30-2029
    -XX:+UnlockExperimentalVMOptions

    parsing numbers in data/shorts.txt
    read 100000 lines
    [...]
    FastDoubleParser               :    207.46 MB/s (+/- 2.8 %)    42.09 Mfloat/s      23.76 ns/f
    FastDoubleParserFromCharArray  :    235.40 MB/s (+/- 7.6 %)    47.40 Mfloat/s      21.10 ns/f
    FastDoubleParserFromByteArray  :    257.50 MB/s (+/- 2.3 %)    52.25 Mfloat/s      19.14 ns/f
    Double                         :    142.48 MB/s (+/- 6.8 %)    28.75 Mfloat/s      34.79 ns/f

    Speedup FastDoubleParser              vs Double: 1.46
    Speedup FastDoubleParserFromCharArray vs Double: 1.65
    Speedup FastDoubleParserFromByteArray vs Double: 1.81

'

    parsing numbers in file data/FastDoubleParser_errorcases.txt
    read 26916 lines
    [...]
    FastDoubleParser               :     88.04 MB/s (+/- 3.2 %)     2.74 Mfloat/s     365.35 ns/f
    FastDoubleParserFromCharArray  :     96.49 MB/s (+/- 4.3 %)     3.00 Mfloat/s     333.69 ns/f
    FastDoubleParserFromByteArray  :     98.33 MB/s (+/- 3.9 %)     3.06 Mfloat/s     327.31 ns/f
    Double                         :    107.25 MB/s (+/- 4.1 %)     3.33 Mfloat/s     300.10 ns/f
    
    Speedup FastDoubleParser              vs Double: 0.82
    Speedup FastDoubleParserFromCharArray vs Double: 0.90
    Speedup FastDoubleParserFromByteArray vs Double: 0.92

## Comparison with C version

For comparison, here are the test results
of [simple_fastfloat_benchmark](https://github.com/lemire/simple_fastfloat_benchmark)  
on the same computer:

    version: Mon Sep 13 22:30:48 2021 -0400 a2eee6b5fc009e215d7f346ae86794aa99b013a4

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256

    $ ./build/benchmarks/benchmark
    # parsing random numbers
    available models (-m): uniform one_over_rand32 simple_uniform32 simple_int32 int_e_int simple_int64 bigint_int_dot_int big_ints
    model: generate random numbers uniformly in the interval [0.0,1.0]
    volume: 100000 floats
    volume = 2.09808 MB
    netlib                          :   284.91 MB/s (+/- 4.0 %)    13.58 Mfloat/s      73.64 ns/f
    doubleconversion                :   266.46 MB/s (+/- 1.5 %)    12.70 Mfloat/s      78.74 ns/f
    strtod                          :    88.05 MB/s (+/- 2.2 %)     4.20 Mfloat/s     238.29 ns/f
    abseil                          :   516.76 MB/s (+/- 2.5 %)    24.63 Mfloat/s      40.60 ns/f
    fastfloat                       :   948.14 MB/s (+/- 5.6 %)    45.19 Mfloat/s      22.13 ns/f

    GraalVM 17.0.1+12-jvmci-21.3-b05
    FastDoubleParser               :    528.86 MB/s (+/- 2.6 %)    30.33 Mfloat/s      32.97 ns/f
    FastDoubleParserFromCharArray(*):   107.47 MB/s (+/- 3.3 %)     6.16 Mfloat/s     162.30 ns/f
    FastDoubleParserFromByteArray  :    765.92 MB/s (+/- 1.2 %)    43.95 Mfloat/s      22.75 ns/f
    Double                         :    115.71 MB/s (+/- 2.8 %)     6.64 Mfloat/s     150.70 ns/f
    
    OpenJDK 18-ea+30-2029
    FastDoubleParser               :    499.62 MB/s (+/- 6.3 %)    28.53 Mfloat/s      35.05 ns/f
    FastDoubleParserFromCharArray  :    648.27 MB/s (+/- 2.0 %)    37.19 Mfloat/s      26.89 ns/f
    FastDoubleParserFromByteArray  :    700.86 MB/s (+/- 3.7 %)    40.17 Mfloat/s      24.89 ns/f
    Double                         :     97.28 MB/s (+/- 2.9 %)     5.58 Mfloat/s     179.25 ns/f

'

    $ ./build/benchmarks/benchmark -f data/canada.txt
    # read 111126 lines
    volume = 1.93374 MB
    netlib                         :   302.39 MB/s (+/- 4.5 %)    17.38 Mfloat/s      57.55 ns/f
    doubleconversion               :   268.79 MB/s (+/- 3.5 %)    15.45 Mfloat/s      64.74 ns/f
    strtod                         :    76.92 MB/s (+/- 4.1 %)     4.42 Mfloat/s     226.22 ns/f
    abseil                         :   482.49 MB/s (+/- 4.3 %)    27.73 Mfloat/s      36.07 ns/f
    fastfloat                      :   827.73 MB/s (+/- 4.4 %)    47.57 Mfloat/s      21.02 ns/f 

    GraalVM 17.0.1+12-jvmci-21.3-b05
    FastDoubleParser               :    438.94 MB/s (+/- 0.6 %)    25.22 Mfloat/s      39.65 ns/f
    FastDoubleParserFromCharArray  :    153.33 MB/s (+/- 6.5 %)     8.75 Mfloat/s     114.25 ns/f
    FastDoubleParserFromByteArray  :    576.40 MB/s (+/- 5.4 %)    33.01 Mfloat/s      30.29 ns/f
    Double                         :     96.99 MB/s (+/- 3.0 %)     5.57 Mfloat/s     179.57 ns/f

    OpenJDK 18-ea+30-2029
    FastDoubleParser               :    375.24 MB/s (+/- 2.9 %)    21.55 Mfloat/s      46.41 ns/f
    FastDoubleParserFromCharArray  :    501.84 MB/s (+/- 1.8 %)    28.83 Mfloat/s      34.69 ns/f
    FastDoubleParserFromByteArray  :    505.38 MB/s (+/- 4.5 %)    28.96 Mfloat/s      34.53 ns/f
    Double                         :     84.54 MB/s (+/- 3.9 %)     4.85 Mfloat/s     206.18 ns/f

*) The Vector API in GraalVM does not create vectorized code yet 