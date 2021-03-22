# FastDoubleParser

A straight forward C++ to Java port of Daniel Lemires fast_double_parser.

https://github.com/lemire/fast_double_parser

Usage:

    import ch.randelshofer.math.FastDoubleParser;

    double d = FastDoubleParser.parseDouble("1.2345");

Note: Method parseDouble takes a CharacterSequence as its argument. So, if you have a text inside of a StringBuffer, you
do not need to convert it to a String, because StringBuffer extends from CharacterSequence.

There is a performance test in class FastDoubleParserTest.performanceTest.

How to run the performance test on a Mac:

1. Install Java JDK 16 from [OpenJDK.](https://jdk.java.net/16/)
2. Install the XCode command line tools from Apple.
3. Open the Terminal and execute the following commands: 


    git clone https://github.com/wrandelshofer/FastDoubleParser.git
    cd FastDoubleParser javac -d out -sourcepath src test/ch/randelshofer/math/FastDoubleParserBenchmark.java 
    java -classpath out ch.randelshofer.math.FastDoubleParserBenchmark 
    java -classpath out ch.randelshofer.math.FastDoubleParserBenchmark data/canada.txt

On my Mac mini (2018) I get the following results:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    OpenJDK 64-Bit Server VM, Oracle Corporation, 16+36-2231

    parsing random integers in the range [0,1)
    === number of trials 32 =====
    FastDoubleParser.parseDouble  MB/s avg: 388.873390, min: 331.77, max: 418.60
    Double.parseDouble            MB/s avg: 84.108783, min: 77.34, max: 90.35
    Speedup FastDoubleParser vs Double: 4.623458

    parsing integers in file /Users/Shared/Developer/Java/FastDoubleParser/github/FastDoubleParser/data/canada.txt
    === number of trials 32 =====
    FastDoubleParser.parseDouble  MB/s avg: 276.744401, min: 174.85, max: 328.97
    Double.parseDouble            MB/s avg: 68.303959, min: 43.39, max: 85.70
    Speedup FastDoubleParser vs Double: 4.051660

