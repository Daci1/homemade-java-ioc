package com.homemade.ioc.examples;

import com.homemade.ioc.decorators.Inject;
import com.homemade.ioc.decorators.Injectable;

@Injectable
public class Example1 {
    @Inject
    private Example2 example2;

    public void printSomething() {
        System.out.println("Hello from Example1");
        example2.printSomething();
    }
}
