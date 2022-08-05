import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class ClientUtil {
    static final NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    static final NioEventLoopGroup workerGroup = new NioEventLoopGroup(1);
    static final HashMap<Integer, Channel> channelMap = new HashMap<>();

    static ChannelFuture cf;

    public static void m() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new UnpackHandlerClient());
                        ch.pipeline().addLast(new HandlerClient());
                    }
                });
        //121.5.229.159
//                cf = bootstrap.connect(new InetSocketAddress("121.5.229.159", 8080)).addListener(future -> {
        cf = bootstrap.connect(new InetSocketAddress("127.0.0.1", 8080)).addListener(future -> {
            if (!future.isSuccess()) {
                System.out.println("连接失败");

            }
        });
    }
}
