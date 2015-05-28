package com.jitlogic.zorka.core.integ.netuitive;

import com.jitlogic.zorka.common.stats.MethodCallStatistic;
import com.jitlogic.zorka.common.stats.MethodCallStatistics;
import com.jitlogic.zorka.common.util.JSONWriter;
import com.jitlogic.zorka.common.util.ZorkaConfig;
import com.jitlogic.zorka.common.util.ZorkaLog;
import com.jitlogic.zorka.common.util.ZorkaLogger;
import com.jitlogic.zorka.common.util.ZorkaUtil;
import com.jitlogic.zorka.core.ZorkaLib;

import java.util.Collections;
import java.util.Map;

public class ZorkaStatsSendTask implements Runnable {

    /* Logger */
    private final ZorkaLog log = ZorkaLogger.getLog(ZorkaStatsSendTask.class);

    private RestClient restClient;
    private JSONWriter jsonWriter;

    private JvmEnvironmentReport environmentReport;
    private JvmSystemStatsReport systemStatsReport;
    private JvmMethodCallStatsReport methodCallStatsReport;

    private Element e;

    public ZorkaStatsSendTask(
            ZorkaConfig config,
            ZorkaLib zorka,
            RestClient restClient) {
        this.environmentReport = new JvmEnvironmentReport(config, zorka);
        this.systemStatsReport = new JvmSystemStatsReport(config, zorka);
        this.methodCallStatsReport = new JvmMethodCallStatsReport(config, zorka);
        this.restClient = restClient;
        this.jsonWriter = new JSONWriter(false);

        //initialize element with static envrionement information
        e = this.environmentReport.collect(System.currentTimeMillis());
    }

    @Override
    public void run() {
        log.debug(ZorkaLogger.ZPM_DEBUG, "start reporting zorka stats");
        Long timestamp = System.currentTimeMillis();
        try {
            Element systemStats = systemStatsReport.collect(timestamp);
            e.merge(systemStats);
            Element methodStats = methodCallStatsReport.collect(timestamp);
            e.merge(methodStats);
            if (e.getSamples().size() > 0) {
                String payload = jsonWriter.write(Collections.singletonList(e));
                restClient.post(payload);
            }
        } catch (Exception e) {
            log.error(ZorkaLogger.ZPM_ERRORS, "finished reporting zorka stats with error: ", e);
        }
        Long finished = System.currentTimeMillis();
        log.debug(ZorkaLogger.ZPM_DEBUG, "finished reporting zorka stats using %d ms", finished - timestamp);
    }
}
