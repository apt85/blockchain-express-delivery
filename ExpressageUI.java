package com.expressage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

/**
 * 快递取件联盟链系统交互式UI
 * 提供图形界面进行包裹注册和取件操作
 */
public class ExpressageUI extends JFrame {
    private static BlockchainService blockchainService;
    
    // 注册包裹相关组件
    private JTabbedPane tabbedPane;
    private JPanel registerPanel;
    private JTextField trackingNumberField;
    private JTextField receiverNameField;
    private JTextField receiverPhoneField;
    private JTextField receiverIdField;
    private JTextField pickupCodeField;
    private JTextField deliveryCompanyField;
    private JButton registerButton;
    private JTextArea resultTextArea;
    
    // 取件相关组件
    private JPanel pickupPanel;
    private JTextField pickupCodeInputField;
    private JTextField personIdInputField;
    private JButton pickupButton;
    
    public ExpressageUI() {
        // 设置窗口基本属性
        setTitle("快递取件联盟链系统");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // 初始化区块链服务
        try {
            blockchainService = new BlockchainService();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "❌ 区块链服务初始化失败: " + e.getMessage() + "\n请检查区块链连接配置和网络状态",
                "初始化失败", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // 创建主界面
        createMainUI();
        
        // 调整窗口大小以适应所有组件
        pack();
        // 设置最小窗口大小，防止用户调整窗口过小导致重叠
        setMinimumSize(new Dimension(600, 700));
        // 显示窗口
        setVisible(true);
    }
    
    private void createMainUI() {
        // 创建标签页
        tabbedPane = new JTabbedPane();
        
        // 创建注册面板
        createRegisterPanel();
        tabbedPane.addTab("包裹注册", registerPanel);
        
        // 创建取件面板
        createPickupPanel();
        tabbedPane.addTab("包裹取件", pickupPanel);
        
        // 创建结果显示区域
        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setFont(new Font("Monaco", Font.PLAIN, 12));
        resultTextArea.setLineWrap(true);
        resultTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        scrollPane.setPreferredSize(new Dimension(600, 150));
        
        // 创建功能按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        // 添加查看已注册包裹按钮
        JButton viewParcelsButton = new JButton("查看已注册包裹");
        viewParcelsButton.setPreferredSize(new Dimension(150, 30));
        viewParcelsButton.setFont(new Font("宋体", Font.BOLD, 14));
        viewParcelsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAllRegisteredParcels();
            }
        });
        buttonPanel.add(viewParcelsButton);
        
        // 添加组件到主窗口
        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.SOUTH);
        
        // 显示欢迎信息
        resultTextArea.append("📦 欢迎使用快递取件联盟链系统！\n");
        resultTextArea.append("====================================================================\n");
        resultTextArea.append("请选择一个功能标签页进行操作，或点击上方按钮查看已注册包裹\n");
    }
    
    private void createRegisterPanel() {
        registerPanel = new JPanel(new GridBagLayout());
        registerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 运单号
        gbc.gridx = 0;
        gbc.gridy = 0;
        registerPanel.add(new JLabel("运单号:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        trackingNumberField = new JTextField(20);
        registerPanel.add(trackingNumberField, gbc);
        
        // 收件人姓名
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        registerPanel.add(new JLabel("收件人姓名:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        receiverNameField = new JTextField(20);
        registerPanel.add(receiverNameField, gbc);
        
        // 手机号
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        registerPanel.add(new JLabel("手机号:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        receiverPhoneField = new JTextField(20);
        registerPanel.add(receiverPhoneField, gbc);
        
        // 身份码
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        registerPanel.add(new JLabel("身份码:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        receiverIdField = new JTextField(20);
        registerPanel.add(receiverIdField, gbc);
        
        // 取件码
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        registerPanel.add(new JLabel("取件码:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        pickupCodeField = new JTextField(20);
        registerPanel.add(pickupCodeField, gbc);
        
        // 快递公司
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        registerPanel.add(new JLabel("快递公司:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        deliveryCompanyField = new JTextField(20);
        registerPanel.add(deliveryCompanyField, gbc);
        
        // 注册按钮
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        registerButton = new JButton("注册包裹");
        registerButton.setPreferredSize(new Dimension(150, 30));
        registerButton.setFont(new Font("宋体", Font.BOLD, 14));
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });
        registerPanel.add(registerButton, gbc);
    }
    
    private void createPickupPanel() {
        pickupPanel = new JPanel(new GridBagLayout());
        pickupPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 取件码
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        pickupPanel.add(new JLabel("取件码:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        pickupCodeInputField = new JTextField(20);
        pickupPanel.add(pickupCodeInputField, gbc);
        
        // 身份码
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        pickupPanel.add(new JLabel("身份码:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        personIdInputField = new JTextField(20);
        pickupPanel.add(personIdInputField, gbc);
        
        // 取件按钮
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        pickupButton = new JButton("取件操作");
        pickupButton.setPreferredSize(new Dimension(150, 30));
        pickupButton.setFont(new Font("宋体", Font.BOLD, 14));
        pickupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePickup();
            }
        });
        pickupPanel.add(pickupButton, gbc);
    }
    
    private void handleRegister() {
        // 清空结果区域
        resultTextArea.setText("");
        
        // 获取输入数据
        String trackingNumber = trackingNumberField.getText().trim();
        String receiverName = receiverNameField.getText().trim();
        String receiverPhone = receiverPhoneField.getText().trim();
        String receiverId = receiverIdField.getText().trim();
        String pickupCode = pickupCodeField.getText().trim();
        String deliveryCompany = deliveryCompanyField.getText().trim();
        
        // 验证输入
        if (trackingNumber.isEmpty() || receiverName.isEmpty() || receiverPhone.isEmpty() || 
            receiverId.isEmpty() || pickupCode.isEmpty() || deliveryCompany.isEmpty()) {
            resultTextArea.append("❌ 错误: 所有字段不能为空！\n");
            return;
        }
        
        // 创建包裹对象
        Parcel parcel = new Parcel(
            trackingNumber,
            receiverName,
            receiverPhone,
            receiverId,
            pickupCode,
            deliveryCompany
        );
        parcel.setStatus(Parcel.ParcelStatus.DELIVERED);
        
        // 调用区块链服务注册包裹
        String result = blockchainService.addParcel(parcel);
        resultTextArea.append(result + "\n");
        
        // 清空输入框
        trackingNumberField.setText("");
        receiverNameField.setText("");
        receiverPhoneField.setText("");
        receiverIdField.setText("");
        pickupCodeField.setText("");
        deliveryCompanyField.setText("");
    }
    
    private void handlePickup() {
        // 清空结果区域
        resultTextArea.setText("");
        
        // 获取输入数据
        String pickupCode = pickupCodeInputField.getText().trim();
        String personId = personIdInputField.getText().trim();
        
        // 验证输入
        if (pickupCode.isEmpty() || personId.isEmpty()) {
            resultTextArea.append("❌ 错误: 取件码和身份码不能为空！\n");
            return;
        }
        
        // 调用区块链服务执行取件
        String result = blockchainService.pickupParcel(pickupCode, personId);
        resultTextArea.append(result + "\n");
        
        // 清空输入框
        pickupCodeInputField.setText("");
        personIdInputField.setText("");
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
            table.setFont(new Font("宋体", Font.PLAIN, 12));
            table.getTableHeader().setFont(new Font("宋体", Font.BOLD, 12));
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
            
            // 在结果区域中记录操作
            resultTextArea.append("🔍 查看了所有已注册包裹，共 " + parcels.size() + " 个包裹\n");
            resultTextArea.append("====================================================================\n");
            
        } catch (Exception e) {
            resultTextArea.append("❌ 查看包裹列表失败: " + e.getMessage() + "\n");
            resultTextArea.append("====================================================================\n");
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // 在事件调度线程中创建和显示UI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ExpressageUI();
            }
        });
    }
}