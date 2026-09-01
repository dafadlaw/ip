package nob;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import nob.command.ClearCommand;
import nob.command.Command;
import nob.command.ExitCommand;
import nob.command.HelpCommand;
import nob.command.ListCommand;
import nob.exception.NobException;
import nob.parser.Parser;
import nob.storage.Storage;
import nob.task.Task;
import nob.task.TaskList;
import nob.ui.Ui;

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
                        Command exitCommand = new ExitCommand();
                        exitCommand.execute(tasks, ui, storage);
                        ui.showDivider();
                        break;
                    }

                    if (command.equals("help")) {
                        Command helpCommand = new HelpCommand();
                        helpCommand.execute(tasks, ui, storage);
                    } else if (command.equals("clear")) {
                        Command clearCommand = new ClearCommand();
                        clearCommand.execute(tasks, ui, storage);
                    } else if (command.equals("list")) {
                        Command listCommand = new ListCommand();
                        listCommand.execute(tasks, ui, storage);
                    } else if (command.equals("find") || command.startsWith("find ")) {
                        findTasks(command, tasks, ui);
                    } else if (command.startsWith("mark ")) {
                        markTask(command, tasks, true, storage, ui);
                    } else if (command.startsWith("unmark ")) {
                        markTask(command, tasks, false, storage, ui);
                    } else if (command.startsWith("delete ")) {
                        deleteTask(command, tasks, storage, ui);
                    } else if (command.equals("todo") || command.startsWith("todo ")) {
                        addTask(Parser.parseTodo(command), tasks, storage, ui);
                    } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                        addTask(Parser.parseDeadline(command), tasks, storage, ui);
                    } else if (command.equals("event") || command.startsWith("event ")) {
                        addTask(Parser.parseEvent(command), tasks, storage, ui);
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
                    ui.showError(exception.getMessage());
                }
                ui.showDivider();
                System.out.println();
            }
        } catch (IOException exception) {
            ui.showInputError();
            exception.printStackTrace();
        }
    }

    /**
     * Stores a task and reports the result to the user when storage is available.
     *
     * @param task the task to store
     * @param tasks the user's task list
     * @param storage persistent task storage
     */
    private static void addTask(Task task, TaskList tasks, Storage storage, Ui ui) {
        if (!tasks.addTask(task)) {
            ui.showTaskLimit(MAX_TASKS);
            return;
        }

        saveTasks(storage, tasks, ui);
        ui.showTaskAdded(task, tasks.getTaskCount());
    }

    /**
     * Marks a task as complete or incomplete using its position in the list.
     *
     * @param command the mark or unmark command entered by the user
     * @param tasks the user's task list
     * @param isDone whether the task should be marked as completed
     * @param storage persistent task storage
     */
    private static void markTask(String command, TaskList tasks, boolean isDone, Storage storage, Ui ui)
            throws NobException {
        Task task = tasks.markTask(Parser.parseTaskNumber(command), isDone);
        saveTasks(storage, tasks, ui);
        ui.showTaskMarked(task, isDone);
    }

    /**
     * Deletes a task from the list by its position.
     *
     * @param command the delete command entered by the user
     * @param tasks the user's task list
     * @param storage persistent task storage
     * @throws NobException if the task index is invalid
     */
    private static void deleteTask(String command, TaskList tasks, Storage storage, Ui ui)
            throws NobException {
        Task removedTask = tasks.deleteTask(Parser.parseTaskNumber(command));
        saveTasks(storage, tasks, ui);
        ui.showTaskDeleted(removedTask, tasks.getTaskCount());
    }

    /**
     * Finds and displays tasks whose descriptions contain the requested keyword.
     *
     * @param command The find command entered by the user.
     * @param tasks The user's task list.
     * @param ui The console user interface.
     * @throws NobException If the keyword is missing.
     */
    private static void findTasks(String command, TaskList tasks, Ui ui) throws NobException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new NobException("Use: find KEYWORD\n(eg., find book)");
        }
        ui.showMatchingTasks(tasks, keyword);
    }

    /** Saves tasks and reports if the file could not be updated. */
    private static void saveTasks(Storage storage, TaskList tasks, Ui ui) {
        if (!storage.saveTasks(tasks.getTasks(), tasks.getTaskCount())) {
            ui.showSavingError();
        }
    }
}
