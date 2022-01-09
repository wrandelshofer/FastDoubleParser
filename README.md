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
FastDoubleParser.parseDouble() can be more than 5 times faster than Double.parseDouble().

If your input is a char array or a byte array you can use FastDoubleParserFromCharArray.parseDouble() or
FastDoubleParserFromByteArray.parseDouble() which are even slightly faster.

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
    x86_64, Mac OS X, 12.1, 12
    OpenJDK 64-Bit Server VM, Oracle Corporation, 18-ea+30-2029
    -XX:+UnlockExperimentalVMOptions
    
    parsing random numbers in the range [0,1)
    [...]
    FastDoubleParser               :    492.76 MB/s (+/- 6.6 %)    28.11 Mfloat/s      35.57 ns/f
    FastDoubleParserFromCharArray  :    539.88 MB/s (+/- 2.6 %)    30.96 Mfloat/s      32.30 ns/f
    FastDoubleParserFromByteArray  :    590.93 MB/s (+/- 2.3 %)    33.89 Mfloat/s      29.50 ns/f
    Double                         :     93.68 MB/s (+/- 3.3 %)     5.37 Mfloat/s     186.22 ns/f
    
    Speedup FastDoubleParser              vs Double: 5.26
    Speedup FastDoubleParserFromCharArray vs Double: 5.76
    Speedup FastDoubleParserFromByteArray vs Double: 6.31

'

    parsing numbers in file data/canada.txt
    read 111126 lines
    [...]
    FastDoubleParser               :    365.04 MB/s (+/- 3.6 %)    20.95 Mfloat/s      47.73 ns/f
    FastDoubleParserFromCharArray  :    491.46 MB/s (+/- 1.0 %)    28.24 Mfloat/s      35.41 ns/f
    FastDoubleParserFromByteArray  :    526.39 MB/s (+/- 4.1 %)    30.17 Mfloat/s      33.14 ns/f
    Double                         :     83.38 MB/s (+/- 4.1 %)     4.78 Mfloat/s     209.07 ns/f
    
    Speedup FastDoubleParser              vs Double: 4.38
    Speedup FastDoubleParserFromCharArray vs Double: 5.89
    Speedup FastDoubleParserFromByteArray vs Double: 6.31

FastDoubleParser also speeds up parsing of hexadecimal float literals:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
    x86_64, Mac OS X, 12.1, 12
    OpenJDK 64-Bit Server VM, Oracle Corporation, 18-ea+30-2029
    -XX:+UnlockExperimentalVMOptions

    parsing numbers in file data/minusOneToOne_hexfloats.txt
    read 100000 lines
    [...]
    FastDoubleParser               :    481.78 MB/s (+/- 2.0 %)    26.41 Mfloat/s      37.87 ns/f
    FastDoubleParserFromCharArray  :    662.23 MB/s (+/- 2.4 %)    36.29 Mfloat/s      27.56 ns/f
    FastDoubleParserFromByteArray  :    676.81 MB/s (+/- 4.9 %)    36.99 Mfloat/s      27.03 ns/f
    Double                         :     52.61 MB/s (+/- 3.7 %)     2.88 Mfloat/s     347.15 ns/f
    
    Speedup FastDoubleParser              vs Double: 9.16
    Speedup FastDoubleParserFromCharArray vs Double: 12.59
    Speedup FastDoubleParserFromByteArray vs Double: 12.86
'

    parsing numbers in file data/canada_hexfloats.txt
    read 111126 lines
    [...]
    FastDoubleParser               :    439.25 MB/s (+/- 2.3 %)    24.07 Mfloat/s      41.54 ns/f
    FastDoubleParserFromCharArray  :    664.98 MB/s (+/- 1.2 %)    36.46 Mfloat/s      27.43 ns/f
    FastDoubleParserFromByteArray  :    690.24 MB/s (+/- 2.1 %)    37.83 Mfloat/s      26.43 ns/f
    Double                         :     52.78 MB/s (+/- 3.5 %)     2.89 Mfloat/s     345.96 ns/f
    
    Speedup FastDoubleParser              vs Double: 8.32
    Speedup FastDoubleParserFromCharArray vs Double: 12.60
    Speedup FastDoubleParserFromByteArray vs Double: 13.08

