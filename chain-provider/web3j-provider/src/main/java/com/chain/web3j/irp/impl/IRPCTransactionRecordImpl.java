package com.chain.web3j.irp.impl;

import com.chain.entity.Transaction;
import com.chain.rpc.IRPCTransactionRecord;
import com.chain.web3j.service.TransactionRecordService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author LiYongQiang
 * @Descrition  钱包
 * @Date 2021/9/23 5:53 下午
 */

@DubboService
public class IRPCTransactionRecordImpl implements IRPCTransactionRecord {

    @Autowired
    TransactionRecordService transactionRecordService;

    @Override
    public void addTransactionRecord(Transaction transaction) {
        transactionRecordService.addTransactionRecord(transaction);
    }

}
