package com.expressage;

import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 区块链配置类
 * 初始化FISCO BCOS SDK和客户端
 */
public class BlockchainConfig {
    private static Client client;
    private static CryptoKeyPair cryptoKeyPair;

    /**
     * 初始化SDK
     */
    public static void initSDK() {
        try {
            // 使用类加载器获取配置文件的绝对路径
            String configPath = BlockchainConfig.class.getClassLoader().getResource("config.toml").getPath();
            System.out.println("✅ 配置文件路径: " + configPath);
            // 使用配置文件路径创建BcosSDK实例
            BcosSDK bcosSDK = BcosSDK.build(configPath);
            System.out.println("✅ 初始化BcosSDK成功");
            
            // 获取客户端实例，groupId为1
            client = bcosSDK.getClient(1);
            System.out.println("✅ 获取客户端成功");

            // 获取账户
            cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
            System.out.println("✅ 获取账户成功");

            System.out.println("✅ FISCO BCOS SDK初始化成功");
            System.out.println("   账户地址: " + cryptoKeyPair.getAddress());
        } catch (Exception e) {
            System.err.println("❌ FISCO BCOS SDK初始化失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("区块链连接失败", e);
        }
    }

    /**
     * 获取区块链客户端
     */
    public static Client getClient() {
        if (client == null) {
            initSDK();
        }
        return client;
    }

    /**
     * 获取账户密钥对
     */
    public static CryptoKeyPair getCryptoKeyPair() {
        if (cryptoKeyPair == null) {
            initSDK();
        }
        return cryptoKeyPair;
    }

    /**
     * 获取合约地址
     */
    public static String getContractAddress() {
        try {
            InputStream propertiesStream = BlockchainConfig.class.getClassLoader().getResourceAsStream("contract.properties");
            if (propertiesStream == null) {
                System.err.println("❌ 未找到contract.properties文件");
                System.err.println("   请运行deploy_blockchain.sh部署智能合约");
                throw new RuntimeException("未找到contract.properties文件");
            }
            Properties properties = new Properties();
            properties.load(propertiesStream);
            String contractAddress = properties.getProperty("contract.address");
            if (contractAddress == null || contractAddress.isEmpty() || contractAddress.equals("0x0000000000000000000000000000000000000001")) {
                System.err.println("❌ 合约地址无效或未设置: " + contractAddress);
                System.err.println("   请运行deploy_blockchain.sh部署智能合约");
                throw new RuntimeException("合约地址无效");
            }
            System.out.println("✅ 成功加载合约地址: " + contractAddress);
            return contractAddress;
        } catch (IOException e) {
            System.err.println("❌ 加载合约地址失败: " + e.getMessage());
            System.err.println("   请检查contract.properties文件格式");
            throw new RuntimeException("加载合约地址失败", e);
        }
    }
}