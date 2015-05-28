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
            addSample("system.LoadedClasses", timestamp, (double)classesLoaded.intValue());
            Long classesUnLoaded = (Long) zorka.jmx(_mbs, _clmbean, "UnloadedClassCount");
            addSample("system.UnloadedClasses", timestamp, (double)classesLoaded.longValue());

            //threads
            String _tmbean = "java.lang:type=Threading";
            Integer threadCount = (Integer) zorka.jmx(_mbs, _tmbean, "ThreadCount");
            addSample("system.threads", timestamp, (double) threadCount.intValue());

            //heap
            String _mmbean = "java.lang:type=Memory";
            Long heapCommitted = (Long) zorka.jmx(_mbs, _mmbean, "HeapMemoryUsage", "committed");
            addSample("heap.committed", timestamp, (double) heapCommitted.longValue());
            Long heapUsed = (Long) zorka.jmx(_mbs, _mmbean, "HeapMemoryUsage", "used");
            addSample("heap.used", timestamp, (double) heapUsed.longValue());

            //memory pool
            String _psesmbean = "java.lang:type=MemoryPool,name=PS Eden Space";
            Long psesCommitted = (Long) zorka.jmx(_mbs, _psesmbean, "Usage", "committed");
            addSample("mempool.PSEdenSpace.committed", timestamp, (double) psesCommitted.longValue());
            Long psesUsed = (Long) zorka.jmx(_mbs, _psesmbean, "Usage", "used");
            addSample("mempool.PSEdenSpace.used", timestamp, (double) psesUsed.longValue());
            Long psesMax = (Long) zorka.jmx(_mbs, _psesmbean, "Usage", "max");
            addSample("mempool.PSEdenSpace.max", timestamp, (double) psesMax.longValue());

            String _psssmbean = "java.lang:type=MemoryPool,name=PS Survivor Space";
            Long psssCommitted = (Long) zorka.jmx(_mbs, _psssmbean, "Usage", "committed");
            addSample("mempool.PSSurvivorSpace.committed", timestamp, (double) psssCommitted.longValue());
            Long psssUsed = (Long) zorka.jmx(_mbs, _psssmbean, "Usage", "used");
            addSample("mempool.PSSurvivorSpace.used", timestamp, (double) psssUsed.longValue());
            Long psssMax = (Long) zorka.jmx(_mbs, _psssmbean, "Usage", "max");
            addSample("mempool.PSSurvivorSpace.max", timestamp, (double) psssMax.longValue());

            String _psogmbean = "java.lang:type=MemoryPool,name=PS Old Gen";
            Long psogCommitted = (Long) zorka.jmx(_mbs, _psogmbean, "Usage", "committed");
            addSample("mempool.PSOldGen.committed", timestamp, (double) psogCommitted.longValue());
            Long psogUsed = (Long) zorka.jmx(_mbs, _psogmbean, "Usage", "used");
            addSample("mempool.PSOldGen.used", timestamp, (double) psogUsed.longValue());
            Long psogMax = (Long) zorka.jmx(_mbs, _psogmbean, "Usage", "max");
            addSample("mempool.PSOldGen.max", timestamp, (double) psogMax.longValue());

            String _msmbean = "java.lang:type=MemoryPool,name=Metaspace";
            Long msUsed = (Long) zorka.jmx(_mbs, _msmbean, "Usage", "used");
            addSample("mempool.Metaspace.committed", timestamp, (double) msUsed.longValue());

            String _ccsmbean = "java.lang:type=MemoryPool,name=Compressed Class Space";
            Long ccsUsed = (Long) zorka.jmx(_mbs, _ccsmbean, "Usage", "used");
            addSample("mempool.CompressedClassSpace.committed", timestamp, (double) ccsUsed.longValue());

            String _ccmbean = "java.lang:type=MemoryPool,name=Code Cache";
            Long _ccUsed = (Long) zorka.jmx(_mbs, _ccmbean, "Usage", "used");
            addSample("mempool.CodeCache.committed", timestamp, (double) ccsUsed.longValue());

            //cpu
            String _osmbean = "java.lang:type=OperatingSystem";
            Double processCpuLoad = (Double) zorka.jmx(_mbs, _osmbean, "ProcessCpuLoad");
            addSample("cpu.used.percent", timestamp, processCpuLoad * 100);

            //garbage collector
            String _psmsmbean = "java.lang:type=GarbageCollector,name=PS MarkSweep";
            Long psmsCollectionTime = (Long) zorka.jmx(_mbs, _psmsmbean, "CollectionTime");
            addSample("gc.PSMarkSweep.CollectionTime", timestamp, (double) psmsCollectionTime.longValue());

            String _pssmbean = "java.lang:type=GarbageCollector,name=PS Scavenge";
            Long pssCollectionTime = (Long) zorka.jmx(_mbs, _pssmbean, "CollectionTime");
            addSample("gc.PSScavenge.CollectionTime", timestamp, (double) pssCollectionTime.longValue());

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
