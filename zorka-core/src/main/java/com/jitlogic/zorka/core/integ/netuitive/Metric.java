package com.jitlogic.zorka.core.integ.netuitive;

public class Metric {
    private String id;
    private String name;
    private String type;
    private String unit;


    public static Metric createMetric(String id) {
        Metric metric = new Metric(id);
        return metric;
    }

    public static Metric createMetric(String id, String name, String type, String unit) {
        Metric metric = new Metric(id, name, type, unit);
        return metric;
    }

    public Metric(String id) {
        this.id = id;
    }

    public Metric(String id, String name, String type, String unit) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.unit = unit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Metric metric = (Metric) o;

        return getId().equals(metric.getId());

    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
