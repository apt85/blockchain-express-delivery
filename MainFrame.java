package com.expressage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

/**
 * 快递取件联盟链系统主界面
 * 使用Swing实现图形化界面，提供包裹录入、取件操作、查询及区块链日志功能
 * 突出演示错取和正确取件的场景效果
 */
public class MainFrame extends JFrame {
    // 区块链服务
    private BlockchainService blockchainService;
    // 终端日志显示
    private JTextArea terminalTextArea;

    // 包裹录入区域组件
    private JTextField trackingNumberField;
    private JTextField receiverNameField;
    private JTextField receiverPhoneField;
    private JTextField receiverIdField;
    private JTextField pickupCodeField;
    private JTextField deliveryCompanyField;

    // 取件操作区域组件
    private JTextField pickupCodeInputField;
    private JTextField personIdInputField;

    public MainFrame() {
        // 初始化区块链服务
        blockchainService = new BlockchainService();

        // 设置窗口基本信息
        setTitle("📦 基于FISCO BCOS的快递取件联盟链系统");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(255, 255, 255));

        // 初始化界面组件
        initializeComponents();

        // 添加演示数据
        addDemoData();

        // 显示欢迎信息
        displayWelcomeMessage();
    }

    private void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // 包裹操作区面板 - 移除"包裹操作中心"标题
        JPanel parcelPanel = new JPanel(new GridBagLayout());
        parcelPanel.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34), 2));
        parcelPanel.setBackground(new Color(248, 255, 248));

        // 包裹录入功能
        gbc.gridx = 0;
gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title1 = new JLabel("📋 包裹录入");
title1.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
title1.setForeground(new Color(34, 139, 34));
        parcelPanel.add(title1, gbc);

        // 运单号
        gbc.gridx = 0;
gbc.gridy = 1;
gbc.gridwidth = 1;
gbc.weightx = 0.5;
        parcelPanel.add(new JLabel("运单号:"), gbc);
        trackingNumberField = new JTextField(15);
        gbc.gridx = 1;
gbc.gridy = 1;
gbc.weightx = 1.0;
        parcelPanel.add(trackingNumberField, gbc);

        // 收件人姓名
        gbc.gridx = 0;
gbc.gridy = 2;
gbc.weightx = 0.5;
        parcelPanel.add(new JLabel("收件人姓名:"), gbc);
        receiverNameField = new JTextField(15);
        gbc.gridx = 1;
gbc.gridy = 2;
gbc.weightx = 1.0;
        parcelPanel.add(receiverNameField, gbc);

        // 收件人电话
        gbc.gridx = 0;
gbc.gridy = 3;
gbc.weightx = 0.5;
        parcelPanel.add(new JLabel("收件人电话:"), gbc);
        receiverPhoneField = new JTextField(15);
        gbc.gridx = 1;
gbc.gridy = 3;
gbc.weightx = 1.0;
        parcelPanel.add(receiverPhoneField, gbc);

        // 收件人身份码 (演示用: A12345 或 B67890)
        gbc.gridx = 0;
gbc.gridy = 4;
gbc.weightx = 0.5;
        parcelPanel.add(new JLabel("收件人身份码:"), gbc);
        receiverIdField = new JTextField(15);
        receiverIdField.setToolTipText("演示用: A12345 或 B67890");
        gbc.gridx = 1;
gbc.gridy = 4;
gbc.weightx = 1.0;
        parcelPanel.add(receiverIdField, gbc);

        // 取件码
        gbc.gridx = 0;
gbc.gridy = 5;
gbc.weightx = 0.5;
        parcelPanel.add(new JLabel("取件码:"), gbc);
        pickupCodeField = new JTextField(15);
        gbc.gridx = 1;
gbc.gridy = 5;
gbc.weightx = 1.0;
        parcelPanel.add(pickupCodeField, gbc);

        // 快递公司
        gbc.gridx = 0;
gbc.gridy = 6;
gbc.weightx = 0.5;
        parcelPanel.add(new JLabel("快递公司:"), gbc);
        deliveryCompanyField = new JTextField(15);
        gbc.gridx = 1;
gbc.gridy = 6;
gbc.weightx = 1.0;
        parcelPanel.add(deliveryCompanyField, gbc);

        // 录入按钮
        gbc.gridx = 0;
