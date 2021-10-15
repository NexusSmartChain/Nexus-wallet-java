package com.chain.web3j.service.impl;

import com.chain.entity.CreateWallet;
import com.chain.entity.EthHDWallet;
import com.chain.entity.TokenTransaction;
import com.chain.web3j.config.Web3jConfig;
import com.chain.web3j.service.ETHWalletService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.bitcoinj.crypto.*;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.*;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.tx.ChainId;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author LiYongQiang
 * @Descrition
 * @Date 2021/9/24 5:49 下午
 */
@Service
public class ETHWalletServiceImpl implements ETHWalletService {

    Logger logger = LoggerFactory.getLogger(ETHWalletServiceImpl.class);

    @Autowired
    private Web3j web3j;

    @Autowired
    private Admin admin;

     /**
     * 通用的以太坊基于bip44协议的助记词路径 （imtoken jaxx Metamask myetherwallet）
     */
    private static SecureRandom secureRandom = new SecureRandom();

    /**
     * path路径
     */
    private final static ImmutableList<ChildNumber> BIP44_ETH_ACCOUNT_ZERO_PATH =
            ImmutableList.of(new ChildNumber(44, true), new ChildNumber(60, true),
                    ChildNumber.ZERO_HARDENED, ChildNumber.ZERO);

    //private final static Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/你自己从infura申请的id"));

    @Override
    public Map<String, Object> ethWalletGenerate(String mnemonic, String mnemonicPath, String passWord) {
        try {
            DeterministicSeed deterministicSeed = null;
            List<String> mnemonicArray = null;

            if (null == mnemonic || 0 == mnemonic.length()) {
                deterministicSeed = new DeterministicSeed(new java.security.SecureRandom() , 128, "", System.currentTimeMillis() / 1000);
                mnemonicArray = deterministicSeed.getMnemonicCode();// 助记词
            } else {
                deterministicSeed = new DeterministicSeed(mnemonic, null, "", System.currentTimeMillis() / 1000);
            }

            byte[] seedBytes = deterministicSeed.getSeedBytes();// 种子
            if (null == seedBytes) {
                //logger.error("生成钱包失败");
                return null;
            }

            //种子对象
            DeterministicKey deterministicKey = HDKeyDerivation.createMasterPrivateKey(seedBytes);

            String[] pathArray = mnemonicPath.split("/");// 助记词路径
            for (int i = 1; i < pathArray.length; i++) {
                ChildNumber childNumber;
                if (pathArray[i].endsWith("'")) {
                    int number = Integer.parseInt(pathArray[i].substring(0, pathArray[i].length() - 1));
                    childNumber = new ChildNumber(number, true);
                } else {
                    int number = Integer.parseInt(pathArray[i]);
                    childNumber = new ChildNumber(number, false);
                }
                deterministicKey = HDKeyDerivation.deriveChildKey(deterministicKey, childNumber);
            }

            ECKeyPair eCKeyPair = ECKeyPair.create(deterministicKey.getPrivKeyBytes());
            WalletFile walletFile = Wallet.createStandard(passWord, eCKeyPair);
            if (null == mnemonic || 0 == mnemonic.length()) {
                StringBuilder mnemonicCode = new StringBuilder();
                for (int i = 0; i < mnemonicArray.size(); i++) {
                    mnemonicCode.append(mnemonicArray.get(i)).append(" ");
                }
                return new HashMap<String, Object>() {
                    private static final long serialVersionUID = -4960785990664709623L;
                    {
                        put("walletFile", walletFile);
                        put("eCKeyPair", eCKeyPair);
                        put("mnemonic", mnemonicCode.substring(0, mnemonicCode.length() - 1));
                    }
                };
            } else {
                return new HashMap<String, Object>() {
                    private static final long serialVersionUID = -947886783923530545L;
                    {
                        put("walletFile", walletFile);
                        put("eCKeyPair", eCKeyPair);
                    }
                };
            }
        } catch (CipherException | UnreadableWalletException e) {
            return null;
        }
    }

