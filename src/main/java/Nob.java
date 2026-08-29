import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Starts the Nob chatbot application.
 */
public class Nob {
    /** The maximum number of tasks Nob can keep during one run. */
    private static final int MAX_TASKS = 100;

    /** The file used to store the current task list. */
    private static final Path DATA_FILE = Path.of("data", "nob.txt");

    /** The divider used around each app response block. */
    private static final String DIVIDER = "____________________________________________________________";

    /**
     * Reads and responds to commands until the user enters {@code bye}.
     *
     * @param args command-line arguments, which this application does not use
     */
    public static void main(String[] args) {
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = loadTasks(tasks);
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

        System.out.println(DIVIDER);
        System.out.print(banner);
        System.out.print(greeting);
        System.out.println(DIVIDER);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            while (true) {
                String command = reader.readLine();
                if (command == null) {
                    command = "";
                }
                System.out.println();
                System.out.println(DIVIDER);

                try {
                    if (command.equals("bye")) {
                        System.out.println(farewell);
                        System.out.println(DIVIDER);
                        break;
                    }

                    if (command.equals("list")) {
                        printTasks(tasks, taskCount);
                    } else if (command.startsWith("mark ")) {
                        markTask(command, tasks, taskCount, true);
                    } else if (command.startsWith("unmark ")) {
                        markTask(command, tasks, taskCount, false);
                    } else if (command.startsWith("delete ")) {
                        taskCount = deleteTask(command, tasks, taskCount);
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
                System.out.println(DIVIDER);
                System.out.println();
            }
        } catch (IOException exception) {
            System.out.println("I couldn't read your input.");
            exception.printStackTrace();
        }
    }

    /** Loads tasks saved by a previous run of Nob. */
    private static int loadTasks(Task[] tasks) {
        if (!Files.exists(DATA_FILE)) {
            return 0;
        }

        int taskCount = 0;
        try {
            List<String> savedTasks = Files.readAllLines(DATA_FILE);
            for (String savedTask : savedTasks) {
                if (taskCount == MAX_TASKS) {
                    break;
                }

                Task task = parseTask(savedTask);
                if (task != null) {
                    tasks[taskCount] = task;
                    taskCount++;
                }
            }
        } catch (IOException exception) {
            System.out.println("I couldn't load the saved tasks, so I'm starting with an empty list.");
            return 0;
        }
        return taskCount;
    }

    /** Parses one task line written by {@link #saveTasks(Task[], int)}. */
    private static Task parseTask(String savedTask) {
        if (savedTask.length() < 7 || !(savedTask.endsWith("[ ]") || savedTask.endsWith("[✓]"))) {
            return null;
        }

        boolean isDone = savedTask.endsWith("[✓]");
        String taskText = savedTask.substring(0, savedTask.length() - 4).trim();
        if (taskText.length() < 4) {
            return null;
        }
        Task task;
        if (taskText.startsWith("[T] ")) {
            String description = taskText.substring(4).trim();
            if (description.isEmpty()) {
                return null;
            }
            task = new Todo(description);
        } else if (taskText.startsWith("[D: ")) {
            int detailsEnd = taskText.lastIndexOf("] ");
            if (detailsEnd < 5) {
                return null;
            }
            String by = taskText.substring(4, detailsEnd);
            String description = taskText.substring(detailsEnd + 2);
            if (by.trim().isEmpty() || description.trim().isEmpty()) {
                return null;
            }
            task = new Deadline(description, by);
        } else if (taskText.startsWith("[E: ")) {
            int detailsEnd = taskText.lastIndexOf("] ");
            int separator = taskText.lastIndexOf(" to ", detailsEnd);
            if (detailsEnd < 5 || separator < 5) {
                return null;
            }
            String from = taskText.substring(4, separator);
            String to = taskText.substring(separator + 4, detailsEnd);
            String description = taskText.substring(detailsEnd + 2);
            if (from.trim().isEmpty() || to.trim().isEmpty() || description.trim().isEmpty()) {
                return null;
            }
            task = new Event(description, from, to);
        } else {
            return null;
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /** Writes the current task list to disk. */
    private static void saveTasks(Task[] tasks, int taskCount) {
        StringBuilder fileContents = new StringBuilder();
        for (int index = 0; index < taskCount; index++) {
            fileContents.append(tasks[index]).append(System.lineSeparator());
        }

        try {
            Files.createDirectories(DATA_FILE.getParent());
            Files.writeString(DATA_FILE, fileContents.toString());
        } catch (IOException exception) {
            System.out.println("I couldn't save the task list to disk. Your changes are kept for this run.");
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
            System.out.println("Your task list is empty right now. Add a task using the 'todo', 'deadline' or 'event' commands.");
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
        if (by.isEmpty()) {
            throw new NobException("Deadline time should not be empty.\n"
                + "Use: deadline DESCRIPTION /by DATE_OR_TIME");
        }
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
        if (from.isEmpty() || to.isEmpty()) {
            throw new NobException("Event times should not be empty.\n"
                + "Use: event DESCRIPTION /from START /to END");
        }
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
        saveTasks(tasks, taskCount + 1);
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
        saveTasks(tasks, taskCount);
        System.out.println("  " + task);
    }

    /**
     * Deletes a task from the list by its position.
     *
     * @param command the delete command entered by the user
     * @param tasks tasks stored by the user
     * @param taskCount number of valid tasks in the array
     * @return the updated number of valid tasks
     * @throws NobException if the task index is invalid
     */
    private static int deleteTask(String command, Task[] tasks, int taskCount) throws NobException {
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

        Task removedTask = tasks[taskNumber - 1];
        for (int index = taskNumber - 1; index < taskCount - 1; index++) {
            tasks[index] = tasks[index + 1];
        }
        tasks[taskCount - 1] = null;
        saveTasks(tasks, taskCount - 1);

        System.out.println("Noted. I've removed the task:");
        System.out.println("  " + removedTask);
        System.out.println("Now you have " + (taskCount - 1) + " tasks left in the list.");
        return taskCount - 1;
    }
}
