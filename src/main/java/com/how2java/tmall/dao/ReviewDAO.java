package com.how2java.tmall.dao;

import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.Review;
import com.how2java.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewDAO extends JpaRepository<Review,Integer> {
    //某产品对应的评价集合
    List<Review> findByProductOrderByIdDesc(Product product);
    //某产品对应的评价数量
    int countByProduct(Product product);
}
