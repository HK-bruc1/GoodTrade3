package com.itheima.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @author: Bruce
 * @description: 封装传入的用户信息
 * @date: 2024/4/21 21:07
 */

@Data
public class User {
    private Integer id;//主键ID

    private Integer roleId;//用户角色类别

    private String username;//用户名

    @JsonIgnore
    private String password;//密码（封装返回结果时忽略密码）

    @NotEmpty
    @Pattern(regexp = "^\\S{1,10}$")
    private String nickname;//昵称

    @NotEmpty
    @Email
    private String email;//邮箱

    private String userPic;//用户头像地址
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;//创建时间（封装信息时转换格式）
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;//更新时间
}
