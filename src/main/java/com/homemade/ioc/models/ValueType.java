package com.homemade.ioc.models;

public enum ValueType {
    STRING(String.class);

    public final Class<?> associatedClass;

    ValueType(Class<?> associatedClass) {
        this.associatedClass = associatedClass;
    }

    public Class<?> getAssociatedClass() {
        return associatedClass;
    }
}
