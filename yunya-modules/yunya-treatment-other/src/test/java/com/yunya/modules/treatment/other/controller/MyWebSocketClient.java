package com.gds.restapi.sysMng.msg.websocket;

/**
 * @author zhangfq
 * @version 1.0
 * Description
 * <p> 中航工业光电所
 * @Date 2021-04-13 15:00
 */

import com.alibaba.fastjson.JSONObject;

import javax.websocket.*;
import java.net.URI;

/**
 * Created by Administrator on 2016/4/17.
 */
@ClientEndpoint
public class MyWebSocketClient {
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("连接成功 ... ");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("收到服务端的消息: " + message);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("连接关闭 ... ");
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    public static void main(String[] args)throws Exception{
        WebSocketContainer connection = ContainerProvider.getWebSocketContainer();
        String uri ="ws://192.168.31.234:8765/api/treatment-netty/websocket/chat";
        System.out.println("Connecting to "+ uri);

        Session session = connection.connectToServer(MyWebSocketClient.class, URI.create(uri));
        while (!session.isOpen()) {
            System.out.println("连接中。。。");
            Thread.sleep(3000);
        }
        JSONObject object = new JSONObject();
        object.put("message", "success连接");
        session.getBasicRemote().sendText(object.toJSONString());
        Thread.sleep(1000);
        session.close();
    }
}