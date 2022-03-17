package com.gys.report.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "GAIA_T_ORDER_INFO")
public class GaiaTOrderInfo implements Serializable {
    /**
     * 订单ID
     */
    @Id
    @Column(name = "ORDER_ID")
    private String orderId;

    @Column(name = "CLIENT")
    private String client;

    @Column(name = "STO_CODE")
    private String stoCode;

    /**
     * 平台
     */
    @Column(name = "PlATFORMS")
    private String platforms;

    /**
     * 平台订单ID


     */
    @Column(name = "PLATFORMS_ORDER_ID")
    private String platformsOrderId;

    /**
     * 门店编码

     */
    @Column(name = "SHOP_NO")
    private String shopNo;

    /**
     * 收件人地址


     */
    @Column(name = "RECIPIENT_ADDRESS")
    private String recipientAddress;

    /**
     * 收件人姓名


     */
    @Column(name = "RECIPIENT_NAME")
    private String recipientName;

    /**
     * 收件人电话


     */
    @Column(name = "RECIPIENT_PHONE")
    private String recipientPhone;

    /**
     * 运费
     */
    @Column(name = "SHIPPING_FEE")
    private String shippingFee;

    /**
     * 总价(门店实际收入)
     */
    @Column(name = "TOTAL_PRICE")
    private BigDecimal totalPrice;

    /**
     * 原价
     */
    @Column(name = "ORIGINAL_PRICE")
    private BigDecimal originalPrice;

    /**
     * 客户支付


     */
    @Column(name = "CUSTOMER_PAY")
    private BigDecimal customerPay;

    /**
     * 备注
     */
    @Column(name = "CAUTION")
    private String caution;

    /**
     * 订单状态（0:新订单[正常订单]1:已确认订单2:已配送订单3:已完成订单4:用户申请退单5:门店申请退单6:用户催单）
     */
    @Column(name = "STATUS")
    private String status;

    /**
     * 是否开票（0：不需要发票1：需要发票）
     */
    @Column(name = "IS_INVOICED")
    private String isInvoiced;

    /**
     * 发票抬头

     */
    @Column(name = "INVOICE_TITLE")
    private String invoiceTitle;

    /**
     * 纳税人识别号

     */
    @Column(name = "TAXPAYER_ID")
    private String taxpayerId;

    /**
     * 延时配送（延迟多久送出，默认0，立刻送出）
     */
    @Column(name = "DELIVERY_TIME")
    private Integer deliveryTime;

    /**
     *  支付类型（0：在线支付1：货到付款）
     */
    @Column(name = "PAY_TYPE")
    private String payType;

    /**
     * 优惠信息

     */
    @Column(name = "ORDER_INFO")
    private String orderInfo;

    /**
     * 外卖员取货码
     */
    @Column(name = "DAY_SEQ")
    private String daySeq;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    /**
     * 注意消息
     */
    @Column(name = "NOTICE_MESSAGE")
    private String noticeMessage;

    /**
     * 退单原因代码
     */
    @Column(name = "REASON_CODE")
    private String reasonCode;

    /**
     * 退单原因
     */
    @Column(name = "REASON")
    private String reason;

    /**
     * 退款
     */
    @Column(name = "REFUND_MONEY")
    private BigDecimal refundMoney;

    /**
     * 售后单号
     */
    @Column(name = "AFSSERVICEORDER")
    private String afsserviceorder;

    private static final long serialVersionUID = 1L;

    /**
     * 获取订单ID

     *
     * @return ORDER_ID - 订单ID

     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * 设置订单ID

     *
     * @param orderId 订单ID

     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * @return CLIENT
     */
    public String getClient() {
        return client;
    }

    /**
     * @param client
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     * @return STO_CODE
     */
    public String getStoCode() {
        return stoCode;
    }

    /**
     * @param stoCode
     */
    public void setStoCode(String stoCode) {
        this.stoCode = stoCode;
    }

    /**
     * 获取平台
     *
     * @return PlATFORMS - 平台
     */
    public String getPlatforms() {
        return platforms;
    }

