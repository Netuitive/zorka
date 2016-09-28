package com.jitlogic.zorka.core.integ.netuitive;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 *
 * @author john.king
 */
public abstract class ComputedMetricCallable<Sample> implements Callable<Sample>{

    final Map<String, Sample> samples;
    final Metric metric;
    final Long timestamp;

    public ComputedMetricCallable(Map<String, Sample> samples, Metric metric, Long timestamp) {
        this.samples = samples;
        this.metric = metric;
        this.timestamp = timestamp;
    }

    @Override
    public abstract Sample call() throws Exception;
}
