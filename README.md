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
    === number of trials 32 =====
    FastDoubleParser               MB/s avg: 349.692124, min: 319.69, max: 365.69
    FastDoubleParserFromByteArray  MB/s avg: 524.594249, min: 488.33, max: 554.92
    Double                         MB/s avg: 83.616813, min: 74.75, max: 87.82
    Speedup FastDoubleParser              vs Double: 4.182079
    Speedup FastDoubleParserFromByteArray vs Double: 6.273789

    parsing numbers in file data/canada.txt
    read 111126 lines
    === number of trials 32 =====
    FastDoubleParser               MB/s avg: 305.171797, min: 240.77, max: 351.33
    FastDoubleParserFromByteArray  MB/s avg: 435.606485, min: 280.09, max: 516.14
    Double                         MB/s avg: 71.206249, min: 42.22, max: 85.21
    Speedup FastDoubleParser              vs Double: 4.285745
    Speedup FastDoubleParserFromByteArray vs Double: 6.117532

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
    === number of trials 32 =====
    FastDoubleParser.parseDouble  MB/s avg: 124.894498, min: 69.98, max: 167.59
    Double.parseDouble            MB/s avg: 88.799489, min: 51.79, max: 122.16
    Speedup FastDoubleParser vs Double: 1.406478


    parsing numbers in file data/FastDoubleParser_errorcases.txt
    read 26916 lines
    === number of trials 32 =====
    FastDoubleParser.parseDouble  MB/s avg: 73.687863, min: 26.99, max: 97.97
    Double.parseDouble            MB/s avg: 81.740633, min: 35.64, max: 109.20
    Speedup FastDoubleParser vs Double: 0.901484

