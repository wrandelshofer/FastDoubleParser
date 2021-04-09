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
    FastDoubleParser.parseDouble  MB/s avg: 341.838343, min: 311.14, max: 367.31
    Double.parseDouble            MB/s avg: 82.909605, min: 75.72, max: 89.24
    Speedup FastDoubleParser vs Double: 4.123025

    parsing numbers in file data/canada.txt
    read 111126 lines
    === number of trials 32 =====
    FastDoubleParser.parseDouble  MB/s avg: 319.496176, min: 216.37, max: 365.83
    Double.parseDouble            MB/s avg: 71.851737, min: 46.78, max: 82.76
    Speedup FastDoubleParser vs Double: 4.446603

FastDoubleParser also speeds up parsing of hexadecimal float literals:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    OpenJDK 64-Bit Server VM, Oracle Corporation, 16+36-2231

    parsing numbers in file data/minusOneToOne_hexfloats.txt
    read 100000 lines
    === number of trials 32 =====
    FastDoubleParser.parseDouble   MB/s avg: 350.574900, min: 270.54, max: 403.85
    Double.parseDouble             MB/s avg: 45.100857, min: 28.56, max: 51.61
    Speedup FastDoubleParser vs Double: 7.773132

    parsing numbers in file data/canada_hexfloats.txt
    read 111126 lines
    === number of trials 32 =====
    FastDoubleParser.parseDouble   MB/s avg: 370.487497, min: 267.66, max: 425.03
    Double.parseDouble             MB/s avg: 45.634442, min: 30.91, max: 53.08
    Speedup FastDoubleParser  vs Double: 8.118594

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