    /**
     * 设置平台
     *
     * @param platforms 平台
     */
    public void setPlatforms(String platforms) {
        this.platforms = platforms;
    }

    /**
     * 获取平台订单ID


     *
     * @return PLATFORMS_ORDER_ID - 平台订单ID


     */
    public String getPlatformsOrderId() {
        return platformsOrderId;
    }

    /**
     * 设置平台订单ID


     *
     * @param platformsOrderId 平台订单ID


     */
    public void setPlatformsOrderId(String platformsOrderId) {
        this.platformsOrderId = platformsOrderId;
    }

    /**
     * 获取门店编码

     *
     * @return SHOP_NO - 门店编码

     */
    public String getShopNo() {
        return shopNo;
    }

    /**
     * 设置门店编码

     *
     * @param shopNo 门店编码

     */
    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    /**
     * 获取收件人地址


     *
     * @return RECIPIENT_ADDRESS - 收件人地址


     */
    public String getRecipientAddress() {
        return recipientAddress;
    }

    /**
     * 设置收件人地址


     *
     * @param recipientAddress 收件人地址


     */
    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    /**
     * 获取收件人姓名


     *
     * @return RECIPIENT_NAME - 收件人姓名


     */
    public String getRecipientName() {
        return recipientName;
    }

    /**
     * 设置收件人姓名


     *
     * @param recipientName 收件人姓名


     */
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    /**
     * 获取收件人电话


     *
     * @return RECIPIENT_PHONE - 收件人电话


     */
    public String getRecipientPhone() {
        return recipientPhone;
    }

    /**
     * 设置收件人电话


     *
     * @param recipientPhone 收件人电话


     */
    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    /**
     * 获取运费
     *
     * @return SHIPPING_FEE - 运费
     */
    public String getShippingFee() {
        return shippingFee;
    }

    /**
     * 设置运费
     *
     * @param shippingFee 运费
     */
    public void setShippingFee(String shippingFee) {
        this.shippingFee = shippingFee;
    }

    /**
     * 获取总价(门店实际收入)
     *
     * @return TOTAL_PRICE - 总价(门店实际收入)
     */
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    /**
     * 设置总价(门店实际收入)
     *
     * @param totalPrice 总价(门店实际收入)
     */
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * 获取客户支付


     *
     * @return CUSTOMER_PAY - 客户支付


     */
    public BigDecimal getCustomerPay() {
        return customerPay;
    }

    /**
     * 设置客户支付


     *
     * @param customerPay 客户支付


     */
    public void setCustomerPay(BigDecimal customerPay) {
        this.customerPay = customerPay;
    }

    /**
     * 获取备注
     *
     * @return CAUTION - 备注
     */
    public String getCaution() {
        return caution;
    }

    /**
     * 设置备注
     *
     * @param caution 备注
     */
    public void setCaution(String caution) {
        this.caution = caution;
    }

    /**
     * 获取订单状态（0:新订单[正常订单]1:已确认订单2:已配送订单3:已完成订单4:用户申请退单5:门店申请退单6:用户催单）
     *
     * @return STATUS - 订单状态（0:新订单[正常订单]1:已确认订单2:已配送订单3:已完成订单4:用户申请退单5:门店申请退单6:用户催单）
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置订单状态（0:新订单[正常订单]1:已确认订单2:已配送订单3:已完成订单4:用户申请退单5:门店申请退单6:用户催单）
     *
     * @param status 订单状态（0:新订单[正常订单]1:已确认订单2:已配送订单3:已完成订单4:用户申请退单5:门店申请退单6:用户催单）
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取是否开票（0：不需要发票1：需要发票）
     *
     * @return IS_INVOICED - 是否开票（0：不需要发票1：需要发票）
     */
    public String getIsInvoiced() {
        return isInvoiced;
    }

    /**
     * 设置是否开票（0：不需要发票1：需要发票）
     *
     * @param isInvoiced 是否开票（0：不需要发票1：需要发票）
     */
    public void setIsInvoiced(String isInvoiced) {
        this.isInvoiced = isInvoiced;
    }

