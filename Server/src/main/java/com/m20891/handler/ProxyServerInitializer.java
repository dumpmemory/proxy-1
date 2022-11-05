package com.m20891.handler;

import com.m20891.codec.CDecoder;
import com.m20891.codec.CEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

public class ProxyServerInitializer extends ChannelInitializer {


    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new CDecoder());
        pipeline.addLast(new CEncoder());
        pipeline.addLast(new ClientRquestHandler());
    }
}
