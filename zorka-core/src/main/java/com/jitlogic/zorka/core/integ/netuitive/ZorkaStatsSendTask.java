package com.jitlogic.zorka.core.integ.netuitive;

import com.jitlogic.zorka.common.util.JSONWriter;
import com.jitlogic.zorka.common.util.ZorkaConfig;
import com.jitlogic.zorka.common.util.ZorkaLog;
import com.jitlogic.zorka.common.util.ZorkaLogger;
import com.jitlogic.zorka.core.ZorkaLib;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZorkaStatsSendTask implements Runnable {

    /* Logger */
    private final ZorkaLog log = ZorkaLogger.getLog(ZorkaStatsSendTask.class);

    private final RestClient restClient;
    private final JSONWriter jsonWriter;
    private final JvmEnvironmentReport environmentReport;
    private final ComputedStatsReport computedStatsReport;
    private final ZorkaConfig config;
    private static final String TYPE_COUNTER_JSON = "\"type\":\"COUNTER\"";
    private static final String TYPE_GAUGE_JSON = "\"type\":\"GAUGE\"";
    private static final String CLUSTERS_PROPERTY = "zorka.clusters";
    private static final String CLUSTER_TYPE_PROPERTY = "zorka.clusters.type";
    Element e = null;

    public ZorkaStatsSendTask(
            ZorkaConfig config,
            ZorkaLib zorka,
            RestClient restClient) {
        this.restClient = restClient;
        this.config = config;
        this.jsonWriter = new JSONWriter(false);
        this.environmentReport = new JvmEnvironmentReport(config, zorka);
        this.computedStatsReport = new ComputedStatsReport(config, zorka);
    }
    
    private void intervalCleanUp() {
        if (e != null) {
            e.clearMetricsAndSamples();
        }
        ZorkaStatsDataStorage.newInterval();
    }

    @Override
    public void run() {
        
        synchronized (ZorkaStatsDataStorage.class) {
            log.debug(ZorkaLogger.ZPM_DEBUG, "start reporting zorka stats");
            Long start = System.currentTimeMillis();
            try {
                Long timestamp = System.currentTimeMillis();
                e = this.environmentReport.collect(timestamp);
                e.merge(computedStatsReport.collect(timestamp));
                if (e != null) {
                    e.merge(ZorkaStatsDataStorage.prepareForExport());
                    if (e.getSamples().size() > 0) {
                        this.sendElement(e);
                        this.sendClusterElements(e);
                    }
                }
            } catch (Exception ex) {
                log.error(ZorkaLogger.ZPM_ERRORS, "finished reporting zorka stats with error: ", ex);
            }
            finally{
                this.intervalCleanUp();
            }
            Long finished = System.currentTimeMillis();
            log.debug(ZorkaLogger.ZPM_DEBUG, "finished reporting zorka stats using %d ms", finished - start);
        }
        
    }
    
    private void sendClusterElements(Element e) {
        String oldType = e.getType();
        String oldId = e.getId();
        String oldName = e.getName();
        try {
            String clusterType = config.stringCfg(CLUSTER_TYPE_PROPERTY, "CLUSTER");
            e.setType(clusterType);
            Map<String, String> elements = this.getClusterElementIdsAndNames();
            for (String elementId : elements.keySet()) {
                e.setId(elementId);
                e.setName(elements.get(elementId));
                this.sendElement(e);
            }
        } finally {
            e.setId(oldId);
            e.setName(oldName);
            e.setType(oldType);
        }

    }
    
    private Map<String, String> getClusterElementIdsAndNames(){
        Map<String, String> ret = new HashMap<String,String>();
        if (config.hasCfg(CLUSTERS_PROPERTY)) {
            List<String> applications = config.listCfg(CLUSTERS_PROPERTY);
            for (String application : applications) {
                ret.put("java cluster: " + application.trim(), application.trim());
            }
        }
        return ret;
    }
    
    private void sendElement(Element e) {
        String payload = jsonWriter.write(Collections.singletonList(e));
        //we convert counter types to gauges because the data coversion is done by us.
        restClient.post(payload.replace(TYPE_COUNTER_JSON, TYPE_GAUGE_JSON));
    }
}
