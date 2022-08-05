import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServerCB {
    public static void main(String[] args) throws InterruptedException {
        ClientUtil.m();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(ClientUtil.bossGroup,ClientUtil.workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ClientHandler());
                    }
                });
        bootstrap.bind(8090).addListener(future -> {
            System.out.println(future);
        });
    }
}
