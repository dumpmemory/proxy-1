import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class ClientProxyHandler extends ChannelInboundHandlerAdapter {
    private final static Logger logger = LogManager.getLogger(ClientProxyHandler.class);
    ByteBuf byteBuf;
    private InetSocketAddress inetSocketAddress;
    private Channel outboundChannel;
    private int password = 356324;


    public ClientProxyHandler(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byteBuf = (ByteBuf) msg;
        if (outboundChannel == null) {
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(0, bytes);
            Host host = ClientUtil.parseUrl(bytes);
            final boolean s = ClientUtil.urlMatch(host.url());
            if (s) {
                inetSocketAddress = new InetSocketAddress(host.url(), host.port());
            }
            final Channel inboundChannel = ctx.channel();
            Bootstrap b = new Bootstrap();
            b.group(inboundChannel.eventLoop())
                    .channel(ctx.channel().getClass())
                    .handler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            if (!s) {
                                pipeline.addLast(new CryEncoder());
                            }
                            pipeline.addLast(new ProxyBackendHandler(inboundChannel));
                        }
                    })
            ;
            ChannelFuture f = b.connect(inetSocketAddress);
            outboundChannel = f.channel();
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (!future.isSuccess()) {
                        inboundChannel.close();
                    } else {
                        if (!s) {
                            outboundChannel.write(Unpooled.copyInt(password));
                            outboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) throws Exception {
                                    if (!future.isSuccess()) {
                                        logger.info("out写入失败 活动:" + outboundChannel.isActive() + "open" + outboundChannel.isOpen() + "引用:" + byteBuf.refCnt());
                                        future.channel().close();
                                    }
                                }
                            });
                        } else {
                            if (host.https()) {
                                ReferenceCountUtil.release(msg);
                                inboundChannel.writeAndFlush(Unpooled.copiedBuffer(StaticValue.connectResponse, StandardCharsets.UTF_8));
                            } else {
                                outboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                                    @Override
                                    public void operationComplete(ChannelFuture future) throws Exception {
                                        if (!future.isSuccess()) {
                                            logger.info("out写入失败 活动:" + outboundChannel.isActive() + "open" + outboundChannel.isOpen() + "引用:" + byteBuf.refCnt());
                                            future.channel().close();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });

        } else if (outboundChannel.isActive()) {
            outboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        logger.info("out写入失败 活动:" + outboundChannel.isActive() + "open" + outboundChannel.isOpen() + "引用:" + byteBuf.refCnt());
                        future.channel().close();
                    }
                }
            });
        } else {
            ReferenceCountUtil.release(msg);
            logger.info("out通道没有活动" + byteBuf.refCnt());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (outboundChannel != null) {
            ClientUtil.closeOnFlush(outboundChannel, 1);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (byteBuf != null) {
            logger.info("异常1" + cause.getMessage() + byteBuf.refCnt());
        } else {
            logger.info("异常1" + cause.getMessage());
        }
        StackTraceElement[] stackTrace = cause.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            logger.info(stackTraceElement);
        }
        ClientUtil.closeOnFlush(ctx.channel(), 2);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }


}
