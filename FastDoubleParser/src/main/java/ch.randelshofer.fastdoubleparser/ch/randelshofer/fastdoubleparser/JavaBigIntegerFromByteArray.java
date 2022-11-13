package ch.randelshofer.fastdoubleparser;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

class JavaBigIntegerFromByteArray {
    private final static BigInteger TEN_POW_16 = BigInteger.valueOf(10000000000000000L);
    /**
     * Threshold for single-threaded algorithm vs. multi-threaded algorithm.
     * <p>
     * Set this to {@link Integer#MAX_VALUE} if you only want to use
     * the single-threaded algorithm.
     * <p>
     * Set this to {@code 0} if you only want to use the multi-threaded
     * algorithm.
     * <p>
     * Rationale for choosing a specific threshold value:
     * We speculate that we need to perform at least 10,000 CPU cycles
     * before it is worth using multiple threads.
     */
    public final static int PARALLEL_THRESHOLD = 1024;

    public BigInteger parseBigIntegerLiteral(byte[] str, int offset, int length) {
        final int endIndex = offset + length;
        if (offset < 0 || endIndex > str.length) {
            return null;
        }
        // Parse optional sign
        // -------------------
        int index = offset;
        byte ch = str[index];
        final boolean isNegative = ch == '-';
        if (isNegative || ch == '+') {
            ch = ++index < endIndex ? str[index] : 0;
            if (ch == 0) {
                return null;
            }
        }

        // Parse optional leading zero
        // ---------------------------
        final boolean hasLeadingZero = ch == '0';
        if (hasLeadingZero) {
            ch = index + 1 < endIndex ? str[index + 1] : 0;
            if (ch == 'x' || ch == 'X') {
                return parseHexBigIntegerLiteral(str, index + 2, endIndex, isNegative);
            }
        }

        // not yet implemented
        return parseDecBigIntegerLiteral(str, index, endIndex, isNegative);
    }

    private BigInteger parseDecBigIntegerLiteral(byte[] str, int from, int to, boolean isNegative) {
        Map<Integer, BigInteger> powersOfTen = fillPowersOfTen(from, to);
        int numDigits = to - from;
        BigInteger result;
        if (numDigits < PARALLEL_THRESHOLD) {
            result = parseDigitsRecursive(str, from, to, powersOfTen);
        } else {
            result = new ParseDigitsTask(from, to, str, powersOfTen).compute();
        }
        return isNegative ? result.negate() : result;
    }

    private static Map<Integer, BigInteger> fillPowersOfTen(int from, int to) {
        Map<Integer, BigInteger> powersOfTen = new HashMap<>();
        powersOfTen.put(16, TEN_POW_16);
        fillPowersOfTenRecursive(powersOfTen, from, to);
        return powersOfTen;
    }

    private static void fillPowersOfTenRecursive(Map<Integer, BigInteger> powersOfTen, int from, int to) {
        int numDigits = to - from;
        // base case:
        if (numDigits <= 18) {
            return;
        }
        // recursion case:
        int mid = split(from, to);
        int raise = to - mid;
        if (!powersOfTen.containsKey(raise)) {
            fillPowersOfTenRecursive(powersOfTen, from, mid);
            fillPowersOfTenRecursive(powersOfTen, mid, to);
            BigInteger pow;
            BigInteger prev = powersOfTen.get(raise - 16);
            if (prev != null) {
                pow = prev.parallelMultiply(TEN_POW_16);
            } else {
                BigInteger half = powersOfTen.get(raise >> 1);
                if (half != null) {
                    pow = half.parallelMultiply(half);
                } else {
                    pow = TEN_POW_16.pow(raise >> 4);
                }
            }
            powersOfTen.put(raise, pow);
        }

    }

    private static BigInteger parseDigitsRecursive(byte[] str, int from, int to, Map<Integer, BigInteger> powersOfTen) {
        // Base case: All sequences of 18 or fewer digits fit into a long.
        int numDigits = to - from;
        if (numDigits <= 18) {
            return parseDigitsUpTo18(str, from, to);
        }

        // Recursion case: Sequences of more than 18 digits do not fit into a long.
        int mid = split(from, to);
        BigInteger high = parseDigitsRecursive(str, from, mid, powersOfTen);
        BigInteger low = parseDigitsRecursive(str, mid, to, powersOfTen);

        high = raiseByPowerOfTen(high, to - mid, powersOfTen);
        return low.add(high);
    }

