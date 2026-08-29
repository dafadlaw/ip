package nob.task;

import java.time.LocalDateTime;

import nob.util.DateTimeUtil;

/**
 * Represents a task that should be completed by a specified time.
 */
public class Deadline extends Task {
    /** The parsed deadline date and time supplied by the user, when it can be parsed. */
    private final LocalDateTime byDateTime;

    /** Original deadline text used for display fallback when the value is not a recognised date/time. */
    private final String byText;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description text describing the task
     * @param by the date or time by which the task should be completed
     */
    public Deadline(String description, String by) {
        super(description);
        this.byDateTime = DateTimeUtil.parseDateTime(by).orElse(null);
        this.byText = by == null ? "" : by.trim();
    }

    /**
     * Creates an incomplete deadline task from an already parsed date-time.
     *
     * @param description text describing the task
     * @param by the parsed deadline value
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.byDateTime = by;
        this.byText = DateTimeUtil.formatDisplay(by);
    }

    /**
     * Returns this deadline in the format used when displaying a task list.
     *
     * @return the deadline, task description, and completion status
     */
    @Override
    public String toString() {
        return "[D: " + (byDateTime == null ? byText : DateTimeUtil.formatDisplay(byDateTime)) + "] " + super.toString();
    }
}
