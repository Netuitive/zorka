package com.jitlogic.zorka.core.integ.netuitive;

import com.jitlogic.zorka.common.stats.MethodCallStatistic;
import com.jitlogic.zorka.common.stats.MethodCallStatistics;
import com.jitlogic.zorka.common.util.ZorkaConfig;
import com.jitlogic.zorka.common.util.ZorkaLog;
import com.jitlogic.zorka.common.util.ZorkaLogger;
import com.jitlogic.zorka.common.util.ZorkaUtil;
import com.jitlogic.zorka.core.ZorkaLib;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to collect Method Call Statistics as Element.metrics, Element.samples from MBeans are created by ZorkaStatsCollector
 */
public class JvmMethodCallStatsReport extends AbstractStatsReport {

    /* Logger */
    private final ZorkaLog log = ZorkaLogger.getLog(JvmMethodCallStatsReport.class);

    public JvmMethodCallStatsReport(ZorkaConfig config, ZorkaLib zorka) {
        super(config, zorka);
    }

    @Override
    public Element collect(Long timestamp) {
        log.debug(ZorkaLogger.ZPM_DEBUG, "start collecting method call stats");
        String objectName = config.stringCfg("netuitive.api.stats.mbean", "zorka:type=ZorkaStats,*");
        String attributes = config.stringCfg("netuitive.api.stats.attributes", "stats");
        Element e = null;
        try {
            for (String attribute : attributes.split(",")) {
                log.debug(ZorkaLogger.ZPM_DEBUG, "retrieving method call status for mbean=" + objectName + ", attribute=" + attribute);
                Map<String, MethodCallStatistics> statsMap = zorka.stats("java", objectName, attribute.trim());
                Element current = build(statsMap);
                if (e == null) {
                    e = current;
                } else {
                    e.merge(current);
                }
            }
            log.debug(ZorkaLogger.ZPM_DEBUG, "finished collecting method call stats with " + e.getMetrics().size() + " metrics");
        } catch (Exception ex) {
            log.error(ZorkaLogger.ZPM_ERRORS, "finished collecting method call stats with error: ", ex);
        }

        return e;
    }

    private Element build(Map<String, MethodCallStatistics> statsMap) {
        Long timestamp = System.currentTimeMillis();
        for (Map.Entry<String, MethodCallStatistics> statsEntry : statsMap.entrySet()) {
            String mbean = statsEntry.getKey();
            String mbeanShortName = mbean.replaceAll("zorka:", "")
                    .replaceAll(",?type=ZorkaStats,?", "")
                    .replaceAll("name=", "");
            if ("Diagnostic".equalsIgnoreCase(mbeanShortName)) {
                continue;//we're not interested in agent's own Diagnostic stats
            }
            MethodCallStatistics stats = statsEntry.getValue();

            for (String statName : stats.getStatisticNames()) {
                MethodCallStatistic stat = stats.getMethodCallStatistic(statName);
                //calls
                String metricId = ZorkaUtil.join(".", mbeanShortName, statName, "calls");
                elementBuilder.metric(metricId, "method calls", "COUNTER", null);
                elementBuilder.sample(metricId, timestamp, (double)stat.getCalls());
                //errors
                metricId = ZorkaUtil.join(".", mbeanShortName, statName, "errors");
                elementBuilder.metric(metricId, "method errors", "COUNTER", null);
                elementBuilder.sample(metricId, timestamp, (double) stat.getErrors());
                //time
                metricId = ZorkaUtil.join(".", mbeanShortName, statName, "time");
                elementBuilder.metric(metricId, "total method execution time", "COUNTER", "microseconds");
                elementBuilder.sample(metricId, timestamp, (double) stat.getTimeUs());
            }
        }
        return elementBuilder.build();
    }
}
