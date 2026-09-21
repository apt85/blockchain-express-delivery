# 基于FISCO BCOS的快递取件联盟链系统

## 系统概述

这是一个基于FISCO BCOS联盟链的快递取件系统，实现了包裹信息的上链存储、身份验证和取件流程的去中心化管理。系统采用Java开发，支持命令行和GUI两种运行模式。

## 技术栈

- **联盟链**: FISCO BCOS 2.8.0
- **智能合约**: Solidity 0.8.0+
- **后端开发**: Java 11, Spring Framework
- **前端开发**: HTML/CSS/JavaScript, Vite
- **构建工具**: Maven, npm
- **运行环境**: Ubuntu 22.04 LTS

## 目录结构

```
expressage/
├── backend/             # 后端代码
│   ├── src/             # Java源代码
│   ├── pom.xml          # Maven配置
│   └── target/          # 构建输出
├── contracts/           # 智能合约
│   └── ExpressDeliveryContract.sol
├── frontend/            # 前端代码
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
├── scripts/             # 部署和运行脚本
│   ├── deploy.sh        # 前端部署脚本
│   ├── deploy_blockchain.sh  # 区块链部署脚本
│   └── run.sh           # 系统运行脚本
└── README_UBUNTU.md     # Ubuntu使用说明
```

## 环境准备

### 硬件要求

- CPU: 2核以上
- 内存: 4GB以上
- 磁盘: 50GB以上可用空间

### 软件要求

- Ubuntu 22.04 LTS
- Java 11 或更高版本
- Maven 3.6.0 或更高版本
- Node.js 20.x 或更高版本
- npm 9.x 或更高版本

## 快速开始

### 步骤1: 部署FISCO BCOS联盟链

```bash
# 登录Ubuntu系统后，切换到项目目录
cd ~/root/IdeaProjects/expressage

# 执行区块链部署脚本
bash scripts/deploy_blockchain.sh
```

这个脚本会自动完成以下工作：
1. 安装系统依赖
2. 下载FISCO BCOS 2.8.0
3. 创建4节点联盟链
4. 启动所有节点
5. 下载并配置控制台
6. 部署智能合约
7. 生成合约地址配置

### 步骤2: 运行快递取件系统

```bash
# 执行系统运行脚本
bash scripts/run.sh
```

根据提示选择运行模式：
- **1. 命令行模式**: 适合终端操作
- **2. GUI模式**: 图形界面操作（需要桌面环境）

## 详细使用说明

### 命令行模式使用

#### 1. 注册包裹

```bash
java -cp target/expressage-blockchain-1.0-SNAPSHOT-jar-with-dependencies.jar com.expressage.CommandLineApp register K1234567890 张三 13812345678 A12345 111111 顺丰快递
```

#### 2. 取件操作

```bash
java -cp target/expressage-blockchain-1.0-SNAPSHOT-jar-with-dependencies.jar com.expressage.CommandLineApp pickup 111111 A12345
```

#### 3. 查看帮助

```bash
java -cp target/expressage-blockchain-1.0-SNAPSHOT-jar-with-dependencies.jar com.expressage.CommandLineApp help
```

### GUI模式使用

1. 启动GUI模式后，会出现主界面
2. 点击"注册包裹"按钮，填写包裹信息
3. 点击"取件操作"按钮，输入取件码和身份码
4. 系统会自动连接区块链并执行相应操作

## 智能合约说明

### 合约地址

合约部署后，地址会自动保存到 `backend/src/main/resources/contract.properties` 文件中。

### 主要功能

1. **deliverPackage**: 配送包裹到快递站，记录包裹信息到区块链
2. **pickupPackage**: 验证身份后取件，更新包裹状态
3. **getPackage**: 查询包裹信息
4. **validatePackage**: 验证包裹是否可以被取件

## 区块链节点管理

### 启动节点

```bash
cd ~/fisco_express/nodes/127.0.0.1
bash start_all.sh
```

### 停止节点

```bash
cd ~/fisco_express/nodes/127.0.0.1
bash stop_all.sh
```

### 查看节点状态

```bash
cd ~/fisco_express/nodes/127.0.0.1
echo "节点0状态:"
curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:8545
```

## 常见问题处理

### 1. 区块链连接失败

- 检查节点是否正常运行：`bash ~/fisco_express/nodes/127.0.0.1/status`
- 检查合约地址是否正确：查看 `backend/src/main/resources/contract.properties`
- 检查证书文件是否存在：`ls ~/fisco_express/console/conf/`

### 2. 智能合约部署失败

- 确保节点已经启动
- 检查控制台日志：`tail -f ~/fisco_express/console/logs/console.log`

### 3. 系统运行缓慢

- 确保系统内存充足（建议4GB以上）
- 检查区块链节点性能：`htop`

## 安全注意事项

1. **身份码保护**: 身份码是取件的关键凭证，请勿泄露
2. **区块链安全**: 确保FISCO BCOS节点的证书和私钥安全存储
3. **网络安全**: 建议在私有网络环境下部署联盟链
4. **定期备份**: 定期备份区块链数据和系统配置

## 许可证

本项目采用 Apache License 2.0 许可证。

## 联系方式

如有问题，请联系项目维护人员。

---

**注意**: 本系统仅用于学习和演示目的，在生产环境使用前请进行充分的安全测试和性能优化。
