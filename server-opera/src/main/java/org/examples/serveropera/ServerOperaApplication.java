package org.examples.serveropera;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.examples.server", "org.examples.serveropera"})
public class ServerOperaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerOperaApplication.class, args);
    }

}
