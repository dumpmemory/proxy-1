import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.concurrent.ConcurrentSkipListMap;

public class Demo {
    private static Logger logger= LogManager.getLogger(Demo.class);
    public static void main(String[] args) {
        String a = "Cbc";
        for (int i = 0; i < a.getBytes().length; i++) {
            System.out.println(a.getBytes()[i]);
        }
    }
}
