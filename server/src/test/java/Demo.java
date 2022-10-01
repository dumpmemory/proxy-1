import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.concurrent.ConcurrentSkipListMap;

public class Demo {
    private static Logger logger= LogManager.getLogger(Demo.class);
    public static void main(String[] args) {
        ByteBuf byteBuf = Unpooled.copiedBuffer("lsjdlfsldd".getBytes());
        System.out.println(byteBuf.readableBytes());
    }
}
