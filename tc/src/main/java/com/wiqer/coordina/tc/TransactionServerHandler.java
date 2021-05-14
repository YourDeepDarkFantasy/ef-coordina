package com.wiqer.coordina.tc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @desc Netty服务逻辑处理
 */
public class TransactionServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 存储每个客户端接入进来时的 channel 对象
     * 主要用于使用 writeAndFlush 方法广播信息
     */


    private static Map<String, Set<String>> transactionIdMap = new HashMap<>();

    private static Map<String, Integer> countTransactionId = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        System.out.println("TransactionServerHandler服务接收数据：" + msg.toString());
        JSONObject jsonObj = JSON.parseObject((String)msg);

        String command = jsonObj.getString("command"); // create-开启全局事务，register-注册分支事务，commit-提交全局事务
        String xid = jsonObj.getString("xid"); // 事务组ID
        String transactionType = jsonObj.getString("transactionType"); // 分支事务类型，commit-待提交，rollback-待回滚，
        String transactionId = jsonObj.getString("transactionId"); // 分支事务ID

        if("create".equals(command)) {
            // 启动全局事务
            if(transactionIdMap.get(xid) == null || transactionIdMap.get(xid).size() == 0){
                transactionIdMap.put(xid, new HashSet<String>(){{add(transactionId);}});
            }
            else{
                transactionIdMap.get(xid).add(transactionId);
            }
        } else if ("register".equals(command)) {
            // 注册分支事务
//            transactionIdMap.get(xid).add(transactionId);

            // 注册的过程中发现有事务需要回滚
            if("rollback".equals(transactionType)) {
                System.out.println("接收到了一个回滚状态");
                countTransactionId.remove(xid);
                transactionIdMap.remove(xid);
                sentMsg(xid, "rollback");
            }
            else if ("commit".equals(transactionType)) {
                // 接收到TM发的提交全局事务
                System.out.println("收到提交分组事务");
                int count = countTransactionId.get(xid) == null ? 1 : countTransactionId.get(xid);
                if( count == transactionIdMap.get(xid).size()) {
                    countTransactionId.remove(xid);
                    transactionIdMap.remove(xid);
                    System.out.println("提交全局事务");
                    sentMsg(xid, "commit");
                } else {
                    countTransactionId.put(xid, count+1);
                }
            }
        }

    }


    private void sentMsg(String xid, String command) {
        JSONObject result = new JSONObject();
        result.put("type", "TS");
        result.put("xid", xid);
        result.put("command", command);
        sendResult(result);
    }

    private void sendResult(JSONObject result) {
        for (Channel channel : NettyServer.channelGroup) {
            System.out.println("发送数据：" + result.toJSONString());
            channel.writeAndFlush(result.toJSONString()+"($.$)");
        }
    }
}
