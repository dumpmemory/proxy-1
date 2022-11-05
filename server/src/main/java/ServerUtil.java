import com.m20891.util.url.Host;
import io.netty.channel.Channel;

public class ServerUtil {
    public  static byte[] password;


    public static String status(Channel channel) {
        return "isActive:  "+channel.isActive()+"isOpen:  "+channel.isOpen();
    }
}
