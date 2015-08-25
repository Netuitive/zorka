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

    private RestClient restClient;
    private JSONWriter jsonWriter;
    private JvmEnvironmentReport environmentReport;
    Element e;
    

    public ZorkaStatsSendTask(
            ZorkaConfig config,
            ZorkaLib zorka,
            RestClient restClient) {
        this.restClient = restClient;
        this.jsonWriter = new JSONWriter(false);
        this.environmentReport = new JvmEnvironmentReport(config, zorka);
        
    }

    @Override
    public void run() {
        
        synchronized (ZorkaStatsDataStorage.class) {
            log.debug(ZorkaLogger.ZPM_DEBUG, "start reporting zorka stats");
            Long timestamp = System.currentTimeMillis();
            try {
                e = this.environmentReport.collect(System.currentTimeMillis());
                e.merge(ZorkaStatsDataStorage.prepareForExport());
                if (e.getSamples().size() > 0) {
                    String payload = jsonWriter.write(Collections.singletonList(e));
                    //we convert counter types to gauges
                    restClient.post(payload.replace("\"type\":\"COUNTER\"", "\"type\":\"GAUGE\""));
                }
            } catch (Exception e) {
                log.error(ZorkaLogger.ZPM_ERRORS, "finished reporting zorka stats with error: ", e);
            }
            finally{
                e.clearMetricsAndSamples();
                ZorkaStatsDataStorage.newInterval();
            }
            Long finished = System.currentTimeMillis();
            log.debug(ZorkaLogger.ZPM_DEBUG, "finished reporting zorka stats using %d ms", finished - timestamp);
        }
        
    }
}
