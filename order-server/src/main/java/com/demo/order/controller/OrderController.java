package com.demo.order.controller;


import com.wiqer.coordina.tm.annotation.GlobalTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author laowu
 * @date 2020/9/22 16:51
 * @desc
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/save")
    @GlobalTransaction
    @Transactional
    public String save() {
        jdbcTemplate.execute("insert into t_order values(1, 'iphone order')");
        return "OK";
    }

    @PostMapping("/save2")
    @GlobalTransaction
    @Transactional
    public String save2() {
        jdbcTemplate.execute("insert into t_order values(1, 'iphone order')");
        return "OK";
    }
}
