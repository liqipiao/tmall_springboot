package com.how2java.tmall.dao;

import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDAO extends JpaRepository<Order,Integer> {
    //前台查看订单方法
    public List<Order> findByUserAndStatusNotOrderByIdDesc(User user, String status);
}
