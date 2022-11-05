package com.m20891;

import com.m20891.handler.ClientInit;
import com.m20891.handler.ProxyInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

public class ClientApplication {
    private final static Logger logger = LogManager.getLogger(ClientApplication.class);
    private static final InetSocketAddress inetSocketAddress= ClientInit.inetSocketAddress;

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new ProxyInitializer(inetSocketAddress))
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .bind(ClientInit.port).sync();
        } catch (Exception e) {
            logger.error("启动失败");
            bossGroup.shutdownGracefully();
        }

    }
}
