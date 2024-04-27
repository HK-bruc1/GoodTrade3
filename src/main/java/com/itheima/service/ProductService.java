package com.itheima.service;

import com.itheima.pojo.PageBean;
import com.itheima.pojo.Product;

/**
 * @author: Bruce
 * @description: 商品表的服务层接口
 * @date: 2024/4/22 16:11
 */
public interface ProductService {
    //上架商品
    void add(Product product);

    //查看商品列表
    PageBean<Product> list(Integer pageNum, Integer pageSize);

    //修改商品信息
    void update(Product product);

    //删除商品信息
    void deleteProduct(Integer id);
}
