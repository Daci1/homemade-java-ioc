package com.homemade.ioc.containers;

import java.lang.reflect.Field;
import java.util.*;

import com.homemade.ioc.annotations.Inject;
import com.homemade.ioc.annotations.Injectable;
import com.homemade.ioc.annotations.Qualifier;
import com.homemade.ioc.models.TypeWithQualifier;
import com.homemade.ioc.utils.ReflectionUtils;
import org.reflections.Reflections;

public class HomemadeIocContainer {
    // So far only singletons
    private final Map<TypeWithQualifier, Object> injectables = new HashMap<>();

    //TODO: add logic for @Value()

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
        assert fields.stream().allMatch(field -> field.isAnnotationPresent(Inject.class)); // TODO: later also @Value

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
}
