package ch.randelshofer.fastdoubleparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemInfo {
    /**
     * Don't let anyone instantiate this class.
     */
    private SystemInfo() {
    }

    static String getCpuInfo() {
        final Runtime rt = Runtime.getRuntime();

        final String osName = System.getProperty("os.name").toLowerCase();
        final String cmd;
        if (osName.startsWith("mac")) {
            cmd = "sysctl -n machdep.cpu.brand_string";
        } else if (osName.startsWith("win")) {
            cmd = "wmic cpu get name";
        } else {
            return "Unknown Processor";
        }
        final StringBuilder buf = new StringBuilder();
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(rt.exec(cmd).getInputStream()))) {
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                buf.append(line);
            }
        } catch (final IOException ex) {
            return ex.getMessage();
        }
        return buf.toString();
    }

    static double getGhz() {
        final Pattern pattern = Pattern.compile("([0-9]+\\.[0-9]+)GHz");
        final Matcher matcher = pattern.matcher(getCpuInfo());
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        } else {
            return 2.0;
        }
    }

    static String getOsInfo() {
        final OperatingSystemMXBean mxbean = ManagementFactory.getOperatingSystemMXBean();
        return mxbean.getArch() + ", " + mxbean.getName() + ", " + mxbean.getVersion() + ", " + mxbean.getAvailableProcessors();
    }

    static int getPagesize() {
        final Runtime rt = Runtime.getRuntime();
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(rt.exec("sysctl -n hw.pagesize").getInputStream()))) {
            return Integer.parseInt(in.readLine());
        } catch (final IOException | NumberFormatException ex) {
            return 4096;
        }
    }

    static String getRtInfo() {
        final RuntimeMXBean mxbean = ManagementFactory.getRuntimeMXBean();
        return mxbean.getVmName() + ", " + mxbean.getVmVendor() + ", " + mxbean.getVmVersion();
    }

    static String getSystemSummary() {
        return SystemInfo.getCpuInfo() +
                "\n" +
                SystemInfo.getOsInfo() +
                "\n" +
                SystemInfo.getRtInfo() +
                "\n" +
                "=============================" +
                "\n" +
                String.format("%.2fns per CPU cycle", 1.0 / SystemInfo.getGhz()) +
                "\n" +
                String.format("%d bytes pagesize", SystemInfo.getPagesize()) +
                "\n" +
                "=============================";
    }
}
