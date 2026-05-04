
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Event.java
 *
 * This class is the Event object that represents a scheduled event in the theater. The class
 * stores the event name, the theatre type, the time the event is scheduled, a 2D array of seats
 * that is created based on the theatre type, and the eventManager that manages the event. The
 * class contains setters and getters for all the instance variables along with methods for
 * Users to reserve and cancel seats. Users using these methods can reserve or cancel one or
 * multiple seats at a time. Everytime an instance variable is mutated using setters or reservation
 * methods, the eventManager writes the new data to the txt file for each event.
 *
 * @author Ryan Poplar, L06
 * @version November 9th, 2025
 */
public class Event implements EventInterface {
    private Seat[][] seats;
    private LocalDateTime time;
    private String eventName;
    private String theatreType;
    private final Object lock = new Object();
    private EventManager eventManager;
    private boolean initialized = false;
    double multiplier = 1.00;
    double capacityMultiplier;

    /**
     * This is the constructor for the class. It creates a new instance of an event based on
     * the time, theatreType, and eventName given. The constructor then assigns the parameters
     * and calls the createSeats method to create the seats for the event.
     */
    public Event(String eventName, String theatreT, int year, int month, 
                 int day, int hour, int minute, EventManager eventManager) {
        try {
            this.time = LocalDateTime.of(year, month, day, hour, minute);
        } catch (DateTimeException e) {
            this.time = LocalDateTime.now();
        }

        if (eventManager == null) {
            eventManager = new EventManager();
        }

        this.eventManager = eventManager;


        this.eventName = eventName;

        try {
            if (theatreT.equals("S") || theatreT.equals("M") || theatreT.equals("L")) {
                this.theatreType = theatreT;
                createSeats(theatreType);
            } else {
                this.theatreType = "S";
                createSeats("S");
            }
        } catch (NullPointerException e) {
            this.theatreType = "S";
            createSeats("S");
        }

        if (hour < 14) {
            multiplier = 1.00;
        } else if (hour >= 14 && hour < 18) {
            multiplier = 1.25;
        } else if (hour >= 18) {
            multiplier = 1.50;
        }
        initialized = true;
    }

    /**
     * This method updated the value of the capacityMultiplier according to the current capacity
     * of an event, and then updates the value of the seats in the event according to the event.
     */
    public void setCapacityMultiplier() {
        int counter = 0;
        for (int i = 0; i < seats.length; i++) {
            for (int j = 0; j < seats[i].length; j++) {
                if (!seats[i][j].getOpen()) {
                    counter++;
                }
            }
        }

        if (theatreType.equals("S") && counter >= 32) {
            capacityMultiplier = 1.25;
        } else if (theatreType.equals("M") && counter >= 162) {
            capacityMultiplier = 1.25;
        } else if (theatreType.equals("L") && counter >= 288) {
            capacityMultiplier = 1.25;
        }

        for (int i = 0; i < seats.length; i++) {
            for (int j = 0; j < seats[i].length; j++) {
                seats[i][j].multiplyValue(capacityMultiplier);
            }
        }
    }


