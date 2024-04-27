package com.itheima.mapper;

import com.itheima.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author: Bruce
 * @description: 用户持久层接口
 * @date: 2024/4/21 21:42
 */
@Mapper
public interface UserMapper {
    //根据用户名查询用户
    @Select("select * from users where username = #{username}")
    User findByUserName(String username);

    //注册到表中
    @Insert("insert into users (role_id, username, password, create_time, update_time)" +
            " values (#{roleId},#{username},#{password},now(),now())")
    void add(String username, String password, Integer roleId);

    //完善个人数据库信息（注意字段名和属性名的区别）
    @Update("update users set nickname=#{nickname},email=#{email},update_time=#{updateTime} where id=#{id}")
    void update(User user);

    //把OSS返回的url链接存到数据库中
    @Update("update users set user_pic=#{url}, update_time=now() where id=#{id}")
    void updateAvatar(Integer id,String url);

    //更改用户密码
    @Update("update users set password=#{newPwd},update_time=now() where id=#{id}")
    void updatePwd(String newPwd, Integer id);
}
