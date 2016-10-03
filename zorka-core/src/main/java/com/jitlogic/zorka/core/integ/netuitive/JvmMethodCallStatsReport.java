package com.jitlogic.zorka.core.integ.netuitive;

import com.jitlogic.zorka.common.stats.MethodCallStatistic;
import com.jitlogic.zorka.common.stats.MethodCallStatistics;
import com.jitlogic.zorka.common.util.ZorkaConfig;
import com.jitlogic.zorka.common.util.ZorkaLog;
import com.jitlogic.zorka.common.util.ZorkaLogger;
import com.jitlogic.zorka.common.util.ZorkaUtil;
import com.jitlogic.zorka.core.ZorkaLib;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashMap;
import java.util.List;
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
            elementBuilder.clearMetricsAndSamples();
            for (String attribute : attributes.split(",")) {
                log.debug(ZorkaLogger.ZPM_DEBUG, "retrieving method call status for mbean=" + objectName + ", attribute=" + attribute);
                Map<String, MethodCallStatistics> statsMap = zorka.stats("java", objectName, attribute.trim());
                log.debug(ZorkaLogger.ZPM_DEBUG, "finished retrieving method call status for mbean=" + objectName + ", attribute=" + attribute);
                if(statsMap == null){
                    statsMap = new HashMap<String, MethodCallStatistics>();
                }
                Element current = build(statsMap, attribute.trim().toLowerCase(), timestamp);
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

    private Element build(Map<String, MethodCallStatistics> statsMap, String attributeName, Long timestamp) {
        log.debug(ZorkaLogger.ZPM_DEBUG, "starting to build method stats element");
        for (Map.Entry<String, MethodCallStatistics> statsEntry : statsMap.entrySet()) {
            String mbean = statsEntry.getKey();
            String mbeanShortName = mbean.replaceAll("zorka:", "")
                    .replaceAll(",?type=ZorkaStats,?", "")
                    .replaceAll("name=", "");
            if ("Diagnostic".equalsIgnoreCase(mbeanShortName)) {
                log.debug(ZorkaLogger.ZPM_DEBUG, "skipping diagnositic mbean");
                continue;//we're not interested in agent's own Diagnostic stats
            }
            log.debug(ZorkaLogger.ZPM_DEBUG, "processing mbean: " + mbeanShortName);
            MethodCallStatistics stats = statsEntry.getValue();

            for (String statName : stats.getStatisticNames()) {
                log.debug(ZorkaLogger.ZPM_DEBUG, "processing stat: " + statName);
                MethodCallStatistic stat = stats.getMethodCallStatistic(statName);
                //calls
                String callsMetricId = constructMetricName(mbeanShortName, attributeName, statName, "calls");
                elementBuilder.metric(callsMetricId.toLowerCase(), "Method Calls", "COUNTER", null);
                elementBuilder.sample(callsMetricId.toLowerCase(), timestamp, (double)stat.getCalls());
                //errors
                String errorMetricId = constructMetricName(mbeanShortName, attributeName, statName, "errors");
                elementBuilder.metric(errorMetricId.toLowerCase(), "Method Errors", "COUNTER", null);
                elementBuilder.sample(errorMetricId.toLowerCase(), timestamp, (double) stat.getErrors());
                //time
                String timeMetricId = constructMetricName(mbeanShortName, attributeName, statName, "time");
                elementBuilder.metric(timeMetricId.toLowerCase(), "Total Method Execution Time", "COUNTER", "ms");
                elementBuilder.sample(timeMetricId.toLowerCase(), timestamp, (double) stat.getTime());
                //avg_time
                String avgTimeMetricId = constructMetricName(mbeanShortName, attributeName, statName, "avg_time").toLowerCase();
                Metric metric = Metric.createMetric(avgTimeMetricId, "Average Method Execution Time", "GAUGE", "ms");
                List<String> inputMetrics = new ArrayList<String>(Arrays.asList(callsMetricId.toLowerCase(), timeMetricId.toLowerCase()));
                ComputedMetric computedMetric = new ComputedMetric(inputMetrics, AverageCallTimeComputedMetric.class, metric);
                ZorkaStatsDataStorage.computedMetrics.put(avgTimeMetricId, computedMetric);
                log.debug(ZorkaLogger.ZPM_DEBUG, "finished processing stat: " + statName);
            }
            log.debug(ZorkaLogger.ZPM_DEBUG, "finished processing mbean: " + mbeanShortName);
        }
        log.debug(ZorkaLogger.ZPM_DEBUG, "finished building method stats element");
        return elementBuilder.build();
    }
    
    private String constructMetricName(String mbean, String attribute, String stat, String metric){
        if(stat == null || stat.isEmpty() || stat.equals("stats") || stat.equals("byTag")){
            return ZorkaUtil.join(".", mbean, stat, metric);
        }
        return ZorkaUtil.join(".", mbean, attribute, stat, metric); 
    }
}
