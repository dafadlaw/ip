/**
 * Represents a task with a description and completion status.
 */
public class Task {
    /** A short description of the task. */
    protected String description;

    /** Whether the task has been completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns an icon that represents this task's completion status.
     *
     * @return a tick for a completed task, or a space for an incomplete task
     */
    public String getStatusIcon() {
        return isDone ? "✓" : " ";
    }
}
