package com.jitlogic.zorka.core.integ.netuitive;

public class Attribute {
    private String name;
    private Object value;

    public static Attribute createAttribute(String name, Object value) {
        Attribute attribute = new Attribute();
        attribute.setName(name);
        attribute.setValue(value);

        return attribute;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attribute attribute = (Attribute) o;

        return getName().equals(attribute.getName());

    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
