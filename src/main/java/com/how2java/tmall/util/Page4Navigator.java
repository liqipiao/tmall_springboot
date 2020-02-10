package com.how2java.tmall.util;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分页工具类
 */
public class Page4Navigator<T> {
    //jpa 传递出来的分页对象， Page4Navigator 类就是对它进行封装以达到扩展的效果
    Page<T> pageFormJap;
    //分页的时候 ,如果总页数比较多，那么显示出来的分页超链一个有几个。 比如如果分页出来的超链是这样的： [8,9,10,11,12], 那么 navigatePages 就是5
    int navigatePages;
    //总页数
    int totalPages;
    //第几页
    int number;
    //总共多少条数据
    long totalElements;
    //一页最多几条数据
    int size;
    //当前页有多少数据
    int numberOfElements;
    //数据集合
    List<T> content;
    //是否有数据
    boolean isHasContenr;
    //是否是首页
    boolean first;
    //是否是最后一页
    boolean last;
    //是否有下一页
    boolean isHasNext;
    //是否有上一页
    boolean isHasPrevious;
    //分页的时候 ,如果总页数比较多，那么显示出来的分页超链一个有几个。 比如如果分页出来的超链是这样的： [8,9,10,11,12]，那么 navigatepageNums 就是这个数组：[8,9,10,11,12]，这样便于前端展示
    int[] navigatepageNums;

    public Page4Navigator(){
        //这个空的分页是为了 Redis 从 json格式转换为 Page4Navigator 对象而专门提供的
    }

    public Page4Navigator(Page<T> pageFormJap,int navigatePages){
        this.pageFormJap=pageFormJap;
        this.navigatePages=navigatePages;
        totalPages=pageFormJap.getTotalPages();
        number=pageFormJap.getNumber();
        totalElements=pageFormJap.getTotalElements();
        size=pageFormJap.getSize();
        numberOfElements=pageFormJap.getNumberOfElements();
        content=pageFormJap.getContent();
        isHasContenr=pageFormJap.hasContent();
        first=pageFormJap.isFirst();
        last=pageFormJap.isLast();
        isHasNext=pageFormJap.hasNext();
        isHasPrevious=pageFormJap.hasPrevious();
        calcNavigatepageNums();
    }

    private void calcNavigatepageNums() {
        int navigatepageNums[];
        int totalPages=getTotalPages();
        int num=getNumber();
        //当总页数小于或等于导航栏页码数时
        if (totalPages<=navigatePages){
            navigatepageNums=new int[totalPages];
            for (int i=0;i<totalPages;i++){
                navigatepageNums[i]=i+1;
            }
        }else {
            //当总页数大于导航栏页码数时
            navigatepageNums=new int[totalPages];
            int startNum=num-navigatePages/2;
            int endNum=num+navigatePages/2;
            if (startNum<1){
                startNum=1;
                //最前navigatePages页
                for (int i=0;i<navigatePages;i++){
                    navigatepageNums[i]=startNum++;
                }
            }else if (endNum>totalPages){
                endNum=totalPages;
                //最后navigatePages页
                for (int i=navigatePages-1;i>=0;i++){
                    navigatepageNums[i]=endNum--;
                }
            }else {
                //所有中间页
                for (int i=0;i<navigatePages;i++){
                    navigatepageNums[i]=startNum++;
                }
            }
        }
        this.navigatepageNums=navigatepageNums;
    }

    public Page<T> getPageFormJap() {
        return pageFormJap;
    }

    public void setPageFormJap(Page<T> pageFormJap) {
        this.pageFormJap = pageFormJap;
    }

    public int getNavigatePages() {
        return navigatePages;
    }

    public void setNavigatePages(int navigatePages) {
        this.navigatePages = navigatePages;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public boolean isHasContenr() {
        return isHasContenr;
    }

    public void setHasContenr(boolean hasContenr) {
        isHasContenr = hasContenr;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isHasNext() {
        return isHasNext;
    }

    public void setHasNext(boolean hasNext) {
        isHasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return isHasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        isHasPrevious = hasPrevious;
    }

    public int[] getNavigatepageNums() {
        return navigatepageNums;
    }

    public void setNavigatepageNums(int[] navigatepageNums) {
        this.navigatepageNums = navigatepageNums;
    }
}
