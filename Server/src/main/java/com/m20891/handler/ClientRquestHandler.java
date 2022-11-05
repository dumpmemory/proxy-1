package com.m20891.handler;

import com.m20891.util.data.StaticValue;
import com.m20891.util.url.Host;
import com.m20891.util.url.URLUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class ClientRquestHandler extends ChannelInboundHandlerAdapter {
    private Channel outboundChannel;
    private final static Logger logger = LogManager.getLogger(ClientRquestHandler.class);
    boolean status = true;
    int type = 0;


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (outboundChannel != null && outboundChannel.isActive()) {
            outboundChannel.close();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        if (status) {
            byte[] password = Init.password;
            if (byteBuf.readableBytes() >= password.length) {
                ByteBuf readPW = byteBuf.readBytes(password.length);
                byte[] bytes = new byte[password.length];
                readPW.getBytes(0,bytes);
                ReferenceCountUtil.release(readPW);
                if (!equals(password,bytes)){
                    logger.warn("不合法请求" + ctx.channel().remoteAddress());
                    ReferenceCountUtil.release(msg);
                    ctx.close();
                    return;
                } else {
                    status = false;
                    logger.info("连接成功" + ctx.channel().remoteAddress());
                }
            } else {
                logger.warn("不合法请求" + ctx.channel().remoteAddress());
                ReferenceCountUtil.release(msg);
                ctx.close();
                return;
            }
        }
        if (outboundChannel == null) {
            int readNum = byteBuf.readableBytes();
            byte[] bytes = new byte[readNum];
            byteBuf.getBytes(byteBuf.readerIndex(), bytes);
            final Host host;
            try {
                host = URLUtil.parseUrl(bytes);
            } catch (Exception e) {
                logger.error("链接解析错误"+new String(bytes)+"//"+ctx.channel().remoteAddress());
                ReferenceCountUtil.release(msg);
                ctx.close();
                return;
            }
            if (bytes[0] == 71) {
                type = 1;
            }
            ctx.channel().eventLoop().execute(() -> {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(ctx.channel().eventLoop())
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .handler(new ServerResponseHandler(ctx.channel(),host)
                        );
                ChannelFuture cf = bootstrap.connect(new InetSocketAddress(host.url(), host.port()));
                outboundChannel=cf.channel();
                cf.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            int readNum = byteBuf.readableBytes();
                            byte[] bytes = new byte[readNum];
                            byteBuf.getBytes(byteBuf.readerIndex(), bytes);
                            ReferenceCountUtil.release(msg);
                            logger.warn("目标客户端连接失败" + host.url()+":"+host.port() + "活动" + cf.channel().isActive() + "打开" + cf.channel().isOpen()+"引用:"+byteBuf.refCnt()+"\n"+new String(bytes));
                            ctx.close();
                        } else {
                            switch (type) {
                                case 0:
                                    ctx.channel().writeAndFlush(Unpooled.copiedBuffer(StaticValue.connectResponse, StandardCharsets.UTF_8));
                                    ReferenceCountUtil.release(msg);
                                    break;
                                case 1:
                                    future.channel().writeAndFlush(byteBuf);
                                    break;
                            }
                        }
                    }
                });
            });
        } else {
            if (outboundChannel.isActive()) {
                outboundChannel.writeAndFlush(msg);
            } else {
                ReferenceCountUtil.release(msg);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.fatal("异常" + cause.getMessage());
        StackTraceElement[] stackTrace = cause.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            logger.fatal(stackTraceElement);
        }
       ctx.close();
    }

    public boolean equals(byte[] value, byte[] other) {
        for (int i = 0; i < value.length; i++) {
            if (value[i] != other[i]) {
                return false;
            }
        }
        return true;
    }
}
