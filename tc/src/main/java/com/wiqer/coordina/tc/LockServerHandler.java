package com.wiqer.coordina.tc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class LockServerHandler extends SimpleChannelInboundHandler<String> {



    private static Map<String, Queue<String>> LockMathNameMap = new HashMap<>();

    private static Map<String, ChannelHandlerContext> lockIdCTXMap = new HashMap<>();


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {

        System.out.println("LockServerHandler->服务接收数据：" + msg.toString());
        JSONObject jsonObj = JSON.parseObject((String) msg);

        String command = jsonObj.getString("command"); //服务端-》 lock-申请锁，unlock-释放锁，客户端-》win-获得锁，syn-同步消息
        String lockKey = jsonObj.getString("lockKey"); // 方法的全名
        String lockId = jsonObj.getString("lockId"); // 方法的分支id
        String msgId = jsonObj.getString("msgId"); // 消息id

        if ("lock".equals(command)) {

            LinkedBlockingQueue linkedBlockingQueue = null;
            if (LockMathNameMap.get(lockKey) == null) {
                synchronized (LockMathNameMap) {

                    //队列不存在添加队列
                    if ((linkedBlockingQueue = (LinkedBlockingQueue) LockMathNameMap.get(lockKey)) == null) {
                        linkedBlockingQueue =new LinkedBlockingQueue<String>() {{
                            add(lockId);
                        }};
                        LockMathNameMap.put(lockKey,linkedBlockingQueue );
                    } else {
                        linkedBlockingQueue.add(lockId);
                    }
                }
            } else {
                linkedBlockingQueue= (LinkedBlockingQueue) LockMathNameMap.get(lockKey);
                linkedBlockingQueue.add(lockId);
            }
            lockIdCTXMap.put(lockId, ctx);
            //刚好一个就直接解锁了
            if (linkedBlockingQueue.size() <= 1) {
                sentMsg(ctx, lockId, "win", msgId);
            }
        } else if ("unlock".equals(command)) {
            lockIdCTXMap.remove(lockId);
            LinkedBlockingQueue lockIdQueue = (LinkedBlockingQueue) LockMathNameMap.get(lockKey);
            if (lockIdQueue != null && lockIdQueue.size() > 0) {
                lockIdQueue.remove(lockId);
                if (lockIdQueue.size() > 0) {
                    String nextId = LockMathNameMap.get(lockKey).peek();
                    //重复释放由客户端处理
                    sentMsg(lockIdCTXMap.get(nextId), nextId, "win", msgId);
                }
            }
        }
        sentMsg(ctx, lockId, "syn", msgId);
    }

    private void sentMsg(ChannelHandlerContext ctx,String lockId, String command,String msgId) {
        if(ctx!=null&&!ctx.isRemoved()){
            JSONObject result = new JSONObject();
            result.put("type", "LK");
            result.put("lockId", lockId);
            result.put("command", command);
            result.put("msgId", msgId);
            ctx.channel().writeAndFlush(result.toJSONString()+"($.$)");
            System.out.println("LockServerHandler->发送数据：" + result.toJSONString());
        }
    }

//    private void sendResult(ChannelHandlerContext ctx,JSONObject result) {
//
//
//
//    }

}
