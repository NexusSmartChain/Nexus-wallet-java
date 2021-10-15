package com.chain.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author LiYongQiang
 * @Descrition
 * @Date 2021/9/28 12:21 下午
 */
@ApiModel("HD 钱包")
@Data
public class EthHDWallet {

    @ApiModelProperty("私钥")
    String privateKey;

    @ApiModelProperty("公钥")
    String publicKey;

    @ApiModelProperty("助记词")
    List<String> mnemonic;

    @ApiModelProperty("助记符路径")
    String mnemonicPath;

    @ApiModelProperty("钱包地址")
    String Address;

    @ApiModelProperty("密钥库")
    String keystore;

    public EthHDWallet(String privateKey, String publicKey, List<String> mnemonic, String mnemonicPath, String address, String keystore) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.mnemonic = mnemonic;
        this.mnemonicPath = mnemonicPath;
        Address = address;
        this.keystore = keystore;
    }
}
