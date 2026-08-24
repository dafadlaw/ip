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
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;
        String divider = "____________________________________________________________";
        String banner = """
                 _   _       _
                | \\ | | ___ | |__
                |  \\| |/ _ \\| '_ \\
                | |\\  | (_) | |_) |
                |_| \\_|\\___/|_.__/

                  (•_•)
                  ( •_•)>⌐■-■
                  (⌐■_■)
                """;
        String greeting = """
                Hii! I'm Nob :)
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
                } else if (command.startsWith("mark ")) {
                    markTask(command, tasks, taskCount, true);
                } else if (command.startsWith("unmark ")) {
                    markTask(command, tasks, taskCount, false);
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    taskCount = addTodo(command, tasks, taskCount);
                } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                    taskCount = addDeadline(command, tasks, taskCount);
                } else if (command.equals("event") || command.startsWith("event ")) {
                    taskCount = addEvent(command, tasks, taskCount);
                } else if (command.startsWith("todo")) {
                    showMissingSpaceHint("todo", command);
                } else if (command.startsWith("deadline")) {
                    showMissingSpaceHint("deadline", command);
                } else if (command.startsWith("event")) {
                    showMissingSpaceHint("event", command);
                } else {
                    showUnknownCommandHint();
                }
                System.out.println(divider);
            }
        }
    }

    /**
     * Adds a to-do parsed from a {@code todo DESCRIPTION} command.
     *
     * @param command the command entered by the user
     * @param tasks tasks stored by the user
     * @param taskCount number of valid tasks in the array
     * @return the updated number of valid tasks
     */
    private static int addTodo(String command, Task[] tasks, int taskCount) {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            System.out.println("Use: todo DESCRIPTION");
            System.out.println("(eg., todo borrow book)");
            return taskCount;
        }

        return addTask(new Todo(description), tasks, taskCount);
    }

    /**
     * Explains that a command name must be separated from its description by a space.
     *
     * @param commandName the expected command name
     * @param command the command entered by the user
     */
    private static void showMissingSpaceHint(String commandName, String command) {
        String description = command.substring(commandName.length()).trim();
        System.out.println("It looks like you are missing a space after '" + commandName + "'.");
        System.out.println("Did you mean: " + commandName + " " + description);
    }

    /** Displays the task commands available in Nob when a command is unrecognised. */
    private static void showUnknownCommandHint() {
        System.out.println("That isn't any of the commands I know. Did you mean one of these?");
        System.out.println("  todo DESCRIPTION");
        System.out.println("  deadline DESCRIPTION /by DATE_OR_TIME");
        System.out.println("  event DESCRIPTION /from START /to END");
    }

    /**
     * Displays every task currently stored in the task list.
     *
     * @param tasks tasks stored by the user
     * @param taskCount number of valid tasks in the array
     */
    private static void printTasks(Task[] tasks, int taskCount) {
        if (taskCount == 0) {
            System.out.println("Your task list is empty.");
            return;
        }

        System.out.println("Here are the tasks in your list:");
        for (int index = 0; index < taskCount; index++) {
            System.out.println((index + 1) + "." + tasks[index]);
        }
    }

    /**
     * Adds a deadline parsed from a {@code deadline DESCRIPTION /by TIME} command.
     *
     * @param command the command entered by the user
     * @param tasks tasks stored by the user
     * @param taskCount number of valid tasks in the array
     * @return the updated number of valid tasks
     */
    private static int addDeadline(String command, Task[] tasks, int taskCount) {
        String details = command.substring("deadline".length()).trim();
        int byIndex = details.indexOf(" /by ");
        if (byIndex < 1 || byIndex + " /by ".length() == details.length()) {
            if (details.contains("/by")) {
                System.out.println("Check that there is a space before and after '/by'.");
            }
            System.out.println("Use: deadline DESCRIPTION /by DATE_OR_TIME");
            System.out.println("(eg., deadline return book /by Sunday)");
            return taskCount;
        }

        String description = details.substring(0, byIndex).trim();
        String by = details.substring(byIndex + " /by ".length()).trim();
        return addTask(new Deadline(description, by), tasks, taskCount);
    }

    /**
     * Adds an event parsed from an {@code event DESCRIPTION /from START /to END} command.
     *
     * @param command the command entered by the user
     * @param tasks tasks stored by the user
     * @param taskCount number of valid tasks in the array
     * @return the updated number of valid tasks
     */
    private static int addEvent(String command, Task[] tasks, int taskCount) {
        String details = command.substring("event".length()).trim();
        int fromIndex = details.indexOf(" /from ");
        int toIndex = details.indexOf(" /to ");
        if (fromIndex < 1 || toIndex <= fromIndex + " /from ".length()
                || toIndex + " /to ".length() == details.length()) {
            if (details.contains("/from") || details.contains("/to")) {
                System.out.println("Check that there is a space before and after '/from' and '/to'.");
            }
            System.out.println("Use: event DESCRIPTION /from START /to END");
            System.out.println("(eg., event project meeting /from Mon 2pm /to 4pm)");
            return taskCount;
        }

        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + " /from ".length(), toIndex).trim();
        String to = details.substring(toIndex + " /to ".length()).trim();
        return addTask(new Event(description, from, to), tasks, taskCount);
    }

    /**
     * Stores a task and reports the result to the user when storage is available.
     *
     * @param task the task to store
     * @param tasks tasks stored by the user
     * @param taskCount number of valid tasks in the array
     * @return the updated number of valid tasks
     */
    private static int addTask(Task task, Task[] tasks, int taskCount) {
        if (taskCount == MAX_TASKS) {
            System.out.println("I can only store up to " + MAX_TASKS + " tasks.");
            return taskCount;
        }

        tasks[taskCount] = task;
        System.out.println("Alrighty. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + (taskCount + 1) + " tasks in the list.");
        return taskCount + 1;
    }

    /**
     * Marks a task as complete or incomplete using its position in the list.
     *
     * @param command the mark or unmark command entered by the user
     * @param tasks tasks stored by the user
     * @param taskCount number of valid tasks in the array
     * @param isDone whether the task should be marked as completed
     */
    private static void markTask(String command, Task[] tasks, int taskCount, boolean isDone) {
        String numberText = command.substring(command.indexOf(' ') + 1).trim();
        try {
            int taskNumber = Integer.parseInt(numberText);
            if (taskNumber < 1 || taskNumber > taskCount) {
                System.out.println("Please enter a task number from 1 to " + taskCount + ".");
                return;
            }

            Task task = tasks[taskNumber - 1];
            if (isDone) {
                task.markAsDone();
                System.out.println("LET'S GOOO! I've marked this task as done:");
            } else {
                task.markAsUndone();
                System.out.println("Okay... I've marked this task as not done yet:");
            }
            System.out.println("  " + task);
        } catch (NumberFormatException exception) {
            System.out.println("Please enter a valid task number.");
        }
    }
}
