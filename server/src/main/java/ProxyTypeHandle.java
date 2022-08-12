import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class ProxyTypeHandle extends ChannelInboundHandlerAdapter {
    private Channel outboundChannel;
    private final static Logger logger = LogManager.getLogger(ProxyTypeHandle.class);
    boolean status = true;
    int password = 356324;
    int type = 0;


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (outboundChannel != null&&outboundChannel.isActive()) {
            outboundChannel.close();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        if (status && byteBuf.readableBytes() >= 4) {
            int ps = byteBuf.readInt();
            if (ps != password) {
                logger.warn("不合法请求" + ctx.channel().remoteAddress());
                ctx.close();
                return;
            } else {
                logger.info("连接成功" + ctx.channel().remoteAddress());
            }
        }

        status = false;
        if (outboundChannel == null) {
            int readNum = byteBuf.readableBytes();
            byte[] bytes = new byte[readNum];
            byteBuf.getBytes(byteBuf.readerIndex(), bytes);
            final Host host;
            try {
                host = Util.parseUrl(bytes);
            } catch (Exception e) {
                logger.error("链接解析错误"+new String(bytes)+"//"+ctx.channel().remoteAddress());
                ReferenceCountUtil.release(msg);
                ctx.close();
                return;
            }
            if (bytes[0] == 71) {
                type = 1;
            } else {
                ReferenceCountUtil.release(msg);
            }
            ctx.channel().eventLoop().execute(() -> {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(ctx.channel().eventLoop())
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .handler(new ProxyBackendServerHandler(ctx.channel())
                        );
                ChannelFuture cf = bootstrap.connect(new InetSocketAddress(host.url(), host.port()));
                outboundChannel=cf.channel();
                cf.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            ctx.close();
                            logger.warn("目标客户端连接失败" + host.url() + "活动" + cf.channel().isActive() + "打开" + cf.channel().isOpen()+"引用:"+byteBuf.refCnt());
                            if (byteBuf.refCnt() == 1) {
                                logger.info("释放消息1"+"引用:"+byteBuf.refCnt());
                                ReferenceCountUtil.release(msg);
                            }
                        } else {
                            switch (type) {
                                case 0:
                                    ctx.channel().writeAndFlush(Unpooled.copiedBuffer(StaticValue.connectResponse, StandardCharsets.UTF_8));
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
                outboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            logger.warn("写入失败002 活动" + outboundChannel.isActive() + "打开" + outboundChannel.isOpen());
                            future.channel().close();
                        }
                    }
                });
            } else {
                logger.warn("in未活动,打开" + outboundChannel.isOpen() + "引用" + ((ByteBuf) msg).refCnt());
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
}
