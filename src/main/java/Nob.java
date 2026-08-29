import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * Starts the Nob chatbot application.
 */
public class Nob {
    /** The maximum number of tasks Nob can keep during one run. */
    private static final int MAX_TASKS = 100;

    /** The file used to store the current task list. */
    private static final Path DATA_FILE = Path.of("data", "nob.txt");

    /**
     * Reads and responds to commands until the user enters {@code bye}.
     *
     * @param args command-line arguments, which this application does not use
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(DATA_FILE);
        Task[] loadedTasks = new Task[MAX_TASKS];
        int taskCount;
        try {
            taskCount = storage.loadTasks(loadedTasks);
        } catch (NobException exception) {
            ui.showLoadingError();
            taskCount = 0;
        }
        TaskList tasks = new TaskList(loadedTasks, taskCount);
        ui.showWelcome();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            while (true) {
                String command = reader.readLine();
                if (command == null) {
                    command = "";
                }
                System.out.println();
                ui.showDivider();

                try {
                    if (command.equals("bye")) {
                        ui.showGoodbye();
                        ui.showDivider();
                        break;
                    }

                    if (command.equals("help")) {
                        ui.showHelp();
                    } else if (command.equals("clear")) {
                        clearTasks(tasks, storage);
                    } else if (command.equals("list")) {
                        printTasks(tasks);
                    } else if (command.startsWith("mark ")) {
                        markTask(command, tasks, true, storage);
                    } else if (command.startsWith("unmark ")) {
                        markTask(command, tasks, false, storage);
                    } else if (command.startsWith("delete ")) {
                        deleteTask(command, tasks, storage);
                    } else if (command.equals("todo") || command.startsWith("todo ")) {
                        addTask(Parser.parseTodo(command), tasks, storage);
                    } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                        addTask(Parser.parseDeadline(command), tasks, storage);
                    } else if (command.equals("event") || command.startsWith("event ")) {
                        addTask(Parser.parseEvent(command), tasks, storage);
                    } else if (command.startsWith("todo")) {
                        ui.showMissingSpaceHint("todo", command);
                    } else if (command.startsWith("deadline")) {
                        ui.showMissingSpaceHint("deadline", command);
                    } else if (command.startsWith("event")) {
                        ui.showMissingSpaceHint("event", command);
                    } else {
                        ui.showUnknownCommandHint();
                    }
                } catch (NobException exception) {
                    System.out.println(exception.getMessage());
                }
                ui.showDivider();
                System.out.println();
            }
        } catch (IOException exception) {
            System.out.println("I couldn't read your input.");
            exception.printStackTrace();
        }
    }

    /** Clears every task in the current list and writes the empty list to disk. */
    private static void clearTasks(TaskList tasks, Storage storage) {
        tasks.clear();
        saveTasks(storage, tasks);
        System.out.println("Noted. I've cleared the entire task list.");
    }

    /**
     * Displays every task currently stored in the task list.
     *
     * @param tasks the user's task list
     */
    private static void printTasks(TaskList tasks) throws NobException {
        if (tasks.getTaskCount() == 0) {
            System.out.println("Your task list is empty right now. Add a task using the 'todo', 'deadline' or 'event' commands.");
            return;
        }

        System.out.println("Here are the tasks in your list:");
        for (int index = 1; index <= tasks.getTaskCount(); index++) {
            System.out.println(index + "." + tasks.getTask(index));
        }
    }

    /**
     * Stores a task and reports the result to the user when storage is available.
     *
     * @param task the task to store
     * @param tasks the user's task list
     * @param storage persistent task storage
     */
    private static void addTask(Task task, TaskList tasks, Storage storage) {
        if (!tasks.addTask(task)) {
            System.out.println("I can only store up to " + MAX_TASKS + " tasks.");
            return;
        }

        saveTasks(storage, tasks);
        System.out.println("Alrighty. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + tasks.getTaskCount() + " tasks in the list.");
    }

    /**
     * Marks a task as complete or incomplete using its position in the list.
     *
     * @param command the mark or unmark command entered by the user
     * @param tasks the user's task list
     * @param isDone whether the task should be marked as completed
     * @param storage persistent task storage
     */
    private static void markTask(String command, TaskList tasks, boolean isDone, Storage storage)
            throws NobException {
        Task task = tasks.markTask(Parser.parseTaskNumber(command), isDone);
        if (isDone) {
            System.out.println("LET'S GOOO! I've marked this task as done:");
        } else {
            System.out.println("Okay... I've marked this task as not done yet:");
        }
        saveTasks(storage, tasks);
        System.out.println("  " + task);
    }

    /**
     * Deletes a task from the list by its position.
     *
     * @param command the delete command entered by the user
     * @param tasks the user's task list
     * @param storage persistent task storage
     * @throws NobException if the task index is invalid
     */
    private static void deleteTask(String command, TaskList tasks, Storage storage)
            throws NobException {
        Task removedTask = tasks.deleteTask(Parser.parseTaskNumber(command));
        saveTasks(storage, tasks);

        System.out.println("Noted. I've removed the task:");
        System.out.println("  " + removedTask);
        System.out.println("Now you have " + tasks.getTaskCount() + " tasks left in the list.");
    }

    /** Saves tasks and reports if the file could not be updated. */
    private static void saveTasks(Storage storage, TaskList tasks) {
        if (!storage.saveTasks(tasks.getTasks(), tasks.getTaskCount())) {
            System.out.println("I couldn't save the task list to disk. Your changes are kept for this run.");
        }
    }
}
