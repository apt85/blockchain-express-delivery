package com.expressage;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // 使用 SwingUtilities.invokeLater 来确保 GUI 在事件调度线程中运行
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    // 设置系统外观
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 创建并显示主界面
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            }
        });
    }
}
