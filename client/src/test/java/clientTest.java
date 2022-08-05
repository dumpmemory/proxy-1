import java.io.IOException;
import java.net.InetSocketAddress;

public class clientTest {
    public static void main(String[] args) throws IOException {
        InetSocketAddress load = Util.getAddr();
        System.out.println(load.getHostName());
    }
}
