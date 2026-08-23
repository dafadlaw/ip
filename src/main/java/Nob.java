import java.util.Scanner;

/**
 * Starts the Nob chatbot application.
 */
public class Nob {
    /**
     * Reads and responds to commands until the user enters {@code bye}.
     *
     * @param args command-line arguments, which this application does not use
     */
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
                Hi! I'm Nob :)
                What's up?
                """;
        String farewell = "Goodbye! Hope to see you soon!";

        System.out.println(divider);
        System.out.print(banner);
        System.out.print(greeting);
        System.out.println(divider);

        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();
                System.out.println(divider);

                //exit command
                if (command.equals("bye")) {
                    System.out.println(farewell);
                    System.out.println(divider);
                    break;
                }

                //echo
                System.out.println(command);
                System.out.println(divider);
            }
        }
    }
}
