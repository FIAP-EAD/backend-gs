package com.backend.gs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class
})
public class BackendGsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendGsApplication.class, args);
    }
}

