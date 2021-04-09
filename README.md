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

1. Install Java JDK 8 or higher, for example [OpenJDK.](https://jdk.java.net/16/)
2. Install the XCode command line tools from Apple.
3. Open the Terminal and execute the following commands: 


Command:

     git clone https://github.com/wrandelshofer/FastDoubleParser.git
     cd FastDoubleParser 
     javac -d out -encoding utf8 -sourcepath src/main/java test/main/java/org/fastdoubleparser/parser/FastDoubleParserBenchmark.java 
     java -classpath out ch.randelshofer.fastdoubleparser.FastDoubleParserBenchmark 
     java -classpath out ch.randelshofer.fastdoubleparser.FastDoubleParserBenchmark data/canada.txt

On my Mac mini (2018) I get the following results:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    OpenJDK 64-Bit Server VM, Oracle Corporation, 16+36-2231

    parsing random numbers in the range [0,1)
    === warmup 1000 times =====
    === number of trials 32 =====
    FastDoubleParser               MB/s avg: 351.624440, min: 342.72, max: 355.81
    FastDoubleParserFromByteArray  MB/s avg: 548.781224, min: 516.85, max: 559.28
    Double                         MB/s avg: 85.386048, min: 79.06, max: 87.35
    Speedup FastDoubleParser              vs Double: 4.118055
    Speedup FastDoubleParserFromByteArray vs Double: 6.427060

    parsing numbers in file data/canada.txt
    read 111126 lines
    === warmup 1000 times =====
    === number of trials 32 =====
    FastDoubleParser               MB/s avg: 341.740221, min: 303.93, max: 366.63
    FastDoubleParserFromByteArray  MB/s avg: 460.556911, min: 405.39, max: 481.79
    Double                         MB/s avg: 80.854956, min: 68.74, max: 85.77
    Speedup FastDoubleParser              vs Double: 4.226583
    Speedup FastDoubleParserFromByteArray vs Double: 5.696088

FastDoubleParser also speeds up parsing of hexadecimal float literals:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    OpenJDK 64-Bit Server VM, Oracle Corporation, 16+36-2231

    parsing numbers in file data/minusOneToOne_hexfloats.txt
    read 100000 lines
    === number of trials 32 =====
    FastDoubleParser               MB/s avg: 326.090217, min: 243.24, max: 354.16
    FastDoubleParserFromByteArray  MB/s avg: 534.037476, min: 368.69, max: 571.57
    Double                         MB/s avg: 44.642579, min: 27.15, max: 50.89
    Speedup FastDoubleParser              vs Double: 7.304466
    Speedup FastDoubleParserFromByteArray vs Double: 11.962514

    parsing numbers in file data/canada_hexfloats.txt
    read 111126 lines
    === number of trials 32 =====
    FastDoubleParser               MB/s avg: 343.409712, min: 275.95, max: 383.22
    FastDoubleParserFromByteArray  MB/s avg: 561.691464, min: 451.36, max: 617.61
    Double                         MB/s avg: 44.749041, min: 30.28, max: 51.08
    Speedup FastDoubleParser              vs Double: 7.674125
    Speedup FastDoubleParserFromByteArray vs Double: 12.552034

Please note that the performance gains depend a lot on the shape of the input
data. Below are two test sets that are less favorable for the current implementation
of the code:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    OpenJDK 64-Bit Server VM, Oracle Corporation, 16+36-2231

    parsing numbers in data/shorts.txt
    read 100000 lines
    === warmup 1000 times =====
    === number of trials 32 =====
    FastDoubleParser               MB/s avg: 191.784259, min: 113.94, max: 198.19
    FastDoubleParserFromByteArray  MB/s avg: 221.088167, min: 168.39, max: 228.36
    Double                         MB/s avg: 135.928399, min: 73.02, max: 144.00
    Speedup FastDoubleParser              vs Double: 1.410921
    Speedup FastDoubleParserFromByteArray vs Double: 1.626505


    parsing numbers in file data/FastDoubleParser_errorcases.txt
    read 26916 lines
    === warmup 1000 times =====
    === number of trials 32 =====
    FastDoubleParser               MB/s avg: 84.076217, min: 72.19, max: 87.22
    FastDoubleParserFromByteArray  MB/s avg: 89.844292, min: 79.29, max: 92.94
    Double                         MB/s avg: 96.746817, min: 31.57, max: 102.75
    Speedup FastDoubleParser              vs Double: 0.869033
    Speedup FastDoubleParserFromByteArray vs Double: 0.928654
