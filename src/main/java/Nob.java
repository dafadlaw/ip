import java.util.Scanner;

/**
 * Starts the Nob chatbot application.
 */
public class Nob {
    /** The maximum number of tasks Nob can keep during one run. */
    private static final int MAX_TASKS = 100;

    /**
     * Reads and responds to commands until the user enters {@code bye}.
     *
     * @param args command-line arguments, which this application does not use
     */
    public static void main(String[] args) {
        String[] tasks = new String[MAX_TASKS];
        int taskCount = 0;
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

                if (command.equals("bye")) {
                    System.out.println(farewell);
                    System.out.println(divider);
                    break;
                }

                if (command.equals("list")) {
                    printTasks(tasks, taskCount);
                } else if (taskCount == MAX_TASKS) {
                    System.out.println("I can only store up to " + MAX_TASKS + " tasks.");
                } else {
                    tasks[taskCount] = command;
                    taskCount++;
                    System.out.println("added: " + command);
                }
                System.out.println(divider);
            }
        }
    }

    /**
     * Displays every task currently stored in the task list.
     *
     * @param tasks tasks stored by the user
     * @param taskCount number of valid tasks in the array
     */
    private static void printTasks(String[] tasks, int taskCount) {
        if (taskCount == 0) {
            System.out.println("Your task list is empty.");
            return;
        }

        for (int index = 0; index < taskCount; index++) {
            System.out.println((index + 1) + ". " + tasks[index]);
        }
    }
}
