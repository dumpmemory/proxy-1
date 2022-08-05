import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.HashMap;

public class CryDecoder extends ChannelInboundHandlerAdapter {
    private static HashMap<Byte, Byte> map = new HashMap<>();
    static {
        byte b=-128;
        byte a=0;
        for (int i = 0; i < 256; i++) {
            map.put(a++, b++);
        }
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf= (ByteBuf) msg;
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(byteBuf.readerIndex(), bytes);
        for (int i = 0; i < bytes.length; i++) {
            bytes[i]=map.get(bytes[i]);
        }
        ReferenceCountUtil.release(msg);
        ctx.fireChannelRead(Unpooled.copiedBuffer(bytes));
    }
}