    @Override
    public Map<String, Object> ethWalletGenerate(String passWord) {
        String mnemonic = generateMnemonic();
        String mnemonicPath = Web3jConfig.ETH_TYPE;
        try {
            DeterministicSeed deterministicSeed = null;
            List<String> mnemonicArray = null;

            if (null == mnemonic || 0 == mnemonic.length()) {
                deterministicSeed = new DeterministicSeed(new java.security.SecureRandom() , 128, "", System.currentTimeMillis() / 1000);
                mnemonicArray = deterministicSeed.getMnemonicCode();// 助记词
            } else {
                deterministicSeed = new DeterministicSeed(mnemonic, null, "", System.currentTimeMillis() / 1000);
            }

            byte[] seedBytes = deterministicSeed.getSeedBytes();// 种子
            if (null == seedBytes) {
                //logger.error("生成钱包失败");
                return null;
            }

            //种子对象
            DeterministicKey deterministicKey = HDKeyDerivation.createMasterPrivateKey(seedBytes);

            String[] pathArray = mnemonicPath.split("/");// 助记词路径
            for (int i = 1; i < pathArray.length; i++) {
                ChildNumber childNumber;
                if (pathArray[i].endsWith("'")) {
                    int number = Integer.parseInt(pathArray[i].substring(0, pathArray[i].length() - 1));
                    childNumber = new ChildNumber(number, true);
                } else {
                    int number = Integer.parseInt(pathArray[i]);
                    childNumber = new ChildNumber(number, false);
                }
                deterministicKey = HDKeyDerivation.deriveChildKey(deterministicKey, childNumber);
            }

            ECKeyPair eCKeyPair = ECKeyPair.create(deterministicKey.getPrivKeyBytes());
            WalletFile walletFile = Wallet.createStandard(passWord, eCKeyPair);
            if (null == mnemonic || 0 == mnemonic.length()) {
                StringBuilder mnemonicCode = new StringBuilder();
                for (int i = 0; i < mnemonicArray.size(); i++) {
                    mnemonicCode.append(mnemonicArray.get(i)).append(" ");
                }
                return new HashMap<String, Object>() {
                    private static final long serialVersionUID = -4960785990664709623L;
                    {
                        put("walletFile", walletFile);
                        put("eCKeyPair", eCKeyPair);
                        put("address", "0x"+walletFile.getAddress());
                        put("mnemonic", mnemonicCode.substring(0, mnemonicCode.length() - 1));
                    }
                };
            } else {
                return new HashMap<String, Object>() {
                    private static final long serialVersionUID = -947886783923530545L;
                    {
                        put("walletFile", walletFile);
                        put("eCKeyPair", eCKeyPair);
                        put("address", "0x"+walletFile.getAddress());
                        put("mnemonic",mnemonic.substring(1, mnemonic.length() - 1));
                    }
                };
            }
        } catch (CipherException | UnreadableWalletException e) {
            return null;
        }
    }

    /**
     * 通过助记词和id生成对应的子账户
     *
     * @param mnemonic 助记词
     * @param id       派生子id
     * @return 子账户key
     */
    @Override
    public DeterministicKey generateKeyFromMnemonicAndUid(String mnemonic, int id) {
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, "");

        DeterministicKey rootKey = HDKeyDerivation.createMasterPrivateKey(seed);
        DeterministicHierarchy hierarchy = new DeterministicHierarchy(rootKey);

