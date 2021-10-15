package com.chain.web3j.wallet;

import com.google.common.collect.ImmutableList;
import org.bitcoinj.crypto.*;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author LiYongQiang
 * @Descrition
 * @Date 2021/9/24 5:49 下午
 */
public class ETHWallet {

    private static final String ETH_MNEMONIC_PATH = "/Users/tiger/wallet";

    Logger logger = LoggerFactory.getLogger(ETHWallet.class);


    /**
     * path路径
     */
    private final static ImmutableList<ChildNumber> BIP44_ETH_ACCOUNT_ZERO_PATH =
            ImmutableList.of(new ChildNumber(44, true), new ChildNumber(60, true),
                    ChildNumber.ZERO_HARDENED, ChildNumber.ZERO);

    private final static Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/你自己从infura申请的id"));

    public static void main(String[] args) throws MnemonicException.MnemonicLengthException {

    }

    public static Map<String, Object> ethWalletGenerate(String mnemonic, String mnemonicPath, String passWord) {

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
        } catch (CipherException e) {
            return null;
        } catch (UnreadableWalletException e) {
            return null;
        }
    }


    /**
     * 生成钱包地址、公私钥、助记词
     */
    public void testGenerateEthWallet(){

        Map<String, Object> wallet = ethWalletGenerate(null, ETH_MNEMONIC_PATH, "123456");
        WalletFile walletFile = (WalletFile) wallet.get("walletFile");
        String address = walletFile.getAddress();
        ECKeyPair eCKeyPair = (ECKeyPair) wallet.get("eCKeyPair");
        String privateKey = eCKeyPair.getPrivateKey().toString(16);
        String publicKey = eCKeyPair.getPublicKey().toString(16);
        String mnemonic = (String) wallet.get("mnemonic");
        logger.warn("address: {}, privateKey: {}, publicKey: {}, mnemonic: {}", address, privateKey, publicKey, mnemonic);
    }

    /**
     * 通过助记词恢复钱包地址、公私钥
     */
    public void testGenerateEthWalletByMnemonic(){
        Map<String, Object> wallet = ethWalletGenerate("clown cat senior keep problem engine degree modify ritual machine syrup company", ETH_MNEMONIC_PATH, "123456");
        WalletFile walletFile = (WalletFile) wallet.get("walletFile");
        String address = walletFile.getAddress();
        ECKeyPair eCKeyPair = (ECKeyPair) wallet.get("eCKeyPair");
        String privateKey = eCKeyPair.getPrivateKey().toString(16);
        String publicKey = eCKeyPair.getPublicKey().toString(16);
        String mnemonic = (String) wallet.get("mnemonic");
        logger.warn("address: {}, privateKey: {}, publicKey: {}, mnemonic: {}", address, privateKey, publicKey, mnemonic);
    }



    /**
     * 通过助记词和id生成对应的子账户
     *
     * @param mnemonic 助记词
     * @param id       派生子id
     * @return 子账户key
     */
    private static DeterministicKey generateKeyFromMnemonicAndUid(String mnemonic, int id) {
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, "");

        DeterministicKey rootKey = HDKeyDerivation.createMasterPrivateKey(seed);
        DeterministicHierarchy hierarchy = new DeterministicHierarchy(rootKey);

        return hierarchy.deriveChild(BIP44_ETH_ACCOUNT_ZERO_PATH, false, true, new ChildNumber(id, false));
    }

    /**
     * 生成地址
     *
     * @param id 用户id
     * @return 地址
     */
    public static String getEthAddress(String mnemonic, int id) {
        DeterministicKey deterministicKey = generateKeyFromMnemonicAndUid(mnemonic, id);
        ECKeyPair ecKeyPair = ECKeyPair.create(deterministicKey.getPrivKey());
        return Keys.getAddress(ecKeyPair);
    }

    /**
     * 生成私钥
     *
     * @param id 用户id
     * @return 私钥
     */
    public static BigInteger getPrivateKey(String mnemonic, int id) {
        return generateKeyFromMnemonicAndUid(mnemonic, id).getPrivKey();
    }


    /**
     * 通过助记词和用户id生成钱包地址和私钥
     */

    public void testGenerateEthChildWallet(){
        String ethAddress = getEthAddress("clown cat senior keep problem engine degree modify ritual machine syrup company", 1);
        BigInteger privateKey = getPrivateKey("clown cat senior keep problem engine degree modify ritual machine syrup company", 1);
        logger.warn("address: {}, privateKey: {}", ethAddress, privateKey);
    }


    /**
     * 获取eth余额
     *
     * @param address 传入查询的地址
     * @return String 余额
     * @throws IOException
     */
    public static String getEthBalance(String address) {
        EthGetBalance ethGetBlance = null;
        try {
            ethGetBlance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
        } catch (IOException e) {
            //logger.error("【获取ETH余额失败】 错误信息: {}", e.getMessage());
        }
        // 格式转换 WEI(币种单位) --> ETHER
        String balance = Convert.fromWei(new BigDecimal(ethGetBlance.getBalance()), Convert.Unit.ETHER).toPlainString();
        return balance;
    }


    /**
     * 获取ETH余额
     */
    public void testGetETHBalance(){
        String balance = getEthBalance("0x09f20ff67db2c5fabeb9a2c8dd5f6b4afab7887b");
        logger.warn("balance: {}", balance);
    }


    /**
     * 获取账户代币余额
     *
     * @param account     账户地址
     * @param coinAddress 合约地址
     * @return 代币余额 （单位：代币最小单位）
     * @throws IOException
     */
    public static String getTokenBalance(String account, String coinAddress) {
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
            //logger.error("【获取合约代币余额失败】 错误信息: {}", e.getMessage());
            return null;
        }
        int decimal = getTokenDecimal(coinAddress);
        BigDecimal balance = new BigDecimal(new BigInteger(value.substring(2), 16).toString(10)).divide(BigDecimal.valueOf(Math.pow(10, decimal)));
        return balance.toPlainString();
    }

    /**
     * 获取代币余额
     */
    public void testGetTokenBalance(){
        String usdtBalance = getTokenBalance("0x09f20ff67db2c5fabeb9a2c8dd5f6b4afab7887b", "0xdac17f958d2ee523a2206206994597c13d831ec7");
        logger.warn("usdtBalance: {}", usdtBalance);
    }



    /**
     * 查询代币符号
     */
    public static String getTokenSymbol(String contractAddress) {
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
        } catch (InterruptedException e) {
            //logger.error("获取代币符号失败");
            e.printStackTrace();
        } catch (ExecutionException e) {
            //logger.error("获取代币符号失败");
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
    public static String getTokenName(String contractAddr) {
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
        } catch (InterruptedException e) {
            //logger.error("获取代币名称失败");
            e.printStackTrace();
        } catch (ExecutionException e) {
            //logger.error("获取代币名称失败");
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
    public static int getTokenDecimal(String contractAddr) {
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
        } catch (InterruptedException e) {
            //logger.error("获取代币精度失败");
            e.printStackTrace();
        } catch (ExecutionException e) {
            //logger.error("获取代币精度失败");
            e.printStackTrace();
        }
        List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        if (null == results || 0 == results.size()) {
            return 0;
        }
        return Integer.parseInt(results.get(0).getValue().toString());
    }

    /**
     * 获取代币名称、符号和精度
     */
    public void testGetTokenInfo(){
        String usdtContractAddress = "0xdac17f958d2ee523a2206206994597c13d831ec7";
        String tokenName = getTokenName(usdtContractAddress);
        String tokenSymbol = getTokenSymbol(usdtContractAddress);
        int tokenDecimal = getTokenDecimal(usdtContractAddress);
        logger.warn("name: {}, symbol: {}, decimal: {}", tokenName, tokenSymbol, tokenDecimal);
    }



    /**
     * 根据区块高度获取区块交易
     * @param height 区块高度
     * @return
     */
//    public static List<CoinTransaction> getTxByHeight(BigInteger height) {
//        List<CoinTransaction> transactions = new ArrayList<>();
//        try {
//            EthBlock.Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(height), false).send().getBlock();
//            for (EthBlock.TransactionResult transactionResult : block.getTransactions()) {
//                CoinTransaction transaction = web3j.ethGetTransactionByHash((String) transactionResult.get()).send().getTransaction().get();
//                transactions.add(transaction);
//            }
//            //logger.info("【获取交易数据成功】 区块哈希: {}, 区块高度: {}", block.getHash(), block.getNumber());
//        } catch (IOException e) {
//            //logger.error("【获取交易数据失败】 错误信息: {}", e.getMessage());
//            return null;
//        }
//        return transactions;
//    }

    /**
     * 根据txid获取交易信息
     * @param txid 交易哈希
     * @return
     */
//    public static CoinTransaction getTxByTxid(String txid) {
//        CoinTransaction transaction = null;
//        try {
//            transaction = web3j.ethGetTransactionByHash(txid).send().getTransaction().orElse(null);
//            //logger.info("【获取交易信息成功】 {} : {}", txid, new Gson().toJson(transaction));
//        } catch (IOException e) {
//            //logger.info("【获取交易信息失败】 交易哈希: {}, 错误信息: {}", txid, e.getMessage());
//            return null;
//        }
//        return transaction;
//    }
//
//    /**
//     * 解析代币交易
//     * @param transaction 交易对象
//     * @return
//     */
//    public static Map<String, Object> getTokenTxInfo(CoinTransaction transaction){
//        Map<String, Object> result = new HashMap<>();
//        String input = transaction.getInput();
//        if(!Erc20Util.isTransferFunc(input)) {
//            return null;
//        }
//        result.put("to", Erc20Util.getToAddress(input));
//        result.put("amount", Erc20Util.getTransferValue(input).divide(BigDecimal.valueOf(Math.pow(10, getTokenDecimal(transaction.getTo())))));
//        result.put("txid", transaction.getHash());
//        result.put("from", transaction.getFrom());
//        result.put("height", transaction.getBlockNumber());
//        result.put("txFee", Convert.fromWei(transaction.getGasPrice().multiply(transaction.getGas()).toString(10), Convert.Unit.ETHER));
//        result.put("gas", transaction.getGas());
//        result.put("gasPrice", transaction.getGasPrice());
//        return result;
//    }
//
//    /**
//     * 解析ETH交易
//     * @param transaction 交易对象
//     * @return
//     */
//    public static Map<String, Object> getEthTxInfo(CoinTransaction transaction){
//        Map<String, Object> result = new HashMap<>();
//        result.put("to", transaction.getTo());
//        result.put("amount", Convert.fromWei(transaction.getValue().toString(10), Convert.Unit.ETHER));
//        result.put("txid", transaction.getHash());
//        result.put("from", transaction.getFrom());
//        result.put("height", transaction.getBlockNumber());
//        result.put("txFee", Convert.fromWei(transaction.getGasPrice().multiply(transaction.getGas()).toString(10), Convert.Unit.ETHER));
//        result.put("gas", transaction.getGas());
//        result.put("gasPrice", transaction.getGasPrice());
//        return result;
//    }
//
//    /**
//     * 根据txid获取ETH/代币交易信息
//     */
//    public void testGetTransactionByTxid(){
//        CoinTransaction ethTx = EthUtil.getTxByTxid("0xd05798408be19ec0adc5e0a7397b4e9d294b8e136eacc1eb606be45533eb97f1");
//        Map<String, Object> ethTxInfo = EthUtil.getEthTxInfo(ethTx);
//
//        CoinTransaction usdtTx = EthUtil.getTxByTxid("0xd5443fad2feafd309f28d86d39af2e3f112b1ca1b8cdce8a2b6b9cdcdef5ad59");
//        Map<String, Object> usdtTxInfo = EthUtil.getTokenTxInfo(usdtTx);
//
//        logger.warn("txInfo: {}, usdtTxInfo: {}", new Gson().toJson(ethTxInfo), new Gson().toJson(usdtTxInfo));
//    }
//
//    /**
//     * 根据区块高度获取交易
//     */
//    public void testGetTransactionByBlockHeight(){
//        List<CoinTransaction> transactions = EthUtil.getTxByHeight(new BigInteger("9159698"));
//        logger.warn("txCount: {}", transactions.size());
//    }
//
//
//    /**
//     * 发送eth离线交易
//     *
//     * @param from        eth持有地址
//     * @param to          发送目标地址
//     * @param amount      金额（单位：eth）
//     * @param credentials 秘钥对象
//     * @return 交易hash
//     */
//    public static String sendEthTx(String from, String to, BigInteger gasLimit, BigInteger gasPrice, BigDecimal amount, Credentials credentials) {
//
//        try {
//            BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();
//
//            BigInteger amountWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
//
//            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, amountWei, "");
//
//            byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
//
//            return web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).sendAsync().get().getTransactionHash();
//        }catch (Exception e) {
//            logger.error("【ETH离线转账失败】 错误信息: {}", e.getMessage());
//            return null;
//        }
//    }
//
//    /**
//     * 发送代币离线交易
//     *
//     * @param from        代币持有地址
//     * @param to          代币目标地址
//     * @param value      金额（单位：代币最小单位）
//     * @param coinAddress 代币合约地址
//     * @param credentials 秘钥对象
//     * @return 交易hash
//     */
//    public static String sendTokenTx(String from, String to, BigInteger gasLimit, BigInteger gasPrice, BigInteger value, String coinAddress, Credentials credentials) {
//
//        try {
//            BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();
//
//            Function function = new Function(
//                    "transfer",
//                    Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(to),
//                            new org.web3j.abi.datatypes.generated.Uint256(value)),
//                    Collections.<TypeReference<?>>emptyList());
//            String data = FunctionEncoder.encode(function);
//
//            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, coinAddress, data);
//            byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
//
//            return web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).sendAsync().get().getTransactionHash();
//        }catch (Exception e) {
//            logger.error("【代币离线转账失败】 错误信息: {}", e.getMessage());
//            return null;
//        }
//    }
//
//
//    /**
//     * 测试ETH转账
//     */
//    public void testETHTransfer() throws Exception{
//        String from = "0xB7Cd09d73a1719b90469Edf7Aa1942d8f89Ba21f";
//        String to = "0xF0B8412C211261B68bc797f31F642Aa14fbDC007";
//        String privateKey = "密钥不可见";
//        BigDecimal value = Convert.toWei("1", Convert.Unit.WEI);
//        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
//        BigInteger gasLimit = web3j.ethEstimateGas(new CoinTransaction(from, null, null, null, to, value.toBigInteger(), null)).send().getAmountUsed();
//        String txid = sendEthTx(from, to, gasLimit, gasPrice, value, Credentials.create(privateKey));
//        logger.warn("txid: {}", txid);
//    }
//
//    /**
//     * 测试代币转账
//     */
//    public void testTokenTransfer() throws Exception{
//        String from = "0xB7Cd09d73a1719b90469Edf7Aa1942d8f89Ba21f";
//        String to = "0xF0B8412C211261B68bc797f31F642Aa14fbDC007";
//        String contractAddress = "0x6a26797a73f558a09a47d2dd56fbe03227a31dbb";
//        String privateKey = "密钥不可见";
//        BigInteger value = BigDecimal.valueOf(Math.pow(10, getTokenDecimal(contractAddress))).toBigInteger();
//        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
//        BigInteger gasLimit = getTransactionGasLimit(from, to, contractAddress, value);
//        String txid = sendTokenTx(from, to, gasLimit, gasPrice, gasLimit, contractAddress, Credentials.create(privateKey));
//        logger.warn("txid: {}", txid);
//    }

}
