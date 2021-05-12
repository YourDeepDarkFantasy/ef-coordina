package com.wiqer.coordina.tm.aspect;


import com.wiqer.coordina.tm.connection.EFConnection;
import com.wiqer.coordina.tm.transactional.GlobalTransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * @author laowu
 * @date 2020/9/22 11:56
 * @desc 定义DataSource切面
 */
@Aspect
@Component
public class EFDataSourceAspect {

    /**
     * 切的是一个接口，所以所有的实现类都会被切到
     * spring肯定会调用这个方法来生成一个本地事务
     * 所以 point.proceed() 返回的也是一个Connection
     * @param point
     * @return
     */
    @Around("execution(* javax.sql.DataSource.getConnection(..))")
    public Connection around(ProceedingJoinPoint point) throws Throwable {
        //Spring本身实现类
        Connection connection =  (Connection)point.proceed();
        if(GlobalTransactionManager.getCurrent() != null)
            return new EFConnection(connection, GlobalTransactionManager.getCurrent());
        else
            return connection;
    }
}
