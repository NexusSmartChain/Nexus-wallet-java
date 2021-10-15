package com.chain.web3j.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.http.HttpService;

/**
 * @author LiYongQiang
 * @Descrition
 * @Date 2021/9/27 11:04 上午
 */

@Configuration
public class Web3jConfig {

    //@Value("${eth.rinked.url}")
    private String url = "https://kovan.infura.io/v3/74d3de6db014405388a32e51189fb6fd";


    public static String ETH_TYPE = "m/44'/60'/0'/0/0";

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(url));
    }

    @Bean
    public Admin admin() {
        return Admin.build(new HttpService(url));
    }

//    @Bean
//    public Geth geth(){
//        return Geth.build(new HttpService(url));
//    }
}
