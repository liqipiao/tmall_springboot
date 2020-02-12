package com.how2java.tmall.dao;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyDAO extends JpaRepository<Property,Integer> {
    //提供分页方法
    Page<Property> findByCategory(Category category, Pageable pageable);
    //通过分类获取所有属性集合的方法
    List<Property> findByCategory(Category category);
}
