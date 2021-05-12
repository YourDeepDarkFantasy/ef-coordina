package com.wiqer.coordina.tc;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class NodesServerStarter {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${netty.port}")
    private int port;

    @PostConstruct
    public void start() {
        ExecutorService threadPoolExecutor = Executors.newCachedThreadPool();
        threadPoolExecutor.submit(() -> {
            //logger.info("netty server is starting");

            NettyServer nodesServer = new NettyServer();
            try {
                nodesServer.startNettyServer(port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
       
    }
}
