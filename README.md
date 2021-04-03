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

    parsing numbers in file data/0to1_hexfloats.txt
    read 100000 lines
    === number of trials 32 =====
    FastDoubleParser.parseDouble  MB/s avg: 256.321683, min: 204.29, max: 294.19
    Double.parseDouble            MB/s avg: 44.747641, min: 27.28, max: 53.00
    Speedup FastDoubleParser vs Double: 5.728161

    parsing numbers in file data/canada_hexfloats.txt
    read 111126 lines
    === number of trials 32 =====
    FastDoubleParser.parseDouble  MB/s avg: 255.023209, min: 210.57, max: 286.97
    Double.parseDouble            MB/s avg: 45.355415, min: 26.25, max: 52.34
    Speedup FastDoubleParser vs Double: 5.622773

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

