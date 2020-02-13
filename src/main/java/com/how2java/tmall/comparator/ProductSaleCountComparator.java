package com.how2java.tmall.comparator;

import com.how2java.tmall.pojo.Product;

import java.util.Comparator;

/**
 * 销量比较器
 * 销量高的放前面
 */
public class ProductSaleCountComparator implements Comparator<Product>{
    @Override
    public int compare(Product p1, Product p2) {
        return p2.getSaleCount()-p1.getSaleCount();
    }
}
