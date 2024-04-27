package com.itheima.config;

import com.itheima.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author: Bruce
 * @description: 拦截器注册，放入IoC容器
 * @date: 2024/4/13 20:16
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    //注入一个bean对象
    @Autowired
    private LoginInterceptor loginInterceptor;

    //添加拦截器
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).excludePathPatterns("/user/login", "/user/register");
    }
}
