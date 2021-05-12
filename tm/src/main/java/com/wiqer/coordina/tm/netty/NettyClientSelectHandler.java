package com.wiqer.coordina.tm.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class NettyClientSelectHandler extends SimpleChannelInboundHandler<String> {
    public static Map<String, CountDownLatch> SYNC_MAP = new HashMap<>();

    private ChannelHandlerContext context;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("NettyClientSelectHandler客户端接收数据：" + msg);
        JSONObject jsonObj = JSON.parseObject((String)msg);
        String msgType = jsonObj.getString("type"); // create-开启全局事务，register-注册分支事务，commit-提交全局事务
        String command = jsonObj.getString("command"); //locked -》唤醒对应线程
        if(!"syn".equals(command)) {
            if (msgType.equals("TS")) {
                ctx.pipeline().addLast(new TransactionClientHandler(context));
                ctx.fireChannelRead(msg);
                //ctx.pipeline().
            } else if (msgType.equals("LK")) {
                ctx.pipeline().addLast(new LockClientHandler());
                ctx.fireChannelRead(msg);
            }
        }else {
            String msgId = jsonObj.getString("msgId");
            if(msgId!=null&&!msgId.equals("")){
                CountDownLatch sync=SYNC_MAP.remove(msgId);
                if(sync!=null){
                    sync.countDown();
                }
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        context = ctx;
    }
    public synchronized Object call(JSONObject data) {
        if(!context.isRemoved()){
            context.writeAndFlush(data.toJSONString()+"($.$)").channel().newPromise();
            System.out.println("NettyClientSelectHandler客户端发送数据：" + data.toJSONString());
        }
        return null;
    }
    public synchronized Object syncCall(JSONObject data,int secondTimes) throws InterruptedException, TimeoutException {
        if(!context.isRemoved()) {
            String msgId = UUID.randomUUID().toString().replace("-", "");
            data.put("msgId", msgId);
            System.out.println("NettyClientSelectHandler客户端同步发送数据：" + data.toJSONString());
            context.writeAndFlush(data.toJSONString()+"($.$)").channel().newPromise();
            CountDownLatch sync=new CountDownLatch(1);
            SYNC_MAP.put(msgId,sync);
            if(!sync.await(secondTimes, TimeUnit.SECONDS)) {
                throw new TimeoutException("syncSend等待超时");
            }
            System.out.println("NettyClientSelectHandler客户端同步发送数据：发送成功");
        }
        return null;
    }
}