        return hierarchy.deriveChild(BIP44_ETH_ACCOUNT_ZERO_PATH, false, true, new ChildNumber(id, false));
    }

    /**
     * 获取eth余额
     *
     * @param address 传入查询的地址
     * @return String 余额
     * @throws IOException
     */
    @Override
    public String getEthBalance(String address) {
        EthGetBalance ethGetBlance = null;
        try {
            ethGetBlance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
        } catch (IOException e) {
            logger.error("【获取ETH余额失败】 错误信息: {}", e.getMessage());
        }
        // 格式转换 WEI(币种单位) --> ETHER
        return Convert.fromWei(new BigDecimal(ethGetBlance.getBalance()), Convert.Unit.ETHER).toPlainString();
    }

    /**
     * 获取账户代币余额
     *
     * @param account     账户地址
     * @param coinAddress 合约地址
     * @return 代币余额 （单位：代币最小单位）
     * @throws IOException
     */
    @Override
    public String getTokenBalance(String account, String coinAddress) {
        Function balanceOf = new Function("balanceOf",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(account)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));

        if (coinAddress == null) {
            return null;
        }
        String value = null;
        try {
            value = web3j.ethCall(Transaction.createEthCallTransaction(account, coinAddress, FunctionEncoder.encode(balanceOf)), DefaultBlockParameterName.PENDING).send().getValue();
        } catch (IOException e) {
            logger.error("【获取合约代币余额失败】 错误信息: {}", e.getMessage());
            return null;
        }
        int decimal = getTokenDecimal(coinAddress);
        BigDecimal balance = new BigDecimal(new BigInteger(value.substring(2), 16).toString(10)).divide(BigDecimal.valueOf(Math.pow(10, decimal)));
        return balance.toPlainString();
    }

    /**
     * 查询代币符号
     */
    @Override
    public String getTokenSymbol(String contractAddress) {
        String methodName = "symbol";
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();

        TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
        };
        outputParameters.add(typeReference);

        Function function = new Function(methodName, inputParameters, outputParameters);

        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction("0x0000000000000000000000000000000000000000", contractAddress, data);

        EthCall ethCall = null;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("获取代币符号失败");
            e.printStackTrace();
        }
        List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        if (null == results || 0 == results.size()) {
            return "";
        }
        return results.get(0).getValue().toString();
    }

    /**
     * 查询代币名称
     */
    @Override
    public String getTokenName(String contractAddr) {
        String methodName = "name";
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();

        TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
        };
        outputParameters.add(typeReference);

        Function function = new Function(methodName, inputParameters, outputParameters);

        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction("0x0000000000000000000000000000000000000000", contractAddr, data);

        EthCall ethCall = null;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("获取代币名称失败");
            e.printStackTrace();
        }
        List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        if (null == results || results.size() <= 0) {
            return "";
        }
        return results.get(0).getValue().toString();
    }

    /**
     * 查询代币精度
     */
    @Override
    public int getTokenDecimal(String contractAddr) {
        String methodName = "decimals";
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();

        TypeReference<Uint8> typeReference = new TypeReference<Uint8>() {
        };
        outputParameters.add(typeReference);

        Function function = new Function(methodName, inputParameters, outputParameters);

        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction("0x0000000000000000000000000000000000000000", contractAddr, data);

        EthCall ethCall = null;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        if (null == results || 0 == results.size()) {
            return 0;
        }
        return Integer.parseInt(results.get(0).getValue().toString());
    }

    @Override
    public String generateMnemonic() {
        if (!Web3jConfig.ETH_TYPE.startsWith("m") && !Web3jConfig.ETH_TYPE.startsWith("M")) {
            //参数非法
            return null;
        }
        String[] pathArray = Web3jConfig.ETH_TYPE.split("/");
        if (pathArray.length <= 1) {
            //内容不对
            return null;
        }
        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed ds = new DeterministicSeed(secureRandom, 128, passphrase, creationTimeSeconds);
        return ds.getMnemonicCode().toString();
    }

    @Override
    public EthHDWallet createEthWallet(CreateWallet createWallet) {
        if (!Web3jConfig.ETH_TYPE.startsWith("m") && !Web3jConfig.ETH_TYPE.startsWith("M")) {
            //参数非法
            return null;
        }
        String[] pathArray = Web3jConfig.ETH_TYPE.split("/");
        if (pathArray.length <= 1) {
            //内容不对
            return null;
        }

        if (createWallet.getPassword().length() < 6) {
            //密码过短
            return null;
        }

        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed ds = new DeterministicSeed(secureRandom, 128, passphrase, creationTimeSeconds);

        //根私钥
        byte[] seedBytes = ds.getSeedBytes();
        System.out.println("根私钥 " + Arrays.toString(seedBytes));
        //助记词
        List<String> mnemonic = ds.getMnemonicCode();
        System.out.println("助记词 " + Arrays.toString(mnemonic.toArray()));

        try {
            //助记词种子
            byte[] mnemonicSeedBytes = MnemonicCode.INSTANCE.toEntropy(mnemonic);
            System.out.println("助记词种子 " + Arrays.toString(mnemonicSeedBytes));
            ECKeyPair mnemonicKeyPair = ECKeyPair.create(mnemonicSeedBytes);
            WalletFile walletFile = Wallet.createLight(createWallet.getPassword(), mnemonicKeyPair);
            ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
            //存这个keystore 用完后删除
            String jsonStr = objectMapper.writeValueAsString(walletFile);
            System.out.println("mnemonic keystore " + jsonStr);
            //验证
            WalletFile checkWalletFile = objectMapper.readValue(jsonStr, WalletFile.class);
            ECKeyPair ecKeyPair = Wallet.decrypt(createWallet.getPassword(), checkWalletFile);
            byte[] checkMnemonicSeedBytes = Numeric.hexStringToByteArray(ecKeyPair.getPrivateKey().toString(16));
            System.out.println("验证助记词种子 "
                    + Arrays.toString(checkMnemonicSeedBytes));
            List<String> checkMnemonic = MnemonicCode.INSTANCE.toMnemonic(checkMnemonicSeedBytes);
            System.out.println("验证助记词 " + Arrays.toString(checkMnemonic.toArray()));

        } catch (MnemonicException.MnemonicLengthException | MnemonicException.MnemonicWordException | MnemonicException.MnemonicChecksumException | CipherException | IOException e) {
            e.printStackTrace();
        }

        if (seedBytes == null)
            return null;
        DeterministicKey dkKey = HDKeyDerivation.createMasterPrivateKey(seedBytes);
        for (int i = 1; i < pathArray.length; i++) {
            ChildNumber childNumber;
            if (pathArray[i].endsWith("'")) {
                int number = Integer.parseInt(pathArray[i].substring(0,
                        pathArray[i].length() - 1));
                childNumber = new ChildNumber(number, true);
            } else {
                int number = Integer.parseInt(pathArray[i]);
                childNumber = new ChildNumber(number, false);
            }
            dkKey = HDKeyDerivation.deriveChildKey(dkKey, childNumber);
        }
        System.out.println("path " + dkKey.getPathAsString());

        ECKeyPair keyPair = ECKeyPair.create(dkKey.getPrivKeyBytes());
        System.out.println("eth privateKey " + keyPair.getPrivateKey().toString(16));
        System.out.println("eth publicKey " + keyPair.getPublicKey().toString(16));

        EthHDWallet ethHDWallet = null;
        try {
            WalletFile walletFile = Wallet.createLight(createWallet.getPassword(), keyPair);
            System.out.println("eth address " + "0x" + walletFile.getAddress());
            ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
            //存
            String jsonStr = objectMapper.writeValueAsString(walletFile);
            System.out.println("eth keystore " + jsonStr);

            ethHDWallet = new EthHDWallet(keyPair.getPrivateKey().toString(16),
                    keyPair.getPublicKey().toString(16),
                    mnemonic, dkKey.getPathAsString(),
                    "0x" + walletFile.getAddress(), jsonStr);
        } catch (CipherException | JsonProcessingException e) {
            e.printStackTrace();
        }

        return ethHDWallet;
    }



    @Override
    public String tokenTransaction(TokenTransaction transaction) {
        BigInteger nonce;
        EthGetTransactionCount ethGetTransactionCount = null;

        //查询代币精度
        int tokenDecimal = getTokenDecimal(transaction.getContractAddress());

        //查询钱包余额
        BigDecimal tokenBalance = new BigDecimal(getTokenBalance(transaction.getFromAddress(),transaction.getContractAddress()));
        if (tokenBalance.compareTo(transaction.getAmount()) < 0){
            //余额不足
            return "余额不足";
        }
        try {
            ethGetTransactionCount = web3j.ethGetTransactionCount(transaction.getFromAddress(), DefaultBlockParameterName.PENDING).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ethGetTransactionCount == null) {
            return "获取交易计数失败";
        }
        nonce = ethGetTransactionCount.getTransactionCount();
        System.out.println("nonce " + nonce);
        BigInteger gasPrice = Convert.toWei(BigDecimal.valueOf(3), Convert.Unit.GWEI).toBigInteger();
        BigInteger gasLimit = BigInteger.valueOf(60000);
        BigInteger value = BigInteger.ZERO;
        //token转账参数
        String methodName = "transfer";
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();
        Address tAddress = new Address(transaction.getToAddress());
        Uint256 tokenValue = new Uint256(transaction.getAmount().multiply(BigDecimal.TEN.pow(tokenDecimal)).toBigInteger());
        inputParameters.add(tAddress);
        inputParameters.add(tokenValue);
        TypeReference<Bool> typeReference = new TypeReference<Bool>() {
        };
        outputParameters.add(typeReference);
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);

        byte chainId = ChainId.NONE;
        String signedData;
        try {
            signedData = signTransaction(nonce, gasPrice, gasLimit, transaction.getContractAddress(), value, data, chainId, transaction.getPrivateKey());
            if (signedData != null) {
                EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedData).send();
                System.out.println(ethSendTransaction.getTransactionHash());
                return ethSendTransaction.getTransactionHash();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage().toString();
        }
        return "";
    }


    /**
     * 签名交易
     */
    public static String signTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to,
                                         BigInteger value, String data, byte chainId, String privateKey) throws IOException {
        byte[] signedMessage;
        RawTransaction rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                to,
                value,
                data);

        if (privateKey.startsWith("0x")) {
            privateKey = privateKey.substring(2);
        }
        ECKeyPair ecKeyPair = ECKeyPair.create(new BigInteger(privateKey, 16));
        Credentials credentials = Credentials.create(ecKeyPair);

        if (chainId > ChainId.NONE) {
            signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
        } else {
            signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        }

        String hexValue = Numeric.toHexString(signedMessage);
        return hexValue;
    }

}
