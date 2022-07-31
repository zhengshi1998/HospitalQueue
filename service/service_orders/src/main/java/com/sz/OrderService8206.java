package com.sz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.sz")
public class OrderService8206 {
    public static void main(String[] args) {
        SpringApplication.run(OrderService8206.class, args);
    }
}
