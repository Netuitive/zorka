package com.jitlogic.zorka.core.integ.netuitive;

import java.util.List;

/**
 *
 * @author john.king
 */

public class ComputedMetric {

    List<String> inputMetrics;
    Metric metric;
    Class<? extends ComputedMetricCallable<Sample>> function;

    public ComputedMetric(List<String> inputMetrics, Class<? extends ComputedMetricCallable<Sample>> function, Metric metric) {
        this.inputMetrics = inputMetrics;
        this.function = function;
        this.metric = metric;
    }

    public List<String> getInputMetrics() {
        return inputMetrics;
    }

    public void setInputMetrics(List<String> inputMetrics) {
        this.inputMetrics = inputMetrics;
    }

    public Class<? extends ComputedMetricCallable<Sample>> getFunction() {
        return function;
    }

    public void setFunction(Class<? extends ComputedMetricCallable<Sample>> function) {
        this.function = function;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }
    
    
}
