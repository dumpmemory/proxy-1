import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

import java.util.HashMap;

public class CryEncoder extends ChannelOutboundHandlerAdapter {
    private static HashMap<Byte, Byte> map = new HashMap<>();

    static {
        byte a=-128;
        byte b=0;
        for (int i = 0; i < 256; i++) {
            map.put(a++, b++);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf byteBuf= (ByteBuf) msg;
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(byteBuf.readerIndex(), bytes);
        for (int i = 0; i < bytes.length; i++) {
            bytes[i]=map.get(bytes[i]);
        }
        ReferenceCountUtil.release(msg);
        ctx.write(Unpooled.copiedBuffer(bytes), promise);
    }

}
