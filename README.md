# Homemade IoC (Inversion of Control)

This project implements a simple IoC container to manage dependencies. The IoC container supports annotations to
facilitate dependency injection. The supported annotations are:

- `@Injectable`
- `@Inject`
- `@Qualifier`
- `@Value`

## Table of Contents

- [Annotations](#annotations)
    - [Injectable](#injectable)
    - [Inject](#inject)
    - [Qualifier](#qualifier)
    - [Value](#value)
- [Usage](#usage)
- [Example](#example)
- [License](#license)

## Annotations

### Injectable

Marks a class as injectable, allowing it to be managed by the IoC container. It can also accept a value that will
determine the qualifier of the class.

```java

@Injectable("mySpecialService")
public class MySpecialService implements MyService {
    // Class implementation
}
```

```java

@Injectable() // Will be marked as "default" qualifier
public class MySpecialService implements MyService {
    // Class implementation
}
```

### Inject

Used to inject an instance of an injectable class.

```java
public class MyController {
    @Inject
    private MyService myService;

    // Controller implementation
}
```

### Qualifier

Used to specify which implementation of an interface should be injected when there are multiple injectable classes for
the same type.

```java

import com.homemade.ioc.annotations.Inject;
import com.homemade.ioc.annotations.Qualifier;

@Injectable("basicService")
public class BasicService implements MyService {
    // Implementation
}

@Injectable("complexService")
public class ComplexService implements MyService {
    // Implementation
}

@Injectable
public class MyController {
    @Inject
    @Qualifier("basicService")
    private MyService myService;

    // Controller implementation
}
```

### Value

Used to inject environmental variables and configuration values, with support for default options.

```java
public class MyConfig {
    @Value("MY_ENV_VAR:default_value")
    private String configValue;

    // Configuration implementation
}
```

## Usage

To use the IoC container, ensure that all classes that need to be managed are annotated properly. In order
to register the injectables, call the method `scanPackage` on an HomemadeIocContainer object.

## Example

### Registering Classes

```java

import com.homemade.ioc.annotations.Inject;
import com.homemade.ioc.annotations.Injectable;

@Injectable("basicService")
public class BasicService implements MyService {
    // Implementation
}

@Injectable("advancedService")
public class AdvancedService implements MyService {
    // Implementation
}

@Injectable
public class MyLogger {
    // Implementation
}

public class MyApplication {
    @Inject
    @Qualifier("basicService")
    private MyService myService;

    @Inject
    private MyLogger myLogger;

    @Value("APP_NAME:MyApp")
    private String appName;

    public void run() {
        System.out.println("Running " + appName);
        myService.doStuff();
    }
}
```
### Scanning all classes of the project in order to register all injectables
```java
public class Main {
    public static void main(String[] args) {
        HomemadeIocContainer container = new HomemadeIocContainer();
        container.scanPackage("com.homemade.ioc");

        MyApplication application = container.getComponent(MyApplication.class).get();
        application.run();
    }
}
```

This example demonstrates how to configure the IoC container with multiple implementations, environment variable
injection, and automatic dependency resolution.

## License

This project is licensed under the MIT License. See the LICENSE file for more details.
