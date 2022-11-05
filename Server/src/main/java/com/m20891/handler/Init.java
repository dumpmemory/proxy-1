package com.m20891.handler;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Init {
    public static final Properties properties=loadProperties();
    public static final byte[] password=((String) properties.get("password")).getBytes(StandardCharsets.UTF_8);
    public static final int port = Integer.parseInt((String) properties.get("port"));

    private static Properties loadProperties()  {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("server.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return  properties;
    }


}
