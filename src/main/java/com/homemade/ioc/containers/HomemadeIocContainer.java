package com.homemade.ioc.containers;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

import com.homemade.ioc.annotations.Inject;
import com.homemade.ioc.annotations.Injectable;
import com.homemade.ioc.annotations.Qualifier;
import com.homemade.ioc.annotations.Value;
import com.homemade.ioc.models.TypeWithQualifier;
import com.homemade.ioc.strategy.value.ValueTypeStrategies;
import com.homemade.ioc.strategy.value.ValueTypeStrategy;
import com.homemade.ioc.utils.ReflectionUtils;
import org.reflections.Reflections;

public class HomemadeIocContainer {
    // So far only singletons
    private final Map<TypeWithQualifier, Object> injectables = new HashMap<>();
    private final Map<String, String> values = new HashMap<>();

    public <T> Optional<T> getComponent(Class<T> clazz) {
        var instance = injectables.get(new TypeWithQualifier(clazz));
        return instance == null ? Optional.empty() : Optional.of(clazz.cast(instance));
    }

    public <T> Optional<T> getComponent(Class<T> clazz, String qualifier) {
        var instance = injectables.get(new TypeWithQualifier(clazz, qualifier));
        return instance == null ? Optional.empty() : Optional.of(clazz.cast(instance));
    }

    public void scanPackage(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> componentClasses = reflections.getTypesAnnotatedWith(Injectable.class);

        for (Class<?> clazz : componentClasses) {
            registerClass(clazz); // TODO: could have just instantiated all of them and just inject fields after :? easier to check duplicates just saying
        }
    }

    private void registerClass(Class<?> clazz) {
        assert clazz.isAnnotationPresent(Injectable.class);

        if (clazz.isInterface() || injectables.containsKey(new TypeWithQualifier(clazz, clazz.getAnnotation(Injectable.class).value()))) {
            return;
        }

        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            injectables.put(new TypeWithQualifier(clazz, clazz.getAnnotation(Injectable.class).value()), instance);
            registerForAllClassTypes(clazz, instance);

            List<Field> annotatedInjectableFields = Arrays.stream(clazz.getDeclaredFields())
                    .filter(c -> c.isAnnotationPresent(Inject.class))
                    .toList();

            List<Field> annotatedValueFields = Arrays.stream(clazz.getDeclaredFields())
                    .filter(c -> c.isAnnotationPresent(Value.class)) //TODO: filter primitive values in the future
                    .toList();

            registerInjectableClassFields(annotatedInjectableFields);
            registerValueClassFields(instance, annotatedValueFields);
            attachClassFields(instance, annotatedInjectableFields);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


    private void registerInjectableClassFields(List<Field> fields) {
        assert fields.stream().allMatch(field -> field.isAnnotationPresent(Inject.class));

        fields.stream()
                .map(Field::getType)
                .forEach(this::registerClass);
    }

    private void attachClassFields(Object instance, List<Field> fields) {
        assert fields.stream().allMatch(field -> field.isAnnotationPresent(Inject.class));

        fields.forEach(field -> {
            Class<?> fieldClass = field.getType();

            try {
                Optional<?> injectable;
                Optional<String> qualifier = getQualifier(field);
                if (qualifier.isPresent()) {
                    injectable = this.getComponent(fieldClass, qualifier.get());
                } else {
                    injectable = this.getComponent(fieldClass);
                }

                if (injectable.isPresent()) {
                    field.setAccessible(true);
                    field.set(instance, injectable.get());
                } else {
                    throw new RuntimeException("Field " + field.getName() + " of class " + fieldClass.getName() + " not injectable");
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Optional<String> getQualifier(Field field) {
        if (field.isAnnotationPresent(Qualifier.class)) {
            return Optional.of(field.getAnnotation(Qualifier.class).value());
        }
        return Optional.empty();
    }

    private void registerForAllClassTypes(Class<?> clazz, Object instance) {
        assert clazz.isAnnotationPresent(Injectable.class);

        String qualifier = clazz.getAnnotation(Injectable.class).value();
        Set<Class<?>> allTypes = ReflectionUtils.getAllTypes(clazz);
        allTypes.forEach(type -> {
            if (!injectables.containsKey(new TypeWithQualifier(type, qualifier))) {
                injectables.put(new TypeWithQualifier(type, qualifier), instance);
            }
        });
    }

    private void registerValueClassFields(Object instance, List<Field> annotatedValueFields) {
        annotatedValueFields.forEach(field -> registerValueClassField(instance, field));
    }

    private void registerValueClassField(Object instance, Field annotatedValueField) {
        assert annotatedValueField.isAnnotationPresent(Value.class);

        ValueTypeStrategy<?> valueTypeStrategy = ValueTypeStrategies.getValueTypeStrategy(annotatedValueField.getType());

        String value = getValueForAnnotatedField(annotatedValueField);
        annotatedValueField.setAccessible(true);
        try {
            annotatedValueField.set(instance, valueTypeStrategy.convertToValueType(value));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String getValueForAnnotatedField(Field annotatedValueField) {
        assert annotatedValueField.isAnnotationPresent(Value.class);
        String annotationValue = annotatedValueField.getAnnotation(Value.class).value();

        boolean hasDefaultValue = Stream.of(annotationValue)
                .filter(str -> str.contains(":"))
                .map(str -> str.split(":"))
                .filter(splittedString -> splittedString.length > 1)
                .map(splittedString -> splittedString[1])
                .findFirst()
                .isPresent();

        if(hasDefaultValue) {
            String envVarKey = annotationValue.split(":")[0];
            String defaultValue = annotationValue.split(":")[1];
            Optional<String> envVar = Optional.ofNullable(System.getenv(envVarKey));
            return envVar.orElse(defaultValue);
        } else {
            Optional<String> envVar = Optional.ofNullable(System.getenv(annotationValue));
            return envVar.orElse("");
        }

    }
}
