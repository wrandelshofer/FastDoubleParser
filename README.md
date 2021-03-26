# FastDoubleParser

A straight forward C++ to Java port of Daniel Lemire's fast_double_parser.

https://github.com/lemire/fast_double_parser

Usage:

    import FastDoubleParser;

    double d = FastDoubleParser.parseDouble("1.2345");

Note: Method parseDouble takes a CharacterSequence as its argument. So, if you have a text inside of a StringBuffer, you
do not need to convert it to a String, because StringBuffer extends from CharacterSequence.

There is a performance test in class FastDoubleParserTest.performanceTest.

How to run the performance test on a Mac:

1. Install Java JDK 16 from [OpenJDK.](https://jdk.java.net/16/)
2. Install the XCode command line tools from Apple.
3. Open the Terminal and execute the following commands: 


Command:

     git clone https://github.com/wrandelshofer/FastDoubleParser.git
     cd FastDoubleParser 
     javac -d out -encoding utf8 -sourcepath src/main/java test/main/java/org/fastdoubleparser/parser/FastDoubleParserBenchmark.java 
     java -classpath out org.fastdoubleparser.parser.FastDoubleParserBenchmark 
     java -classpath out org.fastdoubleparser.parser.FastDoubleParserBenchmark data/canada.txt

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
    FastDoubleParser.parseDouble  MB/s avg: 327.656597, min: 238.82, max: 386.00
    Double.parseDouble            MB/s avg: 70.606008, min: 47.66, max: 85.86
    Speedup FastDoubleParser vs Double: 4.640633

