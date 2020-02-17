package com.how2java.tmall.web;

import com.how2java.tmall.pojo.User;
import com.how2java.tmall.service.UserService;
import com.how2java.tmall.util.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;

@RestController
public class AdminUserController {
    @Autowired
    UserService adminUserService;

    /**
     * 注册方法
     * @param adminUser
     * @return
     */
    @PostMapping("/admin_zc")
    public Object register(@RequestBody User adminUser){
        String name=adminUser.getName();
        String password=adminUser.getPassword();
        name= HtmlUtils.htmlEscape(name);
        adminUser.setName(name);
        boolean exist=adminUserService.isExist(name);
        if (exist){
            String message="用户名已经被使用，请重新输入";
            return Result.fail(message);
        }
        //使用shiro进行密码加密
        String salt=new SecureRandomNumberGenerator().nextBytes().toString();
        int tiems=2;
        String algorithmName = "md5";
        String encodedPassword=new SimpleHash(algorithmName,password,salt,tiems).toString();
        adminUser.setSalt(salt);
        adminUser.setPassword(encodedPassword);
        adminUserService.add(adminUser);
        return Result.success();
    }
    /**
     * 登录方法
     * 1. 账号密码注入到 userParam 对象上
     2. 把账号通过HtmlUtils.htmlEscape进行转义
     3. 根据账号和密码获取User对象
     3.1 如果对象为空，则返回错误信息
     3.2 如果对象存在，则把用户对象放在 session里，并且返回成功信息
     * @param userParam 用户信息
     * @param session 保存用户信息session
     * @return
     */
    @PostMapping("/adminlogin")
    public Object login(@RequestBody User userParam, HttpSession session){
        String name=userParam.getName();
        name=HtmlUtils.htmlEscape(name);
        //使用shiro进行验证
        Subject subject= SecurityUtils.getSubject();
        UsernamePasswordToken token=new UsernamePasswordToken(name,userParam.getPassword());
        try {
            subject.login(token);
            User adminUser=adminUserService.getByName(name);
            session.setAttribute("adminuser",adminUser);
            return Result.success();
        }catch (Exception e){
            e.printStackTrace();
            String message ="账号或密码错误";
            return Result.fail(message);
        }
    }
}
