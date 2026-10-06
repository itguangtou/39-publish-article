package com.itguangtou;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.itguangtou.mapper")
public class PublishArticleServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PublishArticleServerApplication.class, args);
    }

}
