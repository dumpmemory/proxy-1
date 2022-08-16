import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

public class StaticValue {
    public static ByteBuf connectResponse = Unpooled.copiedBuffer("HTTP/1.1 200 Connection Established" + "\r\n" + "\r\n", StandardCharsets.UTF_8).asReadOnly();
}
