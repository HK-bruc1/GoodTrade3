package com.itheima.service.impl;

import com.fasterxml.jackson.databind.ObjectReader;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.mapper.OrderMapper;
import com.itheima.mapper.ProductMapper;
import com.itheima.mapper.UserMapper;
import com.itheima.pojo.*;
import com.itheima.service.OrderService;
import com.itheima.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author: Bruce
 * @description: 订单服务层实现类
 * @date: 2024/4/23 10:33
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private UserMapper userMapper;

    //添加订单
    @Override
    @Transactional //自动开启数据库事务避免产生错误数据
    public void add(Order order) {
        //补充属性
        //通过前端传入的商品id查询商品信息，拿到卖家信息）
        Product product = productMapper.findProductById(order.getProductId());
        //直接更新库存
        //同步块，同一时间只有一个线程可以执行这段代码，避免了多个线程同时修改 productStock 变量导致的并发问题

        if (product.getProductStock() >= order.getPurchaseQuantity()) {
            product.setProductStock(product.getProductStock() - order.getPurchaseQuantity());
        } else {
            // 处理库存不足的情况，可以抛出异常或者采取其他适当的措施
            throw new IllegalStateException("库存不足");
        }

        //下单后的库存数量
        productMapper.updateProductStock(product.getProductStock(),product.getProductId());
        order.setProductName(product.getProductName());
        order.setSellerUsername(product.getSellerUsername());
        order.setSellerEmail(product.getSellerEmail());

        //计算订单总金额
        BigDecimal productPrice = product.getProductPrice();
        Integer purchaseQuantity = order.getPurchaseQuantity();

        // 使用 BigDecimal 的 multiply 方法进行乘法运算
        BigDecimal orderTotal = productPrice.multiply(new BigDecimal(purchaseQuantity));
        order.setOrderTotal(orderTotal);

        //通过拦截器中的信息拿到买家信息
        Map<String,Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User user = userMapper.findByUserName(username);
        order.setBuyerUsername(user.getUsername());
        order.setBuyerEmail(user.getEmail());

        //将订单存入数据库
        orderMapper.add(order);
    }

    //查看订单(只能查看自己的订单，通过拦截器中的当前用户的用户名筛选出该用户的订单)
    @Override
    public PageBean<Order> list(Integer pageNum, Integer pageSize) {
        //创建pageBean对象（复用的代码）
        PageBean<Order> pb = new PageBean<>();

        //开启分页查询（自动会在sql后面加入限制）
        PageHelper.startPage(pageNum,pageSize);

        //从拦截器中拿信息（根据不同的role来决定查询条件）
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer roleId = (Integer) map.get("roleId");
        String username = (String) map.get("username");
        //调用mapper
        List<Order> as = orderMapper.list(username,roleId);//这个方法返回后就是已经分好页啦

        //Page是List的子类中提供了特有的方法，可以获取pageHelper分页查询后得到的总记录条数和当前页数据
        Page<Order> p = (Page<Order>) as;

        //补充一下属性到PageBean对象中（就两个属性，文章总条数，查询到的文章集合）
        pb.setTotal(p.getTotal());
        pb.setItems(p.getResult());
        return pb;
    }

    //删除订单（可以联合查询，但是不会）
    @Transactional
    @Override
    public void deleteOrder(Integer orderId) {
        //根据订单id拿到商品id和购买数量
        Order order = orderMapper.findProductIdByOrderId(orderId);
        Integer productId = order.getProductId();
        Integer purchaseQuantity = order.getPurchaseQuantity();
        //根据商品id拿到库存信息，把购买数量加到存库中
        Product product = productMapper.findProductById(productId);
        product.setProductStock(product.getProductStock()+purchaseQuantity);
        //更新商品表更新库存（把商品对象的库存信息修改一下又存回去）
        product.setUpdateTime(LocalDateTime.now());
        productMapper.update(product);
        //根据订单id删除订单
        orderMapper.deleteOrder(orderId);
    }

    //修改订单信息（只修改购买数量）
    @Transactional
    @Override
    public void updateOrder(Integer orderId, Integer newQuantity) {
        // 根据订单id查询订单信息（不需要校验订单是否有，前端已经定位到某个存在订单id了）
        Order order = orderMapper.findProductIdByOrderId(orderId);
        if (order == null) {
            // 处理订单不存在的情况
            throw new IllegalStateException("处理订单不存在");
        }
        //修改前的购买数量
        Integer oldQuantity = order.getPurchaseQuantity();

        // 更新订单信息，例如修改购买数量
        order.setPurchaseQuantity(newQuantity);
        order.setUpdateTime(LocalDateTime.now());

        // 查询商品信息根据订单中的商品id（不会处理这个没有商品信息的情况）
        Integer productId = order.getProductId();
        Product product = productMapper.findProductById(productId);

        // 计算新的库存数量
        Integer stockDelta = newQuantity - oldQuantity; // 计算购买数量的变化量
        Integer newStock = product.getProductStock() - stockDelta; // 计算新的库存数量
        if (newStock < 0) {
            // 处理库存不足的情况，可以抛出异常或者采取其他适当的措施
            throw new IllegalStateException("库存不足");
        }
        // 更新商品表中的库存信息（改一下商品库存又存回去）
        product.setProductStock(newStock);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.update(product);

        //计算新订单总金额
        BigDecimal productPrice = product.getProductPrice();
        Integer purchaseQuantity = order.getPurchaseQuantity();
        // 使用 BigDecimal 的 multiply 方法进行乘法运算
        BigDecimal orderTotal = productPrice.multiply(new BigDecimal(purchaseQuantity));
        order.setOrderTotal(orderTotal);
        //把订单存回去
        orderMapper.updateOrder(order);
    }
}
