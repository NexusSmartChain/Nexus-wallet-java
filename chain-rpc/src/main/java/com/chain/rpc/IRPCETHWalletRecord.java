package com.chain.rpc;

import com.chain.entity.CreateWallet;
import com.chain.entity.EthHDWallet;
import com.chain.entity.TokenTransaction;
import org.bitcoinj.crypto.DeterministicKey;

import java.util.Map;

/**
 * @author LiYongQiang
 * @Descrition
 * @Date 2021/9/28 10:39 上午
 */
public interface IRPCETHWalletRecord {


    EthHDWallet createEthWallet(CreateWallet createWallet);

    /**
     * 代币转账
     */
    String tokenTransaction(TokenTransaction transaction);

    /**
     * 获取eth余额
     *
     * @param address 传入查询的地址
     * @return String 余额
     */
    String getEthBalance(String address);

    /**
     * 获取账户代币余额
     *
     * @param account     账户地址
     * @param coinAddress 合约地址
     * @return 代币余额 （单位：代币最小单位）
     */
    String getTokenBalance(String account, String coinAddress);


    /**
     * 查询代币符号
     */
    String getTokenSymbol(String contractAddress);

    /**
     * 查询代币名称
     */
    String getTokenName(String contractAddr);


    /**
     * 查询代币精度
     */
    int getTokenDecimal(String contractAddr);


}
