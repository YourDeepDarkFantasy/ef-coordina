package com.wiqer.coordina.tm.aspect;

import com.wiqer.coordina.tm.connection.EFConnection;
import com.wiqer.coordina.tm.menum.TransactionType;
import com.wiqer.coordina.tm.transactional.EFTransaction;
import com.wiqer.coordina.tm.transactional.GlobalTransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * @desc 自定义切面
 */
@Aspect
@Component
public class EFRestTemplateAspect<T> {
    /**
     *无效的修饰，没有交给ioc处理成代理对象
     * @param point
     * @return
     */
    @Around("execution(* org.springframework.web.client.RestTemplate.execute(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object[] objs=point.getArgs();
        //Spring本身实现类
       return  point.proceed();

    }
}
