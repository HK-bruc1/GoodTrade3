package com.itheima.service;

import com.itheima.pojo.User;
import org.springframework.stereotype.Service;

/**
 * @author: Bruce
 * @description: 用户服务层接口
 * @date: 2024/4/21 21:17
 */

public interface UserService {
    //根据用户名去数据库查找是否存在该用户
    User findByUserName(String username);

    //注册
    void register(String username, String password, Integer roleId);

    //完善用户基本信息
    void update(User user);

    //把OSS返回的url链接存到数据库中
    void updateAvatar(String url);

    //更新用户密码
    void updatePwd(String newPwd);
}
