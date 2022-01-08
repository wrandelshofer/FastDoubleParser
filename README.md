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
     javac -d out -encoding utf8 -sourcepath src/main/java:test/main/java test/main/java/ch/randelshofer/fastdoubleparser/FastDoubleParserBenchmark.java 
     java -classpath out ch.randelshofer.fastdoubleparserdemo.FastDoubleParserBenchmark 
     java -classpath out ch.randelshofer.fastdoubleparserdemo.FastDoubleParserBenchmark data/canada.txt

On my Mac mini (2018) I get the results shown below. The results vary on the JVM and platform being used.
FastDoubleParser.parseDouble() can be more than 4 times faster than Double.parseDouble().

If your input is a char array or a byte array you can use FastDoubleParserFromCharArray.parseDouble() or
FastDoubleParserFromByteArray.parseDouble() which are even slightly faster.

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    x86_64, Mac OS X, 12.1, 12
    OpenJDK 64-Bit Server VM, Oracle Corporation, 18-ea+30-2029
    -XX:+UnlockExperimentalVMOptions
    
    parsing random numbers in the range [0,1)
    [...]

    FastDoubleParser               :   438.79 MB/s (+/-10.8 %)    24.75 Mfloat/s      40.40 ns/f
    FastDoubleParserFromCharArray  :   499.46 MB/s (+/-11.9 %)    24.74 Mfloat/s      40.42 ns/f
    FastDoubleParserFromByteArray  :   522.09 MB/s (+/- 7.1 %)    29.73 Mfloat/s      33.64 ns/f
    Double                         :    88.03 MB/s (+/-10.3 %)     4.98 Mfloat/s     200.92 ns/f
    
    Speedup FastDoubleParser              vs Double: 4.98
    Speedup FastDoubleParserFromCharArray vs Double: 5.67
    Speedup FastDoubleParserFromByteArray vs Double: 5.93

'

    parsing numbers in file data/canada.txt
    read 111126 lines
    [...]

    FastDoubleParser               :   341.34 MB/s (+/- 9.5 %)    19.22 Mfloat/s      52.03 ns/f
    FastDoubleParserFromCharArray  :   459.94 MB/s (+/-10.3 %)    22.58 Mfloat/s      44.30 ns/f
    FastDoubleParserFromByteArray  :   470.90 MB/s (+/- 7.3 %)    26.81 Mfloat/s      37.30 ns/f
    Double                         :    85.12 MB/s (+/-10.4 %)     4.82 Mfloat/s     207.62 ns/f
    
    Speedup FastDoubleParser              vs Double: 4.01
    Speedup FastDoubleParserFromCharArray vs Double: 5.40
    Speedup FastDoubleParserFromByteArray vs Double: 5.53

FastDoubleParser also speeds up parsing of hexadecimal float literals:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    x86_64, Mac OS X, 12.1, 12
    OpenJDK 64-Bit Server VM, Oracle Corporation, 18-ea+30-2029
    -XX:+UnlockExperimentalVMOptions

    parsing numbers in file data/minusOneToOne_hexfloats.txt
    read 100000 lines
    Trying to reach a confidence level of 98,0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               :   358.35 MB/s (+/- 9.4 %)    18.30 Mfloat/s      54.64 ns/f
    FastDoubleParserFromCharArray  :   449.51 MB/s (+/-10.5 %)    21.06 Mfloat/s      47.48 ns/f
    FastDoubleParserFromByteArray  :   526.25 MB/s (+/- 7.3 %)    26.98 Mfloat/s      37.07 ns/f
    Double                         :    48.56 MB/s (+/- 8.4 %)     2.49 Mfloat/s     402.27 ns/f
    
    Speedup FastDoubleParser              vs Double: 7.38
    Speedup FastDoubleParserFromCharArray vs Double: 9.26
    Speedup FastDoubleParserFromByteArray vs Double: 10.84

'

    parsing numbers in file data/canada_hexfloats.txt
    read 111126 lines
    [...]
    
    FastDoubleParser               :   380.25 MB/s (+/- 9.3 %)    20.57 Mfloat/s      48.62 ns/f
    FastDoubleParserFromCharArray  :   548.39 MB/s (+/-13.3 %)    25.21 Mfloat/s      39.66 ns/f
    FastDoubleParserFromByteArray  :   599.08 MB/s (+/-10.2 %)    32.26 Mfloat/s      31.00 ns/f
    Double                         :    49.61 MB/s (+/- 8.0 %)     2.70 Mfloat/s     370.64 ns/f
    
    Speedup FastDoubleParser              vs Double: 7.66
    Speedup FastDoubleParserFromCharArray vs Double: 11.05
    Speedup FastDoubleParserFromByteArray vs Double: 12.07

