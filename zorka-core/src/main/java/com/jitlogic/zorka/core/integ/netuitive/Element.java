package com.jitlogic.zorka.core.integ.netuitive;

import java.util.HashSet;
import java.util.Set;

public class Element {
    private String id;
    private String name;
    private String type;
    private Set<Attribute> attributes = new HashSet<Attribute>();
    private Set<Tag> tags = new HashSet<Tag>();
    private Set<Metric> metrics = new HashSet<Metric>();
    private Set<Relation> relations = new HashSet<Relation>();
    private Set<Sample> samples = new HashSet<Sample>();

    public Element(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public void addAttribute(String name, Object value) {
        attributes.add(Attribute.createAttribute(name, value));
    }

    public void setMetrics(Set<Metric> metrics) {
        this.metrics = metrics;
    }

    public void addTag(String name, String value) {
        tags.add(Tag.createTag(name, value));
    }

    public void addRelation(String fqn) {
        relations.add(Relation.createRelation(fqn));
    }

    public void addMetric(String id) {
        metrics.add(Metric.createMetric(id));
    }
    
    public void addMetric(Metric metric) {
        metrics.add(metric);
    }

    public void addMetric(String id, String name, String type, String unit) {
        metrics.add(Metric.createMetric(id, name, type, unit));
    }

    public void addSample(String metricId, Long timestamp, Double val) {
        samples.add(Sample.createSample(metricId, timestamp, val));
    }
    
    public void addSample(Sample sample) {
        samples.add(sample);
    }

    public void merge(Element e) {
        if (e != null && e.id != null && e.id.equals(this.id)) {
            this.attributes.addAll(e.getAttributes());
            this.tags.addAll(e.getTags());
            this.relations.addAll(e.getRelations());
            this.metrics.addAll(e.getMetrics());
            this.samples.addAll(e.getSamples());
        }
    }

    public void clearMetricsAndSamples() {
        this.metrics.clear();
        this.samples.clear();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Set<Attribute> getAttributes() {
        return attributes;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public Set<Metric> getMetrics() {
        return metrics;
    }

    public Set<Relation> getRelations() {
        return relations;
    }

    public Set<Sample> getSamples() {
        return samples;
    }
    
    public void setSamples(Set<Sample> samples){
        this.samples = samples;
    }
    
    
}
