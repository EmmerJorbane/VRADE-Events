/**
 * SeatInterface
 *
 * This interface is to be implemented by Seat.
 * It contains the methods for Seat that all involve
 * getting/setting values for the fields that pertain
 * to a seat, as well as using those fields to update
 * other fields.
 *
 * @author Vaibhav Kanagala, L06
 * @version November 10th, 2025
 */
public interface SeatInterface {
    char getRow(); // Returns row pertaining to seat
    int getNum(); // Returns number pertaining to seat
    String getSeat(); //Returns seat classification pertaining to seat
    boolean getOpen(); //Returns the open status pertaining to seat
    double getValue(); //Returns value pertaining to seat
    User getUser(); //Returns the user pertaining to seat
    String getSize(); //Returns the size pertaining to event of seat
    String getEvent(); //Returns the event pertaining of seat
    String getUsername(); //Returns username of user pertaining to seat
    void setRow(char row); //Updates row, seat classification, and value
    void setNum(int num); //Updates number, seat classification, and value
    void setUser(User user); //Updates user and open status
    void setEvent(String event); //Updates event
    void setUsername(String username); //Updates username
    void multiplyValue(double multiplier); //Multiples value by multiplier
    String toString();  // Converts the seat data to a readable string
    String toDataString();  // Returns a machine readable string for network use
}