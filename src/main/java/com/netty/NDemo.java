package com.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

public class NDemo {
    public static void main(String[] args) {
        byte[] bytes = new byte[]{13, 10};
        System.out.println(new String(bytes)+"jjj");
    }
}
