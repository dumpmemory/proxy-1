import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.LinkedList;

public class UnpackHandlerClient extends ChannelInboundHandlerAdapter {
    int a = 0;
    int channelID = 0;
    int readNum = 0;
    int cNum = 0;
    LinkedList<ByteBuf> list = new LinkedList<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        int size = list.size();
        if (size == 0 && channelID == 0) {
            System.out.println("aaaaaaaaaaaaaa");
            ByteBuf byteBuf = (ByteBuf) msg;
            channelID = byteBuf.readInt();
            readNum = byteBuf.readInt();
            System.out.println("====="+readNum);
            if (byteBuf.readableBytes() >= readNum) {
                ByteBuf buf = byteBuf.readBytes(readNum);
                ctx.fireChannelRead(Unpooled.copyInt(channelID).writeBytes(buf));
                channelID = 0;
                readNum = 0;
            }

            if (byteBuf.readableBytes() >= 8 && channelID == 0) {
                channelID = byteBuf.readInt();
                readNum = byteBuf.readInt();
                System.out.println("====="+readNum);
            }
            if (byteBuf.readableBytes() >= readNum&&readNum!=0) {
                list.clear();
                channelRead(ctx, byteBuf);
                return;
            } else if (byteBuf.readableBytes() != 0) {
                list.add(byteBuf);
            } else {
                list.clear();
            }
        } else if (size != 0 && channelID == 0) {
            System.out.println("bbbbbbbbbbb");
            ByteBuf buf = Unpooled.copiedBuffer(list.get(0), (ByteBuf) msg);
            list.clear();
            a = 1;
            channelRead(ctx, buf);
            return;
        } else if (size != 0 && channelID != 0) {
            System.out.println("ccccccccccc");
            ByteBuf byteBuf = Unpooled.copiedBuffer(list.get(0), (ByteBuf) msg);
            if (byteBuf.readableBytes() >= readNum) {
                ByteBuf buf = byteBuf.readBytes(readNum);
                ctx.fireChannelRead(Unpooled.copyInt(channelID).writeBytes(buf));
                channelID = 0;
                readNum = 0;
            }
            if (byteBuf.readableBytes() >= 8 && channelID == 0) {
                channelID = byteBuf.readInt();
                readNum = byteBuf.readInt();
                System.out.println("====="+readNum);
            }
            if (byteBuf.readableBytes() >= readNum&&readNum!=0) {
                list.clear();
                channelRead(ctx, byteBuf);
                return;
            } else if (byteBuf.readableBytes() != 0) {
                System.out.println("iiiii");
                list.set(0, byteBuf);
            } else {
                list.clear();
            }
        } else if (size == 0 && channelID != 0) {
            System.out.println("ddddddddddddddd");
            ByteBuf byteBuf = (ByteBuf) msg;
            if (byteBuf.readableBytes() >= readNum) {
                ByteBuf buf = byteBuf.readBytes(readNum);
                ctx.fireChannelRead(Unpooled.copyInt(channelID).writeBytes(buf));
                channelID = 0;
                readNum = 0;
            }
            if (byteBuf.readableBytes() >= 8 && channelID == 0) {
                channelID = byteBuf.readInt();
                readNum = byteBuf.readInt();
                System.out.println("====="+readNum);
            }
            if (byteBuf.readableBytes() >= readNum&&readNum!=0) {
                list.clear();
                channelRead(ctx, byteBuf);
                return;
            } else if (byteBuf.readableBytes() != 0) {
                list.add(byteBuf);
            } else {
                list.clear();
            }
        }
    }
}
