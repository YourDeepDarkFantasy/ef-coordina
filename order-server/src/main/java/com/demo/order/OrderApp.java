package com.demo.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author laowu
 * @date 2020/9/22 16:50
 * @desc
 */
@SpringBootApplication(scanBasePackages = {"com.demo.order", "com.wiqer.coordina.tm"})
public class OrderApp {

    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }
}
