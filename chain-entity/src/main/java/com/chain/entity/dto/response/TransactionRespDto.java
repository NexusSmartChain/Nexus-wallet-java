package com.chain.entity.dto.response;

import lombok.Data;

/**
 * @author LiYongQiang
 * @Descrition
 * @Date 2021/9/22 6:57 下午
 */
@Data
public class TransactionRespDto {

    private String hash;
    private String nonce;
    private String blockHash;
    private String blockNumber;
    private String transactionIndex;
    private String from;
    private String to;
    private String value;
    private String gasPrice;
    private String gas;
    private String input;
    private String creates;
    private String publicKey;
    private String raw;
    private String r;
    private String s;
    private long v;
}
