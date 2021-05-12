package com.wiqer.coordina.tm.transactional;

import com.alibaba.fastjson.JSONObject;
import com.wiqer.coordina.tm.netty.NettyClient;
import com.wiqer.coordina.tm.util.EFLRUCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Component
public class GlobalLockManager {

    private static NettyClient nettyClient;

    private static Logger log = LoggerFactory.getLogger(GlobalTransactionManager.class);

    @Autowired
    public void setNettyClient(NettyClient nettyClient) {

        GlobalLockManager.nettyClient = nettyClient;
    }

    public static Map<String, CountDownLatch> EF_LOCK_MAP = new HashMap<>();
    public static EFLRUCache<String,String> NO_CATCH_CACHE= new EFLRUCache(256);



    public static CountDownLatch Lock(String lockKey,String lockId) throws InterruptedException {

        CountDownLatch aqs= new CountDownLatch(1);

        log.info("提交锁");
        JSONObject obj = new JSONObject();
        obj.put("type","LK");
        obj.put("lockId", lockId);
        obj.put("lockKey", lockKey);
        obj.put("command", "lock");
        //同步发送消息
        nettyClient.syncSend(obj,100);
        //去LRU中判断是否有缓存,没有再锁
        if(!GlobalLockManager.NO_CATCH_CACHE.containsKey(lockId)) {
            EF_LOCK_MAP.put(lockId, aqs);
            aqs.await();
        }
        //还需要一个定时器，每段时间扫描一次EF_LOCK_MAP和LRU
        return aqs;
    }


    public static void Unlock(String lockKey,String lockId ) {
        JSONObject obj = new JSONObject();
        obj.put("type","LK");
        obj.put("command", "unlock");
        obj.put("lockId", lockId);
        obj.put("lockKey", lockKey);
        nettyClient.syncSend(obj,100);
        log.info("释放锁");
   }

    public static CountDownLatch  getAQS(String grouId) {
        return EF_LOCK_MAP.get(grouId);
    }

}
