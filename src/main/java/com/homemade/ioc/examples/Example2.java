package com.homemade.ioc.examples;

import com.homemade.ioc.decorators.Inject;
import com.homemade.ioc.decorators.Injectable;

@Injectable
public class Example2 {
    @Inject
    private Example3 example3;

    public void printSomething() {
        System.out.println("Hello from Example2");
        example3.printSomething();
    }
}
