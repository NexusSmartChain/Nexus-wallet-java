package com.chain.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author LiYongQiang
 * @Descrition
 * @Date 2021/10/8 6:54 下午
 */

@Data
@ApiModel("转账")
public class TokenTransaction {

    @ApiModelProperty("从地址")
    private String fromAddress;

    @ApiModelProperty("密钥")
    private String privateKey;

    @ApiModelProperty("合约地址")
    private String contractAddress;

    @ApiModelProperty("到地址")
    private String toAddress;

    @ApiModelProperty("数量")
    private BigDecimal amount;
}
