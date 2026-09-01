package nob.task;

import java.util.Locale;

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

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Returns whether this task's description contains the specified keyword, ignoring case.
     *
     * @param keyword Keyword to find in the task description.
     * @return Whether the task description contains the keyword.
     */
    public boolean hasKeyword(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns this task in the format used when displaying a task list.
     *
     * @return the task status icon and description
     */
    @Override
    public String toString() {
        return description + " [" + getStatusIcon() + "]";
    }
}
