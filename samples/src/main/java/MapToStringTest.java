import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Andrei Rybak
 */
public class MapToStringTest {
    public static void main(String[] args) {
        Map<String, String> config = new HashMap<>();
        config.put("symbols", "/CL,/ZF");
        config.put("tables", "MINS,TICKS");
        System.out.println(config);
        System.out.println(config.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("\n")));
    }
}
