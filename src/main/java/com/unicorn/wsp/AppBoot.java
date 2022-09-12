package com.unicorn.wsp;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@MapperScan("com.unicorn.wsp.mapper")
@SpringBootApplication
@EnableTransactionManagement // 启动事务管理
public class AppBoot {
    public static void main(String[] args) {
        SpringApplication.run(AppBoot.class, args);

    }
}
