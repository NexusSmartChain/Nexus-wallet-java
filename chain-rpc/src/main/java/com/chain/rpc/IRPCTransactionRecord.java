package com.chain.rpc;

import com.chain.entity.Transaction;

/**
 * @author LiYongQiang
 * @Descrition
 * @Date 2021/9/23 5:52 下午
 */
public interface IRPCTransactionRecord {

    /**
     * 添加交易记录
     * @param transaction
     */
    void addTransactionRecord(Transaction transaction);
}
