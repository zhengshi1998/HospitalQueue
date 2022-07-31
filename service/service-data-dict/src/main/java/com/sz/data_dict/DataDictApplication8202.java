package com.sz.data_dict;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.sz")
@EnableDiscoveryClient
public class DataDictApplication8202 {
    public static void main(String[] args) {
        SpringApplication.run(DataDictApplication8202.class, args);
    }
}
