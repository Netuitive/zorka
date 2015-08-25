/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jitlogic.zorka.core.integ.netuitive;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author john.king
 */
public class ZorkaStatsDataStorage {
    public static volatile Element systemStats = null;
    public static volatile Element methodStats = null;
    public static volatile Map<String, Sample> currentSamples = null;
    public static volatile Map<String, Sample> historicalSamples = null;
    public static volatile Map<String, Metric> metrics = null;
    
    public static void resetMaps() {
        currentSamples = new HashMap<String, Sample>();
        if (historicalSamples == null) {
            historicalSamples = new HashMap<String, Sample>();
        }
        metrics = new HashMap<String, Metric>();
    }
    
    public static void newInterval(){
        currentSamples = new HashMap<String, Sample>();
        metrics = new HashMap<String, Metric>();
        if(systemStats != null){
            systemStats.clearMetricsAndSamples();
        }
        if(methodStats != null){
            methodStats.clearMetricsAndSamples();
        }
    }
    
    public static Element prepareForExport(){
        Set<Sample> samples = new HashSet<Sample>();
        for(Sample sample : currentSamples.values()){
            samples.add(sample);
        }
        Set<Metric> exportMetrics = new HashSet<Metric>();
        for(Metric metric : metrics.values()){
            exportMetrics.add(metric);
        }
        Element e = systemStats;
        e.merge(methodStats);
        e.setSamples(samples);
        e.setMetrics(exportMetrics);
        return e;
    }
}
