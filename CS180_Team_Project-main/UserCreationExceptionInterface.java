/**
 * UserCreationExceptionInterface
 *
 * This is the interface for UserCreationException. It is a custom Exception that
 * prints extra formatting in the error message from when there is an error creating a user object
 *
 * @author David Goldfuss, L06
 * @version November 8th, 2025
 */
public interface UserCreationExceptionInterface {

    /**
     * This message returns the error message along with additional fomatting.
     *
     * @return A String that is the method
     */
    public String getMessage();
}