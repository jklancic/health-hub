# Health Hub

A Spring Boot backend service for building RESTful APIs.

## Requirements

- Java 21
- Maven 3.x

## Running

```bash
mvn spring-boot:run
```

Server starts on `http://localhost:8080`.

## Adding a REST endpoint

Create a controller in `src/main/java/com/healthhub/`:

```java
package com.healthhub;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {

    @GetMapping("/example")
    public String example() {
        return "Hello, World!";
    }
}
```

## Build

```bash
mvn clean package
java -jar target/health-hub-0.0.1-SNAPSHOT.jar
```
