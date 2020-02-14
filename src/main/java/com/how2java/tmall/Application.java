package com.how2java.tmall;

import com.how2java.tmall.util.PortUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Application {
    //检查Redis是否启动
    static {
        PortUtil.chekPort(6379,"Redis 服务端",true);
    }
    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }
}
