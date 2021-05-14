package com.wiqer.coordina.tm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.client.*;

import java.io.IOException;
import java.net.URI;

@Configuration
public class EFRestTemplate extends RestTemplate{

//    @Nullable
//    @Override
//    protected <T> T doExecute(URI url, @Nullable HttpMethod method, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {
//       if(requestCallback instanceof RestTemplate){
//
//       }
//
//        return super.doExecute(url,method,requestCallback,responseExtractor);
//    }
    @Bean
    public RestTemplate restTemplate() {
        //RequestCallback requestCallback=
        return new RestTemplate();
    }
}