gbc.gridy = 7;
gbc.gridwidth = 2;
        JButton addParcelBtn = new JButton("📥 录入包裹");
        addParcelBtn.setForeground(Color.WHITE);
        addParcelBtn.setBackground(new Color(34, 139, 34));
        addParcelBtn.addActionListener(e -> addParcel());
        parcelPanel.add(addParcelBtn, gbc);

        // 分隔线
        gbc.gridx = 0;
gbc.gridy = 8;
gbc.gridwidth = 2;
gbc.insets = new Insets(20, 10, 20, 10);
        parcelPanel.add(new JSeparator(), gbc);

        // 取件操作功能
        gbc.gridx = 0;
gbc.gridy = 9;
gbc.gridwidth = 2;
gbc.insets = new Insets(10, 10, 10, 10);
        JLabel title2 = new JLabel("🎁 取件操作");
title2.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
title2.setForeground(new Color(34, 139, 34));
        parcelPanel.add(title2, gbc);

        // 取件码输入
        gbc.gridx = 0;
gbc.gridy = 10;
gbc.gridwidth = 1;
gbc.weightx = 0.5;
        JLabel pickupCodeLabel = new JLabel("取件码:");
pickupCodeLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        parcelPanel.add(pickupCodeLabel, gbc);
        pickupCodeInputField = new JTextField(15);
        pickupCodeInputField.setForeground(new Color(0, 0, 139));
        pickupCodeInputField.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
gbc.gridx = 1;
gbc.gridy = 10;
gbc.weightx = 1.0;
        parcelPanel.add(pickupCodeInputField, gbc);

        // 身份码输入
        gbc.gridx = 0;
gbc.gridy = 11;
gbc.weightx = 0.5;
        JLabel personIdLabel = new JLabel("取件人身份码:");
personIdLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        parcelPanel.add(personIdLabel, gbc);
        personIdInputField = new JTextField(15);
        personIdInputField.setForeground(new Color(0, 0, 139));
        personIdInputField.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
gbc.gridx = 1;
gbc.gridy = 11;
gbc.weightx = 1.0;
        parcelPanel.add(personIdInputField, gbc);

        // 取件按钮
        gbc.gridx = 0;
gbc.gridy = 12;
gbc.gridwidth = 2;
        JButton pickupBtn = new JButton("📤 取件出库");
        pickupBtn.setForeground(Color.WHITE);
        pickupBtn.setBackground(new Color(255, 140, 0));
        pickupBtn.addActionListener(e -> pickupParcel());
        parcelPanel.add(pickupBtn, gbc);






        // 分隔线
        gbc.gridx = 0;
