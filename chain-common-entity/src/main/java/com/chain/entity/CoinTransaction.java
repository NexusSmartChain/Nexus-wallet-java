package com.chain.entity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author LiYongQiang
 * @Descrition
 * @Date 2021/9/28 6:55 下午
 */
@ApiModel("硬币交易")
@Data
public class CoinTransaction {

    @ApiModelProperty("从地址")
    private String fromAddress;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("到地址")
    private String toAddress;

    @ApiModelProperty("合约地址")
    private String contractAddress;

    @ApiModelProperty("数量")
    private BigInteger amount;
}
