package com.how2java.tmall;

import com.how2java.tmall.util.PortUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching
@EnableElasticsearchRepositories(basePackages = "com.how2java.tmall.es")
@EnableJpaRepositories(basePackages = {"com.how2java.tmall.dao", "com.how2java.tmall.pojo"})
public class Application {
    //检查Redis、ElasticSearch、Kibana是否启动
    static {
        PortUtil.chekPort(6379,"Redis 服务端",true);
        PortUtil.chekPort(9300,"ElasticSearch 服务端",true);
        PortUtil.chekPort(5601,"Kibana 工具", true);
    }
    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }
}
