package com.how2java.tmall.service;

import com.how2java.tmall.dao.ProductImageDAO;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.ProductImage;
import com.how2java.tmall.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames="productImages")
public class ProductImageService {
    //单个图片
    public static final String type_single="single";
    //详情图片
    public static final String type_detail="detail";

    @Autowired
    ProductImageDAO productImageDAO;

    @Autowired
    ProductService productService;

    @CacheEvict(allEntries=true)
    public void add(ProductImage productImage){
        productImageDAO.save(productImage);
    }

    @CacheEvict(allEntries=true)
    public void delete(int id){
        productImageDAO.delete(id);
    }

    @Cacheable(key="'productImages-one-'+ #p0")
    public ProductImage get(int id){
        return productImageDAO.findOne(id);
    }

    @Cacheable(key="'productImages-single-pid-'+ #p0.id")
    public List<ProductImage> listSingleProductImages(Product product){
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product,type_single);
    }

    @Cacheable(key="'productImages-detail-pid-'+ #p0.id")
    public List<ProductImage> listDetailProductImages(Product product){
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product,type_detail);
    }

    public void setFirstProdutImage(Product product) {
        ProductImageService productImageService = SpringContextUtil.getBean(ProductImageService.class);
        List<ProductImage> singleImages = productImageService.listSingleProductImages(product);
        if(!singleImages.isEmpty()) {
            product.setFirstProductImage(singleImages.get(0));
        } else {
            product.setFirstProductImage(new ProductImage());
        }
    }
    public void setFirstProdutImages(List<Product> products) {
        for (Product product : products)
            setFirstProdutImage(product);
    }

    //前台结算方法
    public void setFirstProdutImagesOnOrderItems(List<OrderItem> ois) {
        for (OrderItem orderItem : ois){
            setFirstProdutImage(orderItem.getProduct());
        }
    }
}
