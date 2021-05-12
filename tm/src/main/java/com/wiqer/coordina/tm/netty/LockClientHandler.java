package com.wiqer.coordina.tm.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wiqer.coordina.tm.transactional.GlobalLockManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class LockClientHandler extends SimpleChannelInboundHandler<String> {
    private Logger log = LoggerFactory.getLogger(TransactionClientHandler.class);


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        log.info("LockClientHandler接收数据 = {}", msg);
        JSONObject obj = JSON.parseObject((String)msg);
        String lockId = obj.getString("lockId");
        String command = obj.getString("command"); //locked -》唤醒对应线程
        log.info("接收command = {}", command);
        // 获取对应线程的同步锁
        if("win".equals(command)) {
            CountDownLatch downLatch = GlobalLockManager.getAQS(lockId);
            if (downLatch == null) {
                log.info("未找到同步锁，id：" + lockId+",暂存到LRU中");
                //这里需要用LRU缓存一下key，否则高并发容易出BUG
                GlobalLockManager.NO_CATCH_CACHE.put(lockId,"");
                return;
            }
            //解锁
            downLatch.countDown();
        }
    }
}
