package com.chain.web3j.filter;

import com.chain.common.EntityToDtoUtil;
import com.chain.entity.CoinTransaction;
import com.chain.entity.Transaction;
import com.chain.web3j.service.impl.TransactionRecordServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import javax.annotation.PostConstruct;


/**
 * @author LiYongQiang
 * @Descrition filter相关,监听区块、交易,所有监听都在Web3jRx中
 * @Date 2021/9/22 6:46 下午
 */
@Component
public class Filter {
    Logger logger = LoggerFactory.getLogger(Filter.class);

    @Value("${web3j.rpcURL}")
    String rpcURL;

    @Autowired
    private TransactionRecordServiceImpl transactionRecordService;

    /**
     * 新交易监听
     */
    @PostConstruct
    public void newTransactionFilter() {
        Web3j web3j = initWeb3j();
        try {
            web3j.transactionObservable().subscribe(transaction -> {
                Transaction transactionPojo = EntityToDtoUtil.copyObject(transaction, Transaction.class);
                transactionRecordService.addTransactionRecord(transactionPojo);
            });
        }catch (Exception e){
            newTransactionFilter();
            logger.info("e {}",e.getMessage());
        }

    }

    /**
     * 新交易监听
     */
//    @PostConstruct
//    private void newBlockFilter() {
//        Web3j web3j = initWeb3j();
//        Subscription subscription = web3j.
//                blockObservable(false).
//                subscribe(block -> {
//                    List<CoinTransaction> transactions = EntityToDtoUtil.copyList(block.getBlock().getTransactions(), CoinTransaction.class);
//                    logger.info("transactions {}",transactions.toString());
//                });
//    }


    private Web3j initWeb3j(){
        return Web3j.build(new HttpService(rpcURL));
    }
}
