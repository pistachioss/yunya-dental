package com.gds.restapi.sysMng.msg.websocket;

/**
 * @author zhangfq
 * @version 1.0
 * Description
 * <p> 中航工业光电所
 * @Date 2021-04-13 15:00
 */

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.treatment_other.domain.form.ChatMessageBody;

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
//        String uri ="ws://192.168.31.234:8081/websocket/chat";
//        String uri ="wss://test.ivy2.yunya365.com/api/treatment-netty/websocket/chat";
        String uri ="ws://test.ivy2.yunya365.com/api/treatment-netty/websocket/chat";
//        String uri ="ws://114.215.203.148:8765/api/treatment-netty/websocket/chat";
//        String uri ="ws://ivy2.yunya365.com/api/treatment-netty/websocket/chat";
//        String uri ="ws://192.168.31.234:8765/api/treatment-netty/websocket/chat";
//        String uri ="ws://192.168.31.95/api/treatment-netty/websocket/chat";
        System.out.println("Connecting to "+ uri);

        Session session = connection.connectToServer(MyWebSocketClient.class, URI.create(uri));
        while (!session.isOpen()) {
            System.out.println("连接中。。。");
            Thread.sleep(3000);
        }
        ChatMessageBody message = new ChatMessageBody();
        message.setSendId(123);
        message.setReceiveId(234);
        message.setType(2);
        message.setContent("Hello Netty");
        RemoteEndpoint.Basic remote = session.getBasicRemote();
        remote.sendText(JSONObject.toJSONString(message));
        Thread.sleep(3000);
        System.out.println(session.isOpen());
        remote = session.getBasicRemote();
        remote.sendText(JSONObject.toJSONString(message));
//        session.close();
    }
}