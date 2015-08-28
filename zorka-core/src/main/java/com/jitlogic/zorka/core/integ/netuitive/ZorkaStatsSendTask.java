package com.jitlogic.zorka.core.integ.netuitive;

import com.jitlogic.zorka.common.util.JSONWriter;
import com.jitlogic.zorka.common.util.ZorkaConfig;
import com.jitlogic.zorka.common.util.ZorkaLog;
import com.jitlogic.zorka.common.util.ZorkaLogger;
import com.jitlogic.zorka.core.ZorkaLib;
import java.util.Collections;

public class ZorkaStatsSendTask implements Runnable {

    /* Logger */
    private final ZorkaLog log = ZorkaLogger.getLog(ZorkaStatsSendTask.class);

    private final RestClient restClient;
    private final JSONWriter jsonWriter;
    private final JvmEnvironmentReport environmentReport;
    private static final String TYPE_COUNTER_JSON = "\"type\":\"COUNTER\"";
    private static final String TYPE_GAUGE_JSON = "\"type\":\"GAUGE\"";
    Element e = null;

    public ZorkaStatsSendTask(
            ZorkaConfig config,
            ZorkaLib zorka,
            RestClient restClient) {
        this.restClient = restClient;
        this.jsonWriter = new JSONWriter(false);
        this.environmentReport = new JvmEnvironmentReport(config, zorka);
        
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
                e = this.environmentReport.collect(System.currentTimeMillis());
                if (e != null) {
                    e.merge(ZorkaStatsDataStorage.prepareForExport());
                    if (e.getSamples().size() > 0) {
                        String payload = jsonWriter.write(Collections.singletonList(e));
                        //we convert counter types to gauges because the data coversion is done by us.
                        restClient.post(payload.replace(TYPE_COUNTER_JSON, TYPE_GAUGE_JSON));
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
}
