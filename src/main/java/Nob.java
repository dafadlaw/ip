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
                WASSUP! I'm Nob :)
                How can I help you?
                """;
        String farewell = "Goodbye! Hope to see you soon mate!";

        System.out.println(divider);
        System.out.print(banner);
        System.out.print(greeting);
        System.out.println(divider);

        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();
                System.out.println(divider);

                try {
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
                } catch (NobException exception) {
                    System.out.println(exception.getMessage());
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
    private static int addTodo(String command, Task[] tasks, int taskCount) throws NobException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new NobException("Use: todo DESCRIPTION\n(eg., todo borrow book)");
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
    private static int addDeadline(String command, Task[] tasks, int taskCount) throws NobException {
        String details = command.substring("deadline".length()).trim();
        if (details.startsWith("/by")) {
            throw new NobException("Description should not be empty.\n"
                    + "Use: deadline DESCRIPTION /by DATE_OR_TIME\n"
                    + "(eg., deadline return book /by Sunday)");
        }

        int byIndex = details.indexOf(" /by ");
        if (byIndex < 1 || byIndex + " /by ".length() == details.length()) {
            if (details.contains("/by")) {
                throw new NobException("Check that there is a space before and after '/by'.\n"
                        + "Use: deadline DESCRIPTION /by DATE_OR_TIME\n"
                        + "(eg., deadline return book /by Sunday)");
            }
            throw new NobException("Use: deadline DESCRIPTION /by DATE_OR_TIME\n"
                    + "(eg., deadline return book /by Sunday)");
        }

        String description = details.substring(0, byIndex).trim();
        if (description.isEmpty()) {
            throw new NobException("Description should not be empty.\n"
                    + "Use: deadline DESCRIPTION /by DATE_OR_TIME\n"
                    + "(eg., deadline return book /by Sunday)");
        }

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
    private static int addEvent(String command, Task[] tasks, int taskCount) throws NobException {
        String details = command.substring("event".length()).trim();
        if (details.startsWith("/from") || details.startsWith("/to")) {
            throw new NobException("Description should not be empty.\n"
                    + "Use: event DESCRIPTION /from START /to END\n"
                    + "(eg., event project meeting /from Mon 2pm /to 4pm)");
        }

        int fromIndex = details.indexOf(" /from ");
        int toIndex = details.indexOf(" /to ");
        if (fromIndex < 1 || toIndex <= fromIndex + " /from ".length()
                || toIndex + " /to ".length() == details.length()) {
            if (details.contains("/from") || details.contains("/to")) {
                throw new NobException("Check that there is a space before and after '/from' and '/to'.\n"
                        + "Use: event DESCRIPTION /from START /to END\n"
                        + "(eg., event project meeting /from Mon 2pm /to 4pm)");
            }
            throw new NobException("Use: event DESCRIPTION /from START /to END\n"
                    + "(eg., event project meeting /from Mon 2pm /to 4pm)");
        }

        String description = details.substring(0, fromIndex).trim();
        if (description.isEmpty()) {
            throw new NobException("Description should not be empty.\n"
                    + "Use: event DESCRIPTION /from START /to END\n"
                    + "(eg., event project meeting /from Mon 2pm /to 4pm)");
        }

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
    private static void markTask(String command, Task[] tasks, int taskCount, boolean isDone)
            throws NobException {
        String numberText = command.substring(command.indexOf(' ') + 1).trim();
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new NobException("Please enter a valid task number.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new NobException("Please enter a task number from 1 to " + taskCount + ".");
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
    }
}
