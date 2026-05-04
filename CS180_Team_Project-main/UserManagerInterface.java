import java.util.ArrayList;
import java.math.BigDecimal;
/**
 * UserManagerInterface
 *
 * This interface is to be implemented by UserManager.
 * It contains the default functions for the UserManager,
 * all of which involve reading and writing users to the file
 * database, or making, deleting, and searching for them.
 * It also contains an ArrayList for storing all of the users
 * temporarily.
 *
 * @author David Goldfuss, L06
 * @version November 8th, 2025
 */
public interface UserManagerInterface {
    /**
     * This method reads in users and files and should put them in the userList
     */
    public void readUsers();

    /**
     * This method writes all of the users back out to the user file
     */
    public void writeUsers();

    /**
     * This method makes a new User object after ensuring that all of the parameters are valid.
     *
     * @param username The username of the user
     * @param admin If the user is an admin
     * @param password The password of the user
     * @param balance The balance of the user
     * @param seatList The list of all of the seats the user owns
     * @return The user object that was created
     */
    public User makeUser(String username,
                         boolean admin,
                         String password,
                         double balance,
                         ArrayList<Seat> seatList
    ) throws UserCreationException;

    /**
     * This method removes a user from the userList and deletes them from the usersData file
     *
     * @param user The user to be deleted
     */
    public void deleteUser(User user);

    /**
     * This method find and returns the user with the specified username.
     *
     * @param username The username of the user to be found
     * @return The user found
     */
    public User queryUsers(String username);

    /**
     * This method returns the userList that is a part of the class and holds all of the users
     *
     * @return The ArrayList of User object from the class
     */
    public ArrayList<User> getUserList();

    /**
     * Checks to see if the inpurt password matches a given user's password.
     *
     * @param user The user to check the password with
     * @param inPassword The password to check
     * @return A boolean that is if the passwords matched
     */
    public boolean validateUser(User user, String inPassword);
}
