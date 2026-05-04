/**
 * Seat.java
 *
 * This class is the Seat object that represents a seat in a theater. The class
 * stores the seat row, num, its open status, the seat id, the value, the user occupying the seat,
 * the size of the theater, the name of the event, and the username of the user occupying the seat. The
 * class contains setters and getters for all the instance variables along with methods calculating the
 value of the seat, and calling EventManager methods.
 *
 * @author Vaibhav Kanagala, L06
 * @version November 10th, 2025
 */
public class Seat implements SeatInterface {
    private char row;
    private int num;
    private boolean open;
    private String seat;
    private double value;

    private User user;
    private String size;
    private String event;
    private String username;
    private EventManager eventManager;



    /**
     * This is the no parameter constructor that assigns defualt values to the seat.
     */
    public Seat() {
        row = 'A';
        num = 1;
        open = true;
        seat = "A1";
        value = 10.0;
        user = null;
        size = "S";
        event = "";
        username = "";
        eventManager = null;
    }

    /**
     * These are parameter constructor for the class. It creates a new instance of an event based on
     * the information about the seat given. The constructor then assigns the parameters
     * and calls the setValue method to calculate teh value of teh seat depending on the values given.
     */
    public Seat(char row, int num, User user, String size, String event, String username, EventManager eventManager) {
        this.row = row;
        this.num = num;
        this.seat = "" + row + num;
        this.user = user;
        if (this.user != null) {
            this.open = false;
        } else {
            this.open = true;
        }
        this.size = size;
        this.event = event;
        this.username = username;
        this.eventManager = eventManager;
        setValue();
    }

    /**
     * This returns the row pertaining to a seat
     */
    public char getRow() {
        return row;
    }

    /**
     * This returns the seat number pertaining to a seat
     */
    public int getNum() {
        return num;
    }

    /**
     * This returns the open status pertaining to a seat
     */
    public boolean getOpen() {
        return open;
    }

    /**
     * This returns the seat id pertaining to a seat
     */
    public String getSeat() {
        return seat;
    }

    /**
     * This returns the value pertaining to a seat
     */
    public double getValue() {
        return value;
    }

    /**
     * This returns the user pertaining to a seat
     */
    public User getUser() {
        return user;
    }

    /**
     * This returns the size pertaining to a theatre holding the seat
     */
    public String getSize() {
        return size;
    }

    /**
     * This returns the event pertaining to a theatre holding the seat
     */
    public String getEvent() {
        return event;
    }

    /**
     * This returns the username pertaining to user occupying a seat
     */
    public String getUsername() {
        return username;
    }

    /**
     * This changes the value of the row pertaining to the seat, as well as updates the
     * values dependant on the row such as the seat id and value. It also calls the updateSeats
     * method to call the saveSeats method in EventManager
     */
    public void setRow(char row) {
        this.row = row;
        this.seat = "" + row + num;
        setValue();
        updateSeats();
    }

    /**
     * This changes the value of the num pertaining to the seat, as well as updates the
     * values dependant on the row such as the seat id and value. It also calls the updateSeats
     * method to call the saveSeats method in EventManager
     */
    public void setNum(int num) {
        this.num = num;
        this.seat = "" + row + num;
        setValue();
        updateSeats();
    }


    /**
     * This changes the value of the user pertaining to the seat, as well as
     * updates the values dependant on the user like the open status by setting
     the seat to open if there is no user occupying it. It also calls the updateSeats
     * method to call the saveSeats method in EventManager
     */
    public void setUser(User user) {
        this.user = user;
        if (this.user != null) {
            this.open = false;
        } else {
            this.open = true;
        }
        updateSeats();
    }

    /**
     * This changes the value of the event pertaining to the
     * theatre holding the seat. It also calls the updateSeats
     * method to call the saveSeats method in EventManager
     */
    public void setEvent(String event) {
        this.event = event;
        updateSeats();
    }

    /**
     * This changes the value of the username pertaining to the
     * User occupying the seat. It also calls the updateSeats
     * method to call the saveSeats method in EventManager
     */
    public void setUsername(String username) {
        this.username = username;
        updateSeats();
    }

    /**
     * This calls the saveSeats method in EventManager
     */
    private void updateSeats() {
        if (eventManager != null) {
            eventManager.saveSeats();
        }
    }

    /**
     * this method calculated the value of each seat. It takes
     * in the size of the theatre holding the seat to calculate how
     * many seats the theatre has, then it takes in the row/num of the
     * seat to calculate the value of the seat depending on the location
     * of the seat in the theatre. Any given seat can be worth $10, $12,
     * or $14 depending on the location of the seat.
     */
    private void setValue() {
        if (size.equals("S")) {
            if (row >= 'E' && row <= 'H') {
                value = 7;
            } else {
                value = 5;
            }

            if (num >= 5 && num <= 8) {
                value += 7;
            } else {
                value += 5;
            }
        } else if (size.equals("M")) {
            if (row >= 'G' && row <= 'L') {
                value = 7;
            } else {
                value = 5;
            }

            if (num >= 7 && num <= 12) {
                value += 7;
            } else {
                value += 5;
            }
        } else if (size.equals("L")) {
            if (row >= 'I' && row <= 'P') {
                value = 7;
            } else {
                value = 5;
            }

            if (num >= 9 && num <= 16) {
                value += 7;
            } else {
                value += 5;
            }
        }


    }


    /**
     * Updates the value of a seat based on a multipler that's dependant on the
     * time of day. The multiplier logic is as goes: x1.00 before 2pm, x1.25
     * from 2pm to 6pm, and x1.50 6pm and later.
     */
    public void multiplyValue(double multiplier) {
        value = value * multiplier;
    }

    /**
     * This method converts the raw data of the Seat to a string and returns it.
     *
     * @return A String values that shows seat data
     */
    public String toString() {
        String output = "Seat - ";
        output += String.format("movie: %s, reserved by: %s, row: %c, col: %d, cost: %.2f",
                this.getEvent(),
                this.getUsername(),
                this.row,
                this.num,
                this.getValue());
        return output;
    }
    /**
     * This method turns the raw data into a machine readable string to
     * be sent across the network.
     *
     * @return A String that shows the seat data
     */
    public String toDataString() {
        String output = String.format("%s,%s,%s,%s,%s",
                this.getEvent(),
                this.getUsername(),
                this.row,
                this.num,
                this.getValue()
        );
        return output;
    }

}
