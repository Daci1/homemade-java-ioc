package com.homemade.ioc.examples;

import com.homemade.ioc.decorators.Inject;
import com.homemade.ioc.decorators.Injectable;

@Injectable
public class Example3 {

    public void printSomething() {
        System.out.println("Hello from Example3");
    }
}
