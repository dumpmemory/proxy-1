import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Init {
    public static final Init Instance=new Init();

    public Init() {
        try {
            loadPassword();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private byte[] password;

    public void loadPassword() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("server.properties"));
        password = ((String) properties.get("password")).getBytes(StandardCharsets.UTF_8);
    }
    public byte[] getPassword() {
        return password;
    }
}
