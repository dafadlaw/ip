/**
 * Represents errors specific to the Nob chatbot application.
 */
public class NobException extends Exception {
    
    /**
     * Constructs a new NobException with the specified error message.
     *
     * @param message the detail message explaining the error
     */
    public NobException(String message) {
        super(message);
    }
}
