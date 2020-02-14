package com.how2java.tmall.service;

import com.how2java.tmall.dao.CategoryDAO;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames="categories")
public class CategoryService {

    @Autowired
    CategoryDAO categoryDAO;

    /**
     * 无分页
     * @return 查找所有
     */
    @Cacheable(key="'categories-all'")
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
    @Cacheable(key="'categories-page-'+#p0+ '-' + #p1")
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
    @CacheEvict(allEntries=true)
    public void add(Category bean){
        categoryDAO.save(bean);
    }

    /**
     * 用于删除数据
     * @param id 根据id进行删除
     */
    @CacheEvict(allEntries=true)
    public void delete(int id){
        categoryDAO.delete(id);
    }

    /**
     * 查找方法
     * @param id 编辑id
     * @return 返回实体类对象
     */
    @Cacheable(key="'categories-one-'+ #p0")
    public Category get(int id){
        Category category=categoryDAO.findOne(id);
        return category;
    }

    /**
     * 修改方法
     * @param category 实体类
     */
    @CacheEvict(allEntries=true)
    public void update(Category category){
        categoryDAO.save(category);
    }

    public void removeCategoryFromProduct(List<Category> categories){
        for (Category category : categories){
            removeCategoryFromProduct(category);
        }
    }

    public void removeCategoryFromProduct(Category category) {
        List<Product> products=category.getProducts();
        if (null!=products){
            for (Product product : products){
                product.setCategory(null);
            }
        }
        List<List<Product>> productsByRow = category.getProductsByRow();
        if (null!=productsByRow){
            for (List<Product> ps : productsByRow){
                for (Product product : ps){
                    product.setCategory(null);
                }
            }
        }
    }
}
