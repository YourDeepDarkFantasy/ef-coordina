package com.wiqer.coordina.tm.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.wiqer.coordina.tm.menum.TransactionType;
import com.wiqer.coordina.tm.transactional.EFTransaction;
import com.wiqer.coordina.tm.transactional.GlobalTransactionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**

 * @desc
 */
public class TransactionClientHandler extends SimpleChannelInboundHandler<String> {

    private ChannelHandlerContext context;
    private Logger log = LoggerFactory.getLogger(TransactionClientHandler.class);
    public TransactionClientHandler(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
    }
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        context = ctx;
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        log.info("接收数据 = {}", msg);
        JSONObject obj = JSON.parseObject((String)msg);
        String xid = obj.getString("xid");
        String command = obj.getString("command"); //commit, rollback
        log.info("接收command = {}", command);
        // 对事务进行操作
        EFTransaction EFTransaction = GlobalTransactionManager.getEFTransaction(xid);
        if(EFTransaction == null) {
            log.info("未启动分布式事务");
            return;
        }
        if("commit".equals(command)) {
            EFTransaction.setTransactionType(TransactionType.commit);
        } else {
            EFTransaction.setTransactionType(TransactionType.rollback);
        }
        try {
            EFTransaction.getTask().getLock().lock();
            EFTransaction.getTask().getCondition().signal();
        }finally {
            EFTransaction.getTask().getLock().unlock();
        }
    }


    static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 1L;

        //future status
        //LOCK阻塞
        private final int done = 1;
        //UNLOCK释放
        private final int pending = 0;

        public boolean tryAcquireNanosTimeOut(long timeout, TimeUnit unit) throws InterruptedException {
           return this.tryAcquireNanos(1, unit.toNanos(timeout));
        }

        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == done;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == pending) {
                if (compareAndSetState(pending, done)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

        protected boolean isDone() {
            return getState() == done;
        }
    }

}
