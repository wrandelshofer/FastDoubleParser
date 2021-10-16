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
    FastDoubleParser               MB/s avg: 464.690268, stdev: ±47.36, conf98.0%: ±7.95
    FastDoubleParserFromCharArray  MB/s avg: 492.810872, stdev: ±53.89, conf98.0%: ±9.05
    FastDoubleParserFromByteArray  MB/s avg: 527.122961, stdev: ±42.50, conf98.0%: ±7.14
    Double                         MB/s avg: 92.688251, stdev: ±10.05, conf98.0%: ±1.69
    Speedup FastDoubleParser              vs Double: 5.01
    Speedup FastDoubleParserFromCharArray vs Double: 5.32
    Speedup FastDoubleParserFromByteArray vs Double: 5.69

    parsing numbers in file data/canada.txt
    read 111126 lines
    Trying to reach a confidence level of 98,0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg: 369.640510, stdev: ±33.91, conf98.0%: ±6.24
    FastDoubleParserFromCharArray  MB/s avg: 466.286638, stdev: ±40.67, conf98.0%: ±7.48
    FastDoubleParserFromByteArray  MB/s avg: 493.291532, stdev: ±35.01, conf98.0%: ±6.44
    Double                         MB/s avg: 82.690171, stdev: ±8.62, conf98.0%: ±1.59
    Speedup FastDoubleParser              vs Double: 4.47
    Speedup FastDoubleParserFromCharArray vs Double: 5.64
    Speedup FastDoubleParserFromByteArray vs Double: 5.97

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

## JVM tweaks

Disabling the Compact Strings feature with the option `-XX:-CompactStrings` may improve the performance of the parser,
because this affects the performance of the String.charAt(index) method:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    OpenJDK 64-Bit Server VM, Oracle Corporation, 17-ea+33-2705

    parsing random numbers in the range [0,1)
    Trying to reach a confidence level of 98.0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg: 439.846118, stdev: ±67.28, conf98.0%: ±11.30
    FastDoubleParserFromCharArray  MB/s avg: 483.400774, stdev: ±53.33, conf98.0%: ±8.95
    FastDoubleParserFromByteArray  MB/s avg: 506.833120, stdev: ±38.72, conf98.0%: ±6.50
    Double                         MB/s avg: 95.967846, stdev: ±9.87, conf98.0%: ±1.66
    Speedup FastDoubleParser              vs Double: 4.58
    Speedup FastDoubleParserFromCharArray vs Double: 5.04
    Speedup FastDoubleParserFromByteArray vs Double: 5.28
  

