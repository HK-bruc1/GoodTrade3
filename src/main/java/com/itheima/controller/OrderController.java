package com.itheima.controller;

import com.itheima.pojo.Order;
import com.itheima.pojo.PageBean;
import com.itheima.pojo.Product;
import com.itheima.pojo.Result;
import com.itheima.service.OrderService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author: Bruce
 * @description: 订单表的控制层
 * @date: 2024/4/23 10:31
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    //添加订单
    @PostMapping
    public Result add(@RequestBody @Validated Order order) {
        orderService.add(order);
        return Result.success();
    }

    //查看订单列表（条件分页）传入页码和页面大小（待测试。。。。。。。）
    @GetMapping
    public Result<PageBean<Order>> list(Integer pageNum, Integer pageSize) {
        //pb是已经分好页的，pb是包含总条数信息和已经分好页的数据集合
        PageBean<Order> pb = orderService.list(pageNum, pageSize);
        return Result.success(pb);
    }

    //根据订单id删除订单（相应的需要修改商品信息比如库存）
    @DeleteMapping
    public Result deleteOrder(@NotNull(message = "请输入正确的订单id编号") Integer orderId){
        orderService.deleteOrder(orderId);
        return Result.success();
    }

    //修改订单信息（相应的需要修改商品信息比如库存）一般是修改购买数量
    //传入订单id和新的购买数量
    @GetMapping("/update")
    public Result updateOrder(Integer orderId, Integer newQuantity) {
        orderService.updateOrder(orderId,newQuantity);
        return Result.success();
    }
}
