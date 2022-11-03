import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.channel.pool.SimpleChannelPool;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Demo {
    public static void main(String[] args) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("h.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
        for (Object o : Arrays.asList(1,2,3,4,5)) {
            bufferedWriter.write(o.toString());
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }
}
