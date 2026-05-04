/**
 * ServerInterface
 *
 * This interface is to be implemented by Server.
 * It contains the methods for Server that all involve
 * helper methods to help return data to the client. Most
 * methods are private in the server, but they are added
 * here with comments so that all the methods can be seen.
 *
 * @author David Goldfuss, Vaibhav Kanagala, L06
 * @version November 24th, 2025
 */
public interface ServerInterface {
    //public Server(String userFileName, Socket socket)
    //private boolean loginUser(String username, String password)
    //private boolean createUser(String username, String name, String password) throws UserCreationException
    //private void deleteAccount()
    //private double getUserBalance()
    //private void updatePassword(String newPassword)
    //private boolean updateUsername(String newUsername)
    //private void updateName(String name)
    //private ArrayList<String> listEventTimes()
    //private ArrayList<String> listEvents()
    //private ArrayList<Seat> listSeatsAtEvent(String eventName)
    //private boolean reserveSeat(String eventName, String seatID)
    //private boolean cancelSeat(String eventName, String seatStr)
    //private boolean reserveMultipleSeats(String eventName, String seatsString)
    //private boolean cancelMultipleSeats(String eventName, String seatsString)
    //private ArrayList<Seat> getSeatsByUser()
    //private boolean mainCommandMenu(BufferedReader in, PrintWriter out)
    void run();
    //public static void main(String[] args)
}
