/**
 * Starts the Nob chatbot application.
 */
public class Nob {
    public static void main(String[] args) {
        String divider = "____________________________________________________________";
        String banner = """
                 _   _       _
                | \\ | | ___ | |__
                |  \\| |/ _ \\| '_ \\
                | |\\  | (_) | |_) |
                |_| \\_|\\___/|_.__/
                """;
        String greeting = """
                Hello! I'm Nob.
                What's up?
                """;
        String farewell = "Goodbye! Hope to see you soon!";

        System.out.println(divider);
        System.out.print(banner);
        System.out.print(greeting);
        System.out.println(divider);
        System.out.println(farewell);
        System.out.println(divider);
    }
}
