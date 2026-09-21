package com.expressage;

import java.util.Date;

/**
 * 快递包裹数据模型类
 * 实现包裹信息、取件码、身份码、状态管理等核心功能
 */
public class Parcel {
    private String trackingNumber;    // 运单号
    private String receiverName;      // 收件人姓名
    private String receiverPhone;     // 收件人电话
    private String receiverId;        // 收件人身份码（用于身份验证）
    private String pickupCode;        // 取件码（唯一识别）
    private String deliveryCompany;   // 快递公司
    private ParcelStatus status;      // 包裹状态
    private Date deliveryTime;        // 配送时间
    private Date pickupTime;          // 取件时间
    private String pickupPersonId;    // 实际取件人身份码

    // 包裹状态枚举
    public enum ParcelStatus {
        DELIVERED,  // 已送达
        PICKED_UP   // 已取件
    }

    // 构造函数
    public Parcel(String trackingNumber, String receiverName, String receiverPhone,
                 String receiverId, String pickupCode, String deliveryCompany) {
        this.trackingNumber = trackingNumber;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.receiverId = receiverId;
        this.pickupCode = pickupCode;
        this.deliveryCompany = deliveryCompany;
        this.status = ParcelStatus.DELIVERED;
        this.deliveryTime = new Date();
        this.pickupTime = null;
        this.pickupPersonId = null;
    }

    // 获取运单号
    public String getTrackingNumber() {
        return trackingNumber;
    }

    // 设置运单号
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    // 获取收件人姓名
    public String getReceiverName() {
        return receiverName;
    }

    // 设置收件人姓名
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    // 获取收件人电话
    public String getReceiverPhone() {
        return receiverPhone;
    }

    // 设置收件人电话
    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    // 获取收件人身份码
    public String getReceiverId() {
        return receiverId;
    }

    // 设置收件人身份码
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    // 获取取件码
    public String getPickupCode() {
        return pickupCode;
    }

    // 设置取件码
    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
    }

    // 获取快递公司
    public String getDeliveryCompany() {
        return deliveryCompany;
    }

    // 设置快递公司
    public void setDeliveryCompany(String deliveryCompany) {
        this.deliveryCompany = deliveryCompany;
    }

    // 获取包裹状态
    public ParcelStatus getStatus() {
        return status;
    }

    // 设置包裹状态
    public void setStatus(ParcelStatus status) {
        this.status = status;
    }

    // 获取配送时间
    public Date getDeliveryTime() {
        return deliveryTime;
    }

    // 获取取件时间
    public Date getPickupTime() {
        return pickupTime;
    }

    // 获取实际取件人身份码
    public String getPickupPersonId() {
        return pickupPersonId;
    }

    // 取件操作 - 使用当前时间作为取件时间
    public boolean pickup(String personId) {
        return pickup(personId, new Date());
    }
    
    // 取件操作 - 使用指定时间作为取件时间
    public boolean pickup(String personId, Date pickupTime) {
        if (status == ParcelStatus.DELIVERED) {
            this.status = ParcelStatus.PICKED_UP;
            this.pickupTime = pickupTime;
            this.pickupPersonId = personId;
            return true;
        } else if (status == ParcelStatus.PICKED_UP) {
            // 如果已经是取件状态，更新取件人和取件时间
            this.pickupPersonId = personId;
            this.pickupTime = pickupTime;
            return true;
        }
        return false;
    }

    // 验证取件人身份
    public boolean verifyPerson(String personId) {
        return this.receiverId.equals(personId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("运单号: " + trackingNumber + "\n");
        sb.append("收件人: " + receiverName + "\n");
        sb.append("电话: " + receiverPhone + "\n");
        sb.append("身份码: " + receiverId + "\n");
        sb.append("取件码: " + pickupCode + "\n");
        sb.append("快递公司: " + deliveryCompany + "\n");
        sb.append("状态: " + (status == ParcelStatus.DELIVERED ? "已送达" : "已取件") + "\n");
        sb.append("配送时间: " + deliveryTime + "\n");
        if (pickupTime != null) {
            sb.append("取件时间: " + pickupTime + "\n");
            sb.append("实际取件人身份码: " + pickupPersonId + "\n");
        }
        return sb.toString();
    }
}