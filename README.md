# FastDoubleParser

A straight forward C++ to Java port of Daniel Lemire's fast_double_parser.

https://github.com/lemire/fast_double_parser

Usage:

    import FastDoubleParser;

    double d = FastDoubleParser.parseDouble("1.2345");

Note: Method parseDouble takes a CharacterSequence as its argument. So, if you have a text inside of a StringBuffer, you
do not need to convert it to a String, because StringBuffer extends from CharacterSequence.

The test directory contains some functional tests, and some performance tests.

How to run the performance test on a Mac:

1. Install Java JDK 11 or higher, for example [OpenJDK.](https://jdk.java.net/16/)
2. Install the XCode command line tools from Apple.
3. Open the Terminal and execute the following commands:

Command:

     git clone https://github.com/wrandelshofer/FastDoubleParser.git
     cd FastDoubleParser 
     javac -d out -encoding utf8 -sourcepath src/main/java:test/main/java test/main/java/ch/randelshofer/fastdoubleparser/FastDoubleParserBenchmark.java 
     java -classpath out ch.randelshofer.fastdoubleparser.FastDoubleParserBenchmark 
     java -classpath out ch.randelshofer.fastdoubleparser.FastDoubleParserBenchmark data/canada.txt

On my Mac mini (2018) I get the results shown below. The results vary on the JVM and platform being used.
FastDoubleParser.parseDouble() is roughly 4.5 times faster than Double.parseDouble().

If your input is a char array or a byte array you can use FastDoubleParserFromCharArray.parseDouble() or
FastDoubleParserFromByteArray.parseDouble() which are even slightly faster.

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    OpenJDK 64-Bit Server VM, Oracle Corporation, 17+35-2724
    
    parsing random numbers in the range [0,1)
    Trying to reach a confidence level of 98.0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg:  436.79, stdev: ±56.67, conf98.0%: ±9.51
    FastDoubleParserFromCharArray  MB/s avg:  478.50, stdev: ±56.37, conf98.0%: ±9.46
    FastDoubleParserFromByteArray  MB/s avg:  527.01, stdev: ±43.98, conf98.0%: ±7.38
    Double                         MB/s avg:   90.48, stdev: ±10.04, conf98.0%: ±1.69
    
    FastDoubleParser                        :  436.79 MB/s (+/-13.0 %)    24.38 Mfloat/s    41.01 ns/f
    FastDoubleParserFromCharArray           :  478.50 MB/s (+/-11.8 %)    26.62 Mfloat/s    37.57 ns/f
    FastDoubleParserFromByteArray           :  527.01 MB/s (+/- 8.3 %)    29.93 Mfloat/s    33.41 ns/f
    Double                                  :   90.48 MB/s (+/-11.1 %)     5.10 Mfloat/s   195.91 ns/f
    
    Speedup FastDoubleParser              vs Double: 4.83
    Speedup FastDoubleParserFromCharArray vs Double: 5.29
    Speedup FastDoubleParserFromByteArray vs Double: 5.82

    parsing numbers in file data/canada.txt
    read 111126 lines
    Trying to reach a confidence level of 98,0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg:  357.04, stdev: ±28.57, conf98.0%: ±4.80
    FastDoubleParserFromCharArray  MB/s avg:  448.74, stdev: ±39.49, conf98.0%: ±6.63
    FastDoubleParserFromByteArray  MB/s avg:  467.92, stdev: ±32.01, conf98.0%: ±5.37
    Double                         MB/s avg:   80.94, stdev: ±8.28, conf98.0%: ±1.39
    
    FastDoubleParser                        :  357.04 MB/s (+/- 8.0 %)    20.35 Mfloat/s    49.13 ns/f
    FastDoubleParserFromCharArray           :  448.74 MB/s (+/- 8.8 %)    25.47 Mfloat/s    39.26 ns/f
    FastDoubleParserFromByteArray           :  467.92 MB/s (+/- 6.8 %)    26.72 Mfloat/s    37.43 ns/f
    Double                                  :   80.94 MB/s (+/-10.2 %)     4.58 Mfloat/s   218.22 ns/f
    
    Speedup FastDoubleParser              vs Double: 4.41
    Speedup FastDoubleParserFromCharArray vs Double: 5.54
    Speedup FastDoubleParserFromByteArray vs Double: 5.78

FastDoubleParser also speeds up parsing of hexadecimal float literals:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    OpenJDK 64-Bit Server VM, Oracle Corporation, 16+36-2231

    parsing numbers in file data/minusOneToOne_hexfloats.txt
    read 100000 lines
    Trying to reach a confidence level of 98,0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg: 390.681081, stdev: ±45.31, conf98.0%: ±9.32
    FastDoubleParserFromCharArray  MB/s avg: 447.495060, stdev: ±48.82, conf98.0%: ±10.04
    FastDoubleParserFromByteArray  MB/s avg: 528.882824, stdev: ±51.70, conf98.0%: ±10.63
    Double                         MB/s avg: 50.769805, stdev: ±4.14, conf98.0%: ±0.85
    Speedup FastDoubleParser              vs Double: 7.70
    Speedup FastDoubleParserFromCharArray vs Double: 8.81
    Speedup FastDoubleParserFromByteArray vs Double: 10.42

    parsing numbers in file data/canada_hexfloats.txt
    read 111126 lines
    Trying to reach a confidence level of 98,0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg: 411.247838, stdev: ±42.32, conf98.0%: ±8.70
    FastDoubleParserFromCharArray  MB/s avg: 556.745500, stdev: ±59.98, conf98.0%: ±12.33
    FastDoubleParserFromByteArray  MB/s avg: 599.957940, stdev: ±51.17, conf98.0%: ±10.52
    Double                         MB/s avg: 49.896228, stdev: ±3.98, conf98.0%: ±0.82
    Speedup FastDoubleParser              vs Double: 8.24
    Speedup FastDoubleParserFromCharArray vs Double: 11.16
    Speedup FastDoubleParserFromByteArray vs Double: 12.02

Please note that the performance gains depend a lot on the shape of the input
data. Below are two test sets that are less favorable for the current implementation
of the code:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    OpenJDK 64-Bit Server VM, Oracle Corporation, 17+35-2724

    parsing numbers in data/shorts.txt
    read 100000 lines
    Trying to reach a confidence level of 98,0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg: 173.723655, stdev: ±28.41, conf98.0%: ±2.83
    FastDoubleParserFromCharArray  MB/s avg: 212.728590, stdev: ±31.53, conf98.0%: ±3.14
    FastDoubleParserFromByteArray  MB/s avg: 218.875765, stdev: ±28.72, conf98.0%: ±2.86
    Double                         MB/s avg: 126.733727, stdev: ±25.08, conf98.0%: ±2.50
    Speedup FastDoubleParser              vs Double: 1.37
    Speedup FastDoubleParserFromCharArray vs Double: 1.68
    Speedup FastDoubleParserFromByteArray vs Double: 1.73
    


    parsing numbers in file data/FastDoubleParser_errorcases.txt
    read 26916 lines
    Trying to reach a confidence level of 98,0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg: 84.439092, stdev: ±9.11, conf98.0%: ±1.68
    FastDoubleParserFromCharArray  MB/s avg: 87.925803, stdev: ±9.42, conf98.0%: ±1.73
    FastDoubleParserFromByteArray  MB/s avg: 90.087969, stdev: ±9.17, conf98.0%: ±1.69
    Double                         MB/s avg: 98.183157, stdev: ±10.59, conf98.0%: ±1.95
    Speedup FastDoubleParser              vs Double: 0.86
    Speedup FastDoubleParserFromCharArray vs Double: 0.90
    Speedup FastDoubleParserFromByteArray vs Double: 0.92

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

    FastDoubleParser                        :   436.79 MB/s (+/-13.0 %)    24.38 Mfloat/s      41.01 ns/f
    FastDoubleParserFromCharArray           :   478.50 MB/s (+/-11.8 %)    26.62 Mfloat/s      37.57 ns/f
    FastDoubleParserFromByteArray           :   527.01 MB/s (+/- 8.3 %)    29.93 Mfloat/s      33.41 ns/f
    Double                                  :    90.48 MB/s (+/-11.1 %)     5.10 Mfloat/s     195.91 ns/f


    $ ./build/benchmarks/benchmark -f data/canada.txt
    # read 111126 lines
    volume = 1.93374 MB
    netlib                                  :   302.39 MB/s (+/- 4.5 %)    17.38 Mfloat/s      57.55 ns/f
    doubleconversion                        :   268.79 MB/s (+/- 3.5 %)    15.45 Mfloat/s      64.74 ns/f
    strtod                                  :    76.92 MB/s (+/- 4.1 %)     4.42 Mfloat/s     226.22 ns/f
    abseil                                  :   482.49 MB/s (+/- 4.3 %)    27.73 Mfloat/s      36.07 ns/f
    fastfloat                               :   827.73 MB/s (+/- 4.4 %)    47.57 Mfloat/s      21.02 ns/f 

    FastDoubleParser                        :   357.04 MB/s (+/- 8.0 %)    20.35 Mfloat/s      49.13 ns/f
    FastDoubleParserFromCharArray           :   448.74 MB/s (+/- 8.8 %)    25.47 Mfloat/s      39.26 ns/f
    FastDoubleParserFromByteArray           :   467.92 MB/s (+/- 6.8 %)    26.72 Mfloat/s      37.43 ns/f
    Double                                  :    80.94 MB/s (+/-10.2 %)     4.58 Mfloat/s     218.22 ns/f
