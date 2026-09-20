package com.nova.membership;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NovaMembershipApplication {
    public static void main(String[] args) {
        SpringApplication.run(NovaMembershipApplication.class, args);
        System.out.println("Nova membership backend application started successfully.");
    }
}
