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
     javac -d out -encoding utf8 -sourcepath src/main/java test/main/java/ch/randelshofer/fastdoubleparser/FastDoubleParserBenchmark.java 
     java -classpath out ch.randelshofer.fastdoubleparser.FastDoubleParserBenchmark 
     java -classpath out ch.randelshofer.fastdoubleparser.FastDoubleParserBenchmark data/canada.txt

On my Mac mini (2018) I get the results shown below. FastDoubleParser.parseDouble() is up to 4 times faster than
Double.parseDouble(). If your input is a byte array with characters in ISO-8859-1, ASCII or UTF-8 encoding you can use
FastDoubleParserFromByteArray.parseDouble() which is up to 6 times faster than Double.parseDouble().

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
    === warmup 1000 times =====
    === number of trials 32 =====
    FastDoubleParser               MB/s avg: 374.358458, min: 346.14, max: 401.62
    FastDoubleParserFromByteArray  MB/s avg: 571.240149, min: 538.05, max: 616.63
    Double                         MB/s avg: 47.143733, min: 31.27, max: 52.07
    Speedup FastDoubleParser              vs Double: 7.940789
    Speedup FastDoubleParserFromByteArray vs Double: 12.116990

    parsing numbers in file data/canada_hexfloats.txt
    read 111126 lines
    === warmup 1000 times =====
    === number of trials 32 =====
    FastDoubleParser               MB/s avg: 391.324926, min: 335.17, max: 420.64
    FastDoubleParserFromByteArray  MB/s avg: 601.523250, min: 536.01, max: 659.40
    Double                         MB/s avg: 46.716107, min: 32.31, max: 52.26
    Speedup FastDoubleParser              vs Double: 8.376660
    Speedup FastDoubleParserFromByteArray vs Double: 12.876143

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

## JVM tweaks

Disabling the Compact Strings feature with the option `-XX:-CompactStrings` may improve the performance of the parser,
because this affects the performance of the String.charAt(index) method:

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
    OpenJDK 64-Bit Server VM, Oracle Corporation, 16+36-2231

    parsing random numbers in the range [0,1)
    === warmup 1000 times =====
    === number of trials 32 =====
    FastDoubleParser               MB/s avg: 442.370478, min: 387.23, max: 481.27
    FastDoubleParserFromByteArray  MB/s avg: 506.825698, min: 443.88, max: 559.29
    Double                         MB/s avg: 91.292798, min: 82.55, max: 97.96
    Speedup FastDoubleParser              vs Double: 4.845623
    Speedup FastDoubleParserFromByteArray vs Double: 5.551650
  

