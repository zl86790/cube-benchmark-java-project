package com.ecshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EcShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcShopApplication.class, args);
    }
}
