package com.homemade.ioc.examples;

import com.homemade.ioc.annotations.Inject;
import com.homemade.ioc.annotations.Injectable;
import com.homemade.ioc.annotations.Qualifier;

@Injectable("Example1")
public class Example1 implements InterfaceExample{
    @Inject
    @Qualifier("Example2")
    private InterfaceExample example2;

    public void printSomething() {
        System.out.println("Hello from Example1");
        example2.printSomething();
    }
}
