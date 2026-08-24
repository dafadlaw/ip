/**
 * Represents a task that takes place between a start and end time.
 */
public class Event extends Task {
    /** The start date or time supplied by the user. */
    private final String from;

    /** The end date or time supplied by the user. */
    private final String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description text describing the event
     * @param from the event start date or time
     * @param to the event end date or time
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns this event in the format used when displaying a task list.
     *
     * @return the event time frame, description, and completion status
     */
    @Override
    public String toString() {
        return "[E: " + from + " to " + to + "] " + super.toString();
    }
}
