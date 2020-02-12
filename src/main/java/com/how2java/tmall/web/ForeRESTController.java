package com.how2java.tmall.web;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.User;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.ProductService;
import com.how2java.tmall.service.UserService;
import com.how2java.tmall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class ForeRESTController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;

    /**
     * 查询分类方法
     * 1. 查询所有分类
     2. 为这些分类填充产品集合
     3. 为这些分类填充推荐产品集合
     4. 移除产品里的分类信息，以免出现重复递归
     * @return
     */
    @GetMapping("/forehome")
    public Object home(){
        List<Category> cs=categoryService.list();
        productService.fill(cs);
        productService.fillByRow(cs);
        categoryService.removeCategoryFromProduct(cs);
        return cs;
    }

    /**
     * 注册方法
     * @param user
     * @return
     */
    @PostMapping("/foreregister")
    public Object register(@RequestBody User user){
        String name=user.getName();
        String password=user.getPassword();
        name= HtmlUtils.htmlEscape(name);
        user.setName(name);
        boolean exist=userService.isExist(name);
        if (exist){
            String message="用户名已经被使用，请重新输入";
            return Result.fail(message);
        }
        user.setPassword(password);
        userService.add(user);
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
    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session){
        String name=userParam.getName();
        name=HtmlUtils.htmlEscape(name);
        User user=userService.get(name,userParam.getPassword());
        if (null==user){
            String message="账号或密码错误";
            return Result.fail(message);
        }else {
            session.setAttribute("user",user);
            return Result.success();
        }
    }
}
