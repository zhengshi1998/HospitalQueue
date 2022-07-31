package com.sz.service_hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.sz")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.sz")
public class ServiceHospitalApplication8201 {
    public static void main(String[] args) {
            SpringApplication.run(ServiceHospitalApplication8201.class, args);
    }
}
