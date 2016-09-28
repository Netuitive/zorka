package com.jitlogic.zorka.core.integ.netuitive;

import java.util.Map;

/**
 *
 * @author john.king
 */
public class AverageCallTimeComputedMetric extends ComputedMetricCallable<Sample>{

    public AverageCallTimeComputedMetric(Map<String, Sample> samples, Metric metric, Long timestamp) {
        super(samples, metric, timestamp);
    }

    @Override
    public Sample call() throws Exception {
        Sample calls = null;
        Sample time = null;
        for (String key : this.samples.keySet()){
            if(key.endsWith(".time")){
                time = this.samples.get(key);
            }
            else if(key.endsWith(".calls")){
                calls = this.samples.get(key);
            }
        }
        //these samples will always exist or we wouldn't have been called;
        double callSum = calls.getSum() == 0 ? 0 : calls.getSum();
        double timeSum = time.getSum() == 0 ? 0 : time.getSum();
        double val = callSum == 0 ? 0 : timeSum/callSum;
        return Sample.createSample(metric.getId(), timestamp, val);
    }

}
