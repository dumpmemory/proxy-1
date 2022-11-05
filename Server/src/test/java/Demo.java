import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

public class Demo {
    private static Logger logger= LogManager.getLogger(Demo.class);
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        String[] strings = {"a", "b","c"};
        properties.put("abc", strings);


    }
}
