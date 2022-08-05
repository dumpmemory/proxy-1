import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    InetSocketAddress inetSocketAddress;
    int password=356324;
    ChannelFuture cf;

    public ClientHandler() throws IOException {
        inetSocketAddress=Util.getAddr();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = cf.channel();
        if (channel != null) {
            Util.closeOnFlush(channel);
//            channel.close();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (cf == null) {
            ctx.channel().eventLoop().execute(() -> {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(ctx.channel().eventLoop())
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .handler(new ChannelInitializer<>() {
                            @Override
                            protected void initChannel(Channel ch) throws Exception {
                                ch.pipeline().addLast(new CryEncoder());
                                ch.pipeline().addLast("abc",new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelInactive(ChannelHandlerContext ctxb) throws Exception {
                                       Util.closeOnFlush(ctx.channel());
//                                        ctx.channel().close();
                                    }
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctxb, Object msg) throws Exception {
                                        ctx.channel().writeAndFlush(msg);
                                    }

                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        cf.channel().write(Unpooled.copyInt(password));
                                        cf.channel().writeAndFlush(msg);
                                    }
                                });
                            }
                        });
                cf = bootstrap.connect(inetSocketAddress).addListener(future -> {
                    if (!future.isSuccess()) {
                        System.out.println(inetSocketAddress+"连接失败");
//                        ctx.channel().close();
                        Util.closeOnFlush(ctx.channel());
                        ReferenceCountUtil.release(msg);
                    }
                });
            });
        } else {
            cf.channel().writeAndFlush(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常" + cause.getMessage());
        try {
            Util.closeOnFlush(ctx.channel());
//            ctx.channel().close();
            if (cf.channel() != null) {
                Util.closeOnFlush(cf.channel());
//                cf.channel().close();
            } else {
                System.out.println("channel为空");
            }
        } catch (Exception e) {
            System.out.println("88888888888");
        }
    }
}
