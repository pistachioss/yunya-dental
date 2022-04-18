package com.yunya.modules.treatment.other.netty.chat;

import com.alibaba.fastjson.JSON;
import com.yunya.feign.treatment_other.domain.form.ChatMessageBody;
import com.yunya.framework.common.utils.UUIDUtils;
import com.yunya.modules.treatment.other.biz.ChatMessageRecordBiz;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
     * 建立连接以后第一个调用的方法
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("与客户端: {}建立连接", ctx.channel().remoteAddress().toString());
    }
    /**
     * 断开连接会触发该消息
     * 同时当前channel 也会自动从ChannelGroup中被移除
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("与客户端: {}断开连接", ctx.channel().remoteAddress().toString());
    }

    /**
     * 通道连接事件（channel连接就绪状态以后）
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx);
        log.info("有新的连接.>>当前连接数量: {}", channels.size());
        log.info("新建连接通道: {}", ctx.channel().id());
    }

    /**
     * 通道消息事件
     * @param ctx 通道上下文
     * @param wsMessage 消息
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame wsMessage) throws Exception {
//        处理与前端的心跳
        ChatMessageBody message = JSON.parseObject(wsMessage.text(), ChatMessageBody.class);
        Integer type = message.getType();
        if (type == 0) {
            log.info("收到客户端发来的心跳包, {}", message);
            return;
        }
        log.info("接收到客户端发来的消息: {}", wsMessage.text());
        if (ObjectUtils.isEmpty(message.getSendId())) {
            // 无意义数据
            return;
        }
        // 应答
        ackMessageRead(message);
        // 用户上线
        if (type == 1) {
            setMap(ctx, message);
            // 给其他服务器发送上线消息
//            for (ChannelHandlerContext handlerContext : userHandles.values()) {
//                if (handlerContext==ctx) {
//                    continue;
//                }
//                write2flush(handlerContext, message);
//            }
            // 查询未读消息列表
            Map<Integer, List<ChatMessageBody>> unReadHisotry = chatMessageRecordBiz.findChatMessageUnReadHisotry(message);
            write2flush(ctx, unReadHisotry);
            log.info("用户: {}上线了", message.getSendId());
            return;
        }
        // 获取到需要转发的客户端
        Integer receiveId = message.getReceiveId();
        // 没有指定接收者代表要群发
        if (ObjectUtils.isEmpty(receiveId)) {
//            userHandles.forEach((userId, handlerContext)->{
//                if (handlerContext==ctx) {
//                    return;
//                }
//                write2flush(handlerContext, message);
//            });
            ChatMessageBody retMessage = new ChatMessageBody(UUIDUtils.generateShortUuid(),
                    NETTY_SERVER_ID, message.getSendId(), "消息接收者不能为空。",2);
            write2flush(ctx, retMessage);
            log.error("消息接收人不能为空");
            return;
        }
        message.setMsgCode(UUIDUtils.generateShortUuid());
        chatMessageRecordBiz.asyncArchiveChatMessage(message, null);
        // 从缓存的存储用户对应的通道 map中获取
        if (!userHandles.containsKey(receiveId)) {
            // 回写消息
            ChatMessageBody retMessage = new ChatMessageBody(UUIDUtils.generateShortUuid(),
                    NETTY_SERVER_ID, message.getSendId(), "用户离线，消息不能及时送达。",2);
            write2flush(ctx, retMessage);
            log.info("接收人: {}离线，消息不能及时送达", receiveId);
            return;
        }
        // 服务端转发消息到指定的客户端
        ChannelHandlerContext receiveCtx = userHandles.get(receiveId);
        write2flush(receiveCtx, message);
        log.info("向接收人: {}发送消息", receiveId);
    }

    /**
     * 消息写回
     *
     * @param ctx 连接通道
     * @param message 消息
     */
    private void write2flush(ChannelHandlerContext ctx, Object message) {
        ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
    }

    /**
     * 应答消息已读
     *
     * @param message
     */
    private void ackMessageRead(ChatMessageBody message) {
        Boolean ack = message.getAckRead();
        if (!ObjectUtils.isEmpty(ack) && ack) {
            chatMessageRecordBiz.asyncUptMessageHadRead(message);
        }
    }

    /**
     * 用户上线，建立用户-通道映射
     *
     * @param channelHandlerContext
     * @param message
     */
    private void setMap(ChannelHandlerContext channelHandlerContext, ChatMessageBody message) {
        userHandles.put(message.getSendId(), channelHandlerContext);
        channelUsers.put(channelHandlerContext.channel().id().toString(),message.getSendId());
    }

    /**
     * 通道关闭事件（channel连接状态断开后触发）
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ChannelId id = ctx.channel().id();
        Integer userId = channelUsers.get(id.toString());
        String info = "!";
        if (!ObjectUtils.isEmpty(userId)) {
            userHandles.remove(userId);
            // 给其他在线用户发送该用户离线的信息
            for (ChannelHandlerContext handlerContext : userHandles.values()) {
                ChatMessageBody message = new ChatMessageBody(UUIDUtils.generateShortUuid(),
                        NETTY_SERVER_ID, null, "用户id: " + userId + "--已经离线了", 2);
                write2flush(handlerContext, message);
            }
            channels.remove(ctx);
            info += " 用户: " + userId + "下线!";
        }
        log.info("通道: {} 关闭" + info, id);
    }

    /**
     * 连接发生异常时触发
     *
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ChannelId id = ctx.channel().id();
        Integer userId = channelUsers.get(id.toString());
        String info = "!";
        if (!ObjectUtils.isEmpty(userId)) {
            userHandles.remove(userId);
            // 给其他在线用户发送该用户离线的信息
            for (ChannelHandlerContext handlerContext : userHandles.values()) {
                ChatMessageBody message = new ChatMessageBody(UUIDUtils.generateShortUuid(),
                        NETTY_SERVER_ID, null, "用户id: " + userId + "--连接发生问题，已被迫离线了", 2);
                write2flush(handlerContext, message);
            }
            channels.remove(ctx);
            info += " 用户: " + userId + "被迫下线!";
        }
        log.error("通道: {} 连接发生问题" + info, id);
        log.error("Netty Connection error: {}", cause);
    }
}