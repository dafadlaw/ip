package nob.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the state changes and display behavior of {@link Task}.
 */
public class TaskTest {
    /**
     * Verifies that a newly created task is displayed as incomplete.
     */
    @Test
    public void getStatusIcon_newTask_spaceReturned() {
        Task task = new Task("read book");

        assertEquals(" ", task.getStatusIcon());
    }

    /**
     * Verifies that marking an incomplete task as done changes its status icon.
     */
    @Test
    public void markAsDone_incompleteTask_statusIconBecomesTick() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("✓", task.getStatusIcon());
    }

    /**
     * Verifies that unmarking a completed task changes its status icon back to a space.
     */
    @Test
    public void markAsUndone_completedTask_statusIconBecomesSpace() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsUndone();

        assertEquals(" ", task.getStatusIcon());
    }

    /**
     * Verifies that an incomplete task includes its description and blank status icon when displayed.
     */
    @Test
    public void toString_newTask_descriptionAndBlankStatusReturned() {
        Task task = new Task("read book");

        assertEquals("read book [ ]", task.toString());
    }

    /**
     * Verifies that a completed task includes a tick in its display format.
     */
    @Test
    public void toString_completedTask_descriptionAndTickReturned() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("read book [✓]", task.toString());
    }

    /**
     * Verifies that keyword matching ignores case and matches part of a task description.
     */
    @Test
    public void hasKeyword_mixedCasePartialKeyword_keywordFound() {
        Task task = new Task("Read the library book");

        assertTrue(task.hasKeyword("BOOK"));
        assertFalse(task.hasKeyword("report"));
    }
}
