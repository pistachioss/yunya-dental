package com.yunya.modules.treatment.other.netty.chat;

import com.alibaba.fastjson.JSON;
import com.yunya.feign.treatment_other.domain.form.ChatMessageBody;
import com.yunya.framework.common.utils.UUIDUtils;
import com.yunya.modules.treatment.other.biz.ChatMessageRecordBiz;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 服务端自定义处理入站消息
 * @author Administrator
 */
@ChannelHandler.Sharable
@Component
public class WebSocketNettyHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Autowired
    private ChatMessageRecordBiz chatMessageRecordBiz;

    /** 服务端id*/
    private static final Integer NETTY_SERVER_ID = -999;

    /**
     * 存储用户对应的通道
     */
    Map<Integer, ChannelHandlerContext> userHandles = new ConcurrentHashMap<>(16);

    /**
     * 存放通道和用户关联
     */
    Map<String, Integer> channelUsers = new ConcurrentHashMap<>(16);

    /**
     * 存储当前连接上的通道
     */
    List<ChannelHandlerContext> channels = new CopyOnWriteArrayList<>();

    /**
     * 通道连接事件
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx);
        System.out.println("有新的连接.>>当前连接数量: " + channels.size());
    }

    /**
     * 通道消息事件
     * @param ctx 通道上下文
     * @param wsMessage 消息
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame wsMessage) throws Exception {
        System.out.println("接收到客户端发来的消息: " + wsMessage.text());
        ChatMessageBody message = JSON.parseObject(wsMessage.text(), ChatMessageBody.class);
        ackMessageRead(message);
        if (message.getType()==1) {
            setMap(ctx, message);
            // 给其他服务器发送上线消息
            for (ChannelHandlerContext handlerContext : userHandles.values()) {
                if (handlerContext==ctx) {
                    continue;
                }
                handlerContext.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
            }
            return;
        }
        // 获取到需要转发的客户端
        Integer receiveId = message.getReceiveId();
        // 没有指定接收者代表要群发
        if (ObjectUtils.isEmpty(receiveId)) {
            userHandles.forEach((userId, handlerContext)->{
                if (handlerContext==ctx) {
                    return;
                }
                handlerContext.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
                chatMessageRecordBiz.asyncArchiveChatMessage(message, userId);
            });
            return;
        }
        chatMessageRecordBiz.asyncArchiveChatMessage(message, null);
        // 从缓存的存储用户对应的通道 map中获取
        if (!userHandles.containsKey(receiveId)) {
            // 回写消息
            ChatMessageBody retMessage = new ChatMessageBody(UUIDUtils.generateShortUuid(),
                    NETTY_SERVER_ID, message.getSendId(), "用户离线，消息不能及时送达。",2);
            ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(retMessage)));
            return;
        }
        // 服务端转发消息到指定的客户端
        ChannelHandlerContext receiveCtx = userHandles.get(receiveId);
        receiveCtx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
    }

    private void ackMessageRead(ChatMessageBody message) {
        Boolean ack = message.getAckRead();
        if (!ObjectUtils.isEmpty(ack) && ack) {
            chatMessageRecordBiz.asyncUptMessageHadRead(message);
        }
    }

    /**
     * 设置连接映射
     * @param channelHandlerContext
     * @param message
     */
    private void setMap(ChannelHandlerContext channelHandlerContext, ChatMessageBody message) {
        userHandles.put(message.getSendId(), channelHandlerContext);
        channelUsers.put(channelHandlerContext.channel().id().toString(),message.getSendId());
    }

    /**
     * 通道关闭事件
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Integer userId = channelUsers.get(ctx.channel().id().toString());
        userHandles.remove(userId);
        // 给其他在线用户发送该用户离线的信息
        for (ChannelHandlerContext handlerContext : userHandles.values()) {
            ChatMessageBody message = new ChatMessageBody(UUIDUtils.generateShortUuid(),
                    NETTY_SERVER_ID, null, "用户id: "+userId+"--已经离线了",2);
            handlerContext.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
        }
        channels.remove(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Integer userId = channelUsers.get(ctx.channel().id().toString());
        userHandles.remove(userId);
        // 给其他在线用户发送该用户离线的信息
        for (ChannelHandlerContext handlerContext : userHandles.values()) {
            ChatMessageBody message = new ChatMessageBody(UUIDUtils.generateShortUuid(),
                    NETTY_SERVER_ID, null, "用户id: "+userId+"--连接发生问题，已被迫离线了",2);
            handlerContext.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
        }
        channels.remove(ctx);
    }
}