package com.wiqer.coordina.tm.transactional;

import com.alibaba.fastjson.JSONObject;

import com.wiqer.coordina.tm.netty.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author laowu
 * @date 2020/9/22 13:48
 * @desc 事务管理者
 */
@Component
public class GlobalTransactionManager {

    private static NettyClient nettyClient;

    private static ThreadLocal<EFTransaction> current = new ThreadLocal<>();
    private static ThreadLocal<String> currentXid = new ThreadLocal<>();
    private static Logger log = LoggerFactory.getLogger(GlobalTransactionManager.class);

    @Autowired
    public void setNettyClient(NettyClient nettyClient) {
        GlobalTransactionManager.nettyClient = nettyClient;
    }

    public static Map<String, EFTransaction> EF_TRANSACTION_MAP = new HashMap<>();

    /**
     * 创建事务组，并且返回xid
     * @return
     */
    public static String getOrCreateGroup() {
        if(currentXid.get() != null) {
            return currentXid.get();
        } else {
            String xid = UUID.randomUUID().toString();
            currentXid.set(xid);
            log.info("创建事务组 xid = {}", xid);
            return xid;
        }
    }

    /**
     * 创建分支事务
     * @param xid
     * @return
     */
    public static EFTransaction createEFTransaction(String xid) {
        //分支id
        String transaction = UUID.randomUUID().toString().replace("-", "");
        EFTransaction EFTransaction = new EFTransaction(xid, transaction);
        EF_TRANSACTION_MAP.put(xid, EFTransaction);
        current.set(EFTransaction);
        log.info("创建分支事务");
        JSONObject obj = new JSONObject();
        obj.put("type","TS");
        obj.put("xid", xid);
        obj.put("command", "create");
        obj.put("transactionId", transaction);
        nettyClient.send(obj);
        return EFTransaction;
    }

    /**
     * 注册分布式事务
     * @param EFTransaction
     * @return
     */
    public static EFTransaction addEFTransaction(EFTransaction EFTransaction) {
        JSONObject obj = new JSONObject();
        obj.put("type","TS");
        obj.put("command", "register");
        obj.put("xid", EFTransaction.getXid());
        obj.put("transactionId", EFTransaction.getTransactionId());
        obj.put("transactionType", EFTransaction.getTransactionType());
        nettyClient.send(obj);
        log.info("添加事务");
        return EFTransaction;
    }

    public static EFTransaction getEFTransaction(String grouId) {
        return EF_TRANSACTION_MAP.get(grouId);
    }

    public static EFTransaction getCurrent() {
        return current.get();
    }

    public static String getCurrentXid() {
        return currentXid.get();
    }

    public static void setCurrentXid(String xid) {
        currentXid.set(xid);
    }
}
