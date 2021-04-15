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

On my Mac mini (2018) I get the results shown below. FastDoubleParser.parseDouble() is up to 4 times faster than
Double.parseDouble(). If your input is a byte array with characters in ISO-8859-1, ASCII or UTF-8 encoding you can use
FastDoubleParserFromByteArray.parseDouble() which is up to 6 times faster than Double.parseDouble().

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    OpenJDK 64-Bit Server VM, Oracle Corporation, 16+36-2231

    parsing random numbers in the range [0,1)
    Trying to reach a confidence level of 98.0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg: 326.160054, stdev: ±31.29, conf98.0%: ±5.25
    FastDoubleParserFromByteArray  MB/s avg: 501.993346, stdev: ±52.28, conf98.0%: ±8.78
    Double                         MB/s avg: 81.105124, stdev: ±8.53, conf98.0%: ±1.43
    Speedup FastDoubleParser              vs Double: 4.02
    Speedup FastDoubleParserFromByteArray vs Double: 6.19

    parsing numbers in file data/canada.txt
    read 111126 lines
    Trying to reach a confidence level of 98,0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg: 333,739272, stdev: ±29,07, conf98,0%: ±4,88
    FastDoubleParserFromByteArray  MB/s avg: 413,799896, stdev: ±37,49, conf98,0%: ±6,29
    Double                         MB/s avg: 79,596150, stdev: ±8,17, conf98,0%: ±1,37
    Speedup FastDoubleParser              vs Double: 4,19
    Speedup FastDoubleParserFromByteArray vs Double: 5,20



FastDoubleParser also speeds up parsing of hexadecimal float literals:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    OpenJDK 64-Bit Server VM, Oracle Corporation, 16+36-2231

    parsing numbers in file data/minusOneToOne_hexfloats.txt
    read 100000 lines
    Trying to reach a confidence level of 98,0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg: 296,625012, stdev: ±23,01, conf98,0%: ±4,73
    FastDoubleParserFromByteArray  MB/s avg: 472,163855, stdev: ±36,25, conf98,0%: ±7,45
    Double                         MB/s avg: 45,257612, stdev: ±3,87, conf98,0%: ±0,80
    Speedup FastDoubleParser              vs Double: 6,55
    Speedup FastDoubleParserFromByteArray vs Double: 10,43

    parsing numbers in file data/canada_hexfloats.txt
    read 111126 lines
    Trying to reach a confidence level of 98,0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg: 317,536089, stdev: ±23,00, conf98,0%: ±4,73
    FastDoubleParserFromByteArray  MB/s avg: 470,532405, stdev: ±35,86, conf98,0%: ±7,37
    Double                         MB/s avg: 45,145076, stdev: ±4,03, conf98,0%: ±0,83
    Speedup FastDoubleParser              vs Double: 7,03
    Speedup FastDoubleParserFromByteArray vs Double: 10,42

Please note that the performance gains depend a lot on the shape of the input
data. Below are two test sets that are less favorable for the current implementation
of the code:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    OpenJDK 64-Bit Server VM, Oracle Corporation, 16+36-2231

    parsing numbers in data/shorts.txt
    read 100000 lines
    Trying to reach a confidence level of 98,0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg: 143,258115, stdev: ±19,81, conf98,0%: ±2,58
    FastDoubleParserFromByteArray  MB/s avg: 167,969568, stdev: ±23,10, conf98,0%: ±3,00
    Double                         MB/s avg: 96,505807, stdev: ±14,23, conf98,0%: ±1,85
    Speedup FastDoubleParser              vs Double: 1,48
    Speedup FastDoubleParserFromByteArray vs Double: 1,74


    parsing numbers in file data/FastDoubleParser_errorcases.txt
    read 26916 lines
    Trying to reach a confidence level of 98,0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg: 77,284029, stdev: ±9,33, conf98,0%: ±1,57
    FastDoubleParserFromByteArray  MB/s avg: 82,390297, stdev: ±9,32, conf98,0%: ±1,57
    Double                         MB/s avg: 91,186581, stdev: ±9,99, conf98,0%: ±1,68
    Speedup FastDoubleParser              vs Double: 0,85
    Speedup FastDoubleParserFromByteArray vs Double: 0,90

## JVM tweaks

Disabling the Compact Strings feature with the option `-XX:-CompactStrings` may improve the performance of the parser,
because this affects the performance of the String.charAt(index) method:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    OpenJDK 64-Bit Server VM, Oracle Corporation, 16+36-2231

    parsing random numbers in the range [0,1)
    Trying to reach a confidence level of 98.0 % which only deviates by 2 % from the average measured duration.
    === number of trials … =====
    FastDoubleParser               MB/s avg: 405.695648, stdev: ±50.29, conf98.0%: ±8.44
    FastDoubleParserFromByteArray  MB/s avg: 469.423160, stdev: ±49.12, conf98.0%: ±8.25
    Double                         MB/s avg: 86.056767, stdev: ±9.80, conf98.0%: ±1.64
    Speedup FastDoubleParser              vs Double: 4.71
    Speedup FastDoubleParserFromByteArray vs Double: 5.45
  

