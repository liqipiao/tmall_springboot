package com.how2java.tmall.service;

import com.how2java.tmall.dao.ProductDAO;
import com.how2java.tmall.es.ProductESDAO;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.util.Page4Navigator;
import com.how2java.tmall.util.SpringContextUtil;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames="products")
public class ProductService {
    @Autowired
    ProductDAO productDAO;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductImageService productImageService;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    ReviewService reviewService;

    @Autowired
    ProductESDAO productESDAO;

    @CacheEvict(allEntries=true)
    public void add(Product product){
        productDAO.save(product);
        //同步到elasticsearch
        productESDAO.save(product);
    }

    @CacheEvict(allEntries=true)
    public void delete(int id){
        productDAO.delete(id);
        //同步到elasticsearch
        productESDAO.delete(id);
    }

    @Cacheable(key="'products-one-'+ #p0")
    public Product get(int id){
        return productDAO.findOne(id);
    }

    @CacheEvict(allEntries=true)
    public void update(Product product){
        productDAO.save(product);
        //同步到elasticsearch
        productESDAO.save(product);
    }

    @Cacheable(key="'products-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Product> list(int cid,int start,int size,int navigatePages){
        Category category=categoryService.get(cid);
        Sort sort=new Sort(Sort.Direction.DESC,"id");
        Pageable pageable=new PageRequest(start,size,sort);
        Page<Product> pageFormJPA=productDAO.findByCategory(category,pageable);
        return new Page4Navigator<>(pageFormJPA,navigatePages);
    }

    //前台为多个分类填充产品集合
    public void fill(List<Category> categories){
        for (Category category : categories){
            fill(category);
        }
    }

    //前台为分类填充产品集合
    public void fill(Category category) {
        ProductService productService = SpringContextUtil.getBean(ProductService.class);
        List<Product> products = productService.listByCategory(category);
        productImageService.setFirstProdutImages(products);
        category.setProducts(products);
    }

    //前台查询某个分类下的所有产品
    @Cacheable(key="'products-cid-'+ #p0.id")
    public List<Product> listByCategory(Category category) {
        return productDAO.findByCategoryOrderById(category);
    }

    //前台为多个分类填充推荐产品集合，即把分类下的产品集合，按照8个为一行，拆成多行，以利于后续页面上进行显示
    public void fillByRow(List<Category> categories){
        int productNumberEachRow = 8;
        for (Category category : categories){
            List<Product> products=category.getProducts();
            List<List<Product>> productByRow=new ArrayList<>();
            for (int i=0;i<products.size();i+=productNumberEachRow){
                int size=i+productNumberEachRow;
                size=size>products.size()?products.size():size;
                List<Product> productsOfEachRow=products.subList(i,size);
                productByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productByRow);
        }
    }

    //前台产品设置销量
    public void setSaleAndReviewNumber(Product product){
        int saleCount=orderItemService.getSaleCount(product);
        product.setSaleCount(saleCount);
        int reviewCount=reviewService.getCount(product);
        product.setReviewCount(reviewCount);
    }

    //前台评价数量方法
    public void setSaleAndReviewNumber(List<Product> products){
        for (Product product : products){
            setSaleAndReviewNumber(product);
        }
    }

    //模糊查询方法
    public List<Product> search(String keyword,int start,int size){
       /* Sort sort=new Sort(Sort.Direction.DESC,"id");
        Pageable pageable=new PageRequest(start,size,sort);
        List<Product> products=productDAO.findByNameLike("%"+keyword+"%",pageable);
        return products;*/
       initDatabase2ES();
        FunctionScoreQueryBuilder functionScoreQueryBuilder= QueryBuilders.functionScoreQuery().add(QueryBuilders.matchPhraseQuery("name",keyword)
        , ScoreFunctionBuilders.weightFactorFunction(100)).scoreMode("sum").setMinScore(10);
        Sort sort=new Sort(Sort.Direction.DESC,"id");
        Pageable pageable=new PageRequest(start,size,sort);
        SearchQuery searchQuery=new NativeSearchQueryBuilder().withPageable(pageable).withQuery(functionScoreQueryBuilder).build();
        Page<Product> page=productESDAO.search(searchQuery);
       return page.getContent();
    }

    //初始化数据到es. 因为数据刚开始都在数据库中，不在es中，所以刚开始查询，先看看es有没有数据，如果没有，就把数据从数据库同步到es中。
    private void initDatabase2ES(){
        Pageable pageable=new PageRequest(0,5);
        Page<Product> page=productESDAO.findAll(pageable);
        if (page.getContent().isEmpty()){
            List<Product> products=productDAO.findAll();
            for (Product product : products){
                productESDAO.save(product);
            }
        }
    }
}