gbc.gridy = 13;
gbc.gridwidth = 2;
gbc.insets = new Insets(20, 10, 20, 10);
        parcelPanel.add(new JSeparator(), gbc);

        // 演示说明
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel demoLabel = new JLabel("📌 演示说明:");
        demoLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        demoLabel.setForeground(new Color(255, 69, 0));
        parcelPanel.add(demoLabel, gbc);

        JTextArea demoInstructions = new JTextArea();
        demoInstructions.setText(
            "1. 系统已预设了快递包裹数据:\n" +
            "   - 运单号: K1234567890, 收件人: 张三, 身份码: A12345, 取件码: 111111\n" +
            "   - 运单号: K0987654321, 收件人: 李四, 身份码: B67890, 取件码: 222222\n" +
            "\n2. 演示错取场景: 用李四的身份码 B67890 去取张三的取件码 111111\n" +
            "   结果: 身份验证失败，显示错误信息\n" +
            "\n3. 演示正确取件: 用张三的身份码 A12345 去取张三的取件码 111111\n" +
            "   结果: 取件成功，包裹出库" +
            "\n4. 所有操作均会记录在区块链交易日志中"
        );
        demoInstructions.setEditable(false);
        demoInstructions.setLineWrap(true);
        demoInstructions.setWrapStyleWord(true);
        demoInstructions.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
        demoInstructions.setBackground(new Color(255, 250, 205));
        JScrollPane demoScroll = new JScrollPane(demoInstructions);
        demoScroll.setPreferredSize(new Dimension(400, 150));
        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.gridwidth = 2;
        parcelPanel.add(demoScroll, gbc);
        
        // 查看所有包裹信息
        gbc.gridx = 0;
        gbc.gridy = 16;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel viewParcelsLabel = new JLabel("📋 查看所有包裹");
        viewParcelsLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        viewParcelsLabel.setForeground(new Color(34, 139, 34));
        parcelPanel.add(viewParcelsLabel, gbc);
        
        // 查看包裹按钮
        gbc.gridx = 0;
        gbc.gridy = 17;
        gbc.gridwidth = 2;
        JButton viewParcelsBtn = new JButton("🔍 查看已注册包裹");
        viewParcelsBtn.setForeground(Color.WHITE);
        viewParcelsBtn.setBackground(new Color(0, 102, 204));
        viewParcelsBtn.addActionListener(e -> showAllRegisteredParcels());
        parcelPanel.add(viewParcelsBtn, gbc);

        // 区块链终端面板
        JPanel blockchainPanel = new JPanel(new BorderLayout());
        blockchainPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
            "🔗 区块链终端 (FISCO BCOS 联盟链)",
            0, 0, new Font("Microsoft YaHei", Font.BOLD, 14), new Color(0, 102, 204)));

        // 区块链操作日志显示
        terminalTextArea = new JTextArea(25, 60);
        terminalTextArea.setEditable(false);
        terminalTextArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        terminalTextArea.setBackground(new Color(0, 51, 102));
        terminalTextArea.setForeground(new Color(50, 205, 50));
        terminalTextArea.setLineWrap(true);
        terminalTextArea.setWrapStyleWord(true);
        JScrollPane terminalScrollPane = new JScrollPane(terminalTextArea);
        blockchainPanel.add(terminalScrollPane, BorderLayout.CENTER);

        // 添加到主窗口
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
gbc.weighty = 1.0;
        gbc.gridx = 0;
gbc.gridy = 0;
        getContentPane().add(parcelPanel, gbc);

        gbc.gridx = 1;
