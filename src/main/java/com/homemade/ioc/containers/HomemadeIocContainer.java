package com.homemade.ioc.containers;

import java.lang.reflect.Field;
import java.util.*;

import com.homemade.ioc.decorators.Inject;
import com.homemade.ioc.decorators.Injectable;
import com.homemade.ioc.models.ClassWithQualifier;
import org.reflections.Reflections;

public class HomemadeIocContainer {
    // So far only singletons
    // So far no identifiers
    // So far not for interfaces I guess
    private final Map<Class<?>, Object> injectables = new HashMap<>();
    private final Map<ClassWithQualifier, Object> qualifiedInjectables = new HashMap<>();

    //TODO: add logic for @Value()

    public <T> Optional<T> getComponent(Class<T> clazz) {
        var instance = injectables.get(clazz);
        return instance == null ? Optional.empty() : Optional.of(clazz.cast(instance));
    }

    public void scanPackage(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> componentClasses = reflections.getTypesAnnotatedWith(Injectable.class);

        for (Class<?> clazz : componentClasses) {
            if (!clazz.isInterface()) {
                registerClass(clazz);
            }
        }
    }

    private void registerClass(Class<?> clazz) {
        assert clazz.isAnnotationPresent(Injectable.class);

        if (injectables.containsKey(clazz)) {
            return;
        }

        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            injectables.put(clazz, instance); //TODO: this works for current approach: no constructors

            List<Field> annotatedFields = Arrays.stream(clazz.getDeclaredFields())
                    .filter(c -> c.isAnnotationPresent(Inject.class)) //TODO: filter primitive values in the future
                    .toList();

            registerClassFields(annotatedFields);
            attachClassFields(instance, annotatedFields);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private void registerClassFields(List<Field> fields) {
        assert fields.stream().allMatch(field -> field.isAnnotationPresent(Inject.class)); // TODO: later also @Value

        fields.stream()
                .map(Field::getType)
                .forEach(this::registerClass);
    }

    private void attachClassFields(Object instance, List<Field> fields) {
        fields.forEach(field -> {
            Class<?> fieldClass = field.getType();

            try {
                var injectable = this.getComponent(fieldClass);
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

}
