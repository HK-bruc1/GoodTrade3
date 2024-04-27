package com.itheima.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import jakarta.validation.groups.Default;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author: Bruce
 * @description: 封装商品信息的实体类
 * @date: 2024/4/22 16:18
 */
@Data
public class Product {
    @NotNull(groups = Update.class)
    private Integer productId;

    @NotEmpty
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9_\\-()（）【】，。、；：:;,.]{1,50}$")
    private String productName;

    @NotEmpty
    private String productDesc;

    @NotNull
    @DecimalMin(value = "0", inclusive = true, message = "商品价格必须大于等于0")
    private BigDecimal productPrice;
    @NotNull
    @Min(value = 1, message = "商品数量必须大于等于1")
    private Integer productStock;

    //后续利用拦截器中的信息补充
    private String sellerUsername;
    private String sellerEmail;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;//创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;//更新时间

    //定义分组
    public interface Add extends Default {
    }
    public interface Update extends Default{
    }
}
