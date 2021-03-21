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
    cd FastDoubleParser 
    javac -d out -sourcepath src test/ch/randelshofer/math/FastDoubleParserBenchmark.java
    java -classpath out ch.randelshofer.math.FastDoubleParserBenchmark

On my Mac mini (2018), 3.2 GHz, 6-Core Intel Core i7 I get the following results:

    Double.parseDouble:
    LongSummaryStatistics{count=32, sum=1557205175, min=38329997, average=48662661.718750, max=137904316}
    486.6266171875ns per double

    FastDoubleParser.parseDouble:
    LongSummaryStatistics{count=32, sum=283691480, min=6561467, average=8865358.750000, max=51711911}
    88.6535875ns per double

This shows that FastDoubleParser.parseDouble is roughly 5.5 times faster than Double.parseDouble.