import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

public class CommonTest {
    @Test
    public void urlA() {
        String s = "//";
        byte[] bytes = s.getBytes();
        for (byte aByte : bytes) {
            System.out.println(aByte);
        }
    }
}
