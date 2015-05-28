package com.jitlogic.zorka.core.integ.netuitive;

public class ElementBuilder {
    private Element element;

    public ElementBuilder(String id, String name, String type) {
        this.element = new Element(id, name, type);
    }

    public ElementBuilder attribute(String name, Object value) {
        element.addAttribute(name, value);
        return this;
    }

    public ElementBuilder tag(String name, String value) {
        element.addTag(name, value);
        return this;
    }

    public ElementBuilder relation(String fqn) {
        element.addRelation(fqn);
        return this;
    }

    public ElementBuilder metric(String id) {
        element.addMetric(id);
        return this;
    }

    public ElementBuilder metric(String id, String name, String type, String unit) {
        element.addMetric(id, name, type, unit);
        return this;
    }

    public ElementBuilder sample(String metricId, Long timestamp, Double val) {
        element.addSample(metricId, timestamp, val);
        return this;
    }

    public ElementBuilder sample(String metricId, Long timestamp, Double val, boolean addMetric) {
        if (addMetric) {
            element.addMetric(metricId);
        }
        element.addSample(metricId, timestamp, val);
        return this;
    }

    public Element build() {
        return element;
    }
}