    private static BigInteger raiseByPowerOfTen(BigInteger high, int raise, Map<Integer, BigInteger> powersOfTen) {
        BigInteger powerOfTen = powersOfTen.get(raise);
        high = high.multiply(powerOfTen);
        return high;
    }

    private static BigInteger parallelRaiseByPowerOfTen(BigInteger high, int raise, Map<Integer, BigInteger> powersOfTen) {
        BigInteger powerOfTen = powersOfTen.get(raise);
        high = high.parallelMultiply(powerOfTen);
        return high;
    }


    static BigInteger parseDigitsUpTo18(byte[] str, int from, int to) {
        int numDigits = to - from;
        int significand = 0;
        int prerollLimit = from + (numDigits & 7);
        for (; from < prerollLimit; from++) {
            significand = 10 * (significand) + str[from] - '0';
        }
        long significandL = significand;
        for (; from < to; from += 8) {
            significandL = significandL * 100_000_000L + FastDoubleSwar.parseEightDigitsUtf8(str, from);
        }
        return BigInteger.valueOf(significandL);
    }

    private BigInteger parseHexBigIntegerLiteral(byte[] str, int from, int to, boolean isNegative) {
        int numDigits = to - from;
        if (numDigits == 0) {
            return null;
        }
        byte[] bytes = new byte[((numDigits + 1) >> 1) + 1];
        int index = 1;

        if ((numDigits & 1) != 0) {
            byte chLow = str[from++];
            int valueLow = chLow < 0 ? AbstractFloatValueParser.OTHER_CLASS : AbstractFloatValueParser.CHAR_TO_HEX_MAP[chLow];
            if (valueLow >= 0) {
                bytes[index++] = (byte) valueLow;
            } else {
                return null;
            }
        }
        int prerollLimit = from + ((to - from) & 7);
        for (; from < prerollLimit; from += 2) {
            byte chHigh = str[from];
            byte chLow = str[from + 1];
            int valueHigh = chHigh < 0 ? AbstractFloatValueParser.OTHER_CLASS : AbstractFloatValueParser.CHAR_TO_HEX_MAP[chHigh];
            int valueLow = chLow < 0 ? AbstractFloatValueParser.OTHER_CLASS : AbstractFloatValueParser.CHAR_TO_HEX_MAP[chLow];
            if (valueHigh >= 0 && valueLow >= 0) {
                bytes[index++] = (byte) (valueHigh << 4 | valueLow);
            } else {
                return null;
            }
        }
        for (; from < to; from += 8, index += 4) {
            long value = FastDoubleSwar.tryToParseEightHexDigitsUtf8(str, from);
            if (value >= 0) {
                FastDoubleSwar.readIntBE.set(bytes, index, (int) value);
            } else {
                return null;
            }
        }

        BigInteger result = (bytes[1] < 0)
                ? new BigInteger(bytes, 0, bytes.length)
                : new BigInteger(bytes, 1, bytes.length - 1);

        return isNegative ? result.negate() : result;
    }

    /**
     * Parses digits using a multi-threaded recursive algorithm in O(n)
     * with a large constant overhead if there are less than about 1000
     * digits.
     */
    static class ParseDigitsTask extends RecursiveTask<BigInteger> {
        private final int from, to;
        private final byte[] str;
        private final Map<Integer, BigInteger> powersOfTen;

        ParseDigitsTask(int from, int to, byte[] str, Map<Integer, BigInteger> powersOfTen) {
            this.from = from;
            this.to = to;
            this.str = str;
            this.powersOfTen = powersOfTen;
        }

        protected BigInteger compute() {
            int range = to - from;
            // Base case:
            if (range <= PARALLEL_THRESHOLD) {
                return parseDigitsRecursive(str, from, to, powersOfTen);
            }

            // Recursion case:
            int mid = split(from, to);
            ParseDigitsTask high = new ParseDigitsTask(from, mid, str, powersOfTen);
            ParseDigitsTask low = new ParseDigitsTask(mid, to, str, powersOfTen);
            // perform about half the work locally
            high.fork();
            BigInteger lowValue = low.compute();
            BigInteger highValue = parallelRaiseByPowerOfTen(high.join(), to - mid, powersOfTen);
            return lowValue.add(highValue);
        }

    }

    private static int split(int from, int to) {
        int mid = (from + to) >>> 1;// split in half
        mid = to - (((to - mid + 15) >> 4) << 4);// make numDigits of low a multiple of 16
        return mid;
    }

}
