package nob.task;

import nob.exception.NobException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests task storage, retrieval, updates, and deletion in {@link TaskList}.
 */
public class TaskListTest {
    /**
     * Verifies that a task can be added while capacity remains.
     */
    @Test
    public void addTask_availableCapacity_taskAdded() {
        TaskList taskList = new TaskList(1);
        Task task = new Todo("read book");

        assertTrue(taskList.addTask(task));
        assertEquals(1, taskList.getTaskCount());
        assertSame(task, taskList.getTasks()[0]);
    }

    /**
     * Verifies that a task is not added when the list is full.
     */
    @Test
    public void addTask_fullList_taskNotAdded() {
        TaskList taskList = new TaskList(1);
        taskList.addTask(new Todo("first task"));

        assertFalse(taskList.addTask(new Todo("second task")));
        assertEquals(1, taskList.getTaskCount());
    }

    /**
     * Verifies that a task can be marked complete and then incomplete.
     */
    @Test
    public void markTask_validTaskNumber_updatesCompletionStatus() throws NobException {
        TaskList taskList = new TaskList(1);
        taskList.addTask(new Todo("read book"));

        assertEquals("✓", taskList.markTask(1, true).getStatusIcon());
        assertEquals(" ", taskList.markTask(1, false).getStatusIcon());
    }

    /**
     * Verifies that deleting a task shifts later tasks into the removed position.
     */
    @Test
    public void deleteTask_middleTask_laterTaskShiftsLeft() throws NobException {
        TaskList taskList = new TaskList(3);
        Task firstTask = new Todo("first task");
        Task removedTask = new Todo("second task");
        Task lastTask = new Todo("third task");
        taskList.addTask(firstTask);
        taskList.addTask(removedTask);
        taskList.addTask(lastTask);

        assertSame(removedTask, taskList.deleteTask(2));
        assertEquals(2, taskList.getTaskCount());
        assertSame(firstTask, taskList.getTask(1));
        assertSame(lastTask, taskList.getTask(2));
    }

    /**
     * Verifies that clearing a list removes its tasks and resets its count.
     */
    @Test
    public void clear_nonEmptyList_removesAllTasks() {
        TaskList taskList = new TaskList(2);
        taskList.addTask(new Todo("first task"));
        taskList.addTask(new Todo("second task"));

        taskList.clear();

        assertEquals(0, taskList.getTaskCount());
        assertNull(taskList.getTasks()[0]);
        assertNull(taskList.getTasks()[1]);
    }

    /**
     * Verifies that task numbers outside the stored range are rejected.
     */
    @Test
    public void getTask_invalidTaskNumbers_exceptionThrown() {
        TaskList taskList = new TaskList(1);

        NobException zeroException = assertThrows(NobException.class, () -> taskList.getTask(0));
        NobException tooLargeException = assertThrows(NobException.class, () -> taskList.getTask(1));

        assertEquals("Please enter a task number from 1 to 0.", zeroException.getMessage());
        assertEquals("Please enter a task number from 1 to 0.", tooLargeException.getMessage());
    }
}
