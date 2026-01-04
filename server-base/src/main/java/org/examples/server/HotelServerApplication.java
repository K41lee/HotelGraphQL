package org.examples.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.examples")
public class HotelServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(HotelServerApplication.class, args);
    }
}
