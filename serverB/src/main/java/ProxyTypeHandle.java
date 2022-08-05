import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ProxyTypeHandle extends ChannelInboundHandlerAdapter {
    HashMap<Integer, Channel> hashMap = new HashMap<>();
    int type = 0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        System.out.println("连接地址" + socketAddress);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("本地代理断开");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        int channelID = byteBuf.readInt();
        if (hashMap.get(channelID) == null) {
            int readNum = byteBuf.readableBytes();
            byte[] bytes = new byte[readNum];
            byteBuf.getBytes(byteBuf.readerIndex(), bytes);
            System.out.println(new String(bytes));
            Host host = Util.parseUrl(bytes);
            System.out.println("连接" + host);
            if (bytes[0] == 71) {
                type = 1;
            }
            ctx.channel().eventLoop().execute(() -> {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(ctx.channel().eventLoop())
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelInactive(ChannelHandlerContext ctxb) throws Exception {
                                        System.out.println("目标服务器断开");
                                        ctx.close();
                                        ctxb.close();
                                    }

                                    @Override
                                    public void channelActive(ChannelHandlerContext ctxb) {
                                        int l = 0;
                                        for (Map.Entry entry : hashMap.entrySet()) {
                                            if (entry.getValue() == ctxb.channel()) {
                                                l = (int) entry.getKey();
                                            }
                                        }
                                        System.out.println("channelID" + (channelID == l));
                                        switch (type) {
                                            case 0:
                                                ByteBuf buf = Unpooled.copiedBuffer(StaticValue.connectResponse, StandardCharsets.UTF_8);
                                                ctx.channel().write(Unpooled.copyInt(channelID, buf.readableBytes()));
                                                ctx.channel().writeAndFlush(buf);
                                                break;
                                            case 1:
                                                ctxb.writeAndFlush(byteBuf);
                                                break;
                                        }
                                    }

                                    @Override
                                    public void channelRead(ChannelHandlerContext ctxb, Object msg) throws IOException, InterruptedException {
                                        int l = 0;
                                        for (Map.Entry entry : hashMap.entrySet()) {
                                            if (entry.getValue() == ctxb.channel()) {
                                                l = (int) entry.getKey();
                                            }
                                        }

                                        ByteBuf buf = (ByteBuf) msg;
                                        byte[] bytes1 = new byte[buf.readableBytes()];
                                        buf.getBytes(0, bytes1);
                                        IOUtils.write(bytes1,new FileOutputStream("Q:\\O\\demo\\asf.txt",true));
                                        System.out.println();
                                        System.out.println("长度"+buf.readableBytes()+"ID==="+channelID);
                                        ctx.channel().writeAndFlush(Unpooled.copyInt(channelID, buf.readableBytes()).writeBytes(buf));
                                        TimeUnit.MILLISECONDS.sleep(500);
                                    }
                                });
                            }
                        });
                ChannelFuture cf = bootstrap.connect(new InetSocketAddress(host.url(), host.port()));
                cf.addListener(future -> {
                    if (future.isSuccess()) {
                        System.out.println("连接成功===");
                        hashMap.put(channelID, cf.channel());
                    }
                });

            });
        } else {
            try {
                Channel channel = hashMap.get(channelID);
                channel.writeAndFlush(msg);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("异常" + cause.getMessage());
        cause.printStackTrace();

    }
}
