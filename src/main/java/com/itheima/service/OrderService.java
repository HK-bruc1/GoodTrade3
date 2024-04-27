package com.itheima.service;


import com.itheima.pojo.Order;
import com.itheima.pojo.PageBean;

/**
 * @author: Bruce
 * @description: 订单服务层接口
 * @date: 2024/4/23 10:32
 */
public interface OrderService {
    //添加订单
    void add(Order order);

    //查看订单
    PageBean<Order> list(Integer pageNum, Integer pageSize);

    //删除订单
    void deleteOrder(Integer orderId);

    //修改订单
    void updateOrder(Integer orderId, Integer newQuantity);
}
