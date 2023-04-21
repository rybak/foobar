package command_line_interface;

/**
 * See
 * <a href="https://docs.oracle.com/en/java/javase/18/docs/specs/man/java.html#java-command-line-argument-files">Java
 * documentation about <code>java</code> Command-Line Argument Files</a> for more details.
 */
public class CommandLineArgumentFile {
    public static void main(String[] args) {
        System.out.println("Got " + args.length + " command line arguments");
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            System.out.println("============================");
            System.out.println("Argument number " + i + ":");
            System.out.println("----------------------------");
            System.out.println(arg);
            System.out.println("============================");
        }
    }
}
