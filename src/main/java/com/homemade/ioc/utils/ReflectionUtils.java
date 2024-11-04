package com.homemade.ioc.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ReflectionUtils {
    public static Set<Class<?>> getAllTypes(Class<?> clazz) {
        Set<Class<?>> interfaces = new HashSet<>();

        addInterfaceAndSuperInterfaces(clazz, interfaces);
        return interfaces;
    }

    private static void addInterfaceAndSuperInterfaces(Class<?> clazz, Set<Class<?>> allTypes) {
        if (!allTypes.contains(clazz)) {
            allTypes.add(clazz);

            for (Class<?> superIface : ReflectionUtils.getInterfacesAndExtendedClasses(clazz)) {
                addInterfaceAndSuperInterfaces(superIface, allTypes);
            }
        } else {
              Set<Class<?>> remainingInterfaces = allTypes.stream().filter(i -> !allTypes.contains(i)).collect(Collectors.toSet());
              if(!remainingInterfaces.isEmpty()) {
                  addInterfaceAndSuperInterfaces(clazz, remainingInterfaces);
              }
        }
    }

    private static Set<Class<?>> getInterfacesAndExtendedClasses(Class<?> clazz) {
        Set<Class<?>> allTypes = new HashSet<>(Set.of(clazz.getInterfaces()));
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            allTypes.add(clazz.getSuperclass());
        }

        return allTypes;
    }
    // A: b,c
    // b: c,d
}
