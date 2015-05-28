package com.jitlogic.zorka.core.integ.netuitive;


public class Sample {
    private String metricId;

    /**
     * sample timestamp in epoch milli-seconds.
     */
    private Long timestamp;

    private Double val;

    public static Sample createSample(String metricId, Long timestamp, Double val) {
        return new Sample(metricId, timestamp, val);
    }

    public Sample(String metricId, Long timestamp, Double val) {
        this.metricId = metricId;
        this.timestamp = timestamp;
        this.val = val;
    }

    public String getMetricId() {
        return metricId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Double getVal() {
        return val;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sample sample = (Sample) o;

        if (!metricId.equals(sample.metricId)) return false;
        if (!timestamp.equals(sample.timestamp)) return false;
        return val.equals(sample.val);

    }

    @Override
    public int hashCode() {
        int result = metricId.hashCode();
        result = 31 * result + timestamp.hashCode();
        result = 31 * result + val.hashCode();
        return result;
    }
}
