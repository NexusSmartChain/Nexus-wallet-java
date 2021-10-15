package com.chain.web3j.service;

import com.chain.entity.Transaction;

/**
 * @author LiYongQiang
 * @Descrition  交易记录服务
 * @Date 2021/9/22 6:54 下午
 */
public interface TransactionRecordService {

    /**
     * 添加交易记录
     * @param transaction
     */
    void addTransactionRecord(Transaction transaction);

}
