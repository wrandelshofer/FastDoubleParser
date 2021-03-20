# FastDoubleParser

A straight forward C++ to Java port of Daniel Lemires fast_double_parser.

https://github.com/lemire/fast_double_parser

There is a performance test in class FastDoubleParserTest.performanceTest.

On my Mac mini (2018), 3.2 GHz, 3.2 GHz 6-Core Intel Core i7 I get the following results:

    Double.parseDouble:
    LongSummaryStatistics{count=32, sum=1557205175, min=38329997, average=48662661.718750, max=137904316}
    486.6266171875ns per double

    FastDoubleParser.parseDouble:
    LongSummaryStatistics{count=32, sum=283691480, min=6561467, average=8865358.750000, max=51711911}
    88.6535875ns per double

This shows that FastDoubleParser.parseDouble is roughly 5.5 times faster than Double.parseDouble.