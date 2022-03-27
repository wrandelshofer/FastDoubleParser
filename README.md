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
    OpenJDK 64-Bit Server VM, Oracle Corporation, 18+36-2087
    -XX:+UnlockExperimentalVMOptions
    
    parsing random numbers in the range [0,1)
    [...]
    FastDoubleParser               :    549.88 MB/s (+/- 2.0 %)    31.55 Mfloat/s      31.69 ns/f
    FastDoubleParserFromCharArray  :    647.11 MB/s (+/- 4.5 %)    37.04 Mfloat/s      26.99 ns/f
    FastDoubleParserFromByteArray  :    683.30 MB/s (+/- 3.5 %)    39.17 Mfloat/s      25.53 ns/f
    Double                         :     95.30 MB/s (+/- 3.9 %)     5.46 Mfloat/s     183.13 ns/f
    
    Speedup FastDoubleParser              vs Double: 5.77
    Speedup FastDoubleParserFromCharArray vs Double: 6.79
    Speedup FastDoubleParserFromByteArray vs Double: 7.17

'

    parsing numbers in file data/canada.txt
    read 111126 lines
    [...]
    FastDoubleParser               :    367.42 MB/s (+/- 6.0 %)    21.02 Mfloat/s      47.58 ns/f
    FastDoubleParserFromCharArray  :    482.68 MB/s (+/- 6.5 %)    27.59 Mfloat/s      36.24 ns/f
    FastDoubleParserFromByteArray  :    492.07 MB/s (+/- 8.8 %)    28.00 Mfloat/s      35.71 ns/f
    Double                         :     82.71 MB/s (+/- 4.2 %)     4.74 Mfloat/s     210.79 ns/f
    
    Speedup FastDoubleParser              vs Double: 4.44
    Speedup FastDoubleParserFromCharArray vs Double: 5.84
    Speedup FastDoubleParserFromByteArray vs Double: 5.95

FastDoubleParser also speeds up parsing of hexadecimal float literals:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
    x86_64, Mac OS X, 12.1, 12
    OpenJDK 64-Bit Server VM, Oracle Corporation, 18+36-2087
    -XX:+UnlockExperimentalVMOptions

    parsing numbers in file data/minusOneToOne_hexfloats.txt
    read 100000 lines
    [...]
    FastDoubleParser               :    401.71 MB/s (+/- 5.7 %)    20.67 Mfloat/s      48.39 ns/f
    FastDoubleParserFromCharArray  :    569.07 MB/s (+/- 1.6 %)    29.40 Mfloat/s      34.01 ns/f
    FastDoubleParserFromByteArray  :    610.78 MB/s (+/- 3.0 %)    31.53 Mfloat/s      31.71 ns/f
    Double                         :     53.97 MB/s (+/- 3.5 %)     2.79 Mfloat/s     359.01 ns/f
    
    Speedup FastDoubleParser              vs Double: 7.44
    Speedup FastDoubleParserFromCharArray vs Double: 10.54
    Speedup FastDoubleParserFromByteArray vs Double: 11.32

'

    parsing numbers in file data/canada_hexfloats.txt
    read 111126 lines
    [...]
    FastDoubleParser               :    433.40 MB/s (+/- 1.0 %)    23.76 Mfloat/s      42.08 ns/f
    FastDoubleParserFromCharArray  :    620.67 MB/s (+/- 5.1 %)    33.92 Mfloat/s      29.48 ns/f
    FastDoubleParserFromByteArray  :    652.33 MB/s (+/- 1.1 %)    35.76 Mfloat/s      27.96 ns/f
    Double                         :     54.20 MB/s (+/- 3.7 %)     2.97 Mfloat/s     336.98 ns/f
    
    Speedup FastDoubleParser              vs Double: 8.00
    Speedup FastDoubleParserFromCharArray vs Double: 11.45
    Speedup FastDoubleParserFromByteArray vs Double: 12.04

Please note that the performance gains depend a lot on the shape of the input data. Below are two test sets that are
less favorable for the current implementation of the code:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
    x86_64, Mac OS X, 12.1, 12
    OpenJDK 64-Bit Server VM, Oracle Corporation, 18+36-2087
    -XX:+UnlockExperimentalVMOptions

    parsing numbers in data/shorts.txt
    read 100000 lines
    [...]
    FastDoubleParser               :    201.03 MB/s (+/- 2.9 %)    40.78 Mfloat/s      24.52 ns/f
    FastDoubleParserFromCharArray  :    238.28 MB/s (+/- 5.5 %)    48.20 Mfloat/s      20.75 ns/f
    FastDoubleParserFromByteArray  :    248.89 MB/s (+/- 1.7 %)    50.52 Mfloat/s      19.80 ns/f
    Double                         :    144.88 MB/s (+/- 6.1 %)    29.27 Mfloat/s      34.17 ns/f
    
    Speedup FastDoubleParser              vs Double: 1.39
    Speedup FastDoubleParserFromCharArray vs Double: 1.64
    Speedup FastDoubleParserFromByteArray vs Double: 1.72

