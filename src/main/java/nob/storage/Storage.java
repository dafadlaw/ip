package nob.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import nob.exception.NobException;
import nob.task.Deadline;
import nob.task.Event;
import nob.task.Task;
import nob.task.Todo;

/**
 * Loads tasks from, and saves tasks to, Nob's data file.
 */
public class Storage {
    /** The file used to persist the current task list. */
    private final Path filePath;

    /**
     * Creates storage that reads from and writes to the specified file.
     *
     * @param filePath the task data file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads valid task records into the supplied task array.
     *
     * @param tasks the array that receives the loaded tasks
     * @return the number of tasks loaded
     * @throws NobException if the task file cannot be read
     */
    public int loadTasks(Task[] tasks) throws NobException {
        if (!Files.exists(filePath)) {
            return 0;
        }

        int taskCount = 0;
        try {
            List<String> savedTasks = Files.readAllLines(filePath);
            for (String savedTask : savedTasks) {
                if (taskCount == tasks.length) {
                    break;
                }

                Task task = parseTask(savedTask);
                if (task != null) {
                    tasks[taskCount] = task;
                    taskCount++;
                }
            }
        } catch (IOException exception) {
            throw new NobException("I couldn't load the saved tasks, so I'm starting with an empty list.");
        }
        return taskCount;
    }

    /**
     * Saves all valid tasks in the supplied array to the task data file.
     *
     * @param tasks the array containing tasks to save
     * @param taskCount the number of tasks to save from the array
     * @return {@code true} if the task file was written successfully
     */
    public boolean saveTasks(Task[] tasks, int taskCount) {
        StringBuilder fileContents = new StringBuilder();
        for (int index = 0; index < taskCount; index++) {
            fileContents.append(tasks[index]).append(System.lineSeparator());
        }

        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, fileContents.toString());
        } catch (IOException exception) {
            return false;
        }
        return true;
    }

    /** Parses one task line written by {@link #saveTasks(Task[], int)}. */
    private Task parseTask(String savedTask) {
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
}
