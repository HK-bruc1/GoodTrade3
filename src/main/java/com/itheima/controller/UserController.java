package com.itheima.controller;

import com.itheima.pojo.Result;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import com.itheima.utils.JwtUtil;
import com.itheima.utils.Md5Util;
import com.itheima.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author: Bruce
 * @description: 用户表表现层
 * @date: 2024/4/21 21:05
 */
@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    //注入redisAPI对象
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{5,16}$") String username,
                           @Pattern(regexp = "^\\S{5,16}$") String password,
                           @Pattern(regexp = "^\\S{5,16}$") String repassword,
                           @Min(value = 1, message = "roleId必须大于等于1") @Max(value = 3, message = "roleId必须小于等于3") Integer roleId) {
        //校验两次密码是否一致
        if(!password.equals(repassword)){
            return Result.error("两次填写的密码不一致");
        }
        //查询用户
        User user = userService.findByUserName(username);//查找用户名是否存在
        if (user == null) {
            //没有占用
            //注册
            userService.register(username,password,roleId);
            return Result.success();//调用统一的响应结果（有三个）
        }else {
            //占用
            return Result.error("用户名已被占用！");

        }
    }

    //登录
    @RequestMapping("/login")
    public Result<String> login(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password) {
        //根据用户名查询用户（核对账号信息）
        User loginUser = userService.findByUserName(username);
        //判断用户是否存在
        if (loginUser == null) {
            return Result.error("用户名错误！");
        }
        //判断密码是否正确（核对用户密码，login对象中的password是加密过的）
        if (Md5Util.getMD5String(password).equals(loginUser.getPassword())) {
            //登录成功后返回一个jwt令牌（令牌头部不需要存很多东西，能代表用户即可）
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", loginUser.getId());
            claims.put("username", loginUser.getUsername());
            claims.put("roleId", loginUser.getRoleId());
            String token = JwtUtil.genToken(claims);
            //返回令牌的同时存到redis中
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            //过期时间与jwt令牌同步，以token为键又为值，到时候拦截器好拿一点
            operations.set(token,token,1, TimeUnit.HOURS);
            return Result.success(token);
        }
        return Result.error("密码错误！");
    }

    //获取用户详细信息
    @GetMapping("/userInfo")
    public Result<User> userInfo() {
        //根据用户名查询用户（已经注册登陆过的才有token，复用threadLocal中的数据）
        Map<String,Object> map = ThreadLocalUtil.get();
        String  username = (String) map.get("username");
        User user = userService.findByUserName(username);
        return Result.success(user);
    }

    //完善用户基本信息（昵称，邮件）
    @PutMapping("/update")
    public Result update(@RequestBody @Validated User user){
        //调用业务层代码
        userService.update(user);
        return Result.success();
    }

    //更新头像（一般是选择本地图片进行上传（存放到阿里云OSS）），测试用本地坏境
    @PostMapping("/updateAvatar")
    public Result<String> updateAvatar(MultipartFile file) throws IOException {
        //拿到原始文件名
        String originalFilename = file.getOriginalFilename();
        //为了避免名称相同导致的覆盖而丢失资源，保证名称唯一
        //随机生成一个前缀，把原来的名字的格式后缀截取下来拼接即可
        String fileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));

        //本地环境
        file.transferTo(new File("C:\\Users\\bruce_wang\\Desktop" + fileName));
        return Result.success("图片访问地址url");
    }

    //更新用户密码
    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String,String> params,@RequestHeader("Authorization") String token) {
        //1.校验参数（三个必须要填的密码）
        String oldPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String rePwd = params.get("re_pwd");
        if (!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)) {
            //有一个没有就不处理
            return Result.error("缺少必要的参数");
        }
        //校验原密码是否一致（先根据用户名查一下原密码）
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User loginUser = userService.findByUserName(username);
        if (!loginUser.getPassword().equals(Md5Util.getMD5String(oldPwd))) {
            return Result.error("原密码填写不正确");
        }

        //新密码是否一致
        if (!newPwd.equals(rePwd)) {
            return Result.error("两次填写的密码不一致");
        }

        //更新数据
        userService.updatePwd(newPwd);
        //更新完就删除redis中的令牌
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.getOperations().delete(token);
        return Result.success();
    }
}