    /**
     * This method updated the value of the multiplier, and then goes through
     * the seat double array updating the value of each seat according to the multiplier.
     */
    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
        int unicode = 65;
        for (int i = 0; i < seats.length; i++) {
            for (int j = 0; j < seats.length; j++) {
                seats[i][j].multiplyValue(multiplier);
            }
        }

    }

    /**
     * This method returns the event name
     *
     * @return the String eventName
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * This method sets the event name to what the method is called with as a parameter.
     * It is synchronized to prevent multi threading errors. The eventManager saveEvents()
     * method is called to write the changes to the event file after the name is changed.
     *
     * @param eventName, the new eventName
     */
    public void setEventName(String eventName) {
        synchronized (lock) {
            this.eventName = eventName;
            if (initialized) {
                eventManager.saveEvents();
            }
        }
    }

    /**
     * This method returns the theatreType
     *
     * @return the String theatreType
     */
    public String getTheatreType() {
        return theatreType;
    }

    /**
     * This method sets the theatreType to what the method is called with as a parameter.
     * It is synchronized to prevent multi threading errors. When the new theatre type is set
     * the seats are recreated based on the new size of the theatre. The eventManager saveEvents()
     * method is called to write the changes to the event file after the theatreType and seats
     * are changed.
     *
     * @param theatreType the new theatre type to set ("S", "M", or "L")
     */
    public void setTheatreType(String theatreType) {
        synchronized (lock) {
            if (theatreType.equals("S") || theatreType.equals("M") || theatreType.equals("L")) {
                this.theatreType = theatreType;
                createSeats(theatreType);
                if (initialized) {
                    eventManager.saveEvents();
                }
            }
        }
    }

    /**
     * This method returns the 2D seat array seats
     *
     * @return the 2D array seats
     */
    public Seat[][] getSeats() {
        return seats;
    }

    public void setSingleSeat(char row, int col, Seat seat) {
        synchronized (lock) {
            this.seats[row - 'A'][col - 1] = seat;
        }
    }
    /**
     * This method takes in a 2D seats array and changes the current seat array to the parameter.
     * This method is synchronized to prevent multi threading errors. The eventManager save
     * events method is called after to write the new changes to the events file.
     *
     * @param seats
     */
    public void setSeats(Seat[][] seats) {
        synchronized (lock) {
            this.seats = seats;
            if (initialized) {
                eventManager.saveEvents();
            }
        }
    }

    /**
     * This method takes in a Letter representing the row and an int for the seat number.
     * It then returns the seat that corresponds to the seat array stored in the event.
     *
     * @param row, and column
     * @return Seat seat that corresponds to the row and column
     */
    public Seat getSeat(char row, int col) {
        int rowIndex = row - 'A';
        return seats[rowIndex][col - 1];
    }

    /**
     * This method takes in the theatreType as a parameter and creates the seat array
     * based on the size of the theatre. If it is a small theatre it creates a 12x12
     * seat arrangement, medium 18x18, large 24x24.
     *
     * @param theatreType
     */
    public void createSeats(String theatreT) {
        synchronized (lock) {
            if (theatreT.equals("S")) {
                seats = new Seat[12][12];
                int unicode = 65;
                for (int i = 0; i < 12; i++) {
                    char letter = (char) (i + unicode);
                    for (int j = 0; j < 12; j++) {
                        seats[i][j] = new Seat(letter, j + 1, null, theatreT, "", "", eventManager);
                        seats[i][j].multiplyValue(multiplier);
                    }
                }
            } else if (theatreT.equals("M")) {
                seats = new Seat[18][18];
                int unicode = 65;
                for (int i = 0; i < 18; i++) {
                    char letter = (char) (i + unicode);
                    for (int j = 0; j < 18; j++) {
                        seats[i][j] = new Seat(letter, j + 1, null, theatreT, "", "", eventManager);
                        seats[i][j].multiplyValue(multiplier);
                    }
                }
            } else if (theatreT.equals("L")) {
                int unicode = 65;
                seats = new Seat[24][24];
                for (int i = 0; i < 24; i++) {
                    char letter = (char) (i + unicode);
                    for (int j = 0; j < 24; j++) {
                        seats[i][j] = new Seat(letter, j + 1, null, theatreT, "", "", eventManager);
                        seats[i][j].multiplyValue(multiplier);
                    }
                }
            } else {
                int unicode = 65;
                seats = new Seat[12][12];
                for (int i = 0; i < 12; i++) {
                    char letter = (char) (i + unicode);
                    for (int j = 0; j < 12; j++) {
                        seats[i][j] = new Seat(letter, j + 1, null, theatreT, "", null, eventManager);
                    }
                }
            }
            if (initialized) {
                eventManager.saveEvents();
            }
        }
    }

    /**
     * This method takes in a User that is looking to reserve a seat, and a string with
     * the row as a char and num right after, for example B6. If the given input is a valid
     * seat in the event, the method assigns the seat to the user if the seat is not already
     * taken by another user. If the seat is properly reserved by the user, then the method
     * returns true, if not, it returns false. After the seat is reserved, the saveEvents
     * in the eventManager class is called to update the file data.
     *
     * @param u (the user reserving the seat), String rowNum ("A2")
     * @return a boolean representing if the seat was successfully reserved or not
     */
    public boolean reserveSingleSeat(User u, String rowNum) {
        synchronized (lock) {
            char rowChar;
            int column;
            try {
                rowChar = rowNum.charAt(0);
                int i = 1;
                while (i < rowNum.length() && Character.isDigit(rowNum.charAt(i))) {
                    i++;
                }
                column = Integer.parseInt(rowNum.substring(1, i));
            } catch (StringIndexOutOfBoundsException e) {
                return false;
            } catch (NumberFormatException e) {
                return false;
            }

            if (rowChar < 65 || rowChar > 88) {
                return false;
            }
            if (rowChar > 76 && theatreType.equals("S")) {
                return false;
            }
            if (rowChar > 82 && theatreType.equals("M")) {
                return false;
            }
            if (column < 1 || column > 24) {
                return false;
            }
            if (column > 12 && theatreType.equals("S")) {
                return false;
            }
            if (column > 18 && theatreType.equals("M")) {
                return false;
            }

            int row = rowChar - 65;
            if (!seats[row][column - 1].getOpen()) {
                return false;
            }

            if (u.getBalance() < seats[row][column - 1].getValue()) {
                return false;
            }
            seats[row][column - 1].setUser(u);
            
            
            u.subtractBalance(seats[row][column - 1].getValue());
            
            if (initialized) {
                eventManager.saveEvents();
            }
            setCapacityMultiplier();
            return true;
        }
    }

    /**
     * This method takes in a User that is looking to cancel a reservation, and a string with
     * the row as a char and num right after, for example B6. If the given input is a valid
     * seat in the event, the method cancels the reservation to the user if the seat is assigned
     * to that User. If the seat is properly canceled by the user, then the method returns true,
     * if not, it returns false. After the seat is canceled, the saveEvents in the eventManager
     * class is called to update the file data.
     *
     * @param u (the user reserving the seat), String rowNum ("A2")
     * @return a boolean representing if the seat was successfully canceled or not
     */
    public boolean cancelSingleSeat(User u, String rowNum) {
        char rowChar;
        int column;
        try {
            rowChar = rowNum.charAt(0);
            column = Integer.parseInt(rowNum.substring(1));
        } catch (StringIndexOutOfBoundsException e) {
            return false;
        } catch (NumberFormatException e) {
            return false;
        }

        if (rowChar < 65 || rowChar > 88) {
            return false;
        }
        if (rowChar > 76 && theatreType.equals("S")) {
            return false;
        }
        if (rowChar > 82 && theatreType.equals("M")) {
            return false;
        }
        if (column < 1 || column > 24) {
            return false;
        }
        if (column > 12 && theatreType.equals("S")) {
            return false;
        }
        if (column > 18 && theatreType.equals("M")) {
            return false;
        }

        int row = rowChar - 65;

        if (seats[row][column - 1].getUser() == null) {
            return false;
        }
        if (!u.getUsername().equals(seats[row][column - 1].getUser().getUsername())) {
            return false;
        }


        seats[row][column - 1].setUser(null);
        synchronized (lock) {
            u.addBalance(seats[row][column - 1].getValue());
        }
        if (initialized) {
            eventManager.saveEvents();
        }
        setCapacityMultiplier();
        return true;
    }

    /**
     * This method takes in a User that is looking to reserve a seat, and a string with
     * a seat to another seat, for example B6-B12. If the given input is a valid
     * set of seats in the event, the method assigns the seats to the user if the seats are
     * not already taken by another user. If the seats are properly reserved by the user,
     * then the method returns true, if not, it returns false. After the seats are reserved,
     * the saveEvents in the eventManager class is called to update the file data.
     *
     * @param u (the user reserving the seat), String rowNum ("A2-A7")
     * @return a boolean representing if the seats were successfully reserved or not
     */
    public boolean reserveMultipleSeats(User u, String seatString) {
        synchronized (lock) {
            char rowChar1;
            char rowChar2;
            int column1;
            int column2;
            try {
                rowChar1 = seatString.charAt(0);
                int i = 1;
                while (Character.isDigit(seatString.charAt(i))) {
                    i++;
                }
                column1 = Integer.parseInt(seatString.substring(1, i));
                rowChar2 = seatString.charAt(i);
                column2 = Integer.parseInt(seatString.substring(i + 1));
            } catch (StringIndexOutOfBoundsException e) {
                return false;
            } catch (NumberFormatException e) {
                return false;
            }
            if (rowChar1 < 65 || rowChar1 > 88) {
                return false;
            }
            if (rowChar1 > 76 && theatreType.equals("S")) {
                return false;
            }
            if (rowChar1 > 82 && theatreType.equals("M")) {
                return false;
            }
            if (column1 < 1 || column1 > 24) {
                return false;
            }
            if (column1 > 12 && theatreType.equals("S")) {
                return false;
            }
            if (column1 > 18 && theatreType.equals("M")) {
                return false;
            }
            if (rowChar2 < 65 || rowChar2 > 88) {
                return false;
            }
            if (rowChar2 > 76 && theatreType.equals("S")) {
                return false;
            }
            if (rowChar2 > 82 && theatreType.equals("M")) {
                return false;
            }
            if (column2 < 1 || column2 > 24) {
                return false;
            }
            if (column2 > 12 && theatreType.equals("S")) {
                return false;
            }
            if (column2 > 18 && theatreType.equals("M")) {
                return false;
            }
            if (column2 <= column1) {
                return false;
            }
            if (rowChar2 != rowChar1) {
                return false;
            }

            int row = rowChar1 - 'A';
            int startCol = column1 - 1; // convert to 0-based
            int endCol = column2 - 1;
            for (int i = startCol; i < endCol; i++) {
                if (!seats[row][i].getOpen()) {
                    return false;
                }
            }
            for (int i = startCol; i <= endCol; i++) {
                if (u.getBalance() < seats[row][i].getValue()) {
                    continue;
                }
                seats[row][i].setUser(u);
                synchronized (lock) {
                    u.subtractBalance(seats[row][i].getValue());
                }
            }
            if (initialized) {
                eventManager.saveEvents();
            }
            setCapacityMultiplier();
            return true;
        }
    }

    /**
     * This method takes in a User that is looking to reserve a seat, and a string with
     * a seat to another seat, for example B6-B12. If the given input is a valid
     * set of seats in the event, the method cancels the seats that are reserved by the
     * user if the seats are not already taken by another user or open. If the seats are
     * properly canceled by the user, then the method returns true, if not, it returns false.
     * After the seats are reserved the saveEvents in the eventManager class is called
     * to update the file data.
     *
     * @param u (the user reserving the seat), String rowNum ("A2-A7")
     * @return a boolean representing if the reservations were successfully canceled or not
     */
    public boolean cancelMultipleSeats(User u, String seatString) {
        synchronized (lock) {
            char rowChar1;
            char rowChar2;
            int column1;
            int column2;
            try {
                rowChar1 = seatString.charAt(0);
                int i = 1;
                while (Character.isDigit(seatString.charAt(i))) {
                    i++;
                }
                column1 = Integer.parseInt(seatString.substring(1, i));
                rowChar2 = seatString.charAt(i);
                column2 = Integer.parseInt(seatString.substring(i + 1));
            } catch (StringIndexOutOfBoundsException e) {
                return false;
            } catch (NumberFormatException e) {
                return false;
            }
            if (rowChar1 < 65 || rowChar1 > 88) {
                return false;
            }
            if (rowChar1 > 76 && theatreType.equals("S")) {
                return false;
            }
            if (rowChar1 > 82 && theatreType.equals("M")) {
                return false;
            }
            if (column1 < 0 || column1 > 24) {
                return false;
            }
            if (column1 > 12 && theatreType.equals("S")) {
                return false;
            }
            if (column1 > 18 && theatreType.equals("M")) {
                return false;
            }
            if (rowChar2 < 65 || rowChar2 > 88) {
                return false;
            }
            if (rowChar2 > 76 && theatreType.equals("S")) {
                return false;
            }
            if (rowChar2 > 82 && theatreType.equals("M")) {
                return false;
            }
            if (column2 < 0 || column2 > 24) {
                return false;
            }
            if (column2 > 12 && theatreType.equals("S")) {
                return false;
            }
            if (column2 > 18 && theatreType.equals("M")) {
                return false;
            }
            if (column2 <= column1) {
                return false;
            }
            if (rowChar2 != rowChar1) {
                return false;
            }

            int row = rowChar1 - 65;
            int startIndex = column1 - 1;
            int endIndex = column2 - 1;

            int count = 0;
            for (int i = startIndex; i <= endIndex; i++) {
                Seat seat = seats[row][i];
                if (seat.getUser() != null && seat.getUser().getUsername().equals(u.getUsername())) {
                    seat.setUser(null);
                    synchronized (lock) {
                        u.addBalance(seats[row][i].getValue());
                    }
                    count++;
                }
            }
            if (count == 0) {
                return false;
            }
            if (initialized) {
                eventManager.saveEvents();
            }
            setCapacityMultiplier();
            return true;
        }
    }



    /**
     * This method returns the year that the event will take place
     *
     * @return the int that represents the year
     */
    public int getYear() {
        ZoneId estZone = ZoneId.of("America/New_York");
        ZonedDateTime estZoned = time.atZone(estZone);
        return estZoned.getYear();
    }

    /**
     * This method returns the month that the event will take place
     *
     * @return the int that represents the month
     */
    public int getMonth() {
        ZoneId estZone = ZoneId.of("America/New_York");
        ZonedDateTime estZoned = time.atZone(estZone);
        return estZoned.getMonth().getValue();
    }

    /**
     * This method returns the day in the year that the event will take place
     *
     * @return the int that represents the day
     */
    public int getDay() {
        ZoneId estZone = ZoneId.of("America/New_York");
        ZonedDateTime estZoned = time.atZone(estZone);
        return estZoned.getDayOfMonth();
    }

    /**
     * This method returns the hour in the day that the event will take place
     *
     * @return the int that represents the hour
     */
    public int getHour() {
        ZoneId estZone = ZoneId.of("America/New_York");
        ZonedDateTime estZoned = time.atZone(estZone);
        return estZoned.getHour();
    }

    /**
     * This method returns the minute in the hour that the event will take place
     *
     * @return the int that represents the minute in the hour
     */
    public int getMinute() {
        ZoneId estZone = ZoneId.of("America/New_York");
        ZonedDateTime estZoned = time.atZone(estZone);
        return estZoned.getMinute();
    }

    /**
     * This method returns a string that has the full time that the event will take place
     *
     * @return a formatted string that shows the year, month, day, hour, minute, of the event
     */
    public String getFullTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return time.format(formatter);
    }

    /**
     * This method sets a new time for the event from a ISO formatted string as a parameter.
     * After it sets the new time it calls the saveEvents method in Event manager to update
     * the data in the file.
     *
     * @param iSO (string)
     * @return the boolean that represents if the new time was updated properly
     */
    public boolean setFullTime(String iSO) {
        synchronized (lock) {
            try {
                String date = iSO.replace(' ', 'T');
                time = LocalDateTime.parse(date);
                if (time.getHour() < 14) {
                    multiplier = 1.00;
                } else if (time.getHour() >= 14 && time.getHour() < 18) {
                    multiplier = 1.25;
                } else if (time.getHour() >= 18) {
                    multiplier = 1.50;
                }
                setMultiplier(multiplier);
                eventManager.saveEvents();
            } catch (DateTimeParseException e) {
                return false;
            }
            return true;
        }
    }

    /**
     * This method returns a string that says the eventName, theatreType, and full
     * time that the event in taking place
     *
     * @return foramtted String with info
     */
    public String toString() {
        return eventName + " (" + theatreType + ") at " + getFullTime();
    }

    /**
     * This method return the LocalDateTime value of the time this event is
     * supposed to take place.
     *
     * @return The LocalDateTime value of when this event takes place
     */
    public LocalDateTime getTime() {
        return time;
    }
}