Please note that the performance gains depend a lot on the shape of the input data. Below are two test sets that are
less favorable for the current implementation of the code:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256
    x86_64, Mac OS X, 12.1, 12
    OpenJDK 64-Bit Server VM, Oracle Corporation, 18-ea+30-2029
    -XX:+UnlockExperimentalVMOptions

    parsing numbers in data/shorts.txt
    read 100000 lines
    [...]
    FastDoubleParser               :    204.30 MB/s (+/- 2.5 %)    41.45 Mfloat/s      24.12 ns/f
    FastDoubleParserFromCharArray  :    233.69 MB/s (+/- 5.2 %)    47.30 Mfloat/s      21.14 ns/f
    FastDoubleParserFromByteArray  :    247.34 MB/s (+/- 4.3 %)    50.11 Mfloat/s      19.96 ns/f
    Double                         :    143.53 MB/s (+/- 5.2 %)    29.04 Mfloat/s      34.44 ns/f
    
    Speedup FastDoubleParser              vs Double: 1.42
    Speedup FastDoubleParserFromCharArray vs Double: 1.63
    Speedup FastDoubleParserFromByteArray vs Double: 1.72

'

    parsing numbers in file data/FastDoubleParser_errorcases.txt
    read 26916 lines
    [...]
    FastDoubleParser               :     85.64 MB/s (+/- 4.1 %)     2.66 Mfloat/s     375.80 ns/f
    FastDoubleParserFromCharArray  :     94.34 MB/s (+/- 4.1 %)     2.93 Mfloat/s     341.23 ns/f
    FastDoubleParserFromByteArray  :     97.58 MB/s (+/- 4.4 %)     3.03 Mfloat/s     329.97 ns/f
    Double                         :    103.01 MB/s (+/- 4.5 %)     3.20 Mfloat/s     312.55 ns/f
    
    Speedup FastDoubleParser              vs Double: 0.83
    Speedup FastDoubleParserFromCharArray vs Double: 0.92
    Speedup FastDoubleParserFromByteArray vs Double: 0.95

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
    netlib                                  :   284.91 MB/s (+/- 4.0 %)    13.58 Mfloat/s      73.64 ns/f
    doubleconversion                        :   266.46 MB/s (+/- 1.5 %)    12.70 Mfloat/s      78.74 ns/f
    strtod                                  :    88.05 MB/s (+/- 2.2 %)     4.20 Mfloat/s     238.29 ns/f
    abseil                                  :   516.76 MB/s (+/- 2.5 %)    24.63 Mfloat/s      40.60 ns/f
    fastfloat                               :   948.14 MB/s (+/- 5.6 %)    45.19 Mfloat/s      22.13 ns/f

    FastDoubleParser                        :   492.76 MB/s (+/- 6.6 %)    28.11 Mfloat/s      35.57 ns/f
    FastDoubleParserFromCharArray           :   539.88 MB/s (+/- 2.6 %)    30.96 Mfloat/s      32.30 ns/f
    FastDoubleParserFromByteArray           :   590.93 MB/s (+/- 2.3 %)    33.89 Mfloat/s      29.50 ns/f
    Double                                  :    93.68 MB/s (+/- 3.3 %)     5.37 Mfloat/s     186.22 ns/f

'

    $ ./build/benchmarks/benchmark -f data/canada.txt
    # read 111126 lines
    volume = 1.93374 MB
    netlib                                  :   302.39 MB/s (+/- 4.5 %)    17.38 Mfloat/s      57.55 ns/f
    doubleconversion                        :   268.79 MB/s (+/- 3.5 %)    15.45 Mfloat/s      64.74 ns/f
    strtod                                  :    76.92 MB/s (+/- 4.1 %)     4.42 Mfloat/s     226.22 ns/f
    abseil                                  :   482.49 MB/s (+/- 4.3 %)    27.73 Mfloat/s      36.07 ns/f
    fastfloat                               :   827.73 MB/s (+/- 4.4 %)    47.57 Mfloat/s      21.02 ns/f 

    FastDoubleParser                        :   365.04 MB/s (+/- 3.6 %)    20.95 Mfloat/s      47.73 ns/f
    FastDoubleParserFromCharArray           :   491.46 MB/s (+/- 1.0 %)    28.24 Mfloat/s      35.41 ns/f
    FastDoubleParserFromByteArray           :   526.39 MB/s (+/- 4.1 %)    30.17 Mfloat/s      33.14 ns/f
    Double                                  :    83.38 MB/s (+/- 4.1 %)     4.78 Mfloat/s     209.07 ns/f
