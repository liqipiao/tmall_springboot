package com.how2java.tmall.dao;

import com.how2java.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User,Integer> {
    //前台注册
    User findByName(String name);
    //前台登录方法
    User getByNameAndPassword(String name,String password);
}
