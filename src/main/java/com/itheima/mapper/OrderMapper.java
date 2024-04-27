package com.itheima.mapper;

import com.itheima.pojo.Order;
import com.itheima.pojo.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author: Bruce
 * @description: 订单的持久层
 * @date: 2024/4/23 10:34
 */
@Mapper
public interface OrderMapper {
    //添加订单
    @Insert("insert into orders(product_id,product_name,purchase_quantity,order_total,seller_username,seller_email,buyer_username,buyer_email,create_time,update_time)"+
            "values(#{productId},#{productName},#{purchaseQuantity},#{orderTotal},#{sellerUsername},#{sellerEmail},#{buyerUsername},#{buyerEmail},now(),now())")
    void add(Order order);

    //查看订单（根据roleID来决定查询条件，用动态sql）
    List<Order> list(String username,Integer roleId);

    //根据订单id拿到商品id
    @Select("select * from orders where order_id=#{orderId}")
    Order findProductIdByOrderId(Integer orderId);

    //根据订单id删除记录
    @Delete("delete from orders where order_id=#{orderId}")
    void deleteOrder(Integer orderId);

    //更新订单信息
    @Update("update orders set purchase_quantity=#{purchaseQuantity},order_total=#{orderTotal},update_time=#{updateTime} where order_id=#{orderId}")
    void updateOrder(Order order);
}
