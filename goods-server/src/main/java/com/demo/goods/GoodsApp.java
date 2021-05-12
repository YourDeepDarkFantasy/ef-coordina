package com.demo.goods;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author laowu
 * @date 2020/9/22 16:26
 * @desc
 */
@SpringBootApplication(scanBasePackages = {"com.demo.goods", "com.wiqer.coordina.tm"})
public class GoodsApp {
    public static void main(String[] args) {
        SpringApplication.run(GoodsApp.class, args);
    }
}
