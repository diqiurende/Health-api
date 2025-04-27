package com.example.health.api;

import com.example.health.api.async.InitializeWorkAsync;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@SpringBootApplication
@EnableAsync
@MapperScan("com.example.health.api.db.dao")
@ComponentScan("com.example.*")
@ServletComponentScan
@EnableCaching
@EnableScheduling
public class HealthApiApplication {
    @Resource
    private InitializeWorkAsync initializeWorkAsync;

    public static void main(String[] args) {
        SpringApplication.run(HealthApiApplication.class, args);
    }

    @PostConstruct
    public void init() {
        initializeWorkAsync.init();
    }

}
