package com.chain.web3j.service.impl;

import com.chain.entity.CoinTransaction;
import com.chain.entity.Transaction;
import com.chain.web3j.mapper.TransactionRecordMapper;
import com.chain.web3j.service.TransactionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author LiYongQiang
 * @Descrition
 * @Date 2021/9/22 7:02 下午
 */
@Service
public class TransactionRecordServiceImpl implements TransactionRecordService {

    @Autowired
    TransactionRecordMapper transactionRecordMapper;

    @Override
    public void addTransactionRecord(Transaction transaction) {
        transactionRecordMapper.addTransactionRecord(transaction);
    }
}
