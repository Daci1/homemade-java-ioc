package com.homemade.ioc.examples.valueExamples;

import com.homemade.ioc.annotations.Injectable;
import com.homemade.ioc.annotations.Value;

@Injectable
public class SomeClassWithValues {
    @Value("USER")
    private String privateValue;

    @Value("someValue:123")
    protected String protectedValue;

    @Value("PWD")
    public String publicValue;

    @Override
    public String toString() {
        return "SomeClassWithValues{" +
                "privateValue='" + privateValue + '\'' +
                ", protectedValue='" + protectedValue + '\'' +
                ", publicValue='" + publicValue + '\'' +
                '}';
    }
}
