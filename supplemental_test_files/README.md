# Supplemental test files

This directory contains supplemental files for testing floating point number parsers.

I have copied these files from the following repositories:

* nigeltao: parse-number-fxx-test-data
  <br>https://github.com/nigeltao/parse-number-fxx-test-data

* fastfloat: supplemental_test_files
  <br>https://github.com/fastfloat/supplemental_test_files

## License

The files in this folder are Copyright © Nigel Tao, Apache License Version 2.0.

## File format

Each line has the following format:

    float16 bits (4 hex digits)
    │    float32 bits (8 hex digits)
    │    │        float64 bits (16 hex digits)
    │    │        │                input string
    ↓    ↓        ↓                ↓
    0000 00000000 0000000000000000 .0
