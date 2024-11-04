package com.homemade.ioc.examples;

import com.homemade.ioc.annotations.Inject;
import com.homemade.ioc.annotations.Injectable;

@Injectable(value = "Example2")
public class Example2 implements InterfaceExample{
    @Inject
    private Example3 example3;

    public void printSomething() {
        System.out.println("Hello from Example2");
        example3.printSomething();
    }
}
