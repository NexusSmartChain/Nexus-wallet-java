package com.chain.web3j.irp.impl;

import com.chain.entity.CreateWallet;
import com.chain.entity.EthHDWallet;
import com.chain.entity.TokenTransaction;
import com.chain.rpc.IRPCETHWalletRecord;
import com.chain.web3j.service.ETHWalletService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author LiYongQiang
 * @Descrition
 * @Date 2021/9/28 10:42 上午
 */

@DubboService(version = "${dubbo.service.version}")
public class IRPCETHWalletRecordImpl implements IRPCETHWalletRecord {

    @Autowired
    ETHWalletService ethWalletService;


    @Override
    public EthHDWallet createEthWallet(CreateWallet createWallet) {
        return ethWalletService.createEthWallet(createWallet);
    }

    @Override
    public String tokenTransaction(TokenTransaction transaction) {
        return ethWalletService.tokenTransaction(transaction);
    }

    @Override
    public String getEthBalance(String address) {
        return ethWalletService.getEthBalance(address);
    }

    @Override
    public String getTokenBalance(String account, String coinAddress) {
        return ethWalletService.getTokenBalance(account,coinAddress);
    }

    @Override
    public String getTokenSymbol(String contractAddress) {
        return ethWalletService.getTokenSymbol(contractAddress);
    }

    @Override
    public String getTokenName(String contractAddr) {
        return ethWalletService.getTokenName(contractAddr);
    }

    @Override
    public int getTokenDecimal(String contractAddr) {
        return ethWalletService.getTokenDecimal(contractAddr);
    }
}
