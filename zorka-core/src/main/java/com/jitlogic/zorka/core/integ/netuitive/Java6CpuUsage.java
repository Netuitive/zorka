package com.jitlogic.zorka.core.integ.netuitive;

/**
 * This class is used to calculate CPU usage based on OperatingSystem:ProcessCpuTime and Runtime:Uptime
 */
public class Java6CpuUsage {
    private Long prevUpTime; // in milliseconds
    private Long prevProcessCpuTime; // in nanoseconds


    public Double calculate(Integer numCpus, Long upTime, Long processCpuTime) {
        if (prevUpTime == null || prevProcessCpuTime == null) {
            prevUpTime = upTime;
            prevProcessCpuTime = processCpuTime;
            return null;
        }

        Double cpuUsage = null;

        if (prevUpTime > 0L && upTime > prevUpTime && processCpuTime > prevProcessCpuTime) {
            long elapsedTime = upTime - prevUpTime;
            long cpuTime = processCpuTime - prevProcessCpuTime;

            cpuUsage = Math.min(100.0d, cpuTime/(elapsedTime * 1000000d * numCpus));

            prevUpTime = upTime;
            prevProcessCpuTime = processCpuTime;

        }

        return cpuUsage;
    }
}
