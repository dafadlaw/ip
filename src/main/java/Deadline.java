/**
 * Represents a task that should be completed by a specified time.
 */
public class Deadline extends Task {
    /** The deadline text supplied by the user. */
    private final String by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description text describing the task
     * @param by the date or time by which the task should be completed
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns this deadline in the format used when displaying a task list.
     *
     * @return the deadline, task description, and completion status
     */
    @Override
    public String toString() {
        return "[D: " + by + "] " + super.toString();
    }
}
