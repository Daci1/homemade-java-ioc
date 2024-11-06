package com.homemade.ioc.examples.multipleInterfacesExample;

import com.homemade.ioc.annotations.Injectable;
import com.homemade.ioc.annotations.Value;

@Injectable
public class BothInterfacesCar implements DrivableAndFillable, SomeInterface {
    @Value("USER:test")
    private String username;

    @Override
    public void drive() {
        System.out.println("Driving fillable Car");
    }

    @Override
    public void fill() {
        System.out.println("%s: Filling Car".formatted(username));
    }
}
