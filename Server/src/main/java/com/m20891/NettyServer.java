package com.m20891;

import com.m20891.handler.Init;
import com.m20891.handler.ProxyServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NettyServer {
    private final static Logger logger= LogManager.getLogger(NettyServer.class);
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childHandler(new ProxyServerInitializer())
                .childOption(ChannelOption.SO_KEEPALIVE,true)
        ;
        bootstrap.bind(Init.port).addListener(future -> {
            logger.info("启动成功:"+future.isSuccess());
        }).sync();
    }
}
