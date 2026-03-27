package com.graduation.inventory;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 库存管理系统启动类
 * 
 * @author graduation
 * @version 1.0.0
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@MapperScan("com.graduation.inventory.*.mapper")
public class InventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
        System.out.println("==========================================================");
        System.out.println("   库存管理系统启动成功！");
        System.out.println("   API文档地址: http://localhost:8080/api/doc.html");
        System.out.println("   Druid监控地址: http://localhost:8080/api/druid/index.html");
        System.out.println("==========================================================");
    }

}
