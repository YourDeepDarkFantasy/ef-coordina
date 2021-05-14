package com.wiqer.coordina.tm.netty;

import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @desc NettyClient 通讯类
 */
@Component
public class NettyClient implements InitializingBean {

    public NettyClientSelectHandler client = null;

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    private Logger log = LoggerFactory.getLogger(NettyClient.class);
    @Value("${netty.host}")
    private String hostName;
    @Value("${netty.port}")
    private Integer port;
    public void afterPropertiesSet() throws Exception {
        start(hostName, port);
    }

    public void start(String hostName, Integer port) {

        client = new  NettyClientSelectHandler();
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer() {
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new DelimiterBasedFrameDecoder(2048,  Unpooled.copiedBuffer("($.$)".getBytes())));
                        pipeline.addLast("decoder", new StringDecoder());
                        pipeline.addLast("encoder", new StringEncoder());

                        pipeline.addLast("handler", client);

                    }
                });
        try {
            bootstrap.connect(hostName, port).sync();
        } catch (InterruptedException e) {
            log.error("连接异常", e);
        }

    }

    public void send(JSONObject data) {
        try{
            client.call(data);

        } catch (Exception e) {
            log.error("数据发送异常", e);
        }
    }
    public void syncSend(JSONObject data,int secondTimes) {
        try{
            client.syncCall(data, secondTimes);
        } catch (Exception e) {
            log.error("数据发送异常", e);
        }
    }
}
