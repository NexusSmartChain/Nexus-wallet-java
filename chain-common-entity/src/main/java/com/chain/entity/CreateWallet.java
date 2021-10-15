package com.chain.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author LiYongQiang
 * @Descrition
 * @Date 2021/10/8 6:32 下午
 */

@ApiModel("创建钱包")
@Data
public class CreateWallet {

    @ApiModelProperty("密码")
    private String password;
}
