package com.jitlogic.zorka.core.integ.netuitive;

import com.jitlogic.zorka.common.util.JmxObject;
import com.jitlogic.zorka.common.util.ZorkaConfig;
import com.jitlogic.zorka.common.util.ZorkaLog;
import com.jitlogic.zorka.common.util.ZorkaLogger;
import com.jitlogic.zorka.core.ZorkaLib;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 *
 * @author john.king
 */
public class CustomMBeanStatsReport extends AbstractStatsReport {

    private final ZorkaLog log = ZorkaLogger.getLog(CustomMBeanStatsReport.class);
    
    public CustomMBeanStatsReport(ZorkaConfig config, ZorkaLib zorka) {
        super(config, zorka);
    }

    @Override
    public Element collect(Long timestamp) {
        log.debug(ZorkaLogger.ZPM_DEBUG, "start collecting custom stats");
        String objectName = config.stringCfg("netuitive.api.custom.stats.mbean", "");
        Element e = null;
        if (!objectName.isEmpty()) {
            String include = config.stringCfg("netuitive.api.custom.stats.mbean.attr.include", "");
            List<String> includedValues = new ArrayList<String>();
            if (!include.isEmpty()) {
                includedValues = Arrays.asList(include.split(","));
            }
            try {
                String metricPrefix = getMetricPrefix(objectName);
                elementBuilder.clearMetricsAndSamples();
                log.debug(ZorkaLogger.ZPM_DEBUG, "retrieving custom stats for mbean=" + objectName);

                JmxObject jmx = (JmxObject) zorka.jmx("java", objectName);
                if (jmx != null) {
                    Map<String, Object> statsMap = jmx.getAll();

                    log.debug(ZorkaLogger.ZPM_DEBUG, "finished retrieving custom stats for mbean=" + objectName);
                    Element current = build(metricPrefix, statsMap, timestamp, includedValues);
                    if (e == null) {
                        e = current;
                    } else {
                        e.merge(current);
                    }
                }

            } catch (Exception ex) {
                log.error(ZorkaLogger.ZPM_ERRORS, "finished collecting custom stats with error: ", ex);
            }
        }
        log.debug(ZorkaLogger.ZPM_DEBUG, "finished collecting custom stats with " + (e == null ? "0" : e.getMetrics().size()) + " metrics");
        return e;
    }

    private Element build(String metricPrefix, Map<String, Object> statsMap, Long timestamp, List<String> properties) {
        log.debug(ZorkaLogger.ZPM_DEBUG, "starting to build custom stats element");
        Element ret = null;
        try {
            if (properties != null && !properties.isEmpty()) {
                for (String key : properties) {
                    try {
                        this.buildMetric(timestamp, metricPrefix, key.trim().toLowerCase(), statsMap.get(key.trim()));
                    } catch (Exception e) {
                        log.error(ZorkaLogger.ZAG_ERRORS, "failed to process stat: " + key, e);
                    }
                }
            } else {
                for (Map.Entry<String, Object> statsEntry : statsMap.entrySet()) {
                    try {
                        this.buildMetric(timestamp, metricPrefix, statsEntry.getKey().toLowerCase(), statsEntry.getValue());
                    } catch (Exception e) {
                        log.error(ZorkaLogger.ZAG_WARNINGS, "failed to process stat: " + statsEntry.getKey().toLowerCase(), e);
                    }
                }
            }
            log.debug(ZorkaLogger.ZPM_DEBUG, "finished processing mbean: " + metricPrefix);
            ret = elementBuilder.build();
        } catch (Exception ex) {
            log.error(ZorkaLogger.ZPM_ERRORS, "failed to build custom stats element with error: ", ex);
        }
        log.debug(ZorkaLogger.ZPM_DEBUG, "finished building method stats element");
        return ret;
    }
    
    private void buildMetric(long timestamp, String prefix, String key, Object value) {
        Double val = Double.parseDouble(value.toString());
        String metricId = prefix + "." + key;
        elementBuilder.metric(metricId, key, "GAUGE", null);
        elementBuilder.sample(metricId, timestamp, (double) val);
        log.debug(ZorkaLogger.ZPM_DEBUG, "finished processing stat: " + key);
    }
    
    private String getMetricPrefix(String name) throws MalformedObjectNameException{
        ObjectName objectName = new ObjectName(name);
        StringBuilder builder = new StringBuilder();
        String nameProperty = objectName.getKeyProperty("name");
        String domain = objectName.getDomain();
        String type = objectName.getKeyProperty("type");
        builder.append("custom");
        if(domain != null){
            builder.append(".")
            .append(domain.replaceAll("\\.", "_"));
        }
        if(type != null){
            builder.append(".")
                    .append(type);
        }
        if(nameProperty != null){
            builder.append(".")
                    .append(nameProperty);
        }
        return builder.toString().toLowerCase();
    }

}
