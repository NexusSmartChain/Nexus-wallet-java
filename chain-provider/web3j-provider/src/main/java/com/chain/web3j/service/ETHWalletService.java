package com.chain.web3j.service;

import com.chain.entity.CoinTransaction;
import com.chain.entity.CreateWallet;
import com.chain.entity.EthHDWallet;
import com.chain.entity.TokenTransaction;
import org.bitcoinj.crypto.DeterministicKey;
import org.web3j.protocol.core.methods.request.Transaction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * @author LiYongQiang
 * @Descrition
 * @Date 2021/9/28 10:23 上午
 */
public interface ETHWalletService {

    Map<String, Object> ethWalletGenerate(String mnemonic, String mnemonicPath, String passWord);

    Map<String, Object> ethWalletGenerate( String passWord);

    EthHDWallet createEthWallet(CreateWallet createWallet);

    /**
     * 通过助记词和id生成对应的子账户
     *
     * @param mnemonic 助记词
     * @param id       派生子id
     * @return 子账户key
     */
    DeterministicKey generateKeyFromMnemonicAndUid(String mnemonic, int id);

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
    String getTokenName(String contractAddress);


    /**
     * 查询代币精度
     */
    int getTokenDecimal(String contractAddress);

    /**
     * 生成助记词
     */
    String generateMnemonic();

    /**
     * 代币转账
     */
   String tokenTransaction(TokenTransaction transaction);
}
