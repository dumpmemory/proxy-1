import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProxyBackendHandler extends ChannelInboundHandlerAdapter {
    private final static Logger logger = LogManager.getLogger(ProxyBackendHandler.class);
    ByteBuf byteBuf;
    private final Channel inboundChannel;
    public ProxyBackendHandler(Channel inboundChannel) {
        this.inboundChannel=inboundChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byteBuf= (ByteBuf) msg;
        if (inboundChannel.isActive()) {
            inboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        logger.info("in写入失败 活动:" + inboundChannel.isActive() + "open" + inboundChannel.isOpen() + "引用:" + byteBuf.refCnt());
                        future.channel().close();
                    }
                }
            });
        } else {
            ReferenceCountUtil.release(msg);
            ClientUtil.closeOnFlush(ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ClientUtil.closeOnFlush(inboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (byteBuf != null) {
            logger.info("异常2" + cause.getMessage() + byteBuf.refCnt());
        } else {
            logger.info("异常2" + cause.getMessage());
        }
        ClientUtil.closeOnFlush(ctx.channel());
    }
}
