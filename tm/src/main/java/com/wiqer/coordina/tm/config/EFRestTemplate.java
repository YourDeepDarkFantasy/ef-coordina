package com.wiqer.coordina.tm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author laowu
 * @date 2020/8/28 17:00
 * @desc
 */
@Configuration
public class EFRestTemplate {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
