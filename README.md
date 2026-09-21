# 快递取件联盟链系统

## 1. 系统概述

这是一个基于FISCO BCOS联盟链的快递取件系统，实现了快递包裹的上链、取件验证和查询功能。系统采用智能合约确保数据的不可篡改和可追溯性，为快递取件提供安全可靠的解决方案。

## 2. 系统架构

- **前端**：命令行界面（CLI）
- **后端**：Java应用，集成FISCO BCOS Java SDK
- **区块链**：FISCO BCOS 2.8.0联盟链
- **智能合约**：Solidity 0.4.25

## 3. 环境要求

- **操作系统**：Ubuntu 22.04 LTS（推荐）
- **Java**：JDK 11 或更高版本
- **Maven**：3.6.0 或更高版本
- **FISCO BCOS**：2.8.0 联盟链节点

## 4. 系统安装

### 4.1 前提条件

确保您已经安装了FISCO BCOS 2.8.0联盟链节点，并且节点处于运行状态。

### 4.2 下载项目

```bash
git clone <项目仓库地址>
cd expressage
```

### 4.3 配置证书

首先，复制FISCO BCOS节点的证书到项目中：

```bash
bash scripts/copy_certs.sh
```

脚本会自动检测FISCO BCOS节点的证书路径，如果自动检测失败，您需要手动输入证书目录路径。

### 4.4 部署智能合约

```bash
bash scripts/deploy_blockchain.sh
```

#### 注意事项：

如果控制台自动下载失败，您可以手动下载控制台文件：

1. 访问 [FISCO BCOS 2.8.0控制台下载地址](https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v2.8.0/console.tar.gz)
2. 将下载的 `console.tar.gz` 文件复制到 `$HOME/fisco_express/` 目录
3. 重新运行部署脚本

### 4.5 启动系统

```bash
bash scripts/run.sh
```

## 5. 使用说明

### 5.1 命令行界面

系统启动后，将进入命令行界面，您可以使用以下命令：

1. **配送包裹**：输入 `1`，然后按照提示输入运单号、收件人姓名、电话、快递公司和身份码
2. **取件验证**：输入 `2`，然后按照提示输入运单号和身份码
3. **查询包裹**：输入 `3`，然后按照提示输入运单号
4. **退出系统**：输入 `4`

### 5.2 智能合约交互

您可以使用FISCO BCOS控制台直接与智能合约交互：

```bash
cd /root/fisco_express/console
bash start.sh
```

在控制台中，您可以使用以下命令：

```bash
# 查看所有运单号
ExpressDeliveryContract.getWaybillNumbers

# 查询包裹信息
ExpressDeliveryContract.getPackage("运单号")
```

## 6. 常见问题解决

### 6.1 控制台下载失败

**问题**：`deploy_blockchain.sh` 脚本执行时显示控制台下载失败。

**解决方法**：
1. 手动下载控制台文件：[console.tar.gz](https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v2.8.0/console.tar.gz)
2. 将下载的文件复制到 `$HOME/fisco_express/` 目录
3. 重新运行部署脚本

### 6.2 证书复制失败

**问题**：`copy_certs.sh` 脚本执行时显示证书复制失败。

**解决方法**：
1. 手动查找FISCO BCOS节点的证书目录（通常位于 `~/fisco/nodes/127.0.0.1/node0/sdk/` 或 `~/fisco/nodes/127.0.0.1/node1/sdk/`）
2. 将证书文件 `ca.crt`、`sdk.crt` 和 `sdk.key` 复制到项目的 `backend/src/main/resources/` 目录

### 6.3 智能合约部署失败

**问题**：控制台启动后无法找到或部署智能合约。

**解决方法**：
1. 检查智能合约文件 `contracts/ExpressDeliveryContract.sol` 是否存在
2. 确保智能合约使用的是 Solidity 0.4.25 版本
3. 手动将智能合约复制到控制台目录：`/root/fisco_express/console/contracts/solidity/`

## 7. 系统维护

### 7.1 查看区块链节点日志

```bash
cd ~/fisco/nodes/127.0.0.1
tail -f node0/log/log* | grep -i error
```

### 7.2 查看控制台日志

```bash
cd /root/fisco_express/console
tail -f logs/console.log
```

### 7.3 查看系统日志

```bash
cd ~/IdeaProjects/expressage
tail -f expressage.log
```

## 8. 技术支持

如果您在使用过程中遇到任何问题，请参考以下资源：

- [FISCO BCOS 官方文档](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/)
- [FISCO BCOS Java SDK 文档](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html)

## 9. 更新日志

- **v1.0.0**：
  - 实现快递包裹的上链功能
  - 实现取件验证功能
  - 实现包裹查询功能
  - 集成FISCO BCOS 2.8.0联盟链

## 10. 许可证

[MIT License](LICENSE)
