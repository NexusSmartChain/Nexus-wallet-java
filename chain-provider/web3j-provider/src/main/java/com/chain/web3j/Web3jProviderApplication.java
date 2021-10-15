package com.chain.web3j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class Web3jProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(Web3jProviderApplication.class, args);
    }

}
