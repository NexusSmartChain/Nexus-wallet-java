package com.chain.web3j.mapper;

import com.chain.entity.CoinTransaction;
import com.chain.entity.Transaction;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author LiYongQiang
 * @Descrition
 * @Date 2021/9/22 7:03 下午
 */
@Mapper
public interface TransactionRecordMapper {

    void addTransactionRecord(Transaction transaction);
}
