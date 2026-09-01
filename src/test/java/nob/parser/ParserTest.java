package nob.parser;

import nob.exception.NobException;
import nob.task.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests conversion of user command text into Nob task objects and task numbers.
 */
public class ParserTest {
    /**
     * Verifies that a valid to-do command creates the expected task.
     */
    @Test
    public void parseTodo_validDescription_todoCreated() throws NobException {
        Task task = Parser.parseTodo("todo read book");

        assertEquals("[T] read book [ ]", task.toString());
    }

    /**
     * Verifies that a to-do command without a description is rejected.
     */
    @Test
    public void parseTodo_missingDescription_exceptionThrown() {
        NobException exception = assertThrows(NobException.class, () -> Parser.parseTodo("todo   "));

        assertEquals("Use: todo DESCRIPTION\n(eg., todo borrow book)", exception.getMessage());
    }

    /**
     * Verifies that a complete deadline command creates the expected task.
     */
    @Test
    public void parseDeadline_validDetails_deadlineCreated() throws NobException {
        Task task = Parser.parseDeadline("deadline return book /by Friday");

        assertEquals("[D: Friday] return book [ ]", task.toString());
    }

    /**
     * Verifies that a deadline command without a description is rejected.
     */
    @Test
    public void parseDeadline_missingDescription_exceptionThrown() {
        NobException exception = assertThrows(NobException.class,
                () -> Parser.parseDeadline("deadline /by Friday"));

        assertEquals("Description should not be empty.\n"
                + "Use: deadline DESCRIPTION /by DATE_OR_TIME\n"
                + "(eg., deadline return book /by Sunday)", exception.getMessage());
    }

    /**
     * Verifies that a deadline delimiter with missing surrounding spaces is rejected.
     */
    @Test
    public void parseDeadline_malformedDelimiter_exceptionThrown() {
        NobException exception = assertThrows(NobException.class,
                () -> Parser.parseDeadline("deadline return book /byFriday"));

        assertEquals("Check that there is a space before and after '/by'.\n"
                + "Use: deadline DESCRIPTION /by DATE_OR_TIME\n"
                + "(eg., deadline return book /by Sunday)", exception.getMessage());
    }

    /**
     * Verifies that a complete event command creates the expected task.
     */
    @Test
    public void parseEvent_validDetails_eventCreated() throws NobException {
        Task task = Parser.parseEvent("event team sync /from Mon 2pm /to 4pm");

        assertEquals("[E: Mon 2pm to 4pm] team sync [ ]", task.toString());
    }

    /**
     * Verifies that an event command without a description is rejected.
     */
    @Test
    public void parseEvent_missingDescription_exceptionThrown() {
        NobException exception = assertThrows(NobException.class,
                () -> Parser.parseEvent("event /from Mon 2pm /to 4pm"));

        assertEquals("Description should not be empty.\n"
                + "Use: event DESCRIPTION /from START /to END\n"
                + "(eg., event project meeting /from Mon 2pm /to 4pm)", exception.getMessage());
    }

    /**
     * Verifies that event delimiters with missing surrounding spaces are rejected.
     */
    @Test
    public void parseEvent_malformedDelimiter_exceptionThrown() {
        NobException exception = assertThrows(NobException.class,
                () -> Parser.parseEvent("event team sync /fromMon 2pm /to 4pm"));

        assertEquals("Check that there is a space before and after '/from' and '/to'.\n"
                + "Use: event DESCRIPTION /from START /to END\n"
                + "(eg., event project meeting /from Mon 2pm /to 4pm)", exception.getMessage());
    }

    /**
     * Verifies that task numbers are parsed even when surrounded by extra spaces.
     */
    @Test
    public void parseTaskNumber_numberWithExtraSpaces_numberReturned() throws NobException {
        assertEquals(12, Parser.parseTaskNumber("mark   12  "));
    }

    /**
     * Verifies that a non-numeric task number is rejected.
     */
    @Test
    public void parseTaskNumber_nonNumericValue_exceptionThrown() {
        NobException exception = assertThrows(NobException.class, () -> Parser.parseTaskNumber("delete two"));

        assertEquals("Please enter a valid task number.", exception.getMessage());
    }
}
