package com.jitlogic.zorka.core.integ.netuitive;

import java.io.Serializable;

public class Tag implements Serializable {

    private String name;
    private String value;

    public static Tag createTag(String name, String value) {
        Tag tag = new Tag();
        tag.setName(name);
        tag.setValue(value);

        return tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        return getName().equals(tag.getName());

    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
