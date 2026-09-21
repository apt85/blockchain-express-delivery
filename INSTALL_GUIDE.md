# 基于FISCO BCOS的快递取件联盟链系统
## 安装与使用指南

### 系统要求
- **操作系统**: Ubuntu 22.04 LTS
- **已安装**: FISCO BCOS 2.8.0（包含已部署的联盟链节点）
- **推荐配置**: 4GB内存以上，20GB磁盘空间

### 1. 复制项目文件到Ubuntu系统

将整个 `expressage` 项目文件夹复制到Ubuntu系统中，例如复制到 `/home/user/expressage`。

### 2. 设置脚本执行权限

打开终端，进入项目的 `scripts` 目录，并设置所有脚本的执行权限：

```bash
cd /home/user/expressage/scripts
chmod +x *.sh
```

### 3. 复制FISCO BCOS证书文件

运行证书复制脚本，该脚本会自动检测并复制FISCO BCOS节点的证书文件到项目中：

```bash
bash copy_certs.sh
```

**脚本功能说明**：
- 自动检测FISCO BCOS节点的证书路径（支持多种常见路径）
- 如果自动检测失败，会提示您手动输入证书路径
- 复制 `ca.crt`、`sdk.crt`、`sdk.key` 到项目资源目录
- 复制账户私钥文件并更新 `config.toml` 配置

### 4. 部署智能合约

运行部署脚本，将智能合约部署到已有的FISCO BCOS联盟链上：

```bash
bash deploy_blockchain.sh
```

**脚本功能说明**：
- 连接到您已部署的FISCO BCOS 2.8.0联盟链节点
- 使用Solidity 0.4.25编译器编译智能合约
- 部署合约到区块链并获取合约地址
- 自动创建 `contract.properties` 文件保存合约地址

### 5. 启动快递取件系统

运行启动脚本，构建并启动快递取件系统：

```bash
bash run.sh
```

**脚本功能说明**：
- 检查并安装Java和Maven依赖（如果未安装）
- 构建后端项目生成可执行JAR文件
- 启动系统并显示命令行界面

### 6. 使用快递取件系统

系统启动后，可以使用以下命令进行操作：

#### 注册包裹
```bash
java -jar backend/target/expressage-blockchain-1.0-SNAPSHOT-jar-with-dependencies.jar register 运单号 收件人姓名 手机号 身份码 取件码 快递公司
```

**示例**：
```bash
java -jar backend/target/expressage-blockchain-1.0-SNAPSHOT-jar-with-dependencies.jar register K1234567890 张三 13812345678 A12345 111111 顺丰快递
```

#### 取件操作
```bash
java -jar backend/target/expressage-blockchain-1.0-SNAPSHOT-jar-with-dependencies.jar pickup 取件码 身份码
```

**示例**：
```bash
java -jar backend/target/expressage-blockchain-1.0-SNAPSHOT-jar-with-dependencies.jar pickup 111111 A12345
```

#### 显示帮助
```bash
java -jar backend/target/expressage-blockchain-1.0-SNAPSHOT-jar-with-dependencies.jar help
```

### 7. 验证系统功能

1. **注册包裹**：运行注册命令后，系统会将包裹信息写入区块链，并返回交易哈希
2. **取件操作**：使用正确的取件码和身份码进行取件，系统会验证身份并更新包裹状态
3. **查看日志**：检查区块链节点日志和系统输出，确认交易是否成功

### 常见问题与解决方案

#### 1. 证书路径找不到
**问题**：`copy_certs.sh` 脚本无法找到证书路径
**解决方案**：手动输入您的FISCO BCOS SDK证书目录路径，例如：`/home/user/fisco/nodes/127.0.0.1/sdk`

#### 2. 合约部署失败
**问题**：`deploy_blockchain.sh` 脚本部署合约失败
**解决方案**：
- 检查FISCO BCOS节点是否正常运行
- 检查证书文件是否正确
- 确认节点的监听端口和群组ID配置正确

#### 3. 系统启动失败
**问题**：`run.sh` 脚本启动系统失败
**解决方案**：
- 检查Java和Maven是否正确安装
- 检查区块链连接配置是否正确
- 查看系统输出的错误信息

### 系统架构

1. **区块链层**：FISCO BCOS 2.8.0联盟链，存储包裹信息和交易记录
2. **智能合约**：ExpressDeliveryContract.sol，实现包裹注册、取件、查询功能
3. **后端服务**：基于Java的应用程序，连接区块链并提供业务逻辑
4. **用户界面**：命令行界面，用于测试和使用系统功能

### 安全特性

1. **身份验证**：使用身份码进行取件验证
2. **不可篡改**：包裹信息和交易记录存储在区块链上，无法篡改
3. **透明追溯**：所有操作都记录在区块链上，可追溯
4. **权限控制**：基于FISCO BCOS的权限管理机制

### 注意事项

1. **测试环境**：本系统为演示版本，实际使用前请进行充分测试
2. **性能考虑**：区块链操作可能有延迟，系统使用本地缓存提高性能
3. **安全配置**：生产环境请修改默认配置，加强安全措施
4. **版本兼容**：确保FISCO BCOS节点版本为2.8.0，Solidity编译器版本为0.4.25

---

通过以上步骤，您已经成功将快递取件系统部署到FISCO BCOS联盟链上，实现了真正的上链存储和验证功能。如果您有任何问题或需要进一步的帮助，请查看项目文档或联系技术支持。