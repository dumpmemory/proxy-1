import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Demo {
    private static Logger logger= LogManager.getLogger(Demo.class);
    public static void main(String[] args) {
        String a = "abc123";
        System.out.println(a.hashCode());
        System.out.println(a.getBytes().equals(a.getBytes()));
    }
}
