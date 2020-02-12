package com.how2java.tmall.dao;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDAO extends JpaRepository<Product,Integer> {
    //后台分页方法
    Page<Product> findByCategory(Category category, Pageable pageable);
    //前台分类方法，无需分页
    List<Product> findByCategoryOrderById(Category category);
}
