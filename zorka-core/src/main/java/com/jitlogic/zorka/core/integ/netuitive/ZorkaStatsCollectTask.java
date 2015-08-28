package com.jitlogic.zorka.core.integ.netuitive;

import com.jitlogic.zorka.common.util.ZorkaConfig;
import com.jitlogic.zorka.common.util.ZorkaLog;
import com.jitlogic.zorka.common.util.ZorkaLogger;
import com.jitlogic.zorka.core.ZorkaLib;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ZorkaStatsCollectTask implements Runnable{
    
    private final static ZorkaLog log = ZorkaLogger.getLog(ZorkaStatsCollectTask.class);
    
    
    private final JvmSystemStatsReport systemStatsReport;
    private final JvmMethodCallStatsReport methodCallStatsReport;
    
    public ZorkaStatsCollectTask(
            ZorkaConfig config,
            ZorkaLib zorka){
        this.systemStatsReport = new JvmSystemStatsReport(config, zorka);
        this.methodCallStatsReport = new JvmMethodCallStatsReport(config, zorka);
    }

    @Override
    public void run() {
        
        synchronized(ZorkaStatsDataStorage.class){
            log.debug(ZorkaLogger.ZPM_DEBUG, "start collecting zorka stats");
            Long start = System.currentTimeMillis();
            processElements();
            Long finish = System.currentTimeMillis();
            log.debug(ZorkaLogger.ZPM_DEBUG, "finished collecting zorka stats using %d ms", finish - start);
        }
        
    }
    
    private void processElements(){
        
        //system
        Element curSystemStats = systemStatsReport.collect(System.currentTimeMillis());
        if(curSystemStats != null){
            ZorkaStatsDataStorage.systemStats = curSystemStats;
            processElement(curSystemStats);
        }
        
        //methods
        Element curMethodStats = methodCallStatsReport.collect(System.currentTimeMillis());
        if(curSystemStats != null){
            ZorkaStatsDataStorage.methodStats = curMethodStats;
            processElement(curMethodStats);
        }
        
    }
    
    private void processElement(Element element) {
        //add new metrics
        for (Metric metric : element.getMetrics()) {
            if (!ZorkaStatsDataStorage.metrics.containsKey(metric.getId())) {
                ZorkaStatsDataStorage.metrics.put(metric.getId(), metric);
            }
        }

        //update current metrics
        for (Sample sample : element.getSamples()) {
            if (sample != null) {
                Double val = sample.getVal();
                if (val != null) {
                    Metric metric = ZorkaStatsDataStorage.metrics.get(sample.getMetricId());
                    //adjust val to convert counters to gauges
                    if (metric != null) {
                        if (metric.getType().equals("COUNTER")) {
                            Sample oldSample = ZorkaStatsDataStorage.historicalSamples.get(sample.getMetricId());
                            if (oldSample != null) {
                                val = val - oldSample.getVal();
                            }
                            //set historical sample data to keep track of counters
                            ZorkaStatsDataStorage.historicalSamples.put(sample.getMetricId(), sample);
                        }
                    }
                    //if we don't have a sample yet
                    Sample newSample;
                    if (!ZorkaStatsDataStorage.currentSamples.containsKey(sample.getMetricId())) {
                        try {
                            newSample = (Sample) sample.clone();
                            newSample.setAvg(val);
                            newSample.setCnt(1.0);
                            newSample.setMax(val);
                            newSample.setMin(val);
                            newSample.setSum(val);
                            newSample.setVal(null);
                            ZorkaStatsDataStorage.currentSamples.put(newSample.getMetricId(), newSample);
                        } catch (CloneNotSupportedException ex) {
                            log.error(ZorkaLogger.ZAG_ERRORS, "could not clone sample " + sample.getMetricId());
                        }

                    } //if we already have a sample
                    else {
                        newSample = ZorkaStatsDataStorage.currentSamples.get(sample.getMetricId());
                        newSample.setAvg((newSample.getAvg() * newSample.getCnt() + val) / (newSample.getCnt() + 1));
                        newSample.setSum(newSample.getSum() + val);
                        if (val < newSample.getMin()) {
                            newSample.setMin(val);
                        }
                        if (val > newSample.getMax()) {
                            newSample.setMax(val);
                        }
                        newSample.setCnt(newSample.getCnt() + 1);
                        newSample.setVal(null);
                    }
                }
            }
        }
    }

}
