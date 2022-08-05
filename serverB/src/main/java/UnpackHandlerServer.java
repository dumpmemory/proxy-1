import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.LinkedList;

public class UnpackHandlerServer extends ChannelInboundHandlerAdapter {
    int channelID = 0;
    int readNum = 0;
    int cNum = 0;
    LinkedList<ByteBuf> list = new LinkedList<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        int size = list.size();
        if (size == 0 && channelID == 0) {
            channelID = byteBuf.readInt();
            readNum = byteBuf.readInt();
            if (byteBuf.readableBytes() >= readNum) {
                ByteBuf buf = byteBuf.readBytes(readNum);
                if (byteBuf.readableBytes() > 0) {
                    list.add(byteBuf);
                }
                ctx.fireChannelRead(Unpooled.copyInt(channelID).writeBytes(buf));
                channelID = 0;
                readNum = 0;
            } else {
                list.add(byteBuf);
            }
        } else if (size!=0&&channelID==0){
            ByteBuf buf = list.get(0);
            list.clear();
            channelRead(ctx, Unpooled.copiedBuffer(buf, byteBuf));
        }

    }
}
