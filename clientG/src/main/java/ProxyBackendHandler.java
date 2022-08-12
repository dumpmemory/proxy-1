import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
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
        inboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    logger.info("in写入失败 活动:"+inboundChannel.isActive()+"open"+inboundChannel.isOpen()+"引用:"+byteBuf.refCnt());
                    future.channel().close();
                }
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Util.closeOnFlush(inboundChannel,3);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (byteBuf != null) {
            logger.info("异常2" + cause.getMessage() + byteBuf.refCnt());
        } else {
            logger.info("异常2" + cause.getMessage());
        }
        Util.closeOnFlush(ctx.channel(),4);
    }
}