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
            elementBuilder.clearMetricsAndSamples();
            String _mbs = "java";

            String metricId = null;
            //classes
            String _clmbean = "java.lang:type=ClassLoading";
            Integer classesLoaded = (Integer) zorka.jmx(_mbs, _clmbean, "LoadedClassCount");
            metricId = "system.LoadedClasses";
            addMetric(metricId, "Loaded Classes", "GAUGE", "");
            addSample(metricId, timestamp, (double)classesLoaded.intValue());

            Long classesUnLoaded = (Long) zorka.jmx(_mbs, _clmbean, "UnloadedClassCount");
            metricId = "system.UnloadedClasses";
            addMetric(metricId, "Unloaded Classes", "GAUGE", "");
            addSample(metricId, timestamp, (double)classesUnLoaded.longValue());

            //threads
            String _tmbean = "java.lang:type=Threading";
            Integer threadCount = (Integer) zorka.jmx(_mbs, _tmbean, "ThreadCount");
            metricId = "system.threads";
            addMetric(metricId, "Threads", "GAUGE", "");
            addSample(metricId, timestamp, (double) threadCount.intValue());

            //heap
            String _mmbean = "java.lang:type=Memory";
            Long heapCommitted = (Long) zorka.jmx(_mbs, _mmbean, "HeapMemoryUsage", "committed");
            metricId = "heap.committed";
            addMetric(metricId, "Heap Committed", "GAUGE", "B");
            addSample(metricId, timestamp, (double) heapCommitted.longValue());

            Long heapUsed = (Long) zorka.jmx(_mbs, _mmbean, "HeapMemoryUsage", "used");
            metricId = "heap.used";
            addMetric(metricId, "Heap Used", "GAUGE", "B");
            addSample(metricId, timestamp, (double) heapUsed.longValue());

            //memory pool
            String _psesmbean = "java.lang:type=MemoryPool,name=PS Eden Space";
            Long psesCommitted = (Long) zorka.jmx(_mbs, _psesmbean, "Usage", "committed");
            metricId = "mempool.PSEdenSpace.committed";
            addMetric(metricId, "Memory Pool PS Eden Space Committed", "GAUGE", "B");
            addSample(metricId, timestamp, (double) psesCommitted.longValue());

            Long psesUsed = (Long) zorka.jmx(_mbs, _psesmbean, "Usage", "used");
            metricId = "mempool.PSEdenSpace.used";
            addMetric(metricId, "Memory Pool PS Eden Space Used", "GAUGE", "B");
            addSample(metricId, timestamp, (double) psesUsed.longValue());

            Long psesMax = (Long) zorka.jmx(_mbs, _psesmbean, "Usage", "max");
            metricId = "mempool.PSEdenSpace.max";
            addMetric(metricId, "Memory Pool PS Eden Space Max", "GAUGE", "B");
            addSample(metricId, timestamp, (double) psesMax.longValue());

            String _psssmbean = "java.lang:type=MemoryPool,name=PS Survivor Space";
            Long psssCommitted = (Long) zorka.jmx(_mbs, _psssmbean, "Usage", "committed");
            metricId = "mempool.PSSurvivorSpace.committed";
            addMetric(metricId, "Memory Pool PS Survivor Space Committed", "GAUGE", "B");
            addSample(metricId, timestamp, (double) psssCommitted.longValue());

            Long psssUsed = (Long) zorka.jmx(_mbs, _psssmbean, "Usage", "used");
            metricId = "mempool.PSSurvivorSpace.used";
            addMetric(metricId, "Memory Pool PS Survivor Space Used", "GAUGE", "B");
            addSample(metricId, timestamp, (double) psssUsed.longValue());

            Long psssMax = (Long) zorka.jmx(_mbs, _psssmbean, "Usage", "max");
            metricId = "mempool.PSSurvivorSpace.max";
            addMetric(metricId, "Memory Pool PS Survivor Space Max", "GAUGE", "B");
            addSample(metricId, timestamp, (double) psssMax.longValue());

            String _psogmbean = "java.lang:type=MemoryPool,name=PS Old Gen";
            Long psogCommitted = (Long) zorka.jmx(_mbs, _psogmbean, "Usage", "committed");
            metricId = "mempool.PSOldGen.committed";
            addMetric(metricId, "Memory Pool PS Old Gen Committed", "GAUGE", "B");
            addSample(metricId, timestamp, (double) psogCommitted.longValue());

            Long psogUsed = (Long) zorka.jmx(_mbs, _psogmbean, "Usage", "used");
            metricId = "mempool.PSOldGen.used";
            addMetric(metricId, "Memory Pool PS Old Gen Used", "GAUGE", "B");
            addSample(metricId, timestamp, (double) psogUsed.longValue());

            Long psogMax = (Long) zorka.jmx(_mbs, _psogmbean, "Usage", "max");
            metricId = "mempool.PSOldGen.max";
            addMetric(metricId, "Memory Pool PS Old Gen Max", "GAUGE", "B");
            addSample(metricId, timestamp, (double) psogMax.longValue());

            String _msmbean = "java.lang:type=MemoryPool,name=Metaspace";
            Long msUsed = (Long) zorka.jmx(_mbs, _msmbean, "Usage", "used");
            metricId = "mempool.Metaspace.committed";
            addMetric(metricId, "Memory Pool Metaspace Committed", "GAUGE", "B");
            addSample(metricId, timestamp, (double) msUsed.longValue());

            String _ccsmbean = "java.lang:type=MemoryPool,name=Compressed Class Space";
            Long ccsUsed = (Long) zorka.jmx(_mbs, _ccsmbean, "Usage", "used");
            metricId = "mempool.CompressedClassSpace.used";
            addMetric(metricId, "Memory Pool Compressed Class Space Used", "GAUGE", "B");
            addSample(metricId, timestamp, (double) ccsUsed.longValue());

            String _ccmbean = "java.lang:type=MemoryPool,name=Code Cache";
            Long _ccUsed = (Long) zorka.jmx(_mbs, _ccmbean, "Usage", "used");
            metricId = "mempool.CodeCache.used";
            addMetric(metricId, "Memory Pool Code Cache Used", "GAUGE", "B");
            addSample(metricId, timestamp, (double) ccsUsed.longValue());

            //cpu
            String _osmbean = "java.lang:type=OperatingSystem";
            Double processCpuLoad = (Double) zorka.jmx(_mbs, _osmbean, "ProcessCpuLoad");
            metricId = "cpu.used.percent";
            addMetric(metricId, "Operating System Process CPU Load", "GAUGE", "%");
            addSample(metricId, timestamp, processCpuLoad * 100);

            //garbage collector
            String _psmsmbean = "java.lang:type=GarbageCollector,name=PS MarkSweep";
            Long psmsCollectionTime = (Long) zorka.jmx(_mbs, _psmsmbean, "CollectionTime");
            metricId = "gc.PSMarkSweep.CollectionTime";
            addMetric(metricId, "GC PS MarkSweep Collection Time", "GAUGE", "ms");
            addSample(metricId, timestamp, (double) psmsCollectionTime.longValue());

            String _pssmbean = "java.lang:type=GarbageCollector,name=PS Scavenge";
            Long pssCollectionTime = (Long) zorka.jmx(_mbs, _pssmbean, "CollectionTime");
            metricId = "gc.PSScavenge.CollectionTime";
            addMetric(metricId, "GC PS Scavenge Collection Time", "GAUGE", "ms");
            addSample(metricId, timestamp, (double) pssCollectionTime.longValue());

            log.debug(ZorkaLogger.ZPM_DEBUG, "finished collecting system stats");
        } catch (Exception e) {
            log.error(ZorkaLogger.ZPM_ERRORS, "finished collecting system stats with error: ", e);
        }
        return elementBuilder.build();
    }

    private void addMetric(String id, String name, String type, String unit) {
        elementBuilder.metric(id, name, type, unit);
    }

    private void addSample(String metricId, Long timestamp, double val) {
        elementBuilder.sample(metricId, timestamp, val, true);
    }
}
