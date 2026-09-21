package com.expressage;

import com.expressage.contract.ExpressDeliveryContract;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.abi.datatypes.Type;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.Date;

/**
 * 区块链服务类
 * 管理包裹信息和区块链交易，实现取件码和身份码的绑定及验证
 * 基于FISCO BCOS联盟链，实现真正的上链存储和验证
 */
public class BlockchainService {
    // 区块链客户端
    private Client client;
    // 账户密钥对
    private CryptoKeyPair cryptoKeyPair;
    // 智能合约实例
    private ExpressDeliveryContract contract;
    // 取件码与运单号的映射关系（本地维护，方便通过取件码查找）
    private Map<String, String> pickupCodeToTrackingNumber;
    // 交易历史记录
    private List<String> transactionHistory;
    // 包裹缓存（用于提高性能，减少区块链查询）
    private Map<String, Parcel> parcelCache;
    // 取件码与运单号映射的本地存储文件路径
    private static final String PICKUP_CODE_MAPPING_FILE = "pickup_code_mapping.dat";
    

    public BlockchainService() {
        // 初始化本地映射
        pickupCodeToTrackingNumber = new HashMap<>();
        transactionHistory = new LinkedList<>();
        parcelCache = new HashMap<>();
        
        // 加载本地存储的取件码与运单号映射
        loadPickupCodeMappingFromFile();
        
        // 尝试初始化区块链连接
        try {
            initBlockchainConnection();
            
            // 如果本地没有映射，从区块链加载所有包裹信息
            if (pickupCodeToTrackingNumber.isEmpty()) {
                loadAllParcelsFromBlockchain();
            }
        } catch (Exception e) {
            System.err.println("⚠️  区块链连接初始化失败，将使用离线模式: " + e.getMessage());
            
            // 在离线模式下，添加一些测试数据用于演示
            if (pickupCodeToTrackingNumber.isEmpty()) {
                // 添加测试包裹数据
                addTestParcels();
            }
        }
    }
    
    /**
     * 添加测试包裹数据（离线模式下使用）
     */
    private void addTestParcels() {
        // 测试包裹1
        Parcel testParcel1 = new Parcel("K1234567890", "张三", "13800138001", 
                                       "110101199001011234", "A12345", "顺丰速运");
        
        // 测试包裹2
        Parcel testParcel2 = new Parcel("K0987654321", "李四", "13900139001", 
                                       "110101199001015678", "B12345", "中通快递");
        
        // 将测试包裹添加到缓存和映射中
        parcelCache.put(testParcel1.getTrackingNumber(), testParcel1);
        pickupCodeToTrackingNumber.put(testParcel1.getPickupCode(), testParcel1.getTrackingNumber());
        
        parcelCache.put(testParcel2.getTrackingNumber(), testParcel2);
        pickupCodeToTrackingNumber.put(testParcel2.getPickupCode(), testParcel2.getTrackingNumber());
        
        // 保存到本地文件
        savePickupCodeMappingToFile();
        
        System.out.println("✅ 已添加测试包裹数据用于演示");
    }
    
    /**
     * 从本地文件加载取件码与运单号的映射关系
     */
    private void loadPickupCodeMappingFromFile() {
        File file = new File(PICKUP_CODE_MAPPING_FILE);
        if (!file.exists()) {
            System.out.println("📂 本地取件码映射文件不存在，将创建新文件");
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                pickupCodeToTrackingNumber = (Map<String, String>) obj;
                System.out.println("✅ 成功从本地文件加载 " + pickupCodeToTrackingNumber.size() + " 个取件码映射");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("⚠️  加载本地取件码映射文件失败: " + e.getMessage());
            // 如果加载失败，使用空映射
            pickupCodeToTrackingNumber = new HashMap<>();
        }
    }
    
