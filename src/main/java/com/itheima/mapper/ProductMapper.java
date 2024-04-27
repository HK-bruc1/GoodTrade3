package com.itheima.mapper;

import com.itheima.pojo.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author: Bruce
 * @description: 商品表的持久层
 * @date: 2024/4/22 16:13
 */
@Mapper
public interface ProductMapper {
    //添加商品
    @Insert("insert into products(product_name,product_desc,product_price,product_stock,seller_username,seller_email,create_time,update_time)"+
            "values(#{productName},#{productDesc},#{productPrice},#{productStock},#{sellerUsername},#{sellerEmail},#{createTime},#{updateTime})")
    void add(Product product);

    //查询商品表
    @Select("select * from products")
    List<Product> list();

    //更新商品信息
    @Update("update products set product_name=#{productName},product_desc=#{productDesc},product_price=#{productPrice},product_stock=#{productStock},update_time=#{updateTime} where product_id=#{productId}")
    void update(Product product);

    //删除商品信息
    @Delete("delete from products where product_id=#{id}")
    void deleteProduct(Integer id);

    //通过商品id查询商品信息（下订单操作需要）
    @Select("select * from products where product_id=#{productId}")
    Product findProductById(Integer productId);

    //更新库存数据（下订单操作需要）
    @Update("update products set product_stock=#{productStock},update_time=now() where product_id=#{productId}")
    void updateProductStock(Integer productStock, Integer productId);

}
