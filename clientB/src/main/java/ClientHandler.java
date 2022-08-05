import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    Channel channel = ClientUtil.cf.channel();
    HashMap<Integer, Channel> channelMap=ClientUtil.channelMap;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channelMap.put(ctx.channel().hashCode(), ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("浏览器断开");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf= (ByteBuf) msg;
        int channelID = ctx.channel().hashCode();
        ByteBuf byteBuf = Unpooled.copyInt(channelID, buf.readableBytes());
        channel.write(byteBuf);
        channel.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常" + cause.getMessage());
        try {
            ctx.close();
            channel.close();
        } catch (Exception e) {
            System.out.println("88888888888");
        }
    }
}