'

    parsing numbers in file data/FastDoubleParser_errorcases.txt
    read 26916 lines
    [...]
    FastDoubleParser               :     86.01 MB/s (+/- 4.0 %)     2.67 Mfloat/s     374.19 ns/f
    FastDoubleParserFromCharArray  :     97.15 MB/s (+/- 5.1 %)     3.01 Mfloat/s     331.74 ns/f
    FastDoubleParserFromByteArray  :     95.47 MB/s (+/- 4.2 %)     2.97 Mfloat/s     337.16 ns/f
    Double                         :    103.93 MB/s (+/- 3.4 %)     3.23 Mfloat/s     309.52 ns/f
    
    Speedup FastDoubleParser              vs Double: 0.83
    Speedup FastDoubleParserFromCharArray vs Double: 0.93
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

    OpenJDK 18+36-2087
    FastDoubleParser               :    549.88 MB/s (+/- 2.0 %)    31.55 Mfloat/s      31.69 ns/f
    FastDoubleParserFromCharArray  :    647.11 MB/s (+/- 4.5 %)    37.04 Mfloat/s      26.99 ns/f
    FastDoubleParserFromByteArray  :    683.30 MB/s (+/- 3.5 %)    39.17 Mfloat/s      25.53 ns/f
    Double                         :     95.30 MB/s (+/- 3.9 %)     5.46 Mfloat/s     183.13 ns/f


    GraalVM 17.0.1+12-jvmci-21.3-b05
    FastDoubleParser               :    624.01 MB/s (+/- 3.1 %)    35.78 Mfloat/s      27.95 ns/f
    FastDoubleParserFromCharArray*):     93.30 MB/s (+/- 4.8 %)     5.33 Mfloat/s     187.47 ns/f
    FastDoubleParserFromByteArray  :    783.78 MB/s (+/- 2.1 %)    44.96 Mfloat/s      22.24 ns/f
    Double                         :    115.84 MB/s (+/- 2.5 %)     6.64 Mfloat/s     150.51 ns/f

'

    $ ./build/benchmarks/benchmark -f data/canada.txt
    # read 111126 lines
    volume = 1.93374 MB
    netlib                         :   302.39 MB/s (+/- 4.5 %)    17.38 Mfloat/s      57.55 ns/f
    doubleconversion               :   268.79 MB/s (+/- 3.5 %)    15.45 Mfloat/s      64.74 ns/f
    strtod                         :    76.92 MB/s (+/- 4.1 %)     4.42 Mfloat/s     226.22 ns/f
    abseil                         :   482.49 MB/s (+/- 4.3 %)    27.73 Mfloat/s      36.07 ns/f
    fastfloat                      :   827.73 MB/s (+/- 4.4 %)    47.57 Mfloat/s      21.02 ns/f 

    OpenJDK 18+36-2087
    FastDoubleParser               :    401.71 MB/s (+/- 5.7 %)    20.67 Mfloat/s      48.39 ns/f
    FastDoubleParserFromCharArray  :    569.07 MB/s (+/- 1.6 %)    29.40 Mfloat/s      34.01 ns/f
    FastDoubleParserFromByteArray  :    610.78 MB/s (+/- 3.0 %)    31.53 Mfloat/s      31.71 ns/f
    Double                         :     53.97 MB/s (+/- 3.5 %)     2.79 Mfloat/s     359.01 ns/f

    GraalVM 17.0.1+12-jvmci-21.3-b05
    FastDoubleParser               :    435.03 MB/s (+/- 4.8 %)    24.94 Mfloat/s      40.09 ns/f
    FastDoubleParserFromCharArray  :    172.99 MB/s (+/- 5.8 %)     9.89 Mfloat/s     101.11 ns/f
    FastDoubleParserFromByteArray  :    579.55 MB/s (+/- 1.3 %)    33.30 Mfloat/s      30.03 ns/f
    Double                         :     97.24 MB/s (+/- 3.4 %)     5.58 Mfloat/s     179.16 ns/f

*) The Vector API in GraalVM does not create vectorized code yet 