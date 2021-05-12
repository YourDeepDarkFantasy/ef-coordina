package com.wiqer.coordina.tc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class NettySelectHandler extends SimpleChannelInboundHandler<String> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("NettySelectHandler服务接收数据：" + msg);
        JSONObject jsonObj = JSON.parseObject(msg);
        String msgType = jsonObj.getString("type"); // create-开启全局事务，register-注册分支事务，commit-提交全局事务
        if(msgType.equals("TS")){
            ctx.pipeline().addLast(new TransactionServerHandler());
            ctx.fireChannelRead(msg);
            //ctx.pipeline().
        }else if (msgType.equals("LK")){
            ctx.pipeline().addLast(new LockServerHandler());
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户连接 = "+ctx.channel().remoteAddress());
        NettyServer.channelGroup.add(ctx.channel());
        super.channelActive(ctx);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("some thing is error , " + cause.getMessage());
    }


}
