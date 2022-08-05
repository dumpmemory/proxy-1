import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.util.HashMap;

public class HandlerClient extends ChannelInboundHandlerAdapter {
    HashMap<Integer, Channel> channelMap=ClientUtil.channelMap;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        int channelID = byteBuf.readInt();
        System.out.println("长度为"+byteBuf.readableBytes());
        channelMap.get(channelID).writeAndFlush(msg);
        //        channelMap.get(channelID).writeAndFlush(byteBuf);
    }
}
