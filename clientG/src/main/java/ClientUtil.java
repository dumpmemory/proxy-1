import io.netty.channel.Channel;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Stream;

public class ClientUtil {
    private static HashSet<Integer> urls;
    private static HashSet<Integer> urlMatch = new HashSet<>();
    private static HashSet<Integer> urlNoMatch = new HashSet<>();
    private static InetSocketAddress inetSocketAddress;

    static {
        if (urls == null) {
            synchronized (ClientUtil.class) {
                if (urls == null) {
                    urls = new HashSet<>();
                    FileInputStream fileInputStream = null;
                    try {
                        fileInputStream = new FileInputStream("pac.txt");
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    Stream<String> lines = null;
                    try {
                        lines = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8).lines();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    lines.forEach(s -> {
                        String url = s
                                .replace(".","")
                                .toLowerCase();
                        urls.add(url.hashCode());
                    });
                }
            }
        }
    }

    public static InetSocketAddress getAddr() throws IOException {
        if (inetSocketAddress == null) {
            synchronized (ClientUtil.class) {
                if (inetSocketAddress == null) {
                    FileInputStream fileInputStream = new FileInputStream("config.txt");
                    Stream<String> lines = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8).lines();
                    List<String> list = lines.toList();
                    String[] address = list.get(Integer.parseInt(list.get(0))).split(":");
                    inetSocketAddress = new InetSocketAddress(address[0], Integer.parseInt(address[1]));
                }
            }
        }
        return inetSocketAddress;
    }

    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.close();
        }
    }
    static boolean urlMatch(String url) throws IOException {
        int urlHashCode = url.hashCode();
        boolean match = urlMatch.contains(urlHashCode);
        boolean noMatch = urlNoMatch.contains(urlHashCode);
        if (!match && !noMatch) {
            String[] split = url
                    .split("\\.");
            String s = "";
            for (int i = split.length - 1; i >= 0; i--) {
                s = split[i]+s;
                int hashCode = s.hashCode();
                if (urls.contains(hashCode)) {
                    urlMatch.add(urlHashCode);
                    return true;
                }
            }
            if (!urlMatch.contains(urlHashCode)) {
                urlNoMatch.add(urlHashCode);
            }
        }
        return match;
    }

    public static Host parseUrl(byte[] bytes) {
        String url = null;
        String port = null;
        boolean https=true;
        int us = 0;
        int ue = 0;
        int ps = 0;
        int pe = 0;
        if (bytes[0] != 67) {
            https=false;
        }
        for (int i = 0; i < bytes.length; i++) {
            if (us == 0
                    && bytes[i] == 10
                    && bytes[i + 1] == 72
                    && bytes[i + 2] == 111
                    && bytes[i + 3] == 115
                    && bytes[i + 4] == 116
                    && bytes[i + 5] == 58
                    && bytes[i + 6] == 32
            ) {
                us = i + 7;
                i += 6;
            } else if (us != 0 && ps == 0 && bytes[i] == 13 && bytes[i + 1] == 10) {
                ue = i;
                ps = -1;
                port = "80";
                url = new String(bytes, us, ue - us);
                break;
            } else if (us != 0 && ps == 0 && bytes[i] == 58) {
                ue = i;
                ps = i + 1;
            } else if (ps > 0 && bytes[i] == 13 && bytes[i + 1] == 10) {
                pe = i;
                url = new String(bytes, us, ue - us);
                port = new String(bytes, ps, pe - ps);
                break;
            }

        }
        return new Host(url, Integer.parseInt(port),https);
    }
}
