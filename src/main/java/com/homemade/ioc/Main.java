package com.homemade.ioc;

import com.homemade.ioc.containers.HomemadeIocContainer;
import com.homemade.ioc.examples.Example1;
import com.homemade.ioc.examples.multipleInterfacesExample.BothInterfacesCar;
import com.homemade.ioc.examples.multipleInterfacesExample.DrivableAndFillable;
import com.homemade.ioc.examples.multipleInterfacesExample.DrivableCar;
import com.homemade.ioc.examples.multipleInterfacesExample.Fillable;
import com.homemade.ioc.examples.valueExamples.SomeClassWithValues;

public class Main {
    public static void main(String[] args) {
        HomemadeIocContainer container = new HomemadeIocContainer();
        container.scanPackage("com.homemade.ioc");

        Example1 example1 = container.getComponent(Example1.class, "Example1").get();
        example1.printSomething();

        DrivableAndFillable car = container.getComponent(BothInterfacesCar.class).get();
        car.drive();
        car.fill();

        Fillable fillableCar = container.getComponent(BothInterfacesCar.class).get();
        fillableCar.fill();

        DrivableCar drivableCar = container.getComponent(DrivableCar.class).get();
        drivableCar.drive();

        SomeClassWithValues someClassWithValues = container.getComponent(SomeClassWithValues.class).get();
        System.out.println(someClassWithValues);
    }
}
