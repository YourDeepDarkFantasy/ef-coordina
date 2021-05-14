package com.demo.goods.controller;

import com.alibaba.fastjson.JSONObject;

import com.wiqer.coordina.tm.annotation.GlobalLock;
import com.wiqer.coordina.tm.annotation.GlobalTransaction;
import com.wiqer.coordina.tm.transactional.GlobalTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @desc
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${request-url}")
    private String requestUrl;

   // @PostMapping
    @GetMapping("/save")
    @GlobalTransaction
    @Transactional
    public void save() {
        jdbcTemplate.execute("insert into t_goods values(1, 'iphone')");
        HttpHeaders headers = new HttpHeaders();
        headers.set("xid", GlobalTransactionManager.getCurrentXid());
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(headers);
        restTemplate.postForObject(requestUrl+"/save", httpEntity, String.class);
        int i = 100 / 0;
    }
    @GetMapping("/save2")
    @GlobalTransaction
    @Transactional
    public void save2() {
        jdbcTemplate.execute("insert into t_goods values(3, 'wuhu')");
        HttpHeaders headers = new HttpHeaders();
        headers.set("xid", GlobalTransactionManager.getCurrentXid());
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(headers);
        restTemplate.postForObject(requestUrl+"/save2", httpEntity, String.class);
        int i = 100 / 0;
    }
    @GetMapping("/lock")
    @GlobalLock
    public String lock() {
        try {
            Thread.sleep(4000);
        }catch (Exception e){

        }
        return "ok";

    }
}
