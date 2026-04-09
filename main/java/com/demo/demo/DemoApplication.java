package com.demo.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目启动入口。
 * 这里只保留 Spring Boot 启动职责，控制器逻辑会拆分到 MVC 分层中。
 */
@SpringBootApplication
public class DemoApplication {

    /**
     * 应用主方法。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
