package com.how2java.tmall.web;

import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.ProductService;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
public class ProductController {
    @Autowired
    ProductService productService;

    @Autowired
    CategoryService categoryService;

    @GetMapping("/categories/{cid}/products")
    public Page4Navigator<Product> list(@PathVariable("cid") int cid, @RequestParam(value = "start",defaultValue = "0") int start,
                                        @RequestParam(value = "size",defaultValue = "5") int size) throws Exception{
        start=start<0?0:start;
        Page4Navigator<Product> page=productService.list(cid,start,size,5);
        return page;
    }

    @GetMapping("/products/{id}")
    public Product get(@PathVariable("id") int id) throws Exception{
        Product product=productService.get(id);
        return product;
    }

    @PostMapping("/products")
    public Object add(@RequestBody Product product) throws Exception{
        product.setCreateDate(new Date());
        productService.add(product);
        return product;
    }

    @DeleteMapping("/products/{id}")
    public String delete(@PathVariable("id") int id, HttpServletRequest request) throws Exception{
        productService.delete(id);
        return null;
    }

    @PutMapping("/products")
    public Object update(@RequestBody Product product) throws Exception{
        productService.update(product);
        return product;
    }
}
