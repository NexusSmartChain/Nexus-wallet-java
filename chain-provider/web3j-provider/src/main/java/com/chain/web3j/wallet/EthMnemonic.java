package com.chain.web3j.wallet;

import java.security.SecureRandom;

/**
 * @author LiYongQiang
 * @Descrition
 * @Date 2021/9/28 12:22 下午
 */
public class EthMnemonic {

    /**
     * 通用的以太坊基于bip44协议的助记词路径 （imtoken jaxx Metamask myetherwallet）
     */
    private static String ETH_TYPE = "m/44'/60'/0'/0/0";

    private static SecureRandom secureRandom = new SecureRandom();
}
