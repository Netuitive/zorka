package com.jitlogic.zorka.core.integ.netuitive;

import com.jitlogic.zorka.common.ZorkaService;
import com.jitlogic.zorka.common.util.ZorkaConfig;
import com.jitlogic.zorka.common.util.ZorkaLog;
import com.jitlogic.zorka.common.util.ZorkaLogger;
import com.jitlogic.zorka.core.ZorkaLib;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * poll MethodCallStatistics from ZorkaStats mbeans and push data to Netuitive Cloud
 */
public class ZorkaStatsReporter implements ZorkaService {

    /* Logger */
    private final ZorkaLog log = ZorkaLogger.getLog(ZorkaStatsReporter.class);
    
    private final Long MINUTE_MILLIS = 60L * 1000L;

    private long interval;
    
    private final ScheduledExecutorService scheduler;

    private RestClient restClient;
    
    private final ZorkaConfig config;
    private final ZorkaLib zorka;

    private ScheduledFuture sendFuture;
    private ScheduledFuture collectFuture;
    private ZorkaStatsSendTask sendTask;
    private ZorkaStatsCollectTask collectTask;

    public ZorkaStatsReporter(
            ZorkaConfig config,
            ScheduledExecutorService scheduler,
            ZorkaLib zorka) {
        this.config = config;
        this.scheduler = scheduler;
        this.zorka = zorka;
        init();
    }

    private void init() {
        Integer socketTimeout = config.intCfg("netuitive.api.http.socketTimeout", 5000);
        Integer connectTimeout = config.intCfg("netuitive.api.http.connectTimeout", 5000);
        Integer connectionRequesTimeout = config.intCfg("netuitive.api.http.connectionRequestTimeout", 5000);
        this.restClient = new RestClient(
                config.stringCfg("netuitive.api.url", "http://localhost:8000"),
                config.stringCfg("netuitive.api.key", "433f1b264dead28fa093e3676728939e"),
                socketTimeout,
                connectTimeout,
                connectionRequesTimeout);
        this.interval = config.longCfg("netuitive.api.interval", 60L);
        this.sendTask = new ZorkaStatsSendTask(config, zorka, restClient);
        this.collectTask = new ZorkaStatsCollectTask(config, zorka);
        ZorkaStatsDataStorage.newInterval();
    }

    public void start() {
        sendFuture = scheduler.scheduleAtFixedRate(sendTask, calculateMinuteOffset(System.currentTimeMillis()), MINUTE_MILLIS, TimeUnit.MILLISECONDS);
        collectFuture = scheduler.scheduleAtFixedRate(collectTask, 0L, interval, TimeUnit.SECONDS);
    }

    public void restart() {
        this.init();
        this.start();
    }
    
    private Long calculateMinuteOffset(Long timestamp){
        Long ret = MINUTE_MILLIS - (timestamp % MINUTE_MILLIS);
        return ret;
    }

    @Override
    public void shutdown() {
        if (sendFuture != null) {
            sendFuture.cancel(true);
        }
        if(collectFuture != null){
            collectFuture.cancel(true);
        }
    }
}
