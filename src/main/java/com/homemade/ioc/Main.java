package com.homemade.ioc;

import com.homemade.ioc.containers.HomemadeIocContainer;
import com.homemade.ioc.examples.Example1;

public class Main {
    public static void main(String[] args) {
        HomemadeIocContainer container = new HomemadeIocContainer();
        container.scanPackage("com.homemade.ioc");

        Example1 example1 = container.getComponent(Example1.class).get();
        example1.printSomething();
    }
}
