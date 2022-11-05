import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.util.Strings;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

public class Demo {
    public static void main(String[] args) throws IOException {
        try(FileInputStream fileInputStream = new FileInputStream("pac.txt");) {
            Stream<String> lines = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8).lines();
            Stream<String> stringStream = lines.filter(s -> false);
            List<String> list = stringStream.toList();
            Stream<Integer> urlHash = lines.filter(s -> !Strings.isBlank(s) || !s.startsWith("#")).map(s -> s.replace(".", "").toLowerCase().hashCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
