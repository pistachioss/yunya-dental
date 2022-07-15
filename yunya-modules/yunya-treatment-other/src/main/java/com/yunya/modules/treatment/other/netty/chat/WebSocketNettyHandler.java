package com.yunya.modules.treatment.other.netty.chat;

import com.alibaba.fastjson.JSON;
import com.yunya.feign.treatment_other.domain.form.ChatMessageBody;
import com.yunya.framework.common.utils.StringHelper;
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

import java.util.*;
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
     * 存储用户对应的通道（支持同一用户有多个通道）
     */
    Map<Integer, List<ChannelHandlerContext>> userHandles = new ConcurrentHashMap<>(16);

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
        log.info("接收到客户端发来的消息: {}", wsMessage.text());
//        处理与前端的心跳
        ChatMessageBody message = JSON.parseObject(wsMessage.text(), ChatMessageBody.class);
        if (ObjectUtils.isEmpty(message.getSendId())) {
            // 无意义数据
            return;
        }
        Integer type = message.getType();
        if (type == 0) {
            log.info("收到客户端发来的心跳包, {}", message);
            online(ctx, message);
            return;
        }

        // 应答消息
        Boolean ack = message.getAckRead();
        if (!ObjectUtils.isEmpty(ack) && ack) {
            chatMessageRecordBiz.asyncUptMessageHadRead(message);
            return;
        }

        // 查询用户未读消息
        if (type == 1) {
            // 查询未读消息列表
            List<ChatMessageBody> unReadHisotry = chatMessageRecordBiz.findChatMessageUnReadHisotry(message);
            write2flush(ctx, unReadHisotry);
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
            write2flush(Collections.singleton(ctx), retMessage);
            log.error("消息接收人不能为空");
            return;
        }
        message.setSendTime(new Date(System.currentTimeMillis()));
        message.setMsgCode(UUIDUtils.generateShortUuid());
        asyncArchiveMessage(message);
        // 从缓存的存储用户对应的通道 map中获取
        if (!userHandles.containsKey(receiveId)) {
            // 回写消息
            ChatMessageBody retMessage = new ChatMessageBody(UUIDUtils.generateShortUuid(),
                    NETTY_SERVER_ID, message.getSendId(), "用户离线，消息不能及时送达。",2);
            write2flush(Collections.singleton(ctx), retMessage);
            log.info("接收人: {}离线，消息不能及时送达", receiveId);
            return;
        }
        // 服务端转发消息到指定的
        forwardMessage(receiveId, message);
    }

    /**
     * 转发消息给指定用户通道
     *
     * @param receiveId 接收者用户
     * @param message 消息
     */
    private void forwardMessage(Integer receiveId, ChatMessageBody message) {
        List<ChannelHandlerContext> receiveCtxs = userHandles.get(receiveId);
        if (StringHelper.isNotEmpty(receiveCtxs)) {
            write2flush(receiveCtxs, message);
        }
    }

    /**
     * 记录消息
     *
     * @param message
     */
    private void asyncArchiveMessage(ChatMessageBody message) {
        // 只有发消息需要记录，通知属于实时推送无需记录
        if (message.getType() == 2) {
            chatMessageRecordBiz.asyncArchiveChatMessage(message, null);
        }
    }

    /**
     * 消息写回
     *
     * @param ctxs 连接通道列表
     * @param message 消息
     */
    private void write2flush(Collection<ChannelHandlerContext> ctxs, ChatMessageBody message) {
        chatMessageRecordBiz.putChatEmployeeName(message);
        ctxs.forEach(ctx->{
            write2flush(ctx, message);
            log.info("向接收人: {}, 发送消息: {}", message.getReceiveId(), message);
        });
    }

    private void write2flush(ChannelHandlerContext ctx, Object message) {
        ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
    }

    /**
     * 用户上线，建立用户-通道映射
     *
     * @param ctx
     * @param message
     */
    private void online(ChannelHandlerContext ctx, ChatMessageBody message) {
        Integer sendId = message.getSendId();
        if (!ObjectUtils.isEmpty(sendId)) {
            List<ChannelHandlerContext> ctxs = userHandles.get(sendId);
            if (ctxs == null) {
                ctxs = Arrays.asList(ctx);
                userHandles.put(sendId, ctxs);
            } else {
                ctxs.add(ctx);
            }
            channelUsers.put(ctx.channel().id().toString(), sendId);
            log.info("用户: {}, 在通道: {}, 上线了", message.getSendId(), ctx.channel().id());
            // 给其他服务器发送上线消息
//            for (ChannelHandlerContext handlerContext : userHandles.values()) {
//                if (handlerContext==ctx) {
//                    continue;
//                }
//                write2flush(handlerContext, message);
//            }
        }
    }

    /**
     * 通道关闭事件（channel连接状态断开后触发）
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().toString();
        Integer userId = channelUsers.remove(channelId);
        String info = "!";
        if (!ObjectUtils.isEmpty(userId)) {
            userHandles.remove(userId);
            // 给其他在线用户发送该用户离线的信息
            for (List<ChannelHandlerContext> otherCtxs : userHandles.values()) {
                ChatMessageBody message = new ChatMessageBody(UUIDUtils.generateShortUuid(),
                        NETTY_SERVER_ID, null, "用户id: " + userId + "--已经离线了", 2);
                write2flush(otherCtxs, message);
            }
            channels.remove(ctx);
            info += " 用户: " + userId + "下线!";
        }
        log.info("通道: {} 关闭" + info, channelId);
    }

    /**
     * 连接发生异常时触发
     *
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String channelId = ctx.channel().id().toString();
        Integer userId = channelUsers.remove(channelId);
        String info = "!";
        if (!ObjectUtils.isEmpty(userId)) {
            userHandles.remove(userId);
            // 给其他在线用户发送该用户离线的信息
            for (List<ChannelHandlerContext> ctxs : userHandles.values()) {
                ChatMessageBody message = new ChatMessageBody(UUIDUtils.generateShortUuid(),
                        NETTY_SERVER_ID, null, "用户id: " + userId + "--连接发生问题，已被迫离线了", 2);
                write2flush(ctxs, message);
            }
            channels.remove(ctx);
            info += " 用户: " + userId + "被迫下线!";
        }
        log.error("通道: {} 连接发生问题" + info, channelId);
        log.error("Netty Connection error: {}", cause);
    }
}