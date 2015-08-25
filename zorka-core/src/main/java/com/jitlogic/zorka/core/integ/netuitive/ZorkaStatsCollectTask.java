package com.jitlogic.zorka.core.integ.netuitive;

import com.jitlogic.zorka.common.util.ZorkaConfig;
import com.jitlogic.zorka.common.util.ZorkaLog;
import com.jitlogic.zorka.common.util.ZorkaLogger;
import com.jitlogic.zorka.core.ZorkaLib;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ZorkaStatsCollectTask implements Runnable{
    
    private final static ZorkaLog log = ZorkaLogger.getLog(ZorkaStatsCollectTask.class);
    
    
    private static JvmSystemStatsReport systemStatsReport;
    private static JvmMethodCallStatsReport methodCallStatsReport;
    
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
            Long timestamp = System.currentTimeMillis();
            processElements();
            Long finished = System.currentTimeMillis();
            log.debug(ZorkaLogger.ZPM_DEBUG, "finished collecting zorka stats using %d ms", finished - timestamp);
        }
        
    }
    /**
     * this should ONLY be called in a synchronized(ZorkaStatsDataStorage.class) block!!
     */
    private static void processElements(){
        
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
    
    private static void processElement(Element element) {
        //add new metrics
        for (Metric metric : element.getMetrics()) {
            if (!ZorkaStatsDataStorage.metrics.containsKey(metric.getId())) {
                ZorkaStatsDataStorage.metrics.put(metric.getId(), metric);
            }
        }

        //update current metrics
        for (Sample sample : element.getSamples()) {
            Double val = sample.getVal();
            Metric metric = ZorkaStatsDataStorage.metrics.get(sample.getMetricId());
            if (metric != null) {
                if (metric.getType().equals("COUNTER")) {
                    Sample oldSample = ZorkaStatsDataStorage.historicalSamples.get(sample.getMetricId());
                    if (oldSample != null) {
                        val = val - oldSample.getVal();
                    }
                }
            }
            //if we don't have a sample yet
            if (!ZorkaStatsDataStorage.currentSamples.containsKey(sample.getMetricId())) {
                Sample newSample;
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
                    Logger.getLogger(ZorkaStatsCollectTask.class.getName()).log(Level.SEVERE, null, ex);
                }

            } //if we already have a sample
            else {
                Sample newSample = ZorkaStatsDataStorage.currentSamples.get(sample.getMetricId());
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
            //set historical sample data to keep track of counters
            ZorkaStatsDataStorage.historicalSamples.put(sample.getMetricId(), sample);
        }
    }
    
}
