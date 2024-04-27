package com.itheima.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.mapper.ProductMapper;
import com.itheima.pojo.PageBean;
import com.itheima.pojo.Product;
import com.itheima.pojo.User;
import com.itheima.service.ProductService;
import com.itheima.service.UserService;
import com.itheima.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author: Bruce
 * @description: 商品表服务层实现类
 * @date: 2024/4/22 16:12
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private UserService userService;

    //添加商品
    @Override
    public void add(Product product) {
        //补充属性
        Map<String,Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User user = userService.findByUserName(username);
        product.setSellerUsername(user.getUsername());
        product.setSellerEmail(user.getEmail());
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productMapper.add(product);
    }

    //查看商品列表
    @Override
    public PageBean<Product> list(Integer pageNum, Integer pageSize) {
        //创建pageBean对象
        PageBean<Product> pb = new PageBean<>();

        //开启分页查询（自动会在sql后面加入限制）
        PageHelper.startPage(pageNum,pageSize);

        //调用mapper
        List<Product> as = productMapper.list();//这个方法返回后就是已经分好页啦
        //Page是List的子类中提供了特有的方法，可以获取pageHelper分页查询后得到的总记录条数和当前页数据
        Page<Product> p = (Page<Product>) as;

        //补充一下属性到PageBean对象中（就两个属性，文章总条数，查询到的文章集合）
        pb.setTotal(p.getTotal());
        pb.setItems(p.getResult());
        return pb;
    }

    //修改商品信息
    @Override
    public void update(Product product) {
        //补充属性（只能改名称，描述，价格，库存）其他跟用户详细信息绑定的，会有级联操作更新或则删除
        product.setUpdateTime(LocalDateTime.now());
        productMapper.update(product);
    }

    //根据传入的id直接去数据库中删除
    @Override
    public void deleteProduct(Integer id) {
        productMapper.deleteProduct(id);
    }
}
