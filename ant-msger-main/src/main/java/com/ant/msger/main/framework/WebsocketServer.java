package com.ant.msger.main.framework;

import com.ant.msger.main.framework.handler.WebSocketPrefixHandler;
import com.ant.msger.main.framework.handler.WebsocketServerHandler;
import com.ant.msger.main.framework.mapping.HandlerMapper;
import com.ant.msger.main.web.jt808.codec.JT808MessageEncoder;
import com.ant.msger.main.web.jt808.codec.JT808MessageTcpDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class WebsocketServer {

    private static final Logger log = LoggerFactory.getLogger(WebsocketServer.class);
    private volatile boolean isRunning = false;

    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;
    private int port;
    private byte delimiter;

    private HandlerMapper handlerMapper;

    public WebsocketServer() {
    }

    public WebsocketServer(int port, byte delimiter, HandlerMapper handlerMapper) {
        this.port = port;
        this.delimiter = delimiter;
        this.handlerMapper = handlerMapper;
    }

    private void bind() throws Exception {
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(30, 0, 0, TimeUnit.MINUTES));
                        ch.pipeline().addLast(new HttpServerCodec());//设置解码器
                        //聚合器，使用websocket会用到
                        ch.pipeline().addLast(new HttpObjectAggregator(65536));
                        //用于大数据的分区传输
                        ch.pipeline().addLast(new ChunkedWriteHandler());
                        ch.pipeline().addLast(new WebSocketPrefixHandler());
                        ch.pipeline().addLast(new JT808MessageTcpDecoder());
                        ch.pipeline().addLast(new JT808MessageEncoder());
                        ch.pipeline().addLast(new WebsocketServerHandler(handlerMapper));
                    }
                });

        this.log.info("Websocket服务启动完毕,port={}", this.port);
        ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

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
                this.log.info("Websocket服务启动出错:{}", e.getMessage());
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
                log.error("Websocket workerGroup 无法正常停止:{}", future.cause());
            }

            future = this.bossGroup.shutdownGracefully().await();
            if (!future.isSuccess()) {
                log.error("Websocket bossGroup 无法正常停止:{}", future.cause());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.log.info("Websocket服务已经停止...");
    }

    private String getName() {
        return "Websocket-Server";
    }
}