gbc.gridy = 0;
gbc.weightx = 0.5;
gbc.weighty = 1.0;
        getContentPane().add(blockchainPanel, gbc);
    }

    private void addDemoData() {
        // 添加演示包裹数据
        Parcel demoParcel1 = new Parcel(
            "K1234567890",
            "张三",
            "13812345678",
            "A12345",  // 张三的身份码
            "111111",   // 取件码
            "顺丰快递"
        );
        demoParcel1.setStatus(Parcel.ParcelStatus.DELIVERED);

        Parcel demoParcel2 = new Parcel(
            "K0987654321",
            "李四",
            "13987654321",
            "B67890",  // 李四的身份码
            "222222",   // 取件码
            "圆通快递"
        );
        demoParcel2.setStatus(Parcel.ParcelStatus.DELIVERED);

        blockchainService.addParcel(demoParcel1);
        blockchainService.addParcel(demoParcel2);
    }

    private void addParcel() {
        try {
            String trackingNumber = trackingNumberField.getText();
            String receiverName = receiverNameField.getText();
            String receiverPhone = receiverPhoneField.getText();
            String receiverId = receiverIdField.getText();
            String pickupCode = pickupCodeField.getText();
            String deliveryCompany = deliveryCompanyField.getText();

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
            terminalTextArea.append("\n" + result);
            terminalTextArea.append("\n-----------------------------------");
            terminalTextArea.setCaretPosition(terminalTextArea.getDocument().getLength());

        } catch (Exception e) {
            terminalTextArea.append("\n❌ 错误: " + e.getMessage());
            terminalTextArea.append("\n-----------------------------------");
            terminalTextArea.setCaretPosition(terminalTextArea.getDocument().getLength());
        }
    }

    private void pickupParcel() {
        String pickupCode = pickupCodeInputField.getText();
        String personId = personIdInputField.getText();

        if (pickupCode.isEmpty() || personId.isEmpty()) {
            terminalTextArea.append("\n❌ 请填写取件码和身份码");
            terminalTextArea.append("\n-----------------------------------");
            terminalTextArea.setCaretPosition(terminalTextArea.getDocument().getLength());
            return;
        }

        String result = blockchainService.pickupParcel(pickupCode, personId);
        terminalTextArea.append("\n" + result);
        terminalTextArea.append("\n-----------------------------------");
        terminalTextArea.setCaretPosition(terminalTextArea.getDocument().getLength());
    }

    private void displayWelcomeMessage() {
        terminalTextArea.append("🌟 欢迎使用基于FISCO BCOS的快递取件联盟链系统！\n");
        terminalTextArea.append("========================================\n");
        terminalTextArea.append("本系统已预设演示数据，您可以直接进行操作：\n");
        terminalTextArea.append("\n📦 已入库包裹 1: 运单号 K1234567890");
        terminalTextArea.append("\n   收件人: 张三");
        terminalTextArea.append("\n   身份码: A12345");
        terminalTextArea.append("\n   取件码: 111111");
        terminalTextArea.append("\n\n📦 已入库包裹 2: 运单号 K0987654321");
        terminalTextArea.append("\n   收件人: 李四");
        terminalTextArea.append("\n   身份码: B67890");
        terminalTextArea.append("\n   取件码: 222222");
        terminalTextArea.append("\n\n💡 使用指南:");
        terminalTextArea.append("\n   - 取件码和身份码必须一致才能取件成功");
        terminalTextArea.append("\n   - 所有操作都会被区块链记录");
        terminalTextArea.append("\n   - 查看右侧的操作日志了解详细信息");
        terminalTextArea.append("\n   - 点击'查看已注册包裹'按钮查看所有已注册的包裹信息");
        terminalTextArea.append("\n========================================\n");
    }
    
    /**
     * 显示所有已注册包裹的信息
     */
    private void showAllRegisteredParcels() {
        try {
            // 获取所有已注册包裹
            List<Parcel> parcels = blockchainService.getAllRegisteredParcels();
            
            // 创建表格模型
            String[] columnNames = {
                "运单号", "收件人", "联系电话", "身份码", "取件码", "快递公司", "包裹状态", "取件时间"
            };
            
            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // 表格不可编辑
                }
            };
            
            // 添加数据到表格模型
            for (Parcel parcel : parcels) {
                Vector<String> row = new Vector<>();
                row.add(parcel.getTrackingNumber());
                row.add(parcel.getReceiverName());
                row.add(parcel.getReceiverPhone());
                row.add(parcel.getReceiverId());
                row.add(parcel.getPickupCode());
                row.add(parcel.getDeliveryCompany());
                row.add(parcel.getStatus().toString());
                row.add(parcel.getPickupTime() != null ? parcel.getPickupTime().toString() : "未取件");
                model.addRow(row);
            }
            
            // 创建表格
            JTable table = new JTable(model);
            table.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
            table.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
            table.setRowHeight(25);
            table.getColumnModel().getColumn(0).setPreferredWidth(120);
            table.getColumnModel().getColumn(1).setPreferredWidth(80);
            table.getColumnModel().getColumn(2).setPreferredWidth(100);
            table.getColumnModel().getColumn(3).setPreferredWidth(80);
            table.getColumnModel().getColumn(4).setPreferredWidth(80);
            table.getColumnModel().getColumn(5).setPreferredWidth(100);
            table.getColumnModel().getColumn(6).setPreferredWidth(80);
            table.getColumnModel().getColumn(7).setPreferredWidth(150);
            
            // 创建滚动面板
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(800, 400));
            
            // 创建对话框
            JDialog dialog = new JDialog(this, "📦 已注册包裹列表", true);
            dialog.setLayout(new BorderLayout());
            dialog.add(scrollPane, BorderLayout.CENTER);
            
            // 添加关闭按钮
            JButton closeBtn = new JButton("关闭");
            closeBtn.addActionListener(e -> dialog.dispose());
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(closeBtn);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            
            // 在终端日志中记录操作
            terminalTextArea.append("\n🔍 查看了所有已注册包裹，共 " + parcels.size() + " 个包裹");
            terminalTextArea.append("\n-----------------------------------");
            terminalTextArea.setCaretPosition(terminalTextArea.getDocument().getLength());
            
        } catch (Exception e) {
            terminalTextArea.append("\n❌ 查看包裹列表失败: " + e.getMessage());
            terminalTextArea.append("\n-----------------------------------");
            terminalTextArea.setCaretPosition(terminalTextArea.getDocument().getLength());
            e.printStackTrace();
        }
    }
    
    /**
     * 主方法，程序入口
     */
    public static void main(String[] args) {
        // 在事件调度线程中创建和显示GUI
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
