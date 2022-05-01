package com.yunya.modules.treatment.other.netty;

import com.yunya.modules.treatment.other.config.WsProperties;
import com.yunya.modules.treatment.other.netty.chat.WebSocketNettyHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * netty服务端
 * 实现DisposableBean 在容器销毁前会调用destroy 方法进行线程组的关闭
 * @author Administrator
 */
@Slf4j
@Data
public class WebSocketNettyServer implements DisposableBean {
    /** 自定义配置*/
    @Autowired
    private WsProperties wsProperties;

    /**
     * 自定义入站规则
     */
    @Autowired
    private WebSocketNettyHandler webSocketNettyHandler;

    /**
     * 通道初始化对象
     */
    @Autowired
    private WebSocketChannelInit webSocketChannelInit;

    /**
     * boos线程组
     */
    private EventLoopGroup boos;

    /**
     * work线程组
     */
    private EventLoopGroup work;

    /**
     * 自定义启动方法
     */
    @PostConstruct
    public void start() {
        // 设置boos线程组
        boos = new NioEventLoopGroup(1);
        // 设置work线程组
        EventLoopGroup work = new NioEventLoopGroup();
        // 创建启动助手
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boos,work)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler())
                .childHandler(webSocketChannelInit);
        // 绑定ip和端口启动服务端
        ChannelFuture sync = null;
        try {
            // 绑定netty的启动端口
            sync = serverBootstrap.bind(wsProperties.getPort()).sync();
        } catch (InterruptedException e) {
            log.error("Netty Server Start failure!", e);
            close();
        }
        log.info("========================= Netty服务器启动成功--端口: {} =========================", wsProperties.getPort());
        sync.channel().closeFuture();
    }

    /**
     * 容器销毁前关闭线程组
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        close();
        log.info("Netty Server closed!");
    }

    /**
     * 关闭方法
     */
    public void close() {
        if (boos!=null) {
            boos.shutdownGracefully();
        }
        if (work!=null) {
            work.shutdownGracefully();
        }
    }
}