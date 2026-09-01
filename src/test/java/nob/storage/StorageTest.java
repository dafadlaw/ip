package nob.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import nob.exception.NobException;
import nob.task.Deadline;
import nob.task.Event;
import nob.task.Task;
import nob.task.Todo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests persistence of Nob tasks through {@link Storage}.
 */
public class StorageTest {
    /**
     * Verifies that saved task types and completion statuses are restored accurately.
     */
    @Test
    public void saveTasks_thenLoadTasks_tasksRestored(@TempDir Path temporaryDirectory) throws NobException {
        Path dataFile = temporaryDirectory.resolve("data").resolve("nob.txt");
        Storage storage = new Storage(dataFile);
        Task completedTodo = new Todo("read book");
        completedTodo.markAsDone();
        Task[] savedTasks = {
            completedTodo,
            new Deadline("return book", "Friday"),
            new Event("team sync", "Mon 2pm", "4pm")
        };

        assertTrue(storage.saveTasks(savedTasks, savedTasks.length));

        Task[] loadedTasks = new Task[3];
        assertEquals(3, storage.loadTasks(loadedTasks));
        assertEquals("[T] read book [✓]", loadedTasks[0].toString());
        assertEquals("[D: Friday] return book [ ]", loadedTasks[1].toString());
        assertEquals("[E: Mon 2pm to 4pm] team sync [ ]", loadedTasks[2].toString());
    }

    /**
     * Verifies that loading from a file that does not exist returns no tasks.
     */
    @Test
    public void loadTasks_missingFile_noTasksLoaded(@TempDir Path temporaryDirectory) throws NobException {
        Storage storage = new Storage(temporaryDirectory.resolve("data").resolve("nob.txt"));

        assertEquals(0, storage.loadTasks(new Task[2]));
    }

    /**
     * Verifies that malformed saved records are skipped while valid records are loaded.
     */
    @Test
    public void loadTasks_malformedRecords_validRecordsLoaded(@TempDir Path temporaryDirectory)
            throws IOException, NobException {
        Path dataFile = temporaryDirectory.resolve("nob.txt");
        Files.writeString(dataFile, "not a task\n[T] [ ]\n[T] read book [ ]\n");
        Storage storage = new Storage(dataFile);
        Task[] loadedTasks = new Task[3];

        assertEquals(1, storage.loadTasks(loadedTasks));
        assertEquals("[T] read book [ ]", loadedTasks[0].toString());
    }
}
