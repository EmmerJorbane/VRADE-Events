import java.util.ArrayList;

/**
 * UserInterface
 *
 * This interface is to be implemented by User.
 * It contains the methods for User that all involve
 * getting/setting values for the fields that pertain
 * to a user, as well as using those fields to modify
 * seat assignments and manage balance.
 *
 * @author Arnav Nayak
 * @version November 10th, 2025
 */
public interface UserInterface {
    String getUsername(); //Returns username pertaining to user
    String getPassword(); //Returns password pertaining to user
    double getBalance(); //Returns balance pertaining to user
    String getName(); //Returns name pertaining to user
    ArrayList<Seat> getSeatList(); //Returns list of seats pertaining to user

    void setUsername(String username); //Updates username pertaining to user
    void setPassword(String password); //Updates password pertaining to user
    void setBalance(double balance); //Updates balance pertaining to user
    void setName(String name); //Updates name pertaining to user
    void setSeatList(ArrayList<Seat> seatList); //Updates list of seats pertaining to user

    void subtractBalance(double cost); //Subtracts cost from user balance
    void addBalance(double refund); //Adds refund to user balance
    void addSeat(Seat seat); //Adds seat to user's seat list
    void removeSeat(Seat seat); //Removes seat from user's seat list
}
