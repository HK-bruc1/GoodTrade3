package com.itheima.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * @author: Bruce
 * @description: 订单类封住传入的数据
 * @date: 2024/4/23 10:57
 */
@Data
public class Order {
    private Integer orderId;

    @NotNull(message = "下单需要提供商品id!!")
    private Integer productId;

    @NotNull(message = "下单需要提供购买数量！")
    @Min(value = 1, message = "商品数量必须大于等于1")
    private Integer purchaseQuantity;

    //在service的实现类中实现
    private BigDecimal orderTotal;

    //通过商品id去查询补充
    private String productName;
    private String sellerUsername;
    private String sellerEmail;

    //通过拦截器获取当前用户的信息
    private String buyerUsername;
    private String buyerEmail;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
