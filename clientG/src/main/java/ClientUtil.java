import io.netty.channel.Channel;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

public class ClientUtil {
    private static InetSocketAddress inetSocketAddress;

    public static InetSocketAddress getAddr() throws IOException {
        if (inetSocketAddress == null) {
            synchronized (ClientUtil.class) {
                FileInputStream fileInputStream = new FileInputStream("config.txt");
                Stream<String> lines = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8).lines();
                List<String> list = lines.toList();
                String[] address = list.get(Integer.parseInt(list.get(0))).split(":");
                inetSocketAddress=new InetSocketAddress(address[0], Integer.parseInt(address[1]));
            }
        }
        return inetSocketAddress;
    }

    static void closeOnFlush(Channel ch,int i) {
        if (ch.isActive()) {
            ch.close();
        }
    }
    static byte[] getPac() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("pac.txt");
        byte[] bytes = IOUtils.toByteArray(fileInputStream);
        fileInputStream.close();
        return bytes;
    }
}
