package com.chain.web3j.controller;

import com.chain.common.ret.JsonResult;
import com.chain.entity.CreateWallet;
import com.chain.entity.EthHDWallet;
import com.chain.entity.TokenTransaction;
import com.chain.web3j.service.ETHWalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author LiYongQiang
 * @Descrition
 * @Date 2021/9/28 2:47 下午
 */

@Api("web3j")
@RestController
@RequestMapping("/web3j")
public class ETHWalletController {

    @Autowired
    ETHWalletService ethWalletService;

    @ApiOperation("创建钱包")
    @PostMapping("/create/wallet")
    public JsonResult<EthHDWallet> createWallet(@RequestBody CreateWallet createWallet) {
        return JsonResult.success(ethWalletService.createEthWallet(createWallet));
    }

    @ApiOperation("查询钱包ETH余额")
    @GetMapping("/getEthBalance")
    public JsonResult<String> getEthBalance(@ApiParam(name = "address" ,value = "钱包地址" ,required = true) String address){
        return JsonResult.success(ethWalletService.getEthBalance(address));
    }

    @ApiOperation("查询钱包代币余额")
    @GetMapping("/getTokenBalance")
    public JsonResult<String> getTokenBalance(@ApiParam(name = "address" ,value = "钱包地址" ,required = true) String address ,
                                              @ApiParam(name = "coinAddress" ,value = "币地址" ,required = true) String coinAddress){
        return JsonResult.success(ethWalletService.getTokenBalance(address,coinAddress));
    }

    @ApiOperation("代币转账")
    @PostMapping("/sendTokenTransaction")
    public JsonResult<String> sendTokenTransaction (@RequestBody  TokenTransaction transaction){
        ethWalletService.tokenTransaction(transaction);
        return JsonResult.success("");
    }



    @ApiOperation("查询代币符号")
    @GetMapping("/getTokenSymbol")
    public JsonResult<String> getTokenSymbol (@ApiParam(name="contractAddress",value="合约地址",required=true) String contractAddress){
        return JsonResult.success(ethWalletService.getTokenSymbol(contractAddress));
    }

    @ApiOperation("查询代币名称")
    @GetMapping("/getTokenName")
    public JsonResult<String> getTokenName (@ApiParam(name="contractAddress",value="合约地址",required=true) String contractAddress){
        return JsonResult.success(ethWalletService.getTokenName(contractAddress));
    }

    @ApiOperation("查询代币精度")
    @GetMapping("/getTokenDecimal")
    public JsonResult<Integer> getTokenDecimal (@ApiParam(name="contractAddress",value="合约地址",required=true) String contractAddress){
        return JsonResult.success(ethWalletService.getTokenDecimal(contractAddress));
    }

}
