package com.homemade.ioc.examples.multipleInterfacesExample;

import com.homemade.ioc.annotations.Injectable;

@Injectable
public class DrivableCar extends BothInterfacesCar implements Drivable {

    @Override
    public void drive() {
        System.out.println("Driving Car");
    }

    @Override
    public void fill() {
        System.out.println("Filling drivable Car");
    }
}
