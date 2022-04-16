package com.yunya.modules.treatment.other.controller;

import com.alibaba.fastjson.JSONObject;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 *  websocket客户端监听类
 * @author 。
 */
public class MyWebSocketClient extends WebSocketClient {

    private static Logger logger = LoggerFactory.getLogger(MyWebSocketClient.class);

    public MyWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.info(">>>>>>>>>>>websocket open");
    }

    @Override
    public void onMessage(String s) {
        logger.info(">>>>>>>>>> websocket message");
       
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        logger.info(">>>>>>>>>>>websocket close");
    }

    @Override
    public void onError(Exception e) {
        logger.error(">>>>>>>>>websocket error {}",e);
    }


    public static void main(String[] args) {
        try {
            MyWebSocketClient myClient = new MyWebSocketClient(new URI("ws://192.168.31.234:8765/api/treatment-netty/websocket/chat"));
            myClient.connect();
            while (!myClient.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
                System.out.println("连接中。。。");
                Thread.sleep(1000);
            }
            // 连接成功往websocket服务端发送数据
            JSONObject object = new JSONObject();
            object.put("message", "success连接");
            myClient.send(object.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}