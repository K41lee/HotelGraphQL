package org.examples.serverrivage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.examples.server", "org.examples.serverrivage"})
public class ServerRivageApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerRivageApplication.class, args);
    }

}
