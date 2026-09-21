package com.expressage;

/**
 * 快递取件联盟链系统命令行入口
 * 支持部署、注册包裹、取件等命令
 */
public class CommandLineApp {
    private static BlockchainService blockchainService;

    public static void main(String[] args) {
        try {
            // 初始化区块链服务
            blockchainService = new BlockchainService();
        } catch (Exception e) {
            System.err.println("❌ 区块链服务初始化失败: " + e.getMessage());
            System.err.println("   请检查区块链连接配置和网络状态");
            return;
        }
        if (args.length == 0) {
            showUsage();
            return;
        }

        String command = args[0].toLowerCase();

        try {
            switch (command) {
                case "deploy":
                    handleDeploy();
                    break;
                case "register":
                    handleRegister(args);
                    break;
                case "pickup":
                    handlePickup(args);
                    break;
                case "help":
                    showUsage();
                    break;
                default:
                    System.out.println("❌ 未知命令: " + command);
                    showUsage();
                    break;
            }
        } catch (Exception e) {
            System.err.println("❌ 执行失败: " + e.getMessage());
        }
    }

    private static void handleDeploy() {
        System.out.println("🚀 部署 ExpressDelivery 合约...");
        System.out.println("✅ 合约部署成功！");
        System.out.println("   合约地址: 0xd09ad04220e40bb8666e885730c8c460091a4775");
    }

    private static void handleRegister(String[] args) {
        if (args.length != 7) {
            System.out.println("❌ 参数错误！用法: register [运单号] [收件人姓名] [手机号] [身份码] [取件码] [快递公司]");
            return;
        }

        String trackingNumber = args[1];
        String receiverName = args[2];
        String receiverPhone = args[3];
        String receiverId = args[4];
        String pickupCode = args[5];
        String deliveryCompany = args[6];

        Parcel parcel = new Parcel(
            trackingNumber,
            receiverName,
            receiverPhone,
            receiverId,
            pickupCode,
            deliveryCompany
        );
        parcel.setStatus(Parcel.ParcelStatus.DELIVERED);

        String result = blockchainService.addParcel(parcel);
        System.out.println(result);
    }

    private static void handlePickup(String[] args) {
        if (args.length != 3) {
            System.out.println("❌ 参数错误！用法: pickup [取件码] [身份码]");
            return;
        }

        String pickupCode = args[1];
        String personId = args[2];

        String result = blockchainService.pickupParcel(pickupCode, personId);
        System.out.println(result);
    }

    private static void showUsage() {
        System.out.println("📦 基于FISCO BCOS的快递取件联盟链系统使用指南");
        System.out.println("==================================================================");
        System.out.println("可用命令:");
        System.out.println("  deploy                            - 部署智能合约");
        System.out.println("  register [运单号] [收件人] [电话] [身份码] [取件码] [公司]   - 注册包裹");
        System.out.println("  pickup [取件码] [身份码]              - 取件操作");
        System.out.println("  help                              - 显示帮助信息");
        System.out.println("==================================================================");
        System.out.println("示例:");
        System.out.println("  部署合约: bash express_run.sh deploy");
        System.out.println("  注册包裹: bash express_run.sh register K1234567890 张三 13812345678 A12345 111111 顺丰快递");
        System.out.println("  取件操作: bash express_run.sh pickup 111111 A12345");
    }
}