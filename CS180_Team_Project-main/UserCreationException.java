/**
 * UserCreationException
 *
 * This class is an extension of Exception that prints a
 * custom error message when a user creation is invalid.
 *
 * @author David Goldfuss, L06
 * @version November 8th, 2025
 */
public class UserCreationException extends Exception implements UserCreationExceptionInterface {
    String message;  // The message that will be printed

    /**
     * This method constructs a new UserCreationException,
     * calls the parent method, and sets the message to the value passed in
     *
     * @param String The message to use as an error
     */
    public UserCreationException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * This method return the message as a string with a string
     * indicating there was an error creating the user
     *
     * @return A string that is the message
     */
    public String getMessage() {
        return "Error creating user: " + message;
    }

}
