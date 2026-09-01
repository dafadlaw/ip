package nob.parser;

import nob.exception.NobException;
import nob.task.Deadline;
import nob.task.Event;
import nob.task.Task;
import nob.task.Todo;

/**
 * Interprets command arguments and creates the corresponding task objects.
 */
public class Parser {
    /**
     * Parses a {@code todo DESCRIPTION} command into a to-do task.
     *
     * @param command The complete command entered by the user.
     * @return The to-do task described by the command.
     * @throws NobException If the description is missing.
     */
    public static Task parseTodo(String command) throws NobException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new NobException("Use: todo DESCRIPTION\n(eg., todo borrow book)");
        }
        return new Todo(description);
    }

    /**
     * Parses a {@code deadline DESCRIPTION /by TIME} command into a deadline task.
     *
     * @param command The complete command entered by the user.
     * @return The deadline task described by the command.
     * @throws NobException If the command syntax is invalid.
     */
    public static Task parseDeadline(String command) throws NobException {
        String details = command.substring("deadline".length()).trim();
        if (details.startsWith("/by")) {
            throw new NobException("Description should not be empty.\n"
                    + "Use: deadline DESCRIPTION /by DATE_OR_TIME\n"
                    + "(eg., deadline return book /by Sunday)");
        }

        int byIndex = details.indexOf(" /by ");
        if (byIndex < 1 || byIndex + " /by ".length() == details.length()) {
            if (details.contains("/by")) {
                throw new NobException("Check that there is a space before and after '/by'.\n"
                        + "Use: deadline DESCRIPTION /by DATE_OR_TIME\n"
                        + "(eg., deadline return book /by Sunday)");
            }
            throw new NobException("Use: deadline DESCRIPTION /by DATE_OR_TIME\n"
                    + "(eg., deadline return book /by Sunday)");
        }

        String description = details.substring(0, byIndex).trim();
        if (description.isEmpty()) {
            throw new NobException("Description should not be empty.\n"
                    + "Use: deadline DESCRIPTION /by DATE_OR_TIME\n"
                    + "(eg., deadline return book /by Sunday)");
        }

        String by = details.substring(byIndex + " /by ".length()).trim();
        if (by.isEmpty()) {
            throw new NobException("Deadline time should not be empty.\n"
                    + "Use: deadline DESCRIPTION /by DATE_OR_TIME");
        }
        return new Deadline(description, by);
    }

    /**
     * Parses an {@code event DESCRIPTION /from START /to END} command into an event task.
     *
     * @param command The complete command entered by the user.
     * @return The event task described by the command.
     * @throws NobException If the command syntax is invalid.
     */
    public static Task parseEvent(String command) throws NobException {
        String details = command.substring("event".length()).trim();
        if (details.startsWith("/from") || details.startsWith("/to")) {
            throw new NobException("Description should not be empty.\n"
                    + "Use: event DESCRIPTION /from START /to END\n"
                    + "(eg., event project meeting /from Mon 2pm /to 4pm)");
        }

        int fromIndex = details.indexOf(" /from ");
        int toIndex = details.indexOf(" /to ");
        if (fromIndex < 1 || toIndex <= fromIndex + " /from ".length()
                || toIndex + " /to ".length() == details.length()) {
            if (details.contains("/from") || details.contains("/to")) {
                throw new NobException("Check that there is a space before and after '/from' and '/to'.\n"
                        + "Use: event DESCRIPTION /from START /to END\n"
                        + "(eg., event project meeting /from Mon 2pm /to 4pm)");
            }
            throw new NobException("Use: event DESCRIPTION /from START /to END\n"
                    + "(eg., event project meeting /from Mon 2pm /to 4pm)");
        }

        String description = details.substring(0, fromIndex).trim();
        if (description.isEmpty()) {
            throw new NobException("Description should not be empty.\n"
                    + "Use: event DESCRIPTION /from START /to END\n"
                    + "(eg., event project meeting /from Mon 2pm /to 4pm)");
        }

        String from = details.substring(fromIndex + " /from ".length(), toIndex).trim();
        String to = details.substring(toIndex + " /to ".length()).trim();
        if (from.isEmpty() || to.isEmpty()) {
            throw new NobException("Event times should not be empty.\n"
                    + "Use: event DESCRIPTION /from START /to END");
        }
        return new Event(description, from, to);
    }

    /**
     * Parses the task number from a mark, unmark, or delete command.
     *
     * @param command The complete command entered by the user.
     * @return The task number.
     * @throws NobException If the task number is not an integer.
     */
    public static int parseTaskNumber(String command) throws NobException {
        String numberText = command.substring(command.indexOf(' ') + 1).trim();
        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new NobException("Please enter a valid task number.");
        }
    }
}
