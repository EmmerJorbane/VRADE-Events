
/**
 * EventInterface
 *
 * This interface allows any type to act as an Event Manager,
 * essentially allowing them to alter Events (admin only)
 *
 * @author Ryan Poplar, L06
 * @version December 7, 2025
 */

public interface EventInterface {
    String getEventName(); //returns the eventName instance variable
    void setEventName(String eventName); //sets a new eventName for the event
    String getTheatreType(); //returns the theatreType instance variable
    void setTheatreType(String theatreType); //sets a new theatre type if the parameter is "S" "M" or "L"
    Seat[][] getSeats(); //returns the seats 2d array instance variable
    Seat getSeat(char row, int col); //returns the seat in the 2D array based on the row and column 
    // inputted by the user
    void setSeats(Seat[][] seats); //sets the seats in the theatre based on a new 2d array
    void createSeats(String theatreType); //creates a 2D array of seats based on the size of the theatre
    boolean reserveSingleSeat(User u, String rowNum); //reserves a single seat by setting the user of the specific
    // seat to the user that is provided as a parameter
    boolean cancelSingleSeat(User u, String rowNum); //cancels a single seat that is owned by the user based on the
    // seat number given as a parameter
    boolean reserveMultipleSeats(User u, String rowNum); //reserves a set of multiple seats for a user
    // based on the seats
    // given by a string parameter
    boolean cancelMultipleSeats(User u, String rowNum); //cancels multiple seats specified by the string parameter
    // if they are owned by the user parameter
    int getYear(); //returns the year that the event will be taking place
    int getMonth(); //returns the month that the event will be taking place
    int getDay(); //returns the day that the event will be taking place
    int getMinute(); //returns the minute that the event will be taking place
    int getHour(); //returns the hour that the event will be taking place
    String getFullTime(); //returns the full time that the event will be taking place in a string
    boolean setFullTime(String fullTime); //sets a new time that the event will be taking place based on
    // a ISO8601 string parameter
    String toString(); //returns the eventName, theatreType, and fullTime in a formatted String
    //void setMultiplier(); //updates multiplier and applies to the seats inside an event
    void setCapacityMultiplier(); //updates capacityMultiplier and applies to the seats inside an eent.
}
