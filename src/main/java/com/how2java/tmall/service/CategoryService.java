package com.how2java.tmall.service;

import com.how2java.tmall.dao.CategoryDAO;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    CategoryDAO categoryDAO;

    /**
     * 无分页
     * @return 查找所有
     */
     public List<Category> list(){
         Sort sort=new Sort(Sort.Direction.DESC, "id");
         return categoryDAO.findAll(sort);
     }

    /**
     * 实现分页
     * @param start 起始页
     * @param size 数量
     * @param navigatePages 总页数
     * @return 返回分页的所有数据
     */
    public Page4Navigator<Category> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size,sort);
        Page pageFromJPA =categoryDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    /**
     * 增加方法
     * @param  bean
     */
    public void add(Category bean){
        categoryDAO.save(bean);
    }
}
