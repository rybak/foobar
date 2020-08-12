import java.util.function.Supplier;

/**
 * @author Andrei Rybak
 */
public class TernarySupplier {
    public static void main(String[] args) {
        Supplier<String> a = () -> {
            System.out.println("Length > 0");
            return "xx";
        };
        Supplier<String> b = () -> {
            System.out.println("Length == 0");
            return "yy";
        };
        String x = ((args.length > 0)
            ? (Supplier<String>)
            () -> {
                System.out.println("Length > 0");
                return "xx";
            }
            : (Supplier<String>)
            () -> {
                System.out.println("Length == 0");
                return "yy";
            }
        ).get();
        System.out.println("x = " + x);
    }
}
