package com.antnest.msger.main.framework;

import com.antnest.msger.main.framework.handler.UDPServerHandler;
import com.antnest.msger.core.mapping.HandlerMapper;
import com.antnest.msger.core.redis.RedisFragMsgService;
import com.antnest.msger.main.web.jt808.codec.JT808MessageUdpDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class UDPServer {

    private static final Logger log = LoggerFactory.getLogger(UDPServer.class);
    private volatile boolean isRunning = false;

    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;
    private int port;
    private int sessionMinutes;

    private HandlerMapper handlerMapper;
    private RedisFragMsgService redisFragMsgService;

    public UDPServer(int port, HandlerMapper handlerMapper, RedisFragMsgService redisFragMsgService, int sessionMinutes) {
        this.port = port;
        this.handlerMapper = handlerMapper;
        this.sessionMinutes = sessionMinutes;
        this.redisFragMsgService = redisFragMsgService;
    }

    private void bind() throws Exception {
//        this.bossGroup = new NioEventLoopGroup();
        this.bossGroup = new NioEventLoopGroup(5);
//        this.workerGroup = new NioEventLoopGroup();
//        ServerBootstrap serverBootstrap = new ServerBootstrap();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(bossGroup)
//                .channel(NioServerSocketChannel.class)
//                .channel(NioDatagramChannel.class)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_BROADCAST, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION, true)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel ch) throws Exception {
                        ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(30, 0, 0, TimeUnit.MINUTES));
//                        // 1024表示单条消息的最大长度，解码器在查找分隔符的时候，达到该长度还没找到的话会抛异常
//                        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.wrappedBuffer(new byte[]{delimiter}), Unpooled.wrappedBuffer(new byte[]{delimiter, delimiter})));
//                        ch.pipeline().addLast(new UdpServerHandler());
                        ch.pipeline().addLast(new JT808MessageUdpDecoder(new MessageToMessageDecoder<ByteBuf>() {
                            @Override
                            protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
                            }
                        }, handlerMapper, redisFragMsgService));
//                        ch.pipeline().addLast(new JT808MessageEncoder());
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new UDPServerHandler(handlerMapper, sessionMinutes));

                    }

                });
        this.log.info("UDP服务启动完毕,port={}", this.port);
        ChannelFuture channelFuture = bootstrap.bind(port).sync();

        channelFuture.channel().closeFuture().sync();
    }

    public synchronized void startServer() {
        if (this.isRunning) {
            throw new IllegalStateException(this.getName() + " is already started .");
        }
        this.isRunning = true;

        new Thread(() -> {
            try {
                this.bind();
            } catch (Exception e) {
                this.log.info("UDP服务启动出错:{}", e.getMessage());
                e.printStackTrace();
            }
        }, this.getName()).start();
    }

    public synchronized void stopServer() {
        if (!this.isRunning) {
            throw new IllegalStateException(this.getName() + " is not yet started .");
        }
        this.isRunning = false;

        try {
            Future<?> future = this.workerGroup.shutdownGracefully().await();
            if (!future.isSuccess()) {
                log.error("workerGroup 无法正常停止:{}", future.cause());
            }

            future = this.bossGroup.shutdownGracefully().await();
            if (!future.isSuccess()) {
                log.error("bossGroup 无法正常停止:{}", future.cause());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.log.info("UDP服务已经停止...");
    }

    private String getName() {
        return "UDP-Server";
    }
}