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
FastDoubleParser.parseDouble() is roughly 4 times faster than Double.parseDouble(). If your input is a byte array with
characters in ISO-8859-1, ASCII or UTF-8 encoding you can use FastDoubleParserFromByteArray.parseDouble() which is
roughly 6 times faster than Double.parseDouble().

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    OpenJDK 64-Bit Server VM, Oracle Corporation, 17-ea+33-2705
    
    parsing random numbers in the range [0,1)
    Trying to reach a confidence level of 98.0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg: 435.341486, stdev: ±55.49, conf98.0%: ±8.07
    FastDoubleParserFromByteArray  MB/s avg: 521.370267, stdev: ±47.86, conf98.0%: ±6.96
    Double                         MB/s avg: 89.756653, stdev: ±11.03, conf98.0%: ±1.60
    Speedup FastDoubleParser              vs Double: 4.85
    Speedup FastDoubleParserFromByteArray vs Double: 5.81

    parsing numbers in file data/canada.txt
    read 111126 lines
    Trying to reach a confidence level of 98,0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg: 338.604619, stdev: ±31.76, conf98.0%: ±4.94
    FastDoubleParserFromByteArray  MB/s avg: 462.815049, stdev: ±39.13, conf98.0%: ±6.08
    Double                         MB/s avg: 78.991570, stdev: ±8.92, conf98.0%: ±1.39
    Speedup FastDoubleParser              vs Double: 4.29
    Speedup FastDoubleParserFromByteArray vs Double: 5.86


FastDoubleParser also speeds up parsing of hexadecimal float literals:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    OpenJDK 64-Bit Server VM, Oracle Corporation, 16+36-2231

    parsing numbers in file data/minusOneToOne_hexfloats.txt
    read 100000 lines
    Trying to reach a confidence level of 98,0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg: 373.772432, stdev: ±42.60, conf98.0%: ±8.76
    FastDoubleParserFromByteArray  MB/s avg: 527.835675, stdev: ±39.57, conf98.0%: ±8.14
    Double                         MB/s avg: 48.710004, stdev: ±4.46, conf98.0%: ±0.92
    Speedup FastDoubleParser              vs Double: 7.67
    Speedup FastDoubleParserFromByteArray vs Double: 10.84

    parsing numbers in file data/canada_hexfloats.txt
    read 111126 lines
    Trying to reach a confidence level of 98,0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg: 337,405327, stdev: ±32,30, conf98,0%: ±6,64
    FastDoubleParserFromByteArray  MB/s avg: 478,021478, stdev: ±39,24, conf98,0%: ±8,07
    Double                         MB/s avg: 47,085025, stdev: ±4,34, conf98,0%: ±0,89
    Speedup FastDoubleParser              vs Double: 7,17
    Speedup FastDoubleParserFromByteArray vs Double: 10,15

Please note that the performance gains depend a lot on the shape of the input
data. Below are two test sets that are less favorable for the current implementation
of the code:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    OpenJDK 64-Bit Server VM, Oracle Corporation, 17-ea+33-2705

    parsing numbers in data/shorts.txt
    read 100000 lines
    Trying to reach a confidence level of 98,0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg: 176.329049, stdev: ±20.89, conf98.0%: ±2.30
    FastDoubleParserFromByteArray  MB/s avg: 223.173540, stdev: ±20.11, conf98.0%: ±2.21
    Double                         MB/s avg: 128.639261, stdev: ±21.84, conf98.0%: ±2.40
    Speedup FastDoubleParser              vs Double: 1.37
    Speedup FastDoubleParserFromByteArray vs Double: 1.73
    


    parsing numbers in file data/FastDoubleParser_errorcases.txt
    read 26916 lines
    Trying to reach a confidence level of 98,0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg: 82.988551, stdev: ±8.50, conf98.0%: ±1.43
    FastDoubleParserFromByteArray  MB/s avg: 88.221805, stdev: ±9.93, conf98.0%: ±1.67
    Double                         MB/s avg: 96.613648, stdev: ±10.97, conf98.0%: ±1.84
    Speedup FastDoubleParser              vs Double: 0.86
    Speedup FastDoubleParserFromByteArray vs Double: 0.91

## JVM tweaks

Disabling the Compact Strings feature with the option `-XX:-CompactStrings` may improve the performance of the parser,
because this affects the performance of the String.charAt(index) method:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    OpenJDK 64-Bit Server VM, Oracle Corporation, 17-ea+33-2705

    parsing random numbers in the range [0,1)
    Trying to reach a confidence level of 98.0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg: 433.352974, stdev: ±52.01, conf98.0%: ±8.08
    FastDoubleParserFromByteArray  MB/s avg: 494.257921, stdev: ±42.51, conf98.0%: ±6.61
    Double                         MB/s avg: 90.175554, stdev: ±10.16, conf98.0%: ±1.58
    Speedup FastDoubleParser              vs Double: 4.81
    Speedup FastDoubleParserFromByteArray vs Double: 5.48
  

