import com.m20891.util.url.Host;
import io.netty.channel.Channel;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
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
                        if (!s.startsWith("#")) {
                            String url = s
                                    .replace(".","")
                                    .toLowerCase();
                            urls.add(url.hashCode());
                        }
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
}
