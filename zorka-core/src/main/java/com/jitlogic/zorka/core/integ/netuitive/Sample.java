package com.jitlogic.zorka.core.integ.netuitive;

import java.util.Objects;


public class Sample implements Cloneable{
    private String metricId;

    /**
     * sample timestamp in epoch milli-seconds.
     */
    private Long timestamp;

    private Double val;
    
    private Double min;

    /**
     * Sample maximum value.
     */
    private Double max;

    /**
     * Sample average value.
     */
    private Double avg;

    /**
     * Sample sum value.
     */
    private Double sum;

    /**
     * The number values that contributed to the aggregated values of this datapoint.
     */
    private Double cnt;

    public static Sample createSample(String metricId, Long timestamp, Double val) {
        return new Sample(metricId, timestamp, val);
    }

    public Sample(String metricId, Long timestamp, Double val) {
        this.metricId = metricId;
        this.timestamp = timestamp;
        this.val = val;
        this.max = null;
        this.min = null;
        this.sum = null;
        this.cnt = null;
        this.avg = null;
    }
    
    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getAvg() {
        return avg;
    }

    public void setAvg(Double avg) {
        this.avg = avg;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public Double getCnt() {
        return cnt;
    }

    public void setCnt(Double cnt) {
        this.cnt = cnt;
    }
    
    public void setVal(Double val) {
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
        if (!checkDoubleEquality(val, sample.val)) return false;
        if (!checkDoubleEquality(avg, sample.avg)) return false;
        if (!checkDoubleEquality(cnt, sample.cnt)) return false;
        if (!checkDoubleEquality(max, sample.max)) return false;
        if (!checkDoubleEquality(min, sample.min)) return false;
        return checkDoubleEquality(sum, sample.sum);

    }
    
    private boolean checkDoubleEquality(Double a, Double b){
        if (a == null && b == null)return true;
        if (a == null && b != null)return false;
        if (b == null && a != null)return false;
        return a.equals(b);
    }

    @Override
    public int hashCode() {
        int result = metricId.hashCode();
        result = 31 * result + timestamp.hashCode();
        result = 31 * result + Objects.hashCode(val);
        result = 31 * result + timestamp.hashCode();
        result = 31 * result + Objects.hashCode(avg);
        result = 31 * result + Objects.hashCode(cnt);
        result = 31 * result + Objects.hashCode(max);
        result = 31 * result + Objects.hashCode(min);
        result = 31 * result + Objects.hashCode(sum);
        return result;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }
}
