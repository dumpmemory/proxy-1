package com.m20891.handler;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.util.Strings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

public class Init {
    public final static HashSet<Integer> urls = urlHash();
    public static final Properties properties = loadProperties();
    public static final byte[] password = ((String) properties.get("password")).getBytes(StandardCharsets.UTF_8);
    public static final int port = Integer.parseInt((String) properties.get("port"));

    public static final InetSocketAddress inetSocketAddress = loadInetSocketAddress();

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("client.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

    private static HashSet<Integer> urlHash() {
        try(FileInputStream fileInputStream = new FileInputStream("pac.txt");) {
            Stream<String> lines = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8).lines();
            Stream<Integer> urlHash = lines.filter(s -> Strings.isBlank(s) || s.startsWith("#")).map(s -> s.replace(".", "").toLowerCase().hashCode());
            return new HashSet<>(urlHash.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static InetSocketAddress loadInetSocketAddress() {
        try(FileInputStream fileInputStream = new FileInputStream("config.txt");) {
            Stream<String> lines = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8).lines();
            Stream<String> proxyIPs = lines.filter(s -> !s.startsWith("#"));
            List<String> list = proxyIPs.toList();
            String[] address = list.get(Integer.parseInt(list.get(0))).split(":");
            return new InetSocketAddress(address[0], Integer.parseInt(address[1]));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
