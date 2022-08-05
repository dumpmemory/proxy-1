import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;

public class Demo {
    public static void main(String[] args) {
        ArrayList<ByteBuf> objects = new ArrayList<>();
        ByteBuf buf = Unpooled.copyInt(1212, 234);
        ByteBuf buf1 = Unpooled.copiedBuffer( objects.get(0),Unpooled.copyInt(23));
        System.out.println(buf1);
    }
}
