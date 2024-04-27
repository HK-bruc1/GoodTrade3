package com.itheima.controller;

import com.itheima.pojo.Product;
import com.itheima.pojo.Result;
import com.itheima.service.ProductService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.itheima.pojo.PageBean;

/**
 * @author: Bruce
 * @description: 商品表的表现层
 * @date: 2024/4/22 16:05
 */
@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    //上架商品
    @PostMapping
    public Result add(@RequestBody @Validated(Product.Add.class) Product product) {
        productService.add(product);
        return Result.success();
    }

    //查看商品列表（条件分页）传入页码和页面大小
    @GetMapping
    public Result<PageBean<Product>> list(Integer pageNum, Integer pageSize) {
        //pb是已经分好页的，pb是包含总条数信息和已经分好页的数据集合
        PageBean<Product> pb = productService.list(pageNum, pageSize);
        return Result.success(pb);
    }

    //更新商品信息（上架和修改的校验有一点不同，需要加上商品id,所以将规则分组）
    @PutMapping
    public Result update(@RequestBody @Validated(Product.Update.class) Product product) {
        productService.update(product);
        return Result.success();
    }

    //删除商品信息（直接从请求参数中拿）
    @DeleteMapping
    public Result deleteProduct(@NotNull(message = "请输入正确的商品id编号") Integer id){
        productService.deleteProduct(id);
        return Result.success();
    }
}
