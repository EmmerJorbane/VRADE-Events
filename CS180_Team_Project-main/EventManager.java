import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;
/**
 * EventManager
 *
 * This class is made to be able to manage
 * Event objects and keep track of finding, deleting, etc...
 *
 * @author Emma Jordan, L06
 *
 * @version Nov 9, 2025
 *
 */
public class EventManager implements EventManagerInterface {

    // Thread-safe version of an ArrayList
    private ArrayList<Event> events;

    // this is the file the EventManager writes to
    private final String file = "Events.txt";

    // synchronized lock object
    private static final Object LOCK = new Object();

    // This creates an EventManager object to be able to write to a file and save/load/create/delete Events
    public EventManager() {
        if (events == null) {
            synchronized (LOCK) {
                events = new ArrayList<>();
            }
        }
    }

    // create event (only for admin)
    public String createEvent(String eventName, String theatreT, int year, int month, int day, int hour, int minute) {
        Event event = new Event(eventName, theatreT, year, month, day, hour, minute, this);
        for (Event ev : events) {
            if (ev.getEventName().equals(event.getEventName())) {
                return "Only one showing can be created for each title";
            }
        }
        events.add(event);
        saveEvents();
        saveSeats();
        loadSeats();
        return "Showing has been created successfully";
    }

    // delete event (admin only)
    public String deleteEvent(Event e) {
        if (e == null) {
            return "There is no showing to delete";
        } else {
            events.remove(e);
            saveEvents();
            saveSeats();
            return "The showing has been removed";
        }
    }

    // find an event based on a name (EveryOne)
    public Event findEvent(String name) {
        if (name == null) {
            return null;
        } else {
            for (Event e : events) {
                if (e.getEventName().equalsIgnoreCase(name)) {
                    return e;
                }
            }
        }
        return null;
    }

    // find all event for display (EveryOne)
    public ArrayList<Event> getAllEvents() {
        // this returns a copy of the ArrayList events, to avoid manipulation
        return events;
    }

    // ------------------------ Must be Thread safe ----------------------------------

    // ONLY on startup, to populate the array in local memory
    public void loadEvents() {
        synchronized (LOCK) {
            try (BufferedReader br = new BufferedReader(new FileReader("Events.txt"))) {
                events = new ArrayList<Event>();
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (!(line.isEmpty())) {
                        Event event = parseEvents(line);
                        // if it didnt work...
                        if (event != null) {
                            events.add(event);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // This is a helper methods
    // NO MOVIE NAME CAN HAVE A "," IN IT
    public Event parseEvents(String line) {
        try {
            String[] split = line.split(",");
            String name = split[0];
            LocalDateTime dateTime = LocalDateTime.parse(split[1]);
            String theatreType = split[2];

            return new Event(
                    name,
                    theatreType,
                    dateTime.getYear(),
                    dateTime.getMonthValue(),
                    dateTime.getDayOfMonth(),
                    dateTime.getHour(),
                    dateTime.getMinute(),
                    this);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveEvents() {
        synchronized (LOCK) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                String output = "";
                for (Event e : events) {
                    output += (e.getEventName() + "," + e.getTime() + "," + e.getTheatreType());
                    output += "\n";
                }
                bw.write(output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadSeats() {
        UserManager um = new UserManager("usersData.txt");
        um.readUsers();
        ArrayList<String> lines = new ArrayList<String>();
        synchronized (LOCK) {
            try (BufferedReader bfr = new BufferedReader(
                    new FileReader("Seats.txt"))) {
                String line = bfr.readLine();
                while (line != null) {
                    lines.add(line);
                    line = bfr.readLine();
                }
            } catch (Exception e) {
                System.out.println("Error reading in the seats file");
            }
            for (String line : lines) {
                String[] params = line.split(",");
                Seat newSeat = new Seat(
                        params[1].charAt(0),
                        Integer.parseInt(params[2]),
                        um.queryUsers(params[6]),
                        params[7],
                        params[0],
                        params[6],
                        this);
                this.findEvent(params[0]).setSingleSeat(
                        params[1].charAt(0),
                        Integer.parseInt(params[2]),
                        newSeat);

            }
        }
    }
    // write seats to a file, to convert memory data
    public void saveSeats() {
        synchronized (LOCK) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("Seats.txt"))) {
                String output = "";
                for (Event ev : events) {
                    Seat[][] seats = ev.getSeats();
                    for (Seat[] seat : seats) {
                        for (Seat s : seat) {
                            String username = (s.getUser() == null) ? "null" : s.getUser().getUsername();
                            output += (ev.getEventName() + "," + s.getRow() + "," + s.getNum() + "," + s.getOpen() + ","
                                    + s.getSeat() + "," + s.getValue() + "," + username + "," + s.getSize());
                            output += "\n";
                        }
                    }


                }
                bw.write(output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}








