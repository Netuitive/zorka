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
    private Java6CpuUsage java6CpuUsage;

    public JvmSystemStatsReport(ZorkaConfig config, ZorkaLib zorka) {
        super(config, zorka);
        java6CpuUsage = new Java6CpuUsage();
    }

    @Override
    public Element collect(Long timestamp) {
        log.debug(ZorkaLogger.ZPM_DEBUG, "start collecting system stats");
        try {
            elementBuilder.clearMetricsAndSamples();
            String _mbs = "java";

            //classes
            String _clmbean = "java.lang:type=ClassLoading";
            Integer classesLoaded = (Integer) zorka.jmx(_mbs, _clmbean, "LoadedClassCount");
            addMetricSample("system.loadedclasses", "Loaded Classes", "GAUGE", "", timestamp, classesLoaded);

            Long classesUnLoaded = (Long) zorka.jmx(_mbs, _clmbean, "UnloadedClassCount");
            addMetricSample("system.unloadedclasses", "Unloaded Classes", "COUNTER", "", timestamp, classesUnLoaded);

            //threads
            String _tmbean = "java.lang:type=Threading";
            Integer threadCount = (Integer) zorka.jmx(_mbs, _tmbean, "ThreadCount");
            addMetricSample("system.threads", "Threads", "GAUGE", "", timestamp, threadCount);

            //heap
            String _mmbean = "java.lang:type=Memory";
            Long heapCommitted = (Long) zorka.jmx(_mbs, _mmbean, "HeapMemoryUsage", "committed");
            addMetricSample("heap.committed", "Heap Committed", "GAUGE", "B", timestamp, heapCommitted);

            Long heapUsed = (Long) zorka.jmx(_mbs, _mmbean, "HeapMemoryUsage", "used");
            addMetricSample("heap.used", "Heap Used", "GAUGE", "B", timestamp, heapUsed);

            //memory pool
            String _psesmbean = "java.lang:type=MemoryPool,name=PS Eden Space";
            Long psesCommitted = (Long) zorka.jmx(_mbs, _psesmbean, "Usage", "committed");
            addMetricSample("mempool.psedenspace.committed", "Memory Pool PS Eden Space Committed", "GAUGE", "B", timestamp, psesCommitted);

            Long psesUsed = (Long) zorka.jmx(_mbs, _psesmbean, "Usage", "used");
            addMetricSample("mempool.psedenspace.used", "Memory Pool PS Eden Space Used", "GAUGE", "B", timestamp, psesUsed);

            Long psesMax = (Long) zorka.jmx(_mbs, _psesmbean, "Usage", "max");
            addMetricSample("mempool.psedenspace.max", "Memory Pool PS Eden Space Max", "GAUGE", "B", timestamp, psesMax);

            String _psssmbean = "java.lang:type=MemoryPool,name=PS Survivor Space";
            Long psssCommitted = (Long) zorka.jmx(_mbs, _psssmbean, "Usage", "committed");
            addMetricSample("mempool.pssurvivorspace.committed", "Memory Pool PS Survivor Space Committed", "GAUGE", "B", timestamp, psssCommitted);

            Long psssUsed = (Long) zorka.jmx(_mbs, _psssmbean, "Usage", "used");
            addMetricSample("mempool.pssurvivorspace.used", "Memory Pool PS Survivor Space Used", "GAUGE", "B", timestamp, psssUsed);

            Long psssMax = (Long) zorka.jmx(_mbs, _psssmbean, "Usage", "max");
            addMetricSample("mempool.pssurvivorspace.max", "Memory Pool PS Survivor Space Max", "GAUGE", "B", timestamp, psssMax);

            String _psogmbean = "java.lang:type=MemoryPool,name=PS Old Gen";
            Long psogCommitted = (Long) zorka.jmx(_mbs, _psogmbean, "Usage", "committed");
            addMetricSample("mempool.psoldgen.committed", "Memory Pool PS Old Gen Committed", "GAUGE", "B", timestamp, psogCommitted);

            Long psogUsed = (Long) zorka.jmx(_mbs, _psogmbean, "Usage", "used");
            addMetricSample("mempool.psoldgen.used", "Memory Pool PS Old Gen Used", "GAUGE", "B", timestamp, psogUsed);

            Long psogMax = (Long) zorka.jmx(_mbs, _psogmbean, "Usage", "max");
            addMetricSample("mempool.psoldgen.max", "Memory Pool PS Old Gen Max", "GAUGE", "B", timestamp, psogMax);

            String _msmbean = "java.lang:type=MemoryPool,name=Metaspace";
            Long msCommitted = (Long) zorka.jmx(_mbs, _msmbean, "Usage", "committed");
            addMetricSample("mempool.metaspace.committed", "Memory Pool Metaspace Committed", "GAUGE", "B", timestamp, msCommitted);
            Long msUsed = (Long) zorka.jmx(_mbs, _msmbean, "Usage", "used");
            addMetricSample("mempool.metaspace.used", "Memory Pool Metaspace Used", "GAUGE", "B", timestamp, msUsed);

            String _ccsmbean = "java.lang:type=MemoryPool,name=Compressed Class Space";
            Long ccsCommitted = (Long) zorka.jmx(_mbs, _ccsmbean, "Usage", "committed");
            addMetricSample("mempool.compressedclassspace.committed", "Memory Pool Compressed Class Space Committed", "GAUGE", "B", timestamp, ccsCommitted);
            Long ccsUsed = (Long) zorka.jmx(_mbs, _ccsmbean, "Usage", "used");
            addMetricSample("mempool.compressedclassspace.used", "Memory Pool Compressed Class Space Used", "GAUGE", "B", timestamp, ccsUsed);
            Long ccsMax = (Long) zorka.jmx(_mbs, _ccsmbean, "Usage", "max");
            addMetricSample("mempool.compressedclassspace.max", "Memory Pool Compressed Class Space Max", "GAUGE", "B", timestamp, ccsMax);

            String _ccmbean = "java.lang:type=MemoryPool,name=Code Cache";
            Long ccCommitted = (Long) zorka.jmx(_mbs, _ccmbean, "Usage", "committed");
            addMetricSample("mempool.codecache.committed", "Memory Pool Code Cache Committed", "GAUGE", "B", timestamp, ccCommitted);
            Long ccUsed = (Long) zorka.jmx(_mbs, _ccmbean, "Usage", "used");
            addMetricSample("mempool.codecache.used", "Memory Pool Code Cache Used", "GAUGE", "B", timestamp, ccUsed);
            Long ccMax = (Long) zorka.jmx(_mbs, _ccmbean, "Usage", "max");
            addMetricSample("mempool.codecache.max", "Memory Pool Code Cache Max", "GAUGE", "B", timestamp, ccMax);

            //cpu
            String _osmbean = "java.lang:type=OperatingSystem";
            Double processCpuLoad = (Double) zorka.jmx(_mbs, _osmbean, "ProcessCpuLoad");
            if (processCpuLoad == null) {
                Integer numCpus = (Integer) zorka.jmx(_mbs, _osmbean, "AvailableProcessors");
                Long processCpuTime = (Long) zorka.jmx(_mbs, _osmbean, "ProcessCpuTime");
                String _jvmmbean = "java.lang:type=Runtime";
                Long upTime = (Long) zorka.jmx(_mbs, _jvmmbean, "Uptime");

                processCpuLoad = java6CpuUsage.calculate(numCpus, upTime, processCpuTime);
            }

            if (processCpuLoad != null) {
                elementBuilder.metric("cpu.used.percent", "Operating System Process CPU Load", "GAUGE", "%");
                elementBuilder.sample("cpu.used.percent", timestamp, processCpuLoad * 100);
            }

            //garbage collector
            String _psmsmbean = "java.lang:type=GarbageCollector,name=PS MarkSweep";
            Long psmsCollectionTime = (Long) zorka.jmx(_mbs, _psmsmbean, "CollectionTime");
            addMetricSample("gc.psmarksweep.collectiontime", "GC PS MarkSweep Collection Time", "GAUGE", "ms", timestamp, psmsCollectionTime);

            String _pssmbean = "java.lang:type=GarbageCollector,name=PS Scavenge";
            Long pssCollectionTime = (Long) zorka.jmx(_mbs, _pssmbean, "CollectionTime");
            addMetricSample("gc.psscavenge.collectiontime", "GC PS Scavenge Collection Time", "GAUGE", "ms", timestamp, pssCollectionTime);

            String _parNew = "java.lang:type=GarbageCollector,name=ParNew";
            Long _parNewTime = (Long) zorka.jmx(_mbs, _parNew, "CollectionTime");
            addMetricSample("gc.parnew.collectiontime", "GC Par New Collection Time", "GAUGE", "ms", timestamp, _parNewTime);

            String _concurrentMarkSweep = "java.lang:type=GarbageCollector,name=ConcurrentMarkSweep";
            Long _concurrentMarkSweepTime = (Long) zorka.jmx(_mbs, _concurrentMarkSweep, "CollectionTime");
            addMetricSample("gc.concurrentmarksweep.collectiontime", "GC Concurrent MarkSweep Collection Time", "GAUGE", "ms", timestamp, _concurrentMarkSweepTime);

            String _G1OldGeneration = "java.lang:type=GarbageCollector,name=G1 Old Generation";
            Long _G1OldGenerationTime = (Long) zorka.jmx(_mbs, _G1OldGeneration, "CollectionTime");
            addMetricSample("gc.g1oldgeneration.collectiontime", "GC G1 Old Generation Collection Time", "GAUGE", "ms", timestamp, _G1OldGenerationTime);

            String _G1YoungGeneration = "java.lang:type=GarbageCollector,name=G1 Young Generation";
            Long _G1YoungGenerationTime = (Long) zorka.jmx(_mbs, _G1YoungGeneration, "CollectionTime");
            addMetricSample("gc.g1newgeneration.collectiontime", "GC G1 Young Generation Collection Time", "GAUGE", "ms", timestamp, _G1YoungGenerationTime);

            log.debug(ZorkaLogger.ZPM_DEBUG, "finished collecting system stats");
        } catch (Exception e) {
            log.error(ZorkaLogger.ZPM_ERRORS, "finished collecting system stats with error: ", e);
        }
        return elementBuilder.build();
    }

    private void addMetricSample(String metricId, String name, String type, String unit, Long timestamp, Object value) {
        if (value == null) {
            return;
        }
        try {
            Double val = Double.valueOf(value.toString());
            elementBuilder.metric(metricId, name, type, unit);
            elementBuilder.sample(metricId, timestamp, val);
        } catch (NumberFormatException nfe) {
            log.error(ZorkaLogger.ZPM_ERRORS, "unable to parse sample value: " + value.toString() + " to a double");
        }
    }
}