    /**
     * 获取发票抬头

     *
     * @return INVOICE_TITLE - 发票抬头

     */
    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    /**
     * 设置发票抬头

     *
     * @param invoiceTitle 发票抬头

     */
    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    /**
     * 获取纳税人识别号

     *
     * @return TAXPAYER_ID - 纳税人识别号

     */
    public String getTaxpayerId() {
        return taxpayerId;
    }

    /**
     * 设置纳税人识别号

     *
     * @param taxpayerId 纳税人识别号

     */
    public void setTaxpayerId(String taxpayerId) {
        this.taxpayerId = taxpayerId;
    }

    /**
     * 获取延时配送（延迟多久送出，默认0，立刻送出）
     *
     * @return DELIVERY_TIME - 延时配送（延迟多久送出，默认0，立刻送出）
     */
    public Integer getDeliveryTime() {
        return deliveryTime;
    }

    /**
     * 设置延时配送（延迟多久送出，默认0，立刻送出）
     *
     * @param deliveryTime 延时配送（延迟多久送出，默认0，立刻送出）
     */
    public void setDeliveryTime(Integer deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    /**
     * 获取 支付类型（0：在线支付1：货到付款）
     *
     * @return PAY_TYPE -  支付类型（0：在线支付1：货到付款）
     */
    public String getPayType() {
        return payType;
    }

    /**
     * 设置 支付类型（0：在线支付1：货到付款）
     *
     * @param payType  支付类型（0：在线支付1：货到付款）
     */
    public void setPayType(String payType) {
        this.payType = payType;
    }

    /**
     * 获取优惠信息

     *
     * @return ORDER_INFO - 优惠信息

     */
    public String getOrderInfo() {
        return orderInfo;
    }

    /**
     * 设置优惠信息

     *
     * @param orderInfo 优惠信息

     */
    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    /**
     * 获取外卖员取货码
     *
     * @return DAY_SEQ - 外卖员取货码
     */
    public String getDaySeq() {
        return daySeq;
    }

    /**
     * 设置外卖员取货码
     *
     * @param daySeq 外卖员取货码
     */
    public void setDaySeq(String daySeq) {
        this.daySeq = daySeq;
    }

    /**
     * 获取创建时间
     *
     * @return CREATE_TIME - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取更新时间
     *
     * @return UPDATE_TIME - 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取注意消息
     *
     * @return NOTICE_MESSAGE - 注意消息
     */
    public String getNoticeMessage() {
        return noticeMessage;
    }

    /**
     * 设置注意消息
     *
     * @param noticeMessage 注意消息
     */
    public void setNoticeMessage(String noticeMessage) {
        this.noticeMessage = noticeMessage;
    }

    /**
     * 获取退单原因代码
     *
     * @return REASON_CODE - 退单原因代码
     */
    public String getReasonCode() {
        return reasonCode;
    }

    /**
     * 设置退单原因代码
     *
     * @param reasonCode 退单原因代码
     */
    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    /**
     * 获取退单原因
     *
     * @return REASON - 退单原因
     */
    public String getReason() {
        return reason;
    }

    /**
     * 设置退单原因
     *
     * @param reason 退单原因
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * 获取退款
     *
     * @return REFUND_MONEY - 退款
     */
    public BigDecimal getRefundMoney() {
        return refundMoney;
    }

    /**
     * 设置退款
     *
     * @param refundMoney 退款
     */
    public void setRefundMoney(BigDecimal refundMoney) {
        this.refundMoney = refundMoney;
    }

    /**
     * 获取售后单号
     *
     * @return AFSSERVICEORDER - 售后单号
     */
    public String getAfsserviceorder() {
        return afsserviceorder;
    }

    /**
     * 设置售后单号
     *
     * @param afsserviceorder 售后单号
     */
    public void setAfsserviceorder(String afsserviceorder) {
        this.afsserviceorder = afsserviceorder;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {

        this.originalPrice = originalPrice;
    }
}