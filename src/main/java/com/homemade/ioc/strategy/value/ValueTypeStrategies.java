package com.homemade.ioc.strategy.value;

import com.homemade.ioc.models.ValueType;

import java.util.Map;
import java.util.Optional;

public class ValueTypeStrategies {
    private static final Map<ValueType, ValueTypeStrategy<?>> strategies = Map.ofEntries(
            Map.entry(ValueType.STRING, new StringValueTypeStrategy())
    );

    public static ValueTypeStrategy<?> getValueTypeStrategy(Class<?> clazz) {
        Optional<ValueType> strategy = strategies.keySet().stream().filter(key -> key.getAssociatedClass().equals(clazz)).findFirst();

        if (strategy.isPresent()) {
            return strategies.get(strategy.get());
        } else {
            throw new RuntimeException("Class type unsupported for @Value annotation %s".formatted(clazz.getTypeName()));
        }
    }
}
