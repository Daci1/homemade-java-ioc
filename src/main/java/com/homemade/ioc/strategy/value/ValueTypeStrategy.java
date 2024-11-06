package com.homemade.ioc.strategy.value;

public interface ValueTypeStrategy<T> {
    T convertToValueType(String value);
}
