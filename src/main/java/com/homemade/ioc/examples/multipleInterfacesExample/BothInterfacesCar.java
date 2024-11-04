package com.homemade.ioc.examples.multipleInterfacesExample;

import com.homemade.ioc.annotations.Injectable;

@Injectable
public class BothInterfacesCar implements DrivableAndFillable, SomeInterface {
}
