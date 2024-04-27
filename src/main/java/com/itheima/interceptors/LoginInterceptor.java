package com.itheima.interceptors;

import com.itheima.utils.JwtUtil;
import com.itheima.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * @author: Bruce
 * @description: 登录状态拦截器
 * @date: 2024/4/13 18:37
 */
@Component //bean对象注解声明
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //验证令牌
        //从请求中拿到token
        String token = request.getHeader("Authorization");

        //验证登录状态token
        try {
            //从redis中获取相同的令牌进行验证
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String redisToken = operations.get(token);
            //由于token作键又作值，有就必定相同。因为token也是唯一的
            if (redisToken == null) {
                //redisToken已经失效了
                //抛出异常被catch捕获
                throw new RuntimeException();
            }
            //token也会过期，在生成令牌时就有时间，过了时间也解析不了，也会抛异常被捕获。
            Map<String,Object> claims = JwtUtil.parseToken(token);
            //把解析后token携带信息存到threadLocal中，在线程内部共享
            ThreadLocalUtil.set(claims);
            //放行
            return true;
        } catch (Exception e) {
            //响应状态码为401
            response.setStatus(401);
            //不放行
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //释放数据
        ThreadLocalUtil.remove();
    }
}
