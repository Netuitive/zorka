package com.jitlogic.zorka.core.integ.netuitive;

import com.jitlogic.zorka.common.util.ZorkaConfig;
import com.jitlogic.zorka.core.ZorkaLib;

public abstract class AbstractStatsReport implements StatsReport {

    protected ZorkaConfig config;
    protected ZorkaLib zorka;
    protected ElementBuilder elementBuilder;

    public AbstractStatsReport(ZorkaConfig config, ZorkaLib zorka) {
        this.config = config;
        this.zorka = zorka;
        String host = config.stringCfg("zorka.hostname", "localhost");
        String application = config.stringCfg("zorka.application", "My Application");
        String elementId = host + " java: " + application.trim();
        this.elementBuilder = new ElementBuilder(elementId, application, "JVM");
    }

    @Override
    public abstract Element collect(Long timestamp);
}
