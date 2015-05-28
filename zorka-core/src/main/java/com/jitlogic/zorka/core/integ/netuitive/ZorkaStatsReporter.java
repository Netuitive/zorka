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

    private String baseUrl;
    private String apiKey;
    private long interval;

    private ZorkaConfig config;

    private RestClient restClient;

    private ScheduledExecutorService scheduler;
    private ScheduledFuture future;
    private ZorkaStatsSendTask task;

    private ZorkaLib zorka;

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
        this.restClient = new RestClient(
                config.stringCfg("netuitive.api.url", "http://localhost:8000"),
                config.stringCfg("netuitive.api.key", "433f1b264dead28fa093e3676728939e"));
        this.interval = config.longCfg("netuitive.api.interval", 60L);
        this.task = new ZorkaStatsSendTask(
                config,
                zorka,
                restClient);
    }

    public void start() {
        future = scheduler.scheduleAtFixedRate(task, 0, interval, TimeUnit.SECONDS);
    }

    public void restart() {
        this.shutdown();
        this.init();
        this.start();
    }

    @Override
    public void shutdown() {
        if (future != null && !future.isCancelled() && !future.isDone()) {
            future.cancel(true);
        }
    }
}
