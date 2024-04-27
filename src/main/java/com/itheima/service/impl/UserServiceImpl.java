package com.itheima.service.impl;

import com.itheima.mapper.UserMapper;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import com.itheima.utils.Md5Util;
import com.itheima.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author: Bruce
 * @description: 用户服务层实现类
 * @date: 2024/4/21 21:18
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    //根据用户名查找用户
    @Override
    public User findByUserName(String username) {
        //调用数据库查询（交给mapper层实现）
        User user = userMapper.findByUserName(username);
        return user;
    }

    //注册
    @Override
    public void register(String username, String password, Integer roleId) {
        //对密码加密（对程序员设置的）
        String md5String = Md5Util.getMD5String(password);
        //添加到表中
        userMapper.add(username,md5String,roleId);

    }

    //完善用户基本信息（昵称，邮件）
    @Override
    public void update(User user) {
        //通过拦截器解析保存的内容中获取用户id
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        user.setId(id);
        //更新时间（补充的属性）
        user.setUpdateTime(LocalDateTime.now());
        //更新数据
        userMapper.update(user);

    }

    //把OSS返回的url链接存到数据库中
    @Override
    public void updateAvatar(String url) {
        //通过拦截器解析保存的内容中获取用户id
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        userMapper.updateAvatar(id,url);
    }

    //更新用户密码
    @Override
    public void updatePwd(String newPwd) {
        //当然可以在service层传入id，但是每一层有对应的职责。
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        String md5String = Md5Util.getMD5String(newPwd);
        userMapper.updatePwd(md5String,id);
    }

}
