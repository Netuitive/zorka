package com.jitlogic.zorka.core.integ.netuitive;

import com.jitlogic.zorka.common.util.ZorkaConfig;
import com.jitlogic.zorka.common.util.ZorkaLog;
import com.jitlogic.zorka.common.util.ZorkaLogger;
import com.jitlogic.zorka.core.ZorkaLib;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

/**
 *
 * @author john.king
 */
public class ComputedStatsReport extends AbstractStatsReport{

    private final ZorkaLog log = ZorkaLogger.getLog(ComputedStatsReport.class);
    
    public ComputedStatsReport(ZorkaConfig config, ZorkaLib zorka) {
        super(config, zorka);
    }

    @Override
    public Element collect(Long timestamp) {
        log.debug(ZorkaLogger.ZPM_DEBUG, "start collecting computed stats");
        elementBuilder.clearMetricsAndSamples();
        for(Entry<String, ComputedMetric> e : ZorkaStatsDataStorage.computedMetrics.entrySet()){
            Map<String, Sample> samples = new HashMap<String, Sample>();
            int sampleCount = 0;
            for(String id : e.getValue().getInputMetrics()){
                samples.put(id, ZorkaStatsDataStorage.currentSamples.get(id));
                sampleCount++;
            }
            if(sampleCount < e.getValue().getInputMetrics().size()){
                continue;
            }
            try {
                Sample sample = getCallable(e.getValue(), samples, timestamp).call();
                Metric metric = e.getValue().getMetric();
                elementBuilder.metric(metric)
                               .sample(sample);
            } catch (Exception ex) {
                log.error(ZorkaLogger.ZAG_ERRORS, "unable to collect computed metric " + e.getKey(), ex);
            }
        }
        log.debug(ZorkaLogger.ZPM_DEBUG, "finished collecting computed stats");
        return elementBuilder.build();
    }
    
    private Callable<Sample> getCallable(ComputedMetric metric, Map<String, Sample> samples, Long timestamp){
        try {
            Constructor<?> ctor = metric.getFunction().getConstructor(Map.class, Metric.class, Long.class);
            return (Callable<Sample>) ctor.newInstance(new Object[] { samples, metric.getMetric(), timestamp});
        } catch (Exception e){
            throw new RuntimeException("unable to create computed metric callable", e);
        }
    }

}
