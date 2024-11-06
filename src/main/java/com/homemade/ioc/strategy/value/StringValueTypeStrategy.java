package com.homemade.ioc.strategy.value;

public class StringValueTypeStrategy implements ValueTypeStrategy<String> {
    @Override
    public String convertToValueType(String value) {
        return value;
    }
}

