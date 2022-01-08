/*
 * @(#)SystemInfo.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparserdemo;

import jdk.incubator.vector.IntVector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;

public class SystemInfo {
    /**
     * Don't let anyone instantiate this class.
     */
    private SystemInfo() {
    }

    static String getCpuInfo() {
        final Runtime rt = Runtime.getRuntime();
        final StringBuilder buf = new StringBuilder();

        final String osName = System.getProperty("os.name").toLowerCase();
        final String[] cmd;
        if (osName.startsWith("mac")) {
            cmd = new String[]{"sysctl", "-n", "machdep.cpu.brand_string"};
        } else if (osName.startsWith("win")) {
            cmd = new String[]{"wmic", "cpu", "get", "name"};
        } else if (osName.startsWith("linux")) {
            cmd = null;
            try {
                Optional<String> matchedLine = Files.lines(Path.of("/proc/cpuinfo"))
                        .filter(l -> l.startsWith("model name") && l.contains(": "))
                        .map(l -> l.substring(l.indexOf(':') + 2))
                        .findAny();
                buf.append(matchedLine.orElse("Unknown Processor"));
            } catch (IOException e) {
                buf.append("Unknown Processor");
            }
        } else {
            cmd = null;
            buf.append("Unknown Processor");
        }
        if (cmd != null) {
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(rt.exec(cmd).getInputStream()))) {
                for (String line = in.readLine(); line != null; line = in.readLine()) {
                    buf.append(line);
                }
            } catch (final IOException ex) {
                return ex.getMessage();
            }
        }

        buf.append(", ").append(IntVector.SPECIES_PREFERRED.vectorBitSize()).append("-bit SIMD");

        return buf.toString();
    }

    static String getOsInfo() {
        final OperatingSystemMXBean mxbean = ManagementFactory.getOperatingSystemMXBean();
        return mxbean.getArch() + ", " + mxbean.getName() + ", " + mxbean.getVersion() + ", " + mxbean.getAvailableProcessors();
    }


    static String getRtInfo() {
        final RuntimeMXBean mxbean = ManagementFactory.getRuntimeMXBean();
        return mxbean.getVmName()
                + ", " + mxbean.getVmVendor()
                + ", " + mxbean.getVmVersion()
                + "\n" + mxbean.getInputArguments().stream()
                .filter(str -> str.startsWith("-XX:"))
                .collect(Collectors.joining(", "));
    }

    static String getSystemSummary() {
        return SystemInfo.getCpuInfo() +
                "\n" +
                SystemInfo.getOsInfo() +
                "\n" +
                SystemInfo.getRtInfo();
    }
}