    /**
     * 将取件码与运单号的映射关系保存到本地文件
     */
    private void savePickupCodeMappingToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PICKUP_CODE_MAPPING_FILE))) {
            oos.writeObject(pickupCodeToTrackingNumber);
            System.out.println("✅ 成功将取件码映射保存到本地文件");
        } catch (IOException e) {
            System.err.println("⚠️  保存取件码映射到本地文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 从区块链加载所有包裹信息
     * 重建取件码与运单号的映射关系
     */
    private void loadAllParcelsFromBlockchain() {
        try {
            System.out.println("📥 正在从区块链加载所有包裹信息...");
            
            // 获取包裹总数
            BigInteger packagesCount = contract.getPackagesCount();
            System.out.println("📊 区块链上共有 " + packagesCount + " 个包裹");
            
            // 遍历所有包裹
            for (BigInteger i = BigInteger.ZERO; i.compareTo(packagesCount) < 0; i = i.add(BigInteger.ONE)) {
                // 获取运单号
                String trackingNumber = contract.getWaybillNumber(i);
                if (trackingNumber != null && !trackingNumber.isEmpty()) {
                    // 获取包裹信息
                    Parcel parcel = getPackage(trackingNumber);
                    if (parcel != null && parcel.getPickupCode() != null) {
                        // 重建取件码与运单号的映射关系
                        pickupCodeToTrackingNumber.put(parcel.getPickupCode(), trackingNumber);
                        System.out.println("✅ 加载包裹: " + trackingNumber + " (取件码: " + parcel.getPickupCode() + ")");
                    }
                }
            }
            
            System.out.println("✅ 成功加载 " + pickupCodeToTrackingNumber.size() + " 个包裹的映射关系");
        } catch (Exception e) {
            System.err.println("⚠️  加载包裹信息时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 初始化区块链连接
     */
    private void initBlockchainConnection() throws Exception {
        // 获取区块链客户端
        client = BlockchainConfig.getClient();
        // 获取账户密钥对
        cryptoKeyPair = BlockchainConfig.getCryptoKeyPair();
        // 获取合约地址
        String contractAddress = BlockchainConfig.getContractAddress();
        // 加载智能合约
        contract = ExpressDeliveryContract.load(contractAddress, client, cryptoKeyPair);
        System.out.println("✅ 智能合约加载成功: " + contractAddress);
    }

    /**
     * 添加包裹到系统
     * 调用智能合约的deliverPackage方法，将包裹信息上链存储
     */
    public String addParcel(Parcel parcel) {
        if (parcel == null) {
            return "❌ 错误: 包裹信息不能为空";
        }

        String trackingNumber = parcel.getTrackingNumber();
        String pickupCode = parcel.getPickupCode();

        // 检查取件码是否已被使用
        if (pickupCodeToTrackingNumber.containsKey(pickupCode)) {
            return "❌ 错误: 取件码 " + pickupCode + " 已被使用，无法重复分配";
        }

        try {
            // 调用智能合约添加包裹到区块链
            TransactionReceipt receipt = contract.deliverPackage(
                    trackingNumber,
                    parcel.getReceiverName(),
                    parcel.getReceiverPhone(),
                    parcel.getDeliveryCompany(),
                    parcel.getReceiverId()
            );
            
            // 建立取件码和运单号的映射关系（本地维护）
            pickupCodeToTrackingNumber.put(pickupCode, trackingNumber);
            
            // 将包裹加入缓存
            parcelCache.put(trackingNumber, parcel);
            
            // 将映射关系保存到本地文件
            savePickupCodeMappingToFile();

            // 生成区块链交易记录
            String transaction = generateAddParcelTransaction(trackingNumber, parcel);
            addTransactionToBlockchain(transaction);

            return "✅ 包裹录入成功！\n" +
                   "   运单号: " + trackingNumber + "\n" +
                   "   取件码: " + pickupCode + "\n" +
                   "   收件人身份码: " + parcel.getReceiverId() + "\n" +
                   "   区块链交易已完成，交易哈希: " + receipt.getTransactionHash();
        } catch (Exception e) {
            return "❌ 包裹录入失败！\n" +
                   "   错误原因: " + e.getMessage();
        }
    }

    /**
     * 取件操作
     * 使用取件码和取件人身份码进行验证
     * 调用智能合约的pickupPackage方法，实现上链取件
     */
    public String pickupParcel(String pickupCode, String personId) {
        if (pickupCode == null || pickupCode.isEmpty() || 
            personId == null || personId.isEmpty()) {
            return "❌ 错误: 取件码和身份码不能为空";
        }

        // 根据取件码查找运单号（从本地映射获取）
        String trackingNumber = pickupCodeToTrackingNumber.get(pickupCode);

        if (trackingNumber == null) {
            addTransactionToBlockchain(generatePickupFailedTransaction("NOT_FOUND", pickupCode, personId));
            return "❌ 取件失败！\n" +
                   "   取件码: " + pickupCode + "\n" +
                   "   身份码: " + personId + "\n" +
                   "   错误原因: 无效的取件码";
        }

        // 从缓存获取包裹信息
        Parcel parcel = parcelCache.get(trackingNumber);
        if (parcel == null) {
            // 如果本地缓存中没有包裹信息，尝试从区块链获取
            parcel = getParcel(trackingNumber);
            if (parcel == null) {
                addTransactionToBlockchain(generatePickupFailedTransaction("PACKAGE_NOT_FOUND", pickupCode, personId));
                return "❌ 取件失败！\n" +
                       "   取件码: " + pickupCode + "\n" +
                       "   身份码: " + personId + "\n" +
                       "   错误原因: 未找到包裹信息";
            }
        }

        try {
            System.out.println("📋 开始取件操作 - 步骤1：验证包裹");
            // 调用智能合约验证包裹是否可以取件
            boolean isValid = contract.validatePackage(trackingNumber, personId);
            System.out.println("📋 开始取件操作 - 步骤2：验证结果: " + isValid);
            if (!isValid) {
                addTransactionToBlockchain(generatePickupFailedTransaction("UNAUTHORIZED", pickupCode, personId));
                return "❌ 身份验证失败！\n" +
                       "   取件码: " + pickupCode + "\n" +
                       "   取件人身份码: " + personId + "\n" +
                       "   错误: 禁止用他人身份码取件！这是严重的安全违规！";
            }

            System.out.println("📋 开始取件操作 - 步骤3：执行取件");
            // 调用智能合约执行取件操作
            TransactionReceipt receipt = contract.pickupPackage(trackingNumber, personId);
            System.out.println("📋 开始取件操作 - 步骤4：取件成功，交易哈希: " + receipt.getTransactionHash());

            // 更新本地包裹状态
            parcel.pickup(personId);
            parcelCache.put(trackingNumber, parcel);

            addTransactionToBlockchain(generatePickupSuccessTransaction(trackingNumber, pickupCode, personId));
            return "✅ 取件成功！\n" +
                   "   取件码: " + pickupCode + "\n" +
                   "   运单号: " + trackingNumber + "\n" +
                   "   取件人身份码: " + personId + "\n" +
                   "   包裹状态: 已出库\n" +
                   "   🎉 恭喜，身份验证通过，取件完成！\n" +
                   "   区块链交易哈希: " + receipt.getTransactionHash();
        } catch (ContractException e) {
            // 智能合约抛出的异常
            System.out.println("❌ 取件操作失败 - ContractException: " + e.getMessage());
            e.printStackTrace();
            addTransactionToBlockchain(generatePickupFailedTransaction("CONTRACT_ERROR", pickupCode, personId));
            String errorMessage = e.getMessage() != null ? e.getMessage() : "智能合约错误";
            return "❌ 取件失败！\n" +
                   "   取件码: " + pickupCode + "\n" +
                   "   身份码: " + personId + "\n" +
                   "   错误原因: " + errorMessage;
        } catch (Exception e) {
            System.out.println("❌ 取件操作失败 - Exception: " + e.getMessage());
            e.printStackTrace();
            addTransactionToBlockchain(generatePickupFailedTransaction("INTERNAL_ERROR", pickupCode, personId));
            return "❌ 取件失败！\n" +
                   "   取件码: " + pickupCode + "\n" +
                   "   身份码: " + personId + "\n" +
                   "   错误原因: 系统内部错误 - " + e.getMessage();
        }
    }

    /**
     * 获取包裹对象
     */
    public Parcel getPackage(String trackingNumber) {
        // 首先从缓存中查找
        if (parcelCache.containsKey(trackingNumber)) {
            return parcelCache.get(trackingNumber);
        }

        try {
            // 从区块链智能合约获取包裹信息
            List<Type> result = contract.getPackage(trackingNumber);
            if (result == null || result.isEmpty()) {
                return null;
            }

            // 检查结果列表长度是否足够，至少需要8个元素
            if (result.size() < 8) {
                System.err.println("❌ 智能合约返回结果不完整，预期8个元素，实际" + result.size() + "个元素");
                return null;
            }

            // 解析智能合约返回的结果
            String waybillNumber = (String) result.get(0).getValue();
            String recipientName = (String) result.get(1).getValue();
            String recipientPhone = (String) result.get(2).getValue();
            String deliveryCompany = (String) result.get(3).getValue();
            String identityCode = (String) result.get(4).getValue();
            // result.get(5) 是地址类型，暂时不需要
            // result.get(6) 是时间戳，暂时不需要
            BigInteger statusValue = (BigInteger) result.get(7).getValue();
            
            // 构建取件码：简单使用运单号的后6位作为取件码
            String pickupCode = waybillNumber.substring(waybillNumber.length() - 6);
            
            // 创建包裹对象
            Parcel parcel = new Parcel(waybillNumber, recipientName, recipientPhone, identityCode, pickupCode, deliveryCompany);
            
            // 设置包裹状态
            if (statusValue.equals(BigInteger.valueOf(1))) {
                parcel.setStatus(Parcel.ParcelStatus.PICKED_UP);
            }
            
            // 添加到缓存
            parcelCache.put(trackingNumber, parcel);
            
            return parcel;
        } catch (ContractException e) {
            System.err.println("❌ 获取包裹信息失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("❌ 获取包裹信息时发生异常: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 获取包裹对象（保持旧方法名称，用于向后兼容）
     */
    public Parcel getParcel(String trackingNumber) {
        return getPackage(trackingNumber);
    }

    /**
     * 获取交易历史
     */
    public List<String> getTransactionHistory() {
        return transactionHistory;
    }
    
    /**
     * 获取所有已注册包裹
     */
    public List<Parcel> getAllRegisteredParcels() {
        // 如果包裹缓存为空，尝试从区块链加载所有包裹
        if (parcelCache.isEmpty() && !pickupCodeToTrackingNumber.isEmpty()) {
            for (String trackingNumber : pickupCodeToTrackingNumber.values()) {
                getParcel(trackingNumber); // 这会将包裹添加到缓存中
            }
        }
        
        // 如果仍然为空，从区块链重新加载所有包裹
        if (parcelCache.isEmpty()) {
            loadAllParcelsFromBlockchain();
            // 重新填充缓存
            for (String trackingNumber : pickupCodeToTrackingNumber.values()) {
                getParcel(trackingNumber);
            }
        }
        
        return new ArrayList<>(parcelCache.values());
    }

    /**
     * 添加交易到区块链
     */
    private void addTransactionToBlockchain(String transaction) {
        transactionHistory.add(transaction);
    }

    /**
     * 生成包裹添加的交易记录
     */
    private String generateAddParcelTransaction(String trackingNumber, Parcel parcel) {
        return "TRANSACTION [" + new Date() + "]: 添加包裹 \"" + trackingNumber + "\" 成功，取件码 \"" + parcel.getPickupCode() + "\"，身份码 \"" + parcel.getReceiverId() + "\"";
    }

    /**
     * 生成取件成功的交易记录
     */
    private String generatePickupSuccessTransaction(String trackingNumber, String pickupCode, String personId) {
        return "TRANSACTION [" + new Date() + "]: 取件成功 - 运单号 \"" + trackingNumber + "\"，取件码 \"" + pickupCode + "\"，身份码 \"" + personId + "\"";
    }

    /**
     * 生成取件失败的交易记录
     */
    private String generatePickupFailedTransaction(String reason, String pickupCode, String personId) {
        return "TRANSACTION [" + new Date() + "]: 取件失败 - 取件码 \"" + pickupCode + "\"，身份码 \"" + personId + "\"，原因: " + reason;
    }
}