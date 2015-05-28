package com.jitlogic.zorka.core.integ.netuitive;

import com.jitlogic.zorka.common.util.ZorkaConfig;
import com.jitlogic.zorka.common.util.ZorkaLog;
import com.jitlogic.zorka.common.util.ZorkaLogger;
import com.jitlogic.zorka.core.ZorkaLib;

/**
 * This class is used to collect JVM System measures(CPU, Memory, Threads, Classes Loaded) as Element.metrics, Element.samples from JMX->java.lang MBeans
 */
public class JvmSystemStatsReport extends AbstractStatsReport {

    /* Logger */
    private final ZorkaLog log = ZorkaLogger.getLog(JvmSystemStatsReport.class);

    public JvmSystemStatsReport(ZorkaConfig config, ZorkaLib zorka) {
        super(config, zorka);
    }

    @Override
    public Element collect(Long timestamp) {
        log.debug(ZorkaLogger.ZPM_DEBUG, "start collecting system stats");
        try {
            String _mbs = "java";

            //classes
            String _clmbean = "java.lang:type=ClassLoading";
            Integer classesLoaded = (Integer) zorka.jmx(_mbs, _clmbean, "LoadedClassCount");
            addSample("system.classLoaded", timestamp, (double)classesLoaded.intValue());

            //threads
            String _tmbean = "java.lang:type=Threading";
            Integer threadCount = (Integer) zorka.jmx(_mbs, _tmbean, "ThreadCount");
            addSample("system.threadCount", timestamp, (double) threadCount.intValue());

            //heap
            String _mmbean = "java.lang:type=Memory";
            Long heapCommitted = (Long) zorka.jmx(_mbs, _mmbean, "HeapMemoryUsage", "committed");
            addSample("heap.committed", timestamp, (double) heapCommitted.longValue());
            Long heapUsed = (Long) zorka.jmx(_mbs, _mmbean, "HeapMemoryUsage", "used");
            addSample("heap.used", timestamp, (double) heapUsed.longValue());

            //cpu
            String _osmbean = "java.lang:type=OperatingSystem";
            Double processCpuLoad = (Double) zorka.jmx(_mbs, _osmbean, "ProcessCpuLoad");
            addSample("cpu.used.percent", timestamp, processCpuLoad * 100);

            log.debug(ZorkaLogger.ZPM_DEBUG, "finished collecting system stats");
        } catch (Exception e) {
            log.error(ZorkaLogger.ZPM_ERRORS, "finished collecting system stats with error: ", e);
        }
        return elementBuilder.build();
    }

    private void addSample(String metricId, Long timestamp, double val) {
        elementBuilder.sample(metricId, timestamp, val, true);
    }
}
