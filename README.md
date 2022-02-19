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
    Trying to reach a confidence level of 99.8 % which only deviates by 1 % from the average measured duration.
    
    FastDoubleParser               :    525.92 MB/s (+/- 7.8 %)    29.91 Mfloat/s      33.43 ns/f
    FastDoubleParserFromCharArray  :    639.93 MB/s (+/- 6.2 %)    36.56 Mfloat/s      27.35 ns/f
    FastDoubleParserFromByteArray  :    674.28 MB/s (+/- 0.2 %)    38.69 Mfloat/s      25.84 ns/f
    Double                         :     92.66 MB/s (+/- 3.1 %)     5.31 Mfloat/s     188.24 ns/f
    
    Speedup FastDoubleParser              vs Double: 5.68
    Speedup FastDoubleParserFromCharArray vs Double: 6.91
    Speedup FastDoubleParserFromByteArray vs Double: 7.28

'

    parsing numbers in file data/canada.txt
    read 111126 lines
    [...]
    Trying to reach a confidence level of 99.8 % which only deviates by 1 % from the average measured duration.
    
    FastDoubleParser               :    436.99 MB/s (+/- 8.4 %)    24.87 Mfloat/s      40.21 ns/f
    FastDoubleParserFromCharArray  :    493.13 MB/s (+/- 6.7 %)    28.16 Mfloat/s      35.51 ns/f
    FastDoubleParserFromByteArray  :    534.69 MB/s (+/- 4.8 %)    30.64 Mfloat/s      32.64 ns/f
    Double                         :     85.79 MB/s (+/- 3.8 %)     4.92 Mfloat/s     203.13 ns/f
    
    Speedup FastDoubleParser              vs Double: 5.09
    Speedup FastDoubleParserFromCharArray vs Double: 5.75
    Speedup FastDoubleParserFromByteArray vs Double: 6.23

FastDoubleParser also speeds up parsing of hexadecimal float literals:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
    x86_64, Mac OS X, 12.1, 12
    OpenJDK 64-Bit Server VM, Oracle Corporation, 18-ea+30-2029
    -XX:+UnlockExperimentalVMOptions

    parsing numbers in file data/minusOneToOne_hexfloats.txt
    read 100000 lines
    [...]
    FastDoubleParser               :    468.87 MB/s (+/- 8.2 %)    24.00 Mfloat/s      41.67 ns/f
    FastDoubleParserFromCharArray  :    567.58 MB/s (+/- 5.9 %)    29.22 Mfloat/s      34.22 ns/f
    FastDoubleParserFromByteArray  :    571.07 MB/s (+/- 3.8 %)    29.47 Mfloat/s      33.93 ns/f
    Double                         :     53.02 MB/s (+/- 3.1 %)     2.74 Mfloat/s     365.35 ns/f
    
    Speedup FastDoubleParser              vs Double: 8.84
    Speedup FastDoubleParserFromCharArray vs Double: 10.71
    Speedup FastDoubleParserFromByteArray vs Double: 10.77

'

    parsing numbers in file data/canada_hexfloats.txt
    read 111126 lines
    [...]
    FastDoubleParser               :    525.50 MB/s (+/- 9.7 %)    28.42 Mfloat/s      35.18 ns/f
    FastDoubleParserFromCharArray  :    636.90 MB/s (+/- 1.1 %)    34.92 Mfloat/s      28.64 ns/f
    FastDoubleParserFromByteArray  :    676.74 MB/s (+/- 4.9 %)    36.99 Mfloat/s      27.03 ns/f
    Double                         :     51.36 MB/s (+/- 3.6 %)     2.81 Mfloat/s     355.53 ns/f
    
    Speedup FastDoubleParser              vs Double: 10.23
    Speedup FastDoubleParserFromCharArray vs Double: 12.40
    Speedup FastDoubleParserFromByteArray vs Double: 13.18

Please note that the performance gains depend a lot on the shape of the input data. Below are two test sets that are
less favorable for the current implementation of the code:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
    x86_64, Mac OS X, 12.1, 12
    OpenJDK 64-Bit Server VM, Oracle Corporation, 18-ea+30-2029
    -XX:+UnlockExperimentalVMOptions

    parsing numbers in data/shorts.txt
    read 100000 lines
    [...]
    FastDoubleParser               :    145.72 MB/s (+/- 1.9 %)    29.58 Mfloat/s      33.81 ns/f
    FastDoubleParserFromCharArray  :    242.76 MB/s (+/- 6.7 %)    49.01 Mfloat/s      20.40 ns/f
    FastDoubleParserFromByteArray  :    252.12 MB/s (+/- 3.8 %)    51.11 Mfloat/s      19.56 ns/f
    Double                         :    143.66 MB/s (+/- 6.9 %)    28.98 Mfloat/s      34.50 ns/f
    
    Speedup FastDoubleParser              vs Double: 1.01
    Speedup FastDoubleParserFromCharArray vs Double: 1.69
    Speedup FastDoubleParserFromByteArray vs Double: 1.75

'

    parsing numbers in file data/FastDoubleParser_errorcases.txt
    read 26916 lines
    [...]
    FastDoubleParser               :     94.11 MB/s (+/- 3.9 %)     2.92 Mfloat/s     341.95 ns/f
    FastDoubleParserFromCharArray  :     94.76 MB/s (+/- 5.2 %)     2.94 Mfloat/s     340.05 ns/f
    FastDoubleParserFromByteArray  :     94.94 MB/s (+/- 4.0 %)     2.95 Mfloat/s     338.97 ns/f
    Double                         :    106.19 MB/s (+/- 3.8 %)     3.30 Mfloat/s     303.04 ns/f
    
    Speedup FastDoubleParser              vs Double: 0.89
    Speedup FastDoubleParserFromCharArray vs Double: 0.89
    Speedup FastDoubleParserFromByteArray vs Double: 0.89

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

    OpenJDK 18-ea+30-2029
    FastDoubleParser               :    525.92 MB/s (+/- 7.8 %)    29.91 Mfloat/s      33.43 ns/f
    FastDoubleParserFromCharArray  :    639.93 MB/s (+/- 6.2 %)    36.56 Mfloat/s      27.35 ns/f
    FastDoubleParserFromByteArray  :    674.28 MB/s (+/- 0.2 %)    38.69 Mfloat/s      25.84 ns/f
    Double                         :     92.66 MB/s (+/- 3.1 %)     5.31 Mfloat/s     188.24 ns/f

    GraalVM 17.0.1+12-jvmci-21.3-b05
    FastDoubleParser               :    447.68 MB/s (+/- 1.3 %)    25.72 Mfloat/s      38.88 ns/f
    FastDoubleParserFromCharArray  :    180.71 MB/s (+/- 6.0 %)    10.33 Mfloat/s      96.83 ns/f
    FastDoubleParserFromByteArray  :    539.93 MB/s (+/- 3.5 %)    30.99 Mfloat/s      32.27 ns/f
    Double                         :    100.33 MB/s (+/- 3.9 %)     5.76 Mfloat/s     173.71 ns/f

*) The Vector API in GraalVM does not create vectorized code yet 