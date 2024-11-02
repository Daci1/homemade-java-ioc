package com.homemade.ioc.models;

import java.util.Objects;

public record ClassWithQualifier(Class<?> clazz, String qualifier) {
    private final static String defaultValue = "default";

    public ClassWithQualifier(Class<?> clazz) {
        this(clazz, "default");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassWithQualifier that = (ClassWithQualifier) o;
        return Objects.equals(clazz, that.clazz) && Objects.equals(qualifier, that.qualifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, qualifier);
    }
}