Please note that the performance gains depend a lot on the shape of the input data. Below are two test sets that are
less favorable for the current implementation of the code:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    x86_64, Mac OS X, 12.1, 12
    OpenJDK 64-Bit Server VM, Oracle Corporation, 18-ea+30-2029
    -XX:+UnlockExperimentalVMOptions

    parsing numbers in data/shorts.txt
    read 100000 lines
    [...]
    FastDoubleParser               :   185.64 MB/s (+/-10.1 %)    37.10 Mfloat/s      26.96 ns/f
    FastDoubleParserFromCharArray  :   227.18 MB/s (+/- 8.4 %)    45.16 Mfloat/s      22.14 ns/f
    FastDoubleParserFromByteArray  :   234.73 MB/s (+/- 7.1 %)    47.31 Mfloat/s      21.14 ns/f
    Double                         :   131.43 MB/s (+/-16.6 %)    25.67 Mfloat/s      38.95 ns/f
    
    Speedup FastDoubleParser              vs Double: 1.41
    Speedup FastDoubleParserFromCharArray vs Double: 1.73
    Speedup FastDoubleParserFromByteArray vs Double: 1.79 

'

    parsing numbers in file data/FastDoubleParser_errorcases.txt
    read 26916 lines
    [...]
    FastDoubleParser               :    84.43 MB/s (+/-11.0 %)     2.54 Mfloat/s     393.53 ns/f
    FastDoubleParserFromCharArray  :    90.85 MB/s (+/-11.3 %)     2.56 Mfloat/s     391.32 ns/f
    FastDoubleParserFromByteArray  :    93.48 MB/s (+/- 9.1 %)     2.87 Mfloat/s     348.93 ns/f
    Double                         :   102.49 MB/s (+/- 9.9 %)     3.13 Mfloat/s     319.84 ns/f
    
    Speedup FastDoubleParser              vs Double: 0.82
    Speedup FastDoubleParserFromCharArray vs Double: 0.89
    Speedup FastDoubleParserFromByteArray vs Double: 0.91

## Comparison with C version

For comparison, here are the test results
of [simple_fastfloat_benchmark](https://github.com/lemire/simple_fastfloat_benchmark)  
on the same computer:

    version: Mon Sep 13 22:30:48 2021 -0400 a2eee6b5fc009e215d7f346ae86794aa99b013a4

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz

    $ ./build/benchmarks/benchmark
    # parsing random numbers
    available models (-m): uniform one_over_rand32 simple_uniform32 simple_int32 int_e_int simple_int64 bigint_int_dot_int big_ints
    model: generate random numbers uniformly in the interval [0.0,1.0]
    volume: 100000 floats
    volume = 2.09808 MB
    netlib                                  :   284.91 MB/s (+/- 4.0 %)    13.58 Mfloat/s      73.64 ns/f
    doubleconversion                        :   266.46 MB/s (+/- 1.5 %)    12.70 Mfloat/s      78.74 ns/f
    strtod                                  :    88.05 MB/s (+/- 2.2 %)     4.20 Mfloat/s     238.29 ns/f
    abseil                                  :   516.76 MB/s (+/- 2.5 %)    24.63 Mfloat/s      40.60 ns/f
    fastfloat                               :   948.14 MB/s (+/- 5.6 %)    45.19 Mfloat/s      22.13 ns/f

    FastDoubleParser                        :   438.79 MB/s (+/-10.8 %)    24.75 Mfloat/s      40.40 ns/f
    FastDoubleParserFromCharArray           :   499.46 MB/s (+/-11.9 %)    24.74 Mfloat/s      40.42 ns/f
    FastDoubleParserFromByteArray           :   522.09 MB/s (+/- 7.1 %)    29.73 Mfloat/s      33.64 ns/f
    Double                                  :    88.03 MB/s (+/-10.3 %)     4.98 Mfloat/s     200.92 ns/f

'

    $ ./build/benchmarks/benchmark -f data/canada.txt
    # read 111126 lines
    volume = 1.93374 MB
    netlib                                  :   302.39 MB/s (+/- 4.5 %)    17.38 Mfloat/s      57.55 ns/f
    doubleconversion                        :   268.79 MB/s (+/- 3.5 %)    15.45 Mfloat/s      64.74 ns/f
    strtod                                  :    76.92 MB/s (+/- 4.1 %)     4.42 Mfloat/s     226.22 ns/f
    abseil                                  :   482.49 MB/s (+/- 4.3 %)    27.73 Mfloat/s      36.07 ns/f
    fastfloat                               :   827.73 MB/s (+/- 4.4 %)    47.57 Mfloat/s      21.02 ns/f 

    FastDoubleParser                        :   341.34 MB/s (+/- 9.5 %)    19.22 Mfloat/s      52.03 ns/f
    FastDoubleParserFromCharArray           :   459.94 MB/s (+/-10.3 %)    22.58 Mfloat/s      44.30 ns/f
    FastDoubleParserFromByteArray           :   470.90 MB/s (+/- 7.3 %)    26.81 Mfloat/s      37.30 ns/f
    Double                                  :    85.12 MB/s (+/-10.4 %)     4.82 Mfloat/s     207.62 ns/f