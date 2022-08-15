import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProxyBackendServerHandler extends ChannelInboundHandlerAdapter {
    private final static Logger logger=LogManager.getLogger(ProxyBackendServerHandler.class);
    Channel inboundChannel;
    Host host;
    public ProxyBackendServerHandler(Channel inboundChannel,Host host) {
        this.inboundChannel=inboundChannel;
        this.host=host;
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (inboundChannel.isActive()) {
            inboundChannel.close();
        } else if (inboundChannel.isOpen()) {
            logger.warn("in channel 不应该为打开");
        }
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (inboundChannel.isActive()) {
            inboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        logger.warn(host.url()+"数据返回失败001 活动" + inboundChannel.isActive() + "打开" + inboundChannel.isOpen()+ "引用" + ((ByteBuf) msg).refCnt()+"hashcode"+this.hashCode());
                        ctx.channel().close();
                    }
                }
            });
        } else {
            logger.warn("in未活动,打开" + inboundChannel.isOpen() + "引用" + ((ByteBuf) msg).refCnt());
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("out"+cause.getMessage());
        ctx.close();
    }
}
