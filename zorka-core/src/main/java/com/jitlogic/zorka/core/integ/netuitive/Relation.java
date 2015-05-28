package com.jitlogic.zorka.core.integ.netuitive;

public class Relation{
    private String fqn;

    public static Relation createRelation(String fqn) {
        return new Relation(fqn);
    }

    public Relation(String fqn) {
        this.fqn = fqn;
    }

    public String getFqn() {
        return fqn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Relation relation = (Relation) o;

        return fqn.equals(relation.fqn);

    }

    @Override
    public int hashCode() {
        return fqn.hashCode();
    }
}
