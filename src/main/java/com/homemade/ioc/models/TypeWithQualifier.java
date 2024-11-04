package com.homemade.ioc.models;

import java.util.Objects;

public record TypeWithQualifier(Class<?> clazz, String qualifier) {
    private final static String defaultValue = "default"; // TODO: maybe extract this into a constant

    public TypeWithQualifier(Class<?> clazz) {
        this(clazz, defaultValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeWithQualifier that = (TypeWithQualifier) o;
        return Objects.equals(clazz, that.clazz) && Objects.equals(qualifier, that.qualifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, qualifier);
    }

    @Override
    public String toString() {
        return "TypeWithQualifier{" +
                "clazz=" + clazz +
                ", qualifier='" + qualifier + '\'' +
                '}';
    }
}